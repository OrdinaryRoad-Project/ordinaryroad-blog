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
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped

/**
 * Service-BlogUserBrowsedArticle
 *
 * @author mjz
 * @date 2022/9/19
 */
@Unremovable
@ApplicationScoped
class BlogUserBrowsedArticleService : BaseService<BlogUserBrowsedArticleDAO, BlogUserBrowsedArticle>() {

    /**
     * 获取文章浏览量
     */
    fun getBrowsedCount(articleId: String): Long {
        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("article_id", articleId)

        return super.dao.selectCount(wrapper)
    }

    /**
     * 增加浏览记录
     */
    fun browseArticle(articleId: String, ip: String, userId: String? = null) {
        if (userId == null) {
            // 未登录
            val byIpAndArticleId = this.findByIpAndArticleId(ip, articleId)
            if (byIpAndArticleId == null) {
                // 创建
                super.create(BlogUserBrowsedArticle().apply {
                    setIp(ip)
                    setArticleId(articleId)
                    lastBrowsedTime = LocalDateTime.now()
                })
            } else {
                // 更新上次浏览时间，不支持删除，所以不用更新deleted字段
                super.update(BlogUserBrowsedArticle().apply {
                    uuid = byIpAndArticleId.uuid
                    lastBrowsedTime = LocalDateTime.now()
                })
            }
        } else {
            val byUserIdAndIpAndArticleId = this.findByUserIdAndIpAndArticleId(userId, ip, articleId)
            if (byUserIdAndIpAndArticleId == null) {
                // 创建
                super.create(BlogUserBrowsedArticle().apply {
                    setIp(ip)
                    setArticleId(articleId)
                    lastBrowsedTime = LocalDateTime.now()
                })
            } else {
                // 更新
                if (byUserIdAndIpAndArticleId.deleted) {
                    super.update(BlogUserBrowsedArticle().apply {
                        uuid = byUserIdAndIpAndArticleId.uuid
                        deleted = false
                        lastBrowsedTime = LocalDateTime.now()
                    })
                } else {
                    // 更新上次浏览时间
                    super.update(BlogUserBrowsedArticle().apply {
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
    fun unBrowseArticle(articleId: String, userId: String) {
        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("article_id", articleId)
            .eq("create_by", userId)

        super.dao.update(BlogUserBrowsedArticle().apply {
            deleted = true
        }, wrapper)
    }

    /**
     * 获取是否已浏览
     */
    fun getBrowsed(userId: String, articleId: String): Boolean {
        val wrapper = Wrappers.query<BlogUserBrowsedArticle>()
            .eq("create_by", userId)
            .eq("article_id", articleId)
            .eq("deleted", false)

        return super.dao.exists(wrapper)
    }

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

}