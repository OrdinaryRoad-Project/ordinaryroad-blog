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

import com.baomidou.mybatisplus.core.toolkit.Wrappers
import io.quarkus.arc.Unremovable
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogUserBrowsedArticleDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUserBrowsedArticle
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotFoundException
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Service-BlogUserBrowsedArticle
 *
 * @author mjz
 * @date 2022/9/19
 */
@Unremovable
@ApplicationScoped
class BlogUserBrowsedArticleService : BaseService<BlogUserBrowsedArticleDAO, BlogUserBrowsedArticle>() {

    @Inject
    protected lateinit var articleService: BlogArticleService

    override fun getEntityClass(): Class<BlogUserBrowsedArticle> {
        return BlogUserBrowsedArticle::class.java
    }

    //region 业务相关
    /**
     * 获取文章浏览量
     */
    @Deprecated(
        message = "由于增加了pv区分uv和pv，因此该方法启用",
        replaceWith = ReplaceWith("getArticleUvAndPv(articleId)")
    )
    fun getBrowsedCount(articleId: String): Long {
        val firstArticleById = articleService.getFirstById(articleId)
        if (firstArticleById == null) {
            BlogArticleNotFoundException().throws()
        }

        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("article_id", firstArticleById!!.uuid)

        return super.dao.selectCount(wrapper)
    }

    /**
     * 获取文章uv和pv
     */
    fun getArticleUvAndPv(articleId: String): Pair<Long, BigDecimal> {
        val firstArticleById = articleService.getFirstById(articleId)
        if (firstArticleById == null) {
            BlogArticleNotFoundException().throws()
        }

        val articleUvAndPv = super.dao.getArticleUvAndPv(firstArticleById!!.uuid)
        return if (articleUvAndPv == null) {
            Pair(0L, BigDecimal.ZERO)
        } else {
            Pair(articleUvAndPv["uv"] as Long, articleUvAndPv["pv"] as BigDecimal)
        }
    }

    /**
     * 增加浏览记录
     */
    fun browseArticle(articleId: String, ip: String, userId: String? = null) {
        val firstArticleById = articleService.getFirstById(articleId)
        if (firstArticleById == null) {
            BlogArticleNotFoundException().throws()
        }

        if (userId == null) {
            // 未登录
            val byIpAndArticleId = this.findByIpAndArticleId(ip, firstArticleById!!.uuid)
            if (byIpAndArticleId == null) {
                // 创建
                super.create(BlogUserBrowsedArticle().apply {
                    count = 1
                    setIp(ip)
                    setArticleId(firstArticleById.uuid)
                    lastBrowsedTime = LocalDateTime.now()
                })
            } else {
                // 更新上次浏览时间，不支持删除，所以不用更新deleted字段
                super.update(BlogUserBrowsedArticle().apply {
                    count = byIpAndArticleId.count + 1
                    uuid = byIpAndArticleId.uuid
                    lastBrowsedTime = LocalDateTime.now()
                })
            }
        } else {
            val byUserIdAndIpAndArticleId = this.findByUserIdAndIpAndArticleId(userId, ip, firstArticleById!!.uuid)
            if (byUserIdAndIpAndArticleId == null) {
                // 创建
                super.create(BlogUserBrowsedArticle().apply {
                    count = 1
                    setIp(ip)
                    setArticleId(firstArticleById.uuid)
                    lastBrowsedTime = LocalDateTime.now()
                })
            } else {
                // 更新
                if (byUserIdAndIpAndArticleId.deleted) {
                    super.update(BlogUserBrowsedArticle().apply {
                        count = byUserIdAndIpAndArticleId.count + 1
                        uuid = byUserIdAndIpAndArticleId.uuid
                        deleted = false
                        lastBrowsedTime = LocalDateTime.now()
                    })
                } else {
                    // 更新上次浏览时间 增加浏览次数
                    super.update(BlogUserBrowsedArticle().apply {
                        count = byUserIdAndIpAndArticleId.count + 1
                        uuid = byUserIdAndIpAndArticleId.uuid
                        lastBrowsedTime = LocalDateTime.now()
                    })
                }
            }
        }
    }

    /**
     * 删除浏览记录
     */
    fun unBrowseArticle(userId: String, articleId: String) {
        val firstArticleById = articleService.getFirstById(articleId)
        if (firstArticleById == null) {
            BlogArticleNotFoundException().throws()
        }

        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("article_id", firstArticleById!!.uuid)
            .eq("create_by", userId)

        super.dao.update(BlogUserBrowsedArticle().apply {
            deleted = true
        }, wrapper)
    }

    /**
     * 获取是否已浏览
     */
    fun getBrowsed(userId: String, articleId: String): Boolean {
        val firstArticleById = articleService.getFirstById(articleId)
        if (firstArticleById == null) {
            BlogArticleNotFoundException().throws()
        }

        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("create_by", userId)
            .eq("article_id", firstArticleById!!.uuid)
            .eq("deleted", false)

        return super.dao.exists(wrapper)
    }
    //endregion

    //region SQL相关
    fun findByUserIdAndIpAndArticleId(userId: String, ip: String, articleId: String): BlogUserBrowsedArticle? {
        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("create_by", userId)
            .eq("ip", ip)
            .eq("article_id", articleId)

        return super.dao.selectOne(wrapper)
    }

    fun findByIpAndArticleId(ip: String, articleId: String): BlogUserBrowsedArticle? {
        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .isNull("create_by")
            .eq("ip", ip)
            .eq("article_id", articleId)

        return super.dao.selectOne(wrapper)
    }
    //endregion

}