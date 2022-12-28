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

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.http.HttpStatus
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import com.fasterxml.jackson.databind.JsonNode
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.handler.HttpException
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.constant.SaTokenConstants
import tech.ordinaryroad.blog.quarkus.dal.dao.result.BlogArticleUserBrowsed
import tech.ordinaryroad.blog.quarkus.dal.dao.result.BlogArticleUserLiked
import tech.ordinaryroad.blog.quarkus.dal.entity.*
import tech.ordinaryroad.blog.quarkus.dto.BlogArticleDTO
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
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
import tech.ordinaryroad.blog.quarkus.state.article.context.BlogArticleContext
import tech.ordinaryroad.blog.quarkus.util.BlogUtils
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import tech.ordinaryroad.commons.mybatis.quarkus.utils.TableInfoUtils
import java.time.LocalDateTime
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
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

    @Inject
    protected lateinit var userLikedArticleService: BlogUserLikedArticleService

    @Inject
    protected lateinit var validateService: BlogValidateService

    //region 已测试方法

    /**
     * 审核员开始审核文章
     */
    @SaCheckRole(SaTokenConstants.ROLE_AUDITOR)
    @POST
    @Path("start_auditing/{id}")
    @Transactional
    fun startAuditing(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        val article = articleService.findById(id)
        val blogArticleContext =
            BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.startAuditing(article)
    }

    /**
     * 审核员文章审核通过
     */
    @SaCheckRole(SaTokenConstants.ROLE_AUDITOR)
    @POST
    @Path("audit_approved/{id}")
    @Transactional
    fun auditApproved(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        val article = articleService.findById(id)
        val blogArticleContext =
            BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.auditApproved(article)
    }

    /**
     * 审核员文章审核失败
     */
    @SaCheckRole(SaTokenConstants.ROLE_AUDITOR)
    @POST
    @Path("audit_failed/{id}")
    @Transactional
    fun auditFailed(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String,
        jsonNode: JsonNode
    ) {
        if (!jsonNode.has("reason")) {
            throw BaseBlogException("请输入打回原因")
        }
        val reason = jsonNode.get("reason").asText()
        if (reason.isBlank()) {
            throw BaseBlogException("请输入打回原因")
        }
        if (reason.length > 500) {
            throw BaseBlogException("打回原因长度不能大于500")
        }

        val article = articleService.findById(id)
        val blogArticleContext =
            BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.auditFailed(article, reason)
    }

    /**
     * 审核员标记文章违规
     */
    @SaCheckRole(SaTokenConstants.ROLE_AUDITOR)
    @POST
    @Path("article_violation/{id}")
    @Transactional
    fun articleViolation(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String,
        jsonNode: JsonNode
    ) {
        if (!jsonNode.has("reason")) {
            throw BaseBlogException("请输入违规原因")
        }
        val reason = jsonNode.get("reason").asText()
        if (reason.isBlank()) {
            throw BaseBlogException("请输入违规原因")
        }
        if (reason.length > 500) {
            throw BaseBlogException("违规原因长度不能大于500")
        }

        val article = articleService.findById(id)
        val blogArticleContext =
            BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.articleViolation(article, reason)
    }

    /**
     * 用户申诉自己的文章
     */
    @SaCheckLogin
    @POST
    @Path("article_appeal/{id}")
    @Transactional
    fun articleAppeal(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String,
        jsonNode: JsonNode
    ) {
        if (!jsonNode.has("reason")) {
            throw BaseBlogException("请输入申诉理由")
        }
        val reason = jsonNode.get("reason").asText()
        if (reason.isBlank()) {
            throw BaseBlogException("请输入申诉理由")
        }
        if (reason.length > 500) {
            throw BaseBlogException("申诉理由长度不能大于500")
        }

        val article = validateService.validateOwnArticle(id)
        val blogArticleContext = BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.articleAppeal(article, reason)
    }

    /**
     * 用户将自己的文章移动至废纸篓
     */
    @SaCheckLogin
    @POST
    @Path("move_to_trash/{id}")
    @Transactional
    fun moveToTrash(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        val article = validateService.validateOwnArticle(id)
        val blogArticleContext =
            BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.moveToTrash(article)
    }

    /**
     * 用户从废纸篓恢复自己的文章
     */
    @SaCheckLogin
    @POST
    @Path("recover_from_trash/{id}")
    @Transactional
    fun recoverFromTrash(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        val article = validateService.validateOwnArticle(id)
        val blogArticleContext =
            BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)
        blogArticleContext.recoverFromTrash(article)
    }

    /**
     * 用户手动保存草稿
     */
    @SaCheckLogin
    @POST
    @Path("draft")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun saveDraft(@Valid request: BlogArticleSaveDraftRequest): Response {
        val userId = StpUtil.getLoginIdAsString()
        val firstId = request.firstId

        if (request.content.trim() == "\n") {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        val typeId = typeService.getOwnIdByNameAndCreateBy(request.typeName, userId)
        val tagIds = tagService.getIdListByNamesAndCreateBy(request.tagNames, userId)

        if (firstId.isNullOrBlank()) {
            /* 直接创建草稿 */
            val draft = articleService.create(
                BlogArticle(
                    request.title,
                    request.coverImage,
                    request.summary,
                    request.content,
                    BlogArticleStatus.DRAFT,
                    request.canComment,
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
                        request.canComment,
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
     * 用户手动保存草稿
     */
    @SaCheckLogin
    @POST
    @Path("draft_v2")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun saveDraftV2(@Valid request: BlogArticleSaveDraftRequest): Response {
        val userId = StpUtil.getLoginIdAsString()

        if (request.content.trim() == "\n") {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        val firstId = request.firstId
        val article = if (firstId.isNullOrBlank()) {
            null
        } else {
            articleService.findFirstOrLastByFirstIdAndParams(
                firstId,
                BlogArticle().apply {
                    createBy = userId
                },
                false
            ) ?: throw BlogArticleNotValidException()
        }
        val blogArticleContext = BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)

        val draftArticle = if (article == null) {
            blogArticleContext.saveDraft(request)
        } else {
            blogArticleContext.saveDraft(article, request)
        }

        val dto = dtoService.transfer(draftArticle, BlogArticleDTO::class.java)

        return Response.ok(dto).build()
    }

    /**
     * 用户发布文章
     */
    @SaCheckLogin
    @POST
    @Path("publish")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun publish(@Valid request: BlogArticlePublishRequest): Response {
        val userId = StpUtil.getLoginIdAsString()
        val firstId = request.firstId

        if (request.content.trim() == "\n") {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        val typeId = typeService.getOwnIdByNameAndCreateBy(request.typeName, userId)
        val tagIds = tagService.getIdListByNamesAndCreateBy(request.tagNames, userId)

        // 代表没有草稿直接发布
        if (firstId.isNullOrBlank()) {
            /* 直接发布 */
            val publish = articleService.create(
                BlogArticle(
                    request.title,
                    request.coverImage,
                    request.summary,
                    request.content,
                    BlogArticleStatus.PUBLISH,
                    request.canComment,
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
                        BlogArticleStatus.INHERIT_PUBLISH
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
                        request.canComment,
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
     * 用户发布文章v2
     */
    @SaCheckLogin
    @POST
    @Path("publish_v2")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun publishV2(@Valid request: BlogArticlePublishRequest): Response {
        val userId = StpUtil.getLoginIdAsString()

        if (request.content.trim() == "\n") {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        val firstId = request.firstId
        val article = if (firstId.isNullOrBlank()) {
            null
        } else {
            articleService.findFirstOrLastByFirstIdAndParams(
                firstId,
                BlogArticle().apply {
                    createBy = userId
                },
                false
            ) ?: throw BlogArticleNotValidException()
        }
        val blogArticleContext = BlogArticleContext.generateBlogArticleContext(article, articleService.articleStates)

        val publishedArticle = if (article == null) {
            blogArticleContext.publishArticle(request)
        } else {
            blogArticleContext.publishArticle(article, request)
        }

        val dto = dtoService.transfer(publishedArticle, BlogArticleDTO::class.java)

        return Response.ok(dto).build()
    }

    /**
     * 用户分页查询自己数据权限内能看到的所有文章
     */
    @SaCheckLogin
    @GET
    @Path("page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun page(@Valid @BeanParam request: BlogArticleQueryRequest): Page<BlogArticleDTO> {
        val wrapper = ChainWrappers.queryChain(articleService.dao)
            .like(!request.title.isNullOrBlank(), "title", "%" + request.title + "%")
            .like(!request.summary.isNullOrBlank(), "summary", "%" + request.summary + "%")
            .like(!request.content.isNullOrBlank(), "content", "%" + request.content + "%")
            .eq(request.canComment != null, "can_comment", request.canComment)
            .eq(request.canReward != null, "can_reward", request.canReward)
            .eq(request.original != null, "original", request.original)
            .`in`(!request.status.isNullOrEmpty(), "status", request.status)
            .eq(!request.firstId.isNullOrBlank(), "first_id", request.firstId)

        // 数据权限处理
        val statusList = request.status
        val needAddDataScopeStatusSet = setOf(
            BlogArticleStatus.PENDING.name,
            BlogArticleStatus.UNDER_REVIEW.name,
            BlogArticleStatus.ON_APPEAL.name,
            BlogArticleStatus.PUBLISH.name
        )
        if (request.own == true
            || statusList.intersect(needAddDataScopeStatusSet).isEmpty()
            || !StpUtil.hasRole(SaTokenConstants.ROLE_AUDITOR)
        ) {
            wrapper.eq("create_by", StpUtil.getLoginIdAsString())
        }

        val page = articleService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogArticleDTO::class.java)
        }

        return dtoPage
    }

    /**
     * 用户分页查询自己已点赞的文章
     */
    @SaCheckLogin
    @GET
    @Path("page/own/liked/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageOwnLiked(@Valid @BeanParam request: BlogArticleQueryRequest): PageDTO<BlogArticlePreviewUserLikedVO> {
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
    @SaCheckLogin
    @GET
    @Path("page/own/browsed/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageOwnBrowsed(@Valid @BeanParam request: BlogArticleQueryRequest): PageDTO<BlogArticlePreviewUserBrowsedVO> {
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
    @SaCheckLogin
    @GET
    @Path("page/firstId/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageByFirstId(@Valid @BeanParam request: BlogArticleQueryRequest): Response {
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
    fun pagePublish(@Valid @BeanParam request: BlogArticleQueryRequest): Response {
        val status = BlogArticleStatus.PUBLISH

        val tagName = request.tagName
        var tagId: String? = null
        var tagIds = emptyList<String>()
        if (!tagName.isNullOrEmpty()) {
            tagIds = tagService.dao.selectIdByNameIn(listOf(tagName))
            tagId = tagIds.firstOrNull()
        }

        val wrapper = ChainWrappers.queryChain(articleService.dao)
            .like(!request.title.isNullOrBlank(), "title", "%" + request.title + "%")
            .like(!request.summary.isNullOrBlank(), "summary", "%" + request.summary + "%")
            .like(!request.content.isNullOrBlank(), "content", "%" + request.content + "%")
            .eq("status", status)
            .eq(request.createBy != null, "create_by", request.createBy)
            .like(tagIds.isNotEmpty(), "tag_ids", "%\"${tagId}\"%")
            .eq(!request.typeId.isNullOrBlank(), "type_id", request.typeId)

        val page = articleService.page(request, wrapper)

        val voPage = PageUtils.copyPage<BlogArticle, BlogArticlePreviewVO>(page).apply {
            records = page.records.stream().map(articleMapStruct::transferPreview)
                .collect(Collectors.toList())
        }
        return Response.ok().entity(voPage).build()
    }

    /**
     * 分页搜索用户已发布的文章
     */
    @GET
    @Path("search/publish/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun searchPublish(@Valid @BeanParam request: BlogArticleQueryRequest): PageDTO<BlogArticlePreviewVO> {
        val tagName = request.tagName
        var tagId: String? = null
        var tagIds = emptyList<String>()
        if (!tagName.isNullOrEmpty()) {
            tagIds = tagService.dao.selectIdByNameIn(listOf(tagName))
            tagId = tagIds.firstOrNull()
        }

        var orderBySql = ""
        val sortBy = request.sortBy
        if (!sortBy.isNullOrEmpty()) {
            sortBy.forEachIndexed { index, item ->
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

        val page = articleService.dao.searchPublish(
            PageDTO.of(request.page, request.size),
            if (request.title.isNullOrBlank()) null else "%${request.title}%",
            if (tagIds.isEmpty()) null else "%\"${tagId}\"%",
            orderBySql
        ) as PageDTO<BlogArticle>

        val voPage = PageUtils.copyPage<BlogArticle, BlogArticlePreviewVO>(page).apply {
            records = page.records.stream().map(articleMapStruct::transferPreview)
                .collect(Collectors.toList())
        } as PageDTO<BlogArticlePreviewVO>
        return voPage
    }

    /**
     * 查询用户自己创建的文章
     */
    @SaCheckLogin
    @GET
    @Path("own/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findOwnById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): BlogArticleDTO {
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
     * 根据文章id查询用户自己能够编辑的文章
     * id为空时获取第一个draft
     * 否则，根据id查询为draft或者publish的
     */
    @SaCheckLogin
    @GET
    @Path("own/writing")
    @Produces(MediaType.APPLICATION_JSON)
    fun findOwnWritingById(
        @Valid @Size(max = 32, message = "id长度不能大于32")
        @DefaultValue(StrUtil.EMPTY) @RestQuery id: String
    ): BlogArticleDTO? {
        val userId = StpUtil.getLoginIdAsString()

        if (StrUtil.isBlank(id)) {
            val firstDraft = articleService.findFirstOrLastByStatusAndCreatedBy(BlogArticleStatus.DRAFT, userId)
            if (firstDraft == null) {
                return null
            } else {
                return dtoService.transfer(firstDraft, BlogArticleDTO::class.java)
            }
        } else {
            val article = validateService.validateOwnArticle(id)
            val articleDraft =
                articleService.findByFirstIdAndStatusAndCreatedBy(article.firstId, BlogArticleStatus.DRAFT, userId)
            if (articleDraft != null) {
                return dtoService.transfer(articleDraft, BlogArticleDTO::class.java)
            } else {
                val articlePublish = articleService.findByFirstIdAndStatusAndCreatedBy(
                    article.firstId,
                    BlogArticleStatus.PUBLISH,
                    userId
                )
                if (articlePublish != null) {
                    return dtoService.transfer(articlePublish, BlogArticleDTO::class.java)
                }
            }
        }
        return null
    }

    /**
     * 根据Id查询违规的文章
     *
     * TODO 自己的 / 审核员
     */
    @GET
    @Path("offend/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findOffendById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): BlogArticleDetailVO {
        val blogArticle = articleService.findByIdAndStatus(id, BlogArticleStatus.OFFEND)
            ?: throw BlogArticleNotFoundException()

        return articleMapStruct.transferDetail(blogArticle)
    }

    /**
     * 根据Id查询审核中的文章
     *
     * TODO 审核员
     */
    @GET
    @Path("under_review/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findUnderReviewById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): BlogArticleDetailVO {
        val blogArticle = articleService.findByIdAndStatus(id, BlogArticleStatus.UNDER_REVIEW)
            ?: throw BlogArticleNotFoundException()

        return articleMapStruct.transferDetail(blogArticle)
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
    fun findPublishById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): BlogArticleDetailVO {
        var blogArticle: BlogArticle? = articleService.findById(id) ?: throw BlogArticleNotFoundException()

        if (blogArticle!!.status != BlogArticleStatus.PUBLISH) {
            blogArticle =
                articleService.findFirstOrLastByFirstIdAndStatus(blogArticle.firstId, BlogArticleStatus.PUBLISH)
                    ?: throw BlogArticleNotFoundException()
        }

        userBrowsedArticleService.browseArticle(
            blogArticle.firstId,
            BlogUtils.getClientIp(),
            StpUtil.getLoginIdDefaultNull() as String?
        )
        return articleMapStruct.transferDetail(blogArticle)
    }

    /**
     * 获取已发表文章的创建时间和最后更新时间
     */
    @GET
    @Path("publish/{id}/created_update_Time")
    @Produces(MediaType.APPLICATION_JSON)
    fun getPublishCreatedTimeAndUpdateTimeById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): JsonObject {
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
        if (userId.isBlank()) {
            return 0
        }
        if (userService.findById(userId) == null) {
            BlogUserNotFoundException().throws()
        }

        val wrapper = Wrappers.query<BlogArticle>()
            .eq("status", BlogArticleStatus.PUBLISH)
            .eq("create_by", userId)
        return articleService.dao.selectCount(wrapper)
    }

    /**
     * 获取已浏览文章的个数
     */
    @GET
    @Path("count/browsed")
    fun countBrowsed(
        @Valid @Size(
            max = 32,
            message = "userId长度不能大于32"
        ) @DefaultValue("") @RestQuery userId: String
    ): Long {
        if (userId.isBlank()) {
            return 0
        }
        if (userService.findById(userId) == null) {
            BlogUserNotFoundException().throws()
        }

        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("deleted", false)
            .eq("create_by", userId)
        return userBrowsedArticleService.dao.selectCount(wrapper)
    }

    /**
     * 获取已点赞文章的个数
     */
    @GET
    @Path("count/liked")
    fun countLiked(
        @Valid @Size(
            max = 32,
            message = "userId长度不能大于32"
        ) @DefaultValue("") @RestQuery userId: String
    ): Long {
        if (userId.isBlank()) {
            return 0L
        }
        if (userService.findById(userId) == null) {
            BlogUserNotFoundException().throws()
        }

        val wrapper = Wrappers.query<BlogUserLikedArticle>()
            .eq("create_by", userId)
        return userLikedArticleService.dao.selectCount(wrapper)
    }

    /**
     * 获取评论数前N的文章
     */
    @GET
    @Path("top/comment")
    fun getTopNComments(@Valid @BeanParam request: BlogArticleTopNRequest): List<Map<String, String>> {
        if (request.userId.isNotBlank()) {
            if (userService.findById(request.userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        return articleService.dao.getTopNCommentsByUserId(request.n, request.userId)
    }

    /**
     * 获取点赞数前N的文章
     */
    @GET
    @Path("top/liked")
    fun getLikedTopN(@Valid @BeanParam request: BlogArticleTopNRequest): List<Map<String, String>> {
        if (request.userId.isNotBlank()) {
            if (userService.findById(request.userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        return articleService.dao.getTopNLikedByUserId(request.n, request.userId)
    }

    /**
     * 获取浏览数前N的文章
     */
    @GET
    @Path("top/browsed")
    fun getBrowsedTopN(@Valid @BeanParam request: BlogArticleTopNRequest): List<Map<String, String>> {
        if (request.userId.isNotBlank()) {
            if (userService.findById(request.userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        return articleService.dao.getTopNBrowsedByUserId(request.n, request.userId)
    }

    /**
     * 获取有文章发表的日期数组
     */
    @GET
    @Path("days/published")
    fun getArticlePublishedDays(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
    ): List<String> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }

        return articleService.dao.getArticlePublishedDays(userId)
    }

    /**
     * 获取有文章发表的月份数组
     */
    @GET
    @Path("months/published")
    fun getArticlePublishedMonths(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
    ): List<String> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }

        return articleService.dao.getArticlePublishedMonths(userId)
    }

    /**
     * 获取有文章发表的年份数组
     */
    @GET
    @Path("years/published")
    fun getArticlePublishedYears(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
    ): List<String> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }

        return articleService.dao.getArticlePublishedYears(userId)
    }

    /**
     * 获取每日文章发表数
     */
    @GET
    @Path("count/daily/posts")
    fun countDailyPosts(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
        @RestQuery startDateTime: LocalDateTime?,
        @RestQuery endDateTime: LocalDateTime?,
    ): List<Map<String, String>> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }

        val (startDateTimeString, endDateTimeString) = BlogUtils.parseDateRange(startDateTime, endDateTime)
        return articleService.dao.countDailyPosts(startDateTimeString, endDateTimeString, userId)
    }

    /**
     * 获取每月文章发表数
     */
    @GET
    @Path("count/monthly/posts")
    fun countMonthlyPosts(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
        @RestQuery startDateTime: LocalDateTime?,
        @RestQuery endDateTime: LocalDateTime?,
    ): List<Map<String, String>> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }

        val (startDateTimeString, endDateTimeString) = BlogUtils.parseDateRange(startDateTime, endDateTime)
        return articleService.dao.countMonthlyPosts(startDateTimeString, endDateTimeString, userId)
    }

    /**
     * 获取每年文章发表数
     */
    @GET
    @Path("count/yearly/posts")
    fun countYearlyPosts(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
        @RestQuery startDateTime: LocalDateTime?,
        @RestQuery endDateTime: LocalDateTime?,
    ): List<Map<String, String>> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }

        val (startDateTimeString, endDateTimeString) = BlogUtils.parseDateRange(startDateTime, endDateTime)
        return articleService.dao.countYearlyPosts(startDateTimeString, endDateTimeString, userId)
    }

    /**
     * 用户点赞文章
     */
    @SaCheckLogin
    @POST
    @Transactional
    @Path("likes/{id}")
    fun likesArticle(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        articleService.likesArticle(id)
    }

    /**
     * 用户取消点赞文章
     */
    @SaCheckLogin
    @POST
    @Transactional
    @Path("unlikes/{id}")
    fun unlikesArticle(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        articleService.unlikesArticle(id)
    }

    /**
     * 用户删除文章浏览记录
     */
    @SaCheckLogin
    @POST
    @Transactional
    @Path("un_browses/{id}")
    fun unBrowsesArticle(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        articleService.unBrowsesArticle(id)
    }

    /**
     * 获取用户是否点赞
     */
    @SaCheckLogin
    @GET
    @Transactional
    @Path("liked/{id}")
    fun getLiked(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): Boolean {
        return articleService.getLiked(id)
    }

    //endregion

    //region TODO 开发中

    /**
     * 获取固定在个人首页的文章
     */
    @GET
    @Path("pinned")
    fun getPinnedArticles(
        @Valid @NotBlank(message = "userId不能为空")
        @Size(max = 32, message = "userId长度不能大于32") @RestQuery userId: String
    ): List<BlogArticlePreviewVO> {
        val list = ArrayList<BlogArticlePreviewVO>()
        val browsedTopN = this.getBrowsedTopN(BlogArticleTopNRequest().apply {
            this.userId = userId
            this.n = 4
        })
        for (map in browsedTopN) {
            val articleId = map["uuid"]
            val findById = articleService.findById(articleId)
            if (findById != null) {
                list.add(articleMapStruct.transferPreview(findById))
            }
        }
        return list
    }

    /**
     * 查询上一篇/下一篇文章
     */
    @GET
    @Path("pre_and_next/{id}")
    fun getPreAndNextArticle(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): JsonObject {
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
    @DELETE
    @Path("/delete/{id}")
    fun deleteById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): Response {
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
    fun findById(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): Response {
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
