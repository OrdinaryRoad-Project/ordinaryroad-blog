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
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus.Companion.canMoveToTrash
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus.Companion.canRecoverFromTrash
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotValidException
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class BlogArticleService : BaseService<BlogArticleDAO, BlogArticle>() {

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
     * @return
     * (first: createdTime, second: updateTime)
     * }
     */
    fun getPublishCreatedTimeAndUpdateTimeById(id: String): JsonObject {
        val blogArticle = findByIdAndStatus(id, BlogArticleStatus.PUBLISH)

        if (blogArticle == null) {
            BlogArticleNotFoundException().throws()
        } else {
            if (blogArticle.status != BlogArticleStatus.PUBLISH) {
                // 必须是已发布的文章
                BlogArticleNotValidException().throws()
            }
        }

        var createdTime: LocalDateTime = blogArticle!!.createdTime
        var updateTime: LocalDateTime? = null

        if (blogArticle.firstId != blogArticle.uuid) {
            // 查询第一个 PUBLISH_INHERIT 版本
            val byPreIdAndStatus = findFirstOrLastByFirstIdAndStatus(
                blogArticle.firstId,
                BlogArticleStatus.PUBLISH_INHERIT
            )
            if (byPreIdAndStatus != null) {
                createdTime = byPreIdAndStatus.createdTime
                updateTime = blogArticle.createdTime
            }
        }
        val jsonObject = JsonObject()
        jsonObject.put("createdTime", createdTime)
        jsonObject.put("updateTime", updateTime)
        return jsonObject
    }

    /**
     * 移动至废纸篓
     */
    fun moveToTrash(id: String) {
        val blogArticle = validateOwn(id)

        // 只能删除草稿和已发布文章
        if (blogArticle.status.canMoveToTrash()) {
            updateStatusById(id, BlogArticleStatus.TRASH)
        } else {
            BlogArticleNotValidException().throws()
        }
    }

    /**
     * 从废纸篓恢复
     */
    fun recoverFromTrash(id: String) {
        val blogArticle = validateOwn(id)

        // 只能从垃圾箱中恢复
        if (blogArticle.status.canRecoverFromTrash()) {
            updateStatusById(id, BlogArticleStatus.DRAFT)
        } else {
            BlogArticleNotValidException().throws()
        }
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
     *
     */
    fun getPreAndNextArticle(id: String): JsonObject {
        // TODO("Not yet implemented")
        return JsonObject()
    }

    /**
     * 根据Id获取最初版本的文章
     */
    fun getFirstArticleById(id: String): BlogArticle? {
        if (id.isBlank()) {
            return null
        }
        val findById = super.findById(id) ?: return null
        return if (findById.firstId == findById.uuid) {
            super.findById(findById.firstId)
        } else {
            super.findById(findById.firstId)
        }
    }

    /**
     * 校验文章是否属于自己
     *
     * @param id 文章Id
     * @return BlogArticle 文章
     */
    private fun validateOwn(id: String): BlogArticle {
        val userId = StpUtil.getLoginIdAsString()
        if (id.isBlank()) {
            BlogArticleNotFoundException().throws()
        }
        val blogArticle = findById(id) ?: throw BlogArticleNotFoundException()
        if (blogArticle.createBy != userId) {
            BlogArticleNotValidException().throws()
        }
        return blogArticle
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
     * 根据PreId和状态查询最新的或最旧的文章
     */
    fun findFirstOrLastByFirstIdAndStatus(
        firstId: String,
        status: BlogArticleStatus,
        first: Boolean = true
    ): BlogArticle? {
        val wrapper = ChainWrappers.queryChain(super.dao)
        wrapper.eq("first_id", firstId)
        wrapper.eq("status", status)
        wrapper.orderBy(true, first, "created_time")
        val page = wrapper.page(Page(1, 1, 1))
        return page.records.firstOrNull()
    }

    /**
     * 根据PreId和状态查询用户创建的文章
     */
    fun findByPreIdAndStatusAndCreatedBy(firstId: String, status: BlogArticleStatus, createBy: String): BlogArticle? {
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
    //endregion
}
