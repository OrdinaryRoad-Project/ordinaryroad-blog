/*
 * MIT License
 *
 * Copyright (c) 2021 苗锦洲
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package tech.ordinaryroad.blog.quarkus.resource

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.collection.CollUtil
import cn.hutool.http.HttpStatus
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.handler.HttpException
import org.jboss.resteasy.reactive.RestPath
import tech.ordinaryroad.blog.quarkus.dto.BlogArticleDTO
import tech.ordinaryroad.blog.quarkus.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotValidException
import tech.ordinaryroad.blog.quarkus.facade.BlogArticleFacade
import tech.ordinaryroad.blog.quarkus.request.*
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService
import tech.ordinaryroad.blog.quarkus.service.BlogArticleTransferService
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.vo.BlogArticlePreviewVO
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import java.util.stream.Collectors
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("article")
class BlogArticleResource {

    @Inject
    protected lateinit var articleService: BlogArticleService

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    @Inject
    protected lateinit var articleFacade: BlogArticleFacade

    @Inject
    protected lateinit var articleTransferService: BlogArticleTransferService

    //region 已测试方法

    /**
     * 用户移动至废纸篓
     */
    @POST
    @Path("move_to_trash/{id}")
    @Transactional
    fun moveToTrash(@RestPath id: String) {
        /* 登录校验 */
        StpUtil.checkLogin()

        articleFacade.moveToTrash(id)
    }

    /**
     * 用户从废纸篓恢复
     */
    @POST
    @Path("recover_from_trash/{id}")
    @Transactional
    fun recoverFromTrash(@RestPath id: String) {
        /* 登录校验 */
        StpUtil.checkLogin()

        articleFacade.recoverFromTrash(id)
    }

    /**
     * 查询最新的草稿
     */
    @GET
    @Path("draft")
    fun getDraft(): Response {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val allByStatusAndCreateBy = articleService.findAllByStatusAndCreatedBy(BlogArticleStatus.DRAFT, userId)

        if (CollUtil.isEmpty(allByStatusAndCreateBy)) {
            return Response.ok().build()
        } else {
            return Response.ok(allByStatusAndCreateBy.first()).build()
        }
    }

    /**
     * 用户手动保存草稿
     */
    @POST
    @Path("draft")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun saveDraft(@Valid request: BlogArticleSaveDraftRequest): Response {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()
        val firstId = request.firstId

        if (request.content.trim() == "\n") {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        if (firstId.isNullOrBlank()) {
            val allFirstArticleByStatusAndCreatedBy =
                articleService.findAllFirstArticleByStatusAndCreateBy(BlogArticleStatus.DRAFT, userId)

            if (allFirstArticleByStatusAndCreatedBy.isEmpty()) {
                /* 直接创建草稿 */
                val draft = articleService.create(
                    BlogArticle(
                        request.title,
                        request.coverImage,
                        request.summary,
                        request.content,
                        BlogArticleStatus.DRAFT,
                        request.canReward,
                        request.original,
                        ""
                    )
                )
                // 将FirstId更新为当前Id
                val draftUpdate = articleService.update(BlogArticle().apply {
                    this.uuid = draft.uuid
                    this.firstId = draft.uuid
                })

                val dto = dtoService.transfer(draftUpdate, BlogArticleDTO::class.java)

                return Response.ok(dto).build()
            } else {
                /* 需要传FirstId */
                return Response.status(Response.Status.BAD_REQUEST).build()
            }
        } else {
            val byIdAndCreatedBy = articleService.findByIdAndCreatedBy(firstId, userId)
            if (byIdAndCreatedBy == null) {
                /* FirstId不存在 */
                return Response.status(Response.Status.NOT_FOUND).build()
            } else {
                if (byIdAndCreatedBy.firstId != byIdAndCreatedBy.uuid) {
                    /* 传过来的FirstId不是起始版本 */
                    return Response.status(Response.Status.BAD_REQUEST).build()
                }

                /* 如果存在删除的不允许保存草稿 */
                val articleInTrash = articleService.findFirstOrLastByFirstIdAndStatus(
                    byIdAndCreatedBy.uuid,
                    BlogArticleStatus.TRASH,
                    false
                )
                if (articleInTrash != null) {
                    // 判断firstId下的最新版本是否已经移动至垃圾箱
                    BlogArticleNotValidException().throws()
                }

                /* 保存历史版本 */
                if (byIdAndCreatedBy.status == BlogArticleStatus.DRAFT) {
                    // 起始版本是草稿
                    articleService.updateStatusById(byIdAndCreatedBy.uuid, BlogArticleStatus.INHERIT)
                } else {
                    // 将DRAFT更新为INHERIT
                    articleService.updateStatusByFirstIdAndCreatedBy(
                        firstId,
                        userId,
                        BlogArticleStatus.DRAFT,
                        BlogArticleStatus.INHERIT
                    )
                }
                /* 保存草稿 */
                val draft = articleService.create(
                    BlogArticle(
                        request.title,
                        request.coverImage,
                        request.summary,
                        request.content,
                        BlogArticleStatus.DRAFT,
                        request.canReward,
                        request.original,
                        firstId
                    )
                )
                val dto = dtoService.transfer(draft, BlogArticleDTO::class.java)

                return Response.ok(dto).build()
            }
        }
    }

    /**
     * 用户发布文章
     */
    @POST
    @Path("publish")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun publish(@Valid request: BlogArticlePublishRequest): Response {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()
        val firstId = request.firstId

        if (request.content.trim() == "\n") {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        // 代表没有草稿直接发布
        if (firstId.isNullOrBlank()) {
            /* 判断是否存在草稿 */
            val countByStatusAndCreatedBy = articleService.countByStatusAndCreatedBy(BlogArticleStatus.DRAFT, userId)
            if (countByStatusAndCreatedBy != 0L) {
                /* 存在草稿但是没有传过来 */
                return Response.status(Response.Status.BAD_REQUEST).build()
            } else {
                /* 不存在草稿，直接发布 */
                val publish = articleService.create(
                    BlogArticle(
                        request.title,
                        request.coverImage,
                        request.summary,
                        request.content,
                        BlogArticleStatus.PUBLISH,
                        request.canReward,
                        request.original,
                        ""
                    )
                )
                val publishUpdate = articleService.update(BlogArticle().apply {
                    this.uuid = publish.uuid
                    this.firstId = publish.uuid
                })

                val dto = dtoService.transfer(publishUpdate, BlogArticleDTO::class.java)

                return Response.ok(dto).build()
            }
        } else {
            val byIdAndCreatedBy = articleService.findByIdAndCreatedBy(firstId, userId)
            if (byIdAndCreatedBy == null) {
                /* FirstId不存在 */
                return Response.status(Response.Status.NOT_FOUND).build()
            } else {
                if (byIdAndCreatedBy.firstId != byIdAndCreatedBy.uuid) {
                    /* 不是起始版本 */
                    return Response.status(Response.Status.BAD_REQUEST).build()
                }

                /* 如果存在删除的不允许发布 */
                val articleInTrash = articleService.findFirstOrLastByFirstIdAndStatus(
                    byIdAndCreatedBy.uuid,
                    BlogArticleStatus.TRASH,
                    false
                )
                if (articleInTrash != null) {
                    // 判断firstId下的最新版本是否已经移动至垃圾箱
                    BlogArticleNotValidException().throws()
                }

                /* 保存历史版本 */
                if (byIdAndCreatedBy.status == BlogArticleStatus.DRAFT) {
                    // 是起始版本
                    articleService.updateStatusById(byIdAndCreatedBy.uuid, BlogArticleStatus.INHERIT)
                } else {
                    articleService.updateStatusByFirstIdAndCreatedBy(
                        firstId,
                        userId,
                        BlogArticleStatus.DRAFT,
                        BlogArticleStatus.INHERIT
                    )
                    articleService.updateStatusByFirstIdAndCreatedBy(
                        firstId,
                        userId,
                        BlogArticleStatus.PUBLISH,
                        BlogArticleStatus.PUBLISH_INHERIT
                    )
                }
                /* 发布文章 */
                val publish = articleService.create(
                    BlogArticle(
                        request.title,
                        request.coverImage,
                        request.summary,
                        request.content,
                        BlogArticleStatus.PUBLISH,
                        request.canReward,
                        request.original,
                        firstId
                    )
                )

                val dto = dtoService.transfer(publish, BlogArticleDTO::class.java)

                return Response.ok(dto).build()
            }
        }
    }

    /**
     * 用户分页查询自己的文章
     */
    @GET
    @Path("page/own/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageOwn(@BeanParam request: BlogArticleQueryRequest): Response {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val wrapper = ChainWrappers.queryChain(articleService.dao)
            .like(!request.title.isNullOrBlank(), "title", "%" + request.title + "%")
            .like(!request.summary.isNullOrBlank(), "summary", "%" + request.summary + "%")
            .like(!request.content.isNullOrBlank(), "content", "%" + request.content + "%")
            .eq(request.canReward != null, "can_reward", request.canReward)
            .eq(request.original != null, "original", request.original)
            .`in`(!request.status.isNullOrEmpty(), "status", request.status)
            .eq(!request.firstId.isNullOrBlank(), "first_id", request.firstId)
            .eq("create_by", userId)

        val page = articleService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogArticleDTO::class.java)
        }

        return Response.ok()
            .entity(dtoPage)
            .build()
    }

    /**
     * 用户根据FirstId分页查询文章的所有版本
     */
    @GET
    @Path("page/firstId/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageByFirstId(@BeanParam request: BlogArticleQueryRequest): Response {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        /* 参数校验 */
        val firstId = request.firstId
        if (firstId.isNullOrBlank()) {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        val wrapper = ChainWrappers.queryChain(articleService.dao)
            .eq("create_by", userId)
            .eq("first_id", firstId)

        val page = articleService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogArticleDTO::class.java)
        }

        return Response.ok(dtoPage).build()
    }

    /**
     * 分页查询用户已发布的文章
     */
    @GET
    @Path("page/publish/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pagePublish(@BeanParam request: BlogArticleQueryRequest): Response {
        // TODO
        val status = BlogArticleStatus.PUBLISH

        val wrapper = ChainWrappers.queryChain(articleService.dao)
            .eq("status", status)
            .eq(request.createBy != null, "create_by", request.createBy)

        val page = articleService.page(request, wrapper)

        val voPage = PageUtils.copyPage<BlogArticle, BlogArticlePreviewVO>(page).apply {
            records = page.records.stream().map(articleTransferService::transferPreview)
                .collect(Collectors.toList())
        }
        return Response.ok().entity(voPage).build()
    }

    /**
     * 查询用户自己创建的文章
     */
    @GET
    @Path("own/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findOwnById(
        @Valid @NotBlank(message = "Id不能为空")
        @RestPath id: String
    ): Response {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val blogArticle = articleService.findByIdAndCreatedBy(id, userId)

        if (blogArticle == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        } else {
            val dto = dtoService.transfer(blogArticle, BlogArticleDTO::class.java)

            return Response.ok()
                .entity(dto)
                .build()
        }
    }

    /**
     * 根据Id查询已发布的文章
     */
    @GET
    @Path("publish/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findPublishById(@RestPath id: String): Response {
        val blogArticle = articleService.findByIdAndStatus(id, BlogArticleStatus.PUBLISH)

        if (blogArticle == null) {
            return Response.status(Response.Status.NOT_FOUND).build()
        } else {
            val articleVO = articleTransferService.transferDetail(blogArticle)
            return Response.ok().entity(articleVO).build()
        }
    }

    /**
     * 获取已发表文章的创建时间和最后更新时间
     */
    @GET
    @Path("publish/{id}/created_update_Time")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPublishCreatedTimeAndUpdateTimeById(@RestPath id: String): JsonObject {
        return articleFacade.getPublishCreatedTimeAndUpdateTimeById(id)
    }

    //endregion

    //region TODO 开发中

    /**
     * 查询上一篇/下一篇文章
     */
    @GET
    @Path("pre_and_next/{id}")
    fun getPreAndNextArticle(@RestPath id: String): JsonObject {
        return articleFacade.getPreAndNextArticle(id)
    }
    //endregion

    //region 待开发

    /**
     * 自动保存草稿
     * 将用户的之前的草稿设置为历史版本
     */
    @POST
    @Path("auto_draft")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun autoDraft(@Valid @BeanParam request: BlogArticleCreateRequest): Response {
        throwBadRequest()
        val blogArticle = articleService.create(
            BlogArticle().apply {
                title = request.title
                content = request.content
                status = BlogArticleStatus.DRAFT
            }
        )
        return Response.ok().build()
    }

    @PUT
    @Path("{id}/coverImage")
    @Produces(MediaType.APPLICATION_JSON)
    fun updateCoverImage(@Valid @BeanParam request: BlogArticleUpdateCoverImageRequest): Response {
        throwBadRequest()
        val uuid = request.uuid

        val findById = articleService.findById(uuid)
        if (findById == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        }

        val coverImage = request.coverImage
        val article = articleService.update(BlogArticle().apply {
            this.uuid = uuid
            this.coverImage = coverImage
        })
        return Response.ok()
            .entity(article)
            .build()
    }

    //endregion

    //region 待开发（需要管理员权限）

    /**
     * 分页查询所有文章
     */
    @GET
    @Path("admin/page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageAdmin(@BeanParam request: BlogArticleQueryRequest): Response {
        throwBadRequest()

        val wrapper = ChainWrappers.queryChain(articleService.dao)
        val page = articleService.page(request, wrapper)
        return Response.ok()
            .entity(page)
            .build()
    }

    @DELETE
    @Path("/delete/{id}")
    fun deleteById(@RestPath id: String): Response {
        throwBadRequest()

        val success = articleService.delete(id)
        return Response.ok()
            .entity(success)
            .build()
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun update(@Valid @BeanParam request: BlogArticleUpdateRequest): Response {
        throwBadRequest()

        val uuid = request.uuid

        val findById = articleService.findById(uuid)
        if (findById == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        }

        val title = request.title
        val article = articleService.update(BlogArticle().apply {
            this.uuid = uuid
            this.title = title
        })
        return Response.ok()
            .entity(article)
            .build()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findById(@RestPath id: String): Response {
        throwBadRequest()

        val blogArticle = articleService.findById(id)

        if (blogArticle == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        } else {
            return Response.ok()
                .entity(blogArticle)
                .build()
        }
    }

    //endregion

    companion object {
        fun throwBadRequest() {
            throw HttpException(HttpStatus.HTTP_BAD_REQUEST, "暂不支持访问")
        }
    }

}
