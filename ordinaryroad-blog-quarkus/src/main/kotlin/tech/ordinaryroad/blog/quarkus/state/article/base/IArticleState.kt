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
import tech.ordinaryroad.blog.quarkus.request.BlogArticlePublishRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleSaveDraftRequest

/**
 * 文章状态接口
 *
 * @author mjz
 * @date 2022/11/19
 */
interface IArticleState {

    /**
     * 保存草稿
     */
    fun saveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle

    /**
     * 保存草稿
     * firstId对应的DRAFT更新为INHERIT
     * 创建新的DRAFT
     */
    fun saveDraft(article: BlogArticle, articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle

    /**
     * 自动保存草稿
     */
    fun autoSaveDraft(articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle

    /**
     * 自动保存草稿
     */
    fun autoSaveDraft(article: BlogArticle, articleSaveDraftRequest: BlogArticleSaveDraftRequest): BlogArticle

    /**
     * 发布文章
     */
    fun publishArticle(articlePublishRequest: BlogArticlePublishRequest): BlogArticle

    /**
     * 发布文章
     */
    fun publishArticle(article: BlogArticle, articlePublishRequest: BlogArticlePublishRequest): BlogArticle

    /**
     * 删除文章
     */
    fun moveToTrash(article: BlogArticle)

    /**
     * 恢复文章
     */
    fun recoverFromTrash(article: BlogArticle)

    /**
     * 开始审核
     */
    fun startAuditing(article: BlogArticle)

    /**
     * 审核通过
     */
    fun auditApproved(article: BlogArticle)

    /**
     * 审核失败
     */
    fun auditFailed(article: BlogArticle, reason: String)

    /**
     * 文章违规
     */
    fun articleViolation(article: BlogArticle, reason: String)

    /**
     * 文章申诉
     */
    fun articleAppeal(article: BlogArticle, reason: String)
}