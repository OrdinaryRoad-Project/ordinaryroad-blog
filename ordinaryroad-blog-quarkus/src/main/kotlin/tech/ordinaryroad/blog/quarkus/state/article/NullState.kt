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
import cn.hutool.core.util.StrUtil
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.request.BlogArticlePublishRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleSaveDraftRequest
import tech.ordinaryroad.blog.quarkus.state.article.base.ArticleState
import javax.enterprise.context.ApplicationScoped

/**
 * 文章还未入库时的状态
 *
 * @author mjz
 * @date 2022/12/6
 */
@ApplicationScoped
class NullState : ArticleState() {

    override fun saveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        /* 保存草稿 */
        val loginIdAsString = StpUtil.getLoginIdAsString()
        val create = super.articleService.create(
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
        return articleService.update(
            BlogArticle().apply {
                this.uuid = create.uuid
                this.firstId = create.uuid
            }
        )
    }

    override fun autoSaveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle {
        /* 自动保存草稿 */
        val loginIdAsString = StpUtil.getLoginIdAsString()
        val create = super.articleService.create(
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
        return articleService.update(
            BlogArticle().apply {
                this.uuid = create.uuid
                this.firstId = create.uuid
            }
        )
    }

    override fun publishArticle(articlePublishRequest: BlogArticlePublishRequest): BlogArticle {
        /* 发布文章 */
        val loginIdAsString = StpUtil.getLoginIdAsString()
        val create = super.articleService.create(
            BlogArticle(
                articlePublishRequest.title,
                articlePublishRequest.coverImage,
                articlePublishRequest.summary,
                articlePublishRequest.content,
                BlogArticleStatus.PENDING,
                articlePublishRequest.canComment,
                articlePublishRequest.canReward,
                articlePublishRequest.original,
                StrUtil.EMPTY,
                super.typeService.getOwnIdByNameAndCreateBy(articlePublishRequest.typeName, loginIdAsString),
                super.tagService.getIdListByNamesAndCreateBy(articlePublishRequest.tagNames, loginIdAsString),
            )
        )
        return articleService.update(
            BlogArticle().apply {
                this.uuid = create.uuid
                this.firstId = create.uuid
            }
        )
    }
}