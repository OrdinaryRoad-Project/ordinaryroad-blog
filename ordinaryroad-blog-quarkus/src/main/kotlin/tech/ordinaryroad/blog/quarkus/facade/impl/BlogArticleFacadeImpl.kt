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

package tech.ordinaryroad.blog.quarkus.facade.impl

import cn.dev33.satoken.stp.StpUtil
import io.vertx.core.json.JsonObject
import tech.ordinaryroad.blog.quarkus.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus.Companion.canMoveToTrash
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus.Companion.canRecoverFromTrash
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotValidException
import tech.ordinaryroad.blog.quarkus.facade.BlogArticleFacade
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService
import tech.ordinaryroad.commons.base.cons.StatusCode
import tech.ordinaryroad.commons.base.exception.BaseException
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class BlogArticleFacadeImpl : BlogArticleFacade {

    @Inject
    protected lateinit var articleService: BlogArticleService

    override fun getPublishCreatedTimeAndUpdateTimeById(id: String): JsonObject {
        val blogArticle = articleService.findByIdAndStatus(id, BlogArticleStatus.PUBLISH)

        if (blogArticle == null) {
            throw BaseException(StatusCode.DATA_NOT_EXIST)
        } else {
            if (blogArticle.status != BlogArticleStatus.PUBLISH) {
                // 必须是已发布的文章
                throw BaseException(StatusCode.PARAM_NOT_VALID)
            }
        }

        var createdTime: LocalDateTime = blogArticle.createdTime
        var updateTime: LocalDateTime? = null

        if (blogArticle.firstId != blogArticle.uuid) {
            // 查询第一个 PUBLISH_INHERIT 版本
            val byPreIdAndStatus = articleService.findFirstOrLastByFirstIdAndStatus(
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

    override fun moveToTrash(id: String) {
        val blogArticle = validateOwn(id)

        // 只能删除草稿和已发布文章
        if (blogArticle.status.canMoveToTrash()) {
            articleService.updateStatusById(id, BlogArticleStatus.TRASH)
        } else {
            BlogArticleNotValidException().throws()
        }
    }

    override fun recoverFromTrash(id: String) {
        val blogArticle = validateOwn(id)

        // 只能从垃圾箱中恢复
        if (blogArticle.status.canRecoverFromTrash()) {
            articleService.updateStatusById(id, BlogArticleStatus.DRAFT)
        } else {
            BlogArticleNotValidException().throws()
        }
    }

    override fun getPreAndNextArticle(id: String): JsonObject {
        // TODO("Not yet implemented")
        return JsonObject()
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
            throw BlogArticleNotFoundException()
        }
        val blogArticle = articleService.findById(id) ?: throw BlogArticleNotFoundException()
        if (blogArticle.createBy != userId) {
            BlogArticleNotValidException().throws()
        }
        return blogArticle
    }

}