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

package tech.ordinaryroad.blog.quarkus.state.article.base

import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.request.BlogArticlePublishRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleSaveDraftRequest
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService
import tech.ordinaryroad.blog.quarkus.service.BlogPushService
import tech.ordinaryroad.blog.quarkus.service.BlogTagService
import tech.ordinaryroad.blog.quarkus.service.BlogTypeService
import javax.inject.Inject

/**
 * 文章状态抽象类
 *
 * @author mjz
 * @date 2022/11/19
 */
abstract class ArticleState : IArticleState {

    @Inject
    lateinit var articleService: BlogArticleService

    @Inject
    lateinit var typeService: BlogTypeService

    @Inject
    lateinit var tagService: BlogTagService

    @Inject
    protected lateinit var pushService: BlogPushService

    override fun saveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        throw newNotSupportedOperationException("保存草稿")
    }

    override fun saveDraft(article: BlogArticle, articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        throw newNotSupportedOperationException("保存草稿", article)
    }

    override fun autoSaveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        throw newNotSupportedOperationException("自动保存草稿")
    }

    override fun autoSaveDraft(
        article: BlogArticle,
        articleSaveDraftRequest: BlogArticleSaveDraftRequest
    ): BlogArticle {
        throw newNotSupportedOperationException("自动保存草稿", article)
    }

    override fun publishArticle(articlePublishRequest: BlogArticlePublishRequest): BlogArticle {
        throw newNotSupportedOperationException("发布文章")
    }

    override fun publishArticle(article: BlogArticle, articlePublishRequest: BlogArticlePublishRequest): BlogArticle {
        throw newNotSupportedOperationException("发布文章", article)
    }

    override fun moveToTrash(article: BlogArticle) {
        throw newNotSupportedOperationException("移动到垃圾箱", article)
    }

    override fun recoverFromTrash(article: BlogArticle) {
        throw newNotSupportedOperationException("从垃圾箱移出", article)
    }

    override fun startAuditing(article: BlogArticle) {
        throw newNotSupportedOperationException("开始审核", article)
    }

    override fun auditApproved(article: BlogArticle) {
        throw newNotSupportedOperationException("审核通过", article)
    }

    override fun auditFailed(article: BlogArticle, reason: String) {
        throw newNotSupportedOperationException("审核失败", article)
    }

    override fun articleViolation(article: BlogArticle, reason: String) {
        throw newNotSupportedOperationException("文章违规", article)
    }

    override fun articleAppeal(article: BlogArticle, reason: String) {
        throw newNotSupportedOperationException("文章申诉", article)
    }

    private fun throwNotSupportedOperationException(operation: String, article: BlogArticle? = null) {
        throw newNotSupportedOperationException(operation, article)
    }

    private fun newNotSupportedOperationException(operation: String, article: BlogArticle? = null) =
        BaseBlogException("当前文章状态 ${article?.status} 不支持 $operation 操作")
}