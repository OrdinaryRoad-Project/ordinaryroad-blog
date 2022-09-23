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
import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpStatus
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.handler.HttpException
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.dal.dao.result.BlogArticleUserBrowsed
import tech.ordinaryroad.blog.quarkus.dal.dao.result.BlogArticleUserLiked
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogTag
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogType
import tech.ordinaryroad.blog.quarkus.dto.BlogArticleDTO
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotValidException
import tech.ordinaryroad.blog.quarkus.exception.BlogUserNotFoundException
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogArticleMapStruct
import tech.ordinaryroad.blog.quarkus.request.*
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticleDetailVO
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticlePreviewUserBrowsedVO
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticlePreviewUserLikedVO
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticlePreviewVO
import tech.ordinaryroad.blog.quarkus.service.*
import tech.ordinaryroad.blog.quarkus.util.BlogUtils
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import tech.ordinaryroad.commons.mybatis.quarkus.utils.TableInfoUtils
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("article")
class BlogArticleResource {

    val articleMapStruct = BlogArticleMapStruct.INSTANCE

    @Inject
    protected lateinit var articleService: BlogArticleService

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    @Inject
    protected lateinit var typeService: BlogTypeService

    @Inject
    protected lateinit var tagService: BlogTagService

    @Inject
    protected lateinit var userBrowsedArticleService: BlogUserBrowsedArticleService

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

        articleService.moveToTrash(id)
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

        articleService.recoverFromTrash(id)
    }

    /**
     * 查询最新的草稿
     */
    @GET
    @Path("draft")
    fun getDraft(): BlogArticleDTO? {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val allByStatusAndCreateBy = articleService.findAllByStatusAndCreatedBy(BlogArticleStatus.DRAFT, userId)

        if (CollUtil.isEmpty(allByStatusAndCreateBy)) {
            return null
        } else {
            val firstArticle = allByStatusAndCreateBy.first()
            val dto = dtoService.transfer(firstArticle, BlogArticleDTO::class.java)

            return dto
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

        var typeId = StrUtil.EMPTY
        val typeName = request.typeName
        if (!typeName.isNullOrBlank()) {
            var type = typeService.findByNameAndCreateBy(typeName, userId)
            if (Objects.isNull(type)) {
                // 创建
                type = typeService.create(BlogType().apply {
                    name = typeName
                })
            }
            typeId = type!!.uuid
        }

        val tagIds: List<String>
        val tagNames = request.tagNames
        tagIds = if (CollUtil.isEmpty(tagNames)) {
            arrayListOf()
        } else {
            val ids = arrayListOf<String>()
            tagNames.forEach {
                val wrapper = Wrappers.query<BlogTag>()
                    .eq("name", it)
                var tag = tagService.dao.selectOne(wrapper)
                if (Objects.isNull(tag)) {
                    tag = tagService.create(BlogTag().apply {
                        name = it
                    })
                }
                ids.add(tag.uuid)
            }
            ids
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
                        "",
                        typeId,
                        tagIds
                    )
                )
                // 将FirstId更新为当前Id
                val draftUpdate = articleService.update(
                    BlogArticle().apply {
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
                        firstId,
                        typeId,
                        tagIds
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

        var typeId = StrUtil.EMPTY
        val typeName = request.typeName
        if (!typeName.isNullOrBlank()) {
            var type = typeService.findByNameAndCreateBy(typeName, userId)
            if (Objects.isNull(type)) {
                // 创建
                type = typeService.create(BlogType().apply {
                    name = typeName
                })
            }
            typeId = type!!.uuid
        }

        val tagIds: List<String>
        val tagNames = request.tagNames
        tagIds = if (CollUtil.isEmpty(tagNames)) {
            arrayListOf()
        } else {
            val ids = arrayListOf<String>()
            tagNames.forEach {
                val wrapper = Wrappers.query<BlogTag>()
                    .eq("name", it)
                var tag = tagService.dao.selectOne(wrapper)
                if (Objects.isNull(tag)) {
                    tag = tagService.create(BlogTag().apply {
                        name = it
                    })
                }
                ids.add(tag.uuid)
            }
            ids
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
                        "",
                        typeId,
                        tagIds
                    )
                )
                val publishUpdate = articleService.update(
                    BlogArticle().apply {
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
                        firstId,
                        typeId,
                        tagIds
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
    fun pageOwn(@BeanParam request: BlogArticleQueryRequest): Page<BlogArticleDTO> {
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

        return dtoPage
    }

    /**
     * 用户分页查询自己已点赞的文章
     */
    @GET
    @Path("page/own/liked/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageOwnLiked(@BeanParam request: BlogArticleQueryRequest): PageDTO<BlogArticlePreviewUserLikedVO> {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        var orderBySql = ""
        val sortBy = request.sortBy
        if (!sortBy.isNullOrEmpty()) {
            sortBy.forEachIndexed { index, item ->
                orderBySql += "ba."
                orderBySql += TableInfoUtils.getTableFieldColumn(articleService.entityClass, item)
                if (request.sortDesc.getOrElse(index) { false }) {
                    orderBySql += " "
                    orderBySql += "DESC"
                }
                if (index != sortBy.size - 1) {
                    orderBySql += ", "
                }
            }
        }

        val page = articleService.dao.pageLiked(
            PageDTO.of(request.page, request.size), userId,
            BlogArticle().apply {
                title = request.title
                summary = request.summary
                content = request.content
                canReward = request.canReward
                original = request.original
                firstId = request.firstId
                createBy = request.createBy
            }, orderBySql
        ) as PageDTO<BlogArticleUserLiked>

        val voPage = PageUtils.copyPage<BlogArticleUserLiked, BlogArticlePreviewUserLikedVO>(page).apply {
            setRecords(page.records.map(articleMapStruct::transferPreviewUserLiked))
        } as PageDTO<BlogArticlePreviewUserLikedVO>

        return voPage
    }

    /**
     * 用户分页查询自己已浏览的文章
     */
    @GET
    @Path("page/own/browsed/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageOwnBrowsed(@BeanParam request: BlogArticleQueryRequest): PageDTO<BlogArticlePreviewUserBrowsedVO> {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        var orderBySql = ""
        val sortBy = request.sortBy
        if (!sortBy.isNullOrEmpty()) {
            sortBy.forEachIndexed { index, item ->
                orderBySql += "ba."
                orderBySql += TableInfoUtils.getTableFieldColumn(articleService.entityClass, item)
                if (request.sortDesc.getOrElse(index) { false }) {
                    orderBySql += " "
                    orderBySql += "DESC"
                }
                if (index != sortBy.size - 1) {
                    orderBySql += ", "
                }
            }
        }

        val page = articleService.dao.pageBrowsed(
            PageDTO.of(request.page, request.size), userId,
            BlogArticle().apply {
                title = request.title
                summary = request.summary
                content = request.content
                canReward = request.canReward
                original = request.original
                firstId = request.firstId
                createBy = request.createBy
            }, orderBySql
        ) as PageDTO<BlogArticleUserBrowsed>

        val voPage = PageUtils.copyPage<BlogArticleUserBrowsed, BlogArticlePreviewUserBrowsedVO>(page).apply {
            setRecords(page.records.map(articleMapStruct::transferPreviewUserBrowsed))
        } as PageDTO<BlogArticlePreviewUserBrowsedVO>

        return voPage
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

        val tagName = request.tagName
        var tagId = ""
        var tagIds = emptyList<String>()
        // TODO 防止sql注入
        if (!tagName.isNullOrEmpty() && tagName != "%") {
            tagIds = tagService.dao.selectIdByNameIn(listOf(tagName))
            tagId = tagIds.first()
        }

        val wrapper = ChainWrappers.queryChain(articleService.dao)
            .like(!request.title.isNullOrBlank(), "title", "%" + request.title + "%")
            .like(!request.summary.isNullOrBlank(), "summary", "%" + request.summary + "%")
            .like(!request.content.isNullOrBlank(), "content", "%" + request.content + "%")
            .eq("status", status)
            .eq(request.createBy != null, "create_by", request.createBy)
            .like(tagIds.isNotEmpty(), "tag_ids", "%${tagId}%")

        val page = articleService.page(request, wrapper)

        val voPage = PageUtils.copyPage<BlogArticle, BlogArticlePreviewVO>(page).apply {
            records = page.records.stream().map(articleMapStruct::transferPreview)
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
    ): BlogArticleDTO {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val blogArticle = articleService.findByIdAndCreatedBy(id, userId)

        if (blogArticle == null) {
            throw BlogArticleNotFoundException()
        } else {
            val dto = dtoService.transfer(blogArticle, BlogArticleDTO::class.java)

            return dto
        }
    }

    /**
     * 根据Id查询已发布的文章
     *
     * 会保存浏览记录
     */
    @GET
    @Path("publish/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun findPublishById(@RestPath id: String): BlogArticleDetailVO {
        val blogArticle = articleService.findByIdAndStatus(id, BlogArticleStatus.PUBLISH)

        if (blogArticle == null) {
            throw BlogArticleNotFoundException()
        } else {
            userBrowsedArticleService.browseArticle(
                blogArticle.uuid,
                BlogUtils.getClientIp(),
                StpUtil.getLoginIdDefaultNull() as String?
            )
            return articleMapStruct.transferDetail(blogArticle)
        }
    }

    /**
     * 获取已发表文章的创建时间和最后更新时间
     */
    @GET
    @Path("publish/{id}/created_update_Time")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPublishCreatedTimeAndUpdateTimeById(@RestPath id: String): JsonObject {
        return articleService.getPublishCreatedTimeAndUpdateTimeById(id)
    }

    /**
     * 获取已发布文章的个数
     */
    @GET
    @Path("count")
    fun count(
        @Valid @Size(
            max = 32,
            message = "userId长度不能大于32"
        ) @DefaultValue("") @RestQuery userId: String
    ): Long {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        val wrapper = Wrappers.query<BlogArticle>()
            .eq("status", BlogArticleStatus.PUBLISH)
            .eq(userId.isNotBlank(), "create_by", userId)
        return articleService.dao.selectCount(wrapper)
    }

    /**
     * 获取评论数前N的文章
     */
    @GET
    @Path("top/comment")
    fun getCommentTopN(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
        @Valid @Max(value = 50, message = "n不能大于50") @DefaultValue("10") @RestQuery n: Int
    ): List<Map<String, String>> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        return articleService.dao.getTopNByUserId(n, userId)
    }

    /**
     * 获取用户每日发表数
     */
    @GET
    @Path("count/daily/posts")
    fun countDailyPosts(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
        @RestQuery fromTime: LocalDateTime?,
        @RestQuery endTime: LocalDateTime?,
    ): List<Map<String, String>> {
        val now = LocalDateTime.now()
        val currentYear = now.year
        val startDateTime = "${currentYear}-01-01 00:00:00"
        val endDateTime = "${currentYear}-12-31 23:59:59"
        return articleService.dao.countDailyPosts(startDateTime, endDateTime, userId)
    }

    /**
     * 用户点赞文章
     */
    @POST
    @Transactional
    @Path("likes/{id}")
    fun likesArticle(@Valid @Size(max = 32, message = "id长度不能大于32") @RestPath id: String) {
        StpUtil.checkLogin()

        articleService.likesArticle(id)
    }

    /**
     * 用户取消点赞文章
     */
    @POST
    @Transactional
    @Path("unlikes/{id}")
    fun unlikesArticle(@Valid @Size(max = 32, message = "id长度不能大于32") @RestPath id: String) {
        StpUtil.checkLogin()

        articleService.unlikesArticle(id)
    }

    /**
     * 用户删除文章浏览记录
     */
    @POST
    @Transactional
    @Path("un_browses/{id}")
    fun unBrowsesArticle(@Valid @Size(max = 32, message = "id长度不能大于32") @RestPath id: String) {
        StpUtil.checkLogin()

        articleService.unBrowsesArticle(id)
    }

    /**
     * 获取用户是否点赞
     */
    @GET
    @Transactional
    @Path("liked/{id}")
    fun getLiked(@Valid @Size(max = 32, message = "id长度不能大于32") @RestPath id: String): Boolean {
        StpUtil.checkLogin()

        return articleService.getLiked(id)
    }

    //endregion

    //region TODO 开发中

    /**
     * 查询上一篇/下一篇文章
     */
    @GET
    @Path("pre_and_next/{id}")
    fun getPreAndNextArticle(@RestPath id: String): JsonObject {
        return articleService.getPreAndNextArticle(id)
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
        val article = articleService.update(
            BlogArticle().apply {
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
        val article = articleService.update(
            BlogArticle().apply {
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
