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

package tech.ordinaryroad.blog.quarkus.state.article

import cn.dev33.satoken.stp.StpUtil
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.request.BlogArticlePublishRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleSaveDraftRequest
import tech.ordinaryroad.blog.quarkus.state.article.base.ArticleState
import javax.enterprise.context.ApplicationScoped

/**
 * 手动保存的草稿
 *
 * @author mjz
 * @date 2022/11/19
 */
@ApplicationScoped
class DraftState : ArticleState() {

    override fun saveDraft(article: BlogArticle, articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        val loginIdAsString = StpUtil.getLoginIdAsString()

        /* 更新状态 */
        super.articleService.updateStatusByFirstIdAndCreatedBy(
            article.firstId,
            loginIdAsString,
            BlogArticleStatus.DRAFT,
            BlogArticleStatus.INHERIT
        )

        /* 保存草稿 */
        return super.articleService.create(
            BlogArticle(
                articleSaveDraftRequest.title,
                articleSaveDraftRequest.coverImage,
                articleSaveDraftRequest.summary,
                articleSaveDraftRequest.content,
                BlogArticleStatus.DRAFT,
                articleSaveDraftRequest.canComment,
                articleSaveDraftRequest.canReward,
                articleSaveDraftRequest.original,
                articleSaveDraftRequest.firstId,
                super.typeService.getOwnIdByNameAndCreateBy(articleSaveDraftRequest.typeName, loginIdAsString),
                super.tagService.getIdListByNamesAndCreateBy(articleSaveDraftRequest.tagNames, loginIdAsString),
            )
        )
    }

    override fun autoSaveDraft(
        article: BlogArticle,
        articleSaveDraftRequest: BlogArticleSaveDraftRequest
    ): BlogArticle {
        val loginIdAsString = StpUtil.getLoginIdAsString()

        /* 更新状态 */
        super.articleService.updateStatusByFirstIdAndCreatedBy(
            article.firstId,
            loginIdAsString,
            BlogArticleStatus.DRAFT,
            BlogArticleStatus.INHERIT
        )

        /* 自动保存草稿 */
        return super.articleService.create(
            BlogArticle(
                articleSaveDraftRequest.title,
                articleSaveDraftRequest.coverImage,
                articleSaveDraftRequest.summary,
                articleSaveDraftRequest.content,
                BlogArticleStatus.DRAFT_AUTO,
                articleSaveDraftRequest.canComment,
                articleSaveDraftRequest.canReward,
                articleSaveDraftRequest.original,
                articleSaveDraftRequest.firstId,
                super.typeService.getOwnIdByNameAndCreateBy(articleSaveDraftRequest.typeName, loginIdAsString),
                super.tagService.getIdListByNamesAndCreateBy(articleSaveDraftRequest.tagNames, loginIdAsString),
            )
        )
    }

    override fun publishArticle(article: BlogArticle, articlePublishRequest: BlogArticlePublishRequest): BlogArticle {
        val loginIdAsString = StpUtil.getLoginIdAsString()

        /* 更新状态 */
        super.articleService.updateStatusByFirstIdAndCreatedBy(
            article.firstId,
            loginIdAsString,
            BlogArticleStatus.DRAFT,
            BlogArticleStatus.INHERIT
        )

        /* 发布文章 */
        return super.articleService.create(
            BlogArticle(
                articlePublishRequest.title,
                articlePublishRequest.coverImage,
                articlePublishRequest.summary,
                articlePublishRequest.content,
                BlogArticleStatus.PENDING,
                articlePublishRequest.canComment,
                articlePublishRequest.canReward,
                articlePublishRequest.original,
                articlePublishRequest.firstId,
                super.typeService.getOwnIdByNameAndCreateBy(articlePublishRequest.typeName, loginIdAsString),
                super.tagService.getIdListByNamesAndCreateBy(articlePublishRequest.tagNames, loginIdAsString),
            )
        )
    }

    override fun moveToTrash(article: BlogArticle) {
        /* 更新状态 */
        val loginIdAsString = StpUtil.getLoginIdAsString()
        super.articleService.updateStatusByFirstIdAndCreatedBy(
            article.firstId,
            loginIdAsString,
            BlogArticleStatus.DRAFT,
            BlogArticleStatus.TRASH
        )
    }
}