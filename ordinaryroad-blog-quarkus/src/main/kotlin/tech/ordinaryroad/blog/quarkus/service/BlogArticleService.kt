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

package tech.ordinaryroad.blog.quarkus.service

import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import io.vertx.core.json.JsonObject
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogArticleDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotFoundException
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticlePreviewVO
import tech.ordinaryroad.blog.quarkus.state.article.base.ArticleState
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.inject.Instance
import javax.inject.Inject

@ApplicationScoped
class BlogArticleService : BaseService<BlogArticleDAO, BlogArticle>() {

    @Inject
    lateinit var articleStates: Instance<ArticleState>

    override fun getEntityClass(): Class<BlogArticle> {
        return BlogArticle::class.java
    }

    @Inject
    protected lateinit var userLikedArticleService: BlogUserLikedArticleService

    @Inject
    protected lateinit var userBrowsedArticleService: BlogUserBrowsedArticleService

    //region 业务相关
    /**
     * 根据Id获取已发布文章的创建时间和更新时间
     *
     * @return {"createdTime":"xxx","updateTime":"xxx"}
     */
    fun getPublishCreatedTimeAndUpdateTimeById(id: String): JsonObject {
        val blogArticle = findById(id) ?: throw BlogArticleNotFoundException()

        var createdTime: LocalDateTime = blogArticle.createdTime
        var updateTime: LocalDateTime? = null

        // 只处理已发布的文章
        if (blogArticle.status == BlogArticleStatus.PUBLISH) {
            if (blogArticle.firstId != blogArticle.uuid) {
                // 查询第一个 发布的历史版本 版本
                val byFirstIdAndStatus = findFirstOrLastByFirstIdAndStatus(
                    blogArticle.firstId,
                    BlogArticleStatus.INHERIT_PUBLISH
                )
                if (byFirstIdAndStatus != null) {
                    createdTime = byFirstIdAndStatus.createdTime
                    updateTime = blogArticle.createdTime
                }
            }
        }
        val jsonObject = JsonObject()
        jsonObject.put("createdTime", createdTime)
        jsonObject.put("updateTime", updateTime)
        return jsonObject
    }

    /**
     * 用户点赞文章
     */
    fun likesArticle(id: String) {
        val userId = StpUtil.getLoginIdAsString()
        if (userLikedArticleService.getLiked(userId, id)) {
            BaseBlogException("已经赞过了...").throws()
        } else {
            userLikedArticleService.likesArticle(id)
        }
    }

    /**
     * 用户取消点赞文章
     */
    fun unlikesArticle(id: String) {
        val userId = StpUtil.getLoginIdAsString()
        if (userLikedArticleService.getLiked(userId, id)) {
            userLikedArticleService.unlikesArticle(userId, id)
        } else {
            BaseBlogException("还未点赞...").throws()
        }
    }

    /**
     * 获取用户是否点赞
     */
    fun getLiked(id: String): Boolean {
        val userId = StpUtil.getLoginIdAsString()
        return userLikedArticleService.getLiked(userId, id)
    }

    /**
     * 用户删除文章浏览记录
     */
    fun unBrowsesArticle(id: String) {
        val userId = StpUtil.getLoginIdAsString()
        if (userBrowsedArticleService.getBrowsed(userId, id)) {
            userBrowsedArticleService.unBrowseArticle(userId, id)
        } else {
            BaseBlogException("还未浏览...").throws()
        }
    }

    /**
     * 获取用户是否浏览
     */
    fun getBrowsed(id: String): Boolean {
        val userId = StpUtil.getLoginIdAsString()
        return userBrowsedArticleService.getBrowsed(userId, id)
    }

    /**
     * 根据文章ID获取上一篇 下一篇文章
     */
    fun getPreAndNextArticle(id: String, transfer: (BlogArticle) -> (BlogArticlePreviewVO)): JsonObject {
        val preAndNextArticle = super.dao.getPreAndNextArticle(id).map(transfer)
        val result = JsonObject()
        if (preAndNextArticle.size == 1) {
            val blogArticle = preAndNextArticle[0]
            result.put(if (blogArticle.uuid > id) "next" else "pre", blogArticle)
        }
        if (preAndNextArticle.size == 2) {
            result.put("pre", preAndNextArticle[0])
            result.put("next", preAndNextArticle[1])
        }
        return result
    }

    /**
     * 根据Id获取最初版本的文章
     */
    fun getFirstById(id: String): BlogArticle? {
        if (id.isBlank()) {
            return null
        }
        val findById = super.findById(id) ?: return null
        return if (findById.firstId == findById.uuid) {
            findById
        } else {
            super.findById(findById.firstId)
        }
    }
    //endregion

    //region SQL相关
    /**
     * 根据状态查询所有最初版本的文章
     */
    fun findAllFirstArticleByStatusAndCreateBy(state: BlogArticleStatus, createBy: String): List<BlogArticle> {
        return super.dao.selectAllFirstArticleByStateAndCreateBy(state, createBy)
    }

    /**
     * 根据状态查询文章
     */
    fun findAllByStatus(status: BlogArticleStatus): List<BlogArticle> {
        val wrapper = Wrappers.query<BlogArticle>()
        wrapper.eq("status", status)
        return super.dao.selectList(wrapper)
    }

    /**
     * 根据Id和状态查询文章
     */
    fun findByIdAndStatus(id: String, status: BlogArticleStatus): BlogArticle? {
        val wrapper = Wrappers.query<BlogArticle>()
        wrapper.eq("uuid", id)
        wrapper.eq("status", status)
        return super.dao.selectOne(wrapper)
    }

    /**
     * 根据Id查询用户创建的文章
     */
    fun findByIdAndCreatedBy(id: String, createBy: String): BlogArticle? {
        val wrapper = Wrappers.query<BlogArticle>()
        wrapper.eq("uuid", id)
        wrapper.eq("create_by", createBy)
        return super.dao.selectOne(wrapper)
    }

    /**
     * 根据FirstId和状态查询最新的或最旧的文章
     */
    fun findFirstOrLastByFirstIdAndStatus(
        firstId: String,
        status: BlogArticleStatus? = null,
        first: Boolean = true
    ): BlogArticle? {
        val wrapper = ChainWrappers.queryChain(super.dao)
        wrapper.eq("first_id", firstId)
        wrapper.eq(status != null, "status", status)
        wrapper.orderBy(true, first, "created_time")
        val page = wrapper.page(Page(1, 1, 1))
        return page.records.firstOrNull()
    }

    /**
     * 根据firstId和其他参数（status、create_by）查询最新的或最旧的文章
     */
    fun findFirstOrLastByFirstIdAndParams(firstId: String, article: BlogArticle, first: Boolean = true): BlogArticle? {
        val wrapper = ChainWrappers.queryChain(super.dao)
        wrapper.eq("first_id", firstId)
        wrapper.eq(article.status != null, "status", article.status)
        wrapper.eq(!article.createBy.isNullOrBlank(), "create_by", article.createBy)
        wrapper.orderBy(true, first, "created_time")
        val page = wrapper.page(Page(1, 1, 1))
        return page.records.firstOrNull()
    }

    /**
     * 根据FirstId和状态查询用户创建的文章
     */
    fun findByFirstIdAndStatusAndCreatedBy(firstId: String, status: BlogArticleStatus, createBy: String): BlogArticle? {
        val wrapper = Wrappers.query<BlogArticle>()
        wrapper.eq("first_id", firstId)
        wrapper.eq("status", status)
        wrapper.eq("create_by", createBy)
        return super.dao.selectOne(wrapper)
    }

    /**
     * 根据状态查询所有用户创建的文章
     */
    fun findAllByStatusAndCreatedBy(status: BlogArticleStatus, createBy: String): List<BlogArticle> {
        val wrapper = Wrappers.query<BlogArticle>()
        wrapper.eq("status", status)
        wrapper.eq("create_by", createBy)
        return super.dao.selectList(wrapper)
    }

    /**
     * 根据状态和创建者查询最新的或最旧的文章
     */
    fun findFirstOrLastByStatusAndCreatedBy(
        status: BlogArticleStatus,
        createBy: String,
        first: Boolean = true
    ): BlogArticle? {
        val wrapper = ChainWrappers.queryChain(super.dao)
        wrapper.eq("status", status)
        wrapper.eq("create_by", createBy)
        wrapper.orderBy(true, first, "created_time")
        val page = wrapper.page(Page(1, 1, 1))
        return page.records.firstOrNull()
    }

    /**
     * 根据状态查询所有用户创建的文章个数
     */
    fun countByStatusAndCreatedBy(status: BlogArticleStatus, createBy: String): Long {
        val wrapper = Wrappers.query<BlogArticle>()
        wrapper.eq("status", status)
        wrapper.eq("create_by", createBy)
        return super.dao.selectCount(wrapper)
    }

    /**
     * 根据Id更新状态
     */
    fun updateStatusById(id: String, status: BlogArticleStatus) {
        super.update(BlogArticle().apply {
            this.uuid = id
            this.status = status
        })
    }

    /**
     * 根据FirstId和CreatedBy和状态更新状态
     */
    fun updateStatusByFirstIdAndCreatedBy(
        firstId: String,
        createBy: String,
        oldStatus: BlogArticleStatus,
        newStatus: BlogArticleStatus
    ) {
        val wrapper = Wrappers.update<BlogArticle>()
            .eq("first_id", firstId)
            .eq("create_by", createBy)
            .eq("status", oldStatus)
        super.update(BlogArticle().apply {
            this.status = newStatus
        }, wrapper)
    }

    /**
     * 查询某个分类下的已发布文章的个数
     */
    fun countPublishByTypeId(typeId: String): Long {
        val wrapper = Wrappers.query<BlogArticle>()
            .eq("type_id", typeId)
            .eq("status", BlogArticleStatus.PUBLISH)

        return super.dao.selectCount(wrapper)
    }
    //endregion
}
