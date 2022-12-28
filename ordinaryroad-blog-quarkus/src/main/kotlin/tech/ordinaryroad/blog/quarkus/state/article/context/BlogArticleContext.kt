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

package tech.ordinaryroad.blog.quarkus.state.article.context

import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.request.BlogArticlePublishRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleSaveDraftRequest
import tech.ordinaryroad.blog.quarkus.state.article.*
import tech.ordinaryroad.blog.quarkus.state.article.base.ArticleState
import javax.enterprise.inject.Instance

/**
 * 博客文章上下文
 *
 * @author mjz
 * @date 2022/11/19
 */
class BlogArticleContext(private val articleState: ArticleState) {

    fun saveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        return this.articleState.saveDraft(articleSaveDraftRequest)
    }

    fun saveDraft(article: BlogArticle, articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        return this.articleState.saveDraft(article, articleSaveDraftRequest)
    }

    fun autoSaveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        return this.articleState.autoSaveDraft(articleSaveDraftRequest)
    }

    fun autoSaveDraft(article: BlogArticle, articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        return this.articleState.autoSaveDraft(article, articleSaveDraftRequest)
    }

    fun publishArticle(articlePublishRequest: BlogArticlePublishRequest): BlogArticle {
        return this.articleState.publishArticle(articlePublishRequest)
    }

    fun publishArticle(article: BlogArticle, articlePublishRequest: BlogArticlePublishRequest): BlogArticle {
        return this.articleState.publishArticle(article, articlePublishRequest)
    }

    fun moveToTrash(article: BlogArticle) {
        this.articleState.moveToTrash(article)
    }

    fun recoverFromTrash(article: BlogArticle) {
        this.articleState.recoverFromTrash(article)
    }

    fun startAuditing(article: BlogArticle) {
        this.articleState.startAuditing(article)
    }

    fun auditApproved(article: BlogArticle) {
        this.articleState.auditApproved(article)
    }

    fun auditFailed(article: BlogArticle, reason: String) {
        this.articleState.auditFailed(article, reason)
    }

    fun articleViolation(article: BlogArticle, reason: String) {
        this.articleState.articleViolation(article, reason)
    }

    fun articleAppeal(article: BlogArticle, reason: String) {
        this.articleState.articleAppeal(article, reason)
    }

    companion object {
        /**
         * 根据BlogArticleStatus生成ArticleState
         */
        fun generateArticleState(
            blogArticleStatus: BlogArticleStatus?,
            articleStates: Instance<ArticleState>
        ): ArticleState {
            return articleStates.select(
                when (blogArticleStatus) {
                    BlogArticleStatus.DRAFT -> DraftState::class.java
                    BlogArticleStatus.DRAFT_AUTO -> DraftAutoState::class.java
                    BlogArticleStatus.INHERIT -> InheritState::class.java
                    BlogArticleStatus.INHERIT_AUTO -> InheritAutoState::class.java
                    BlogArticleStatus.INHERIT_PUBLISH -> InheritPublishState::class.java
                    BlogArticleStatus.PUBLISH -> PublishState::class.java
                    BlogArticleStatus.PENDING -> PendingState::class.java
                    BlogArticleStatus.UNDER_REVIEW -> UnderReviewState::class.java
                    BlogArticleStatus.TRASH -> TrashState::class.java
                    BlogArticleStatus.OFFEND -> OffendState::class.java
                    BlogArticleStatus.ON_APPEAL -> OnAppealState::class.java
                    null -> NullState::class.java
                    else -> throw BaseBlogException("暂不支持文章状态 $blogArticleStatus")
                }
            ).get()
        }

        /**
         * 生成博客文章上下文
         */
        fun generateBlogArticleContext(
            article: BlogArticle?,
            articleStates: Instance<ArticleState>
        ): BlogArticleContext {
            val articleState = generateArticleState(article?.status, articleStates)
            return BlogArticleContext(articleState)
        }
    }

}