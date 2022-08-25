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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import tech.ordinaryroad.blog.quarkus.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.entity.BlogComment
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogCommentNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogCommentNotValidException
import tech.ordinaryroad.blog.quarkus.facade.BlogCommentFacade
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogCommentMapStruct
import tech.ordinaryroad.blog.quarkus.request.BlogCommentPostRequest
import tech.ordinaryroad.blog.quarkus.request.BlogCommentQueryRequest
import tech.ordinaryroad.blog.quarkus.service.*
import tech.ordinaryroad.blog.quarkus.vo.BlogArticleCommentVO
import tech.ordinaryroad.blog.quarkus.vo.BlogSubCommentVO
import tech.ordinaryroad.commons.base.cons.StatusCode
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

@ApplicationScoped
class BlogCommentFacadeImpl : BlogCommentFacade {

    val blogCommentMapStruct: BlogCommentMapStruct = BlogCommentMapStruct.INSTANCE

    @Inject
    protected lateinit var commentService: BlogCommentService

    @Inject
    protected lateinit var commentTransferService: BlogCommentTransferService

    @Inject
    protected lateinit var articleService: BlogArticleService

    @Inject
    protected lateinit var blogService: BlogService

    @Inject
    protected lateinit var pushService: BlogPushService

    override fun post(request: BlogCommentPostRequest): Response {
        // 校验登录
        StpUtil.checkLogin()
        val user = blogService.currentUser()

        // 先转换，后面需要填充
        val comment = blogCommentMapStruct.request2do(request)

        // 传入parentId视为回复评论
        val isReply = !request.parentId.isNullOrBlank()

        val who: String = user.username
        val actionString: String
        var content = request.content
        val notifier: String

        if (isReply) {
            // 回复，直接使用parent的articleId
            val parentComment = validateComment(request.parentId, true)!!

            // 校验articleId
            if (request.articleId.isBlank()) {
                request.articleId = parentComment.articleId
            } else {
                if (request.articleId != parentComment.articleId) {
                    // 父评论文章和传入的不一致
                    throw BlogCommentNotValidException()
                } else {
                    validateArticle(request.articleId)
                }
            }

            // 填充originalId
            comment.originalId = if (parentComment.originalId.isNullOrBlank()) {
                parentComment.uuid
            } else {
                parentComment.originalId
            }

            actionString = "回复了我的评论"
            notifier = parentComment.createBy
            content += " ${parentComment.content}"
        } else {
            val article = validateArticle(request.articleId)

            comment.originalId = ""

            actionString = "对我的文章发表了评论"
            notifier = article.createBy
            content += " ${article.title}"
        }

        val create = commentService.create(comment)

        val title = who + actionString

        pushService.comment(title, content, notifier)

        return Response.ok(
            if (isReply) {
                commentTransferService.transferSub(create)
            } else {
                commentTransferService.transferArticle(create)
            }
        ).build()
    }

    override fun pageSubComment(request: BlogCommentQueryRequest): Page<BlogSubCommentVO> {
        val originalComment = validateComment(request.originalId, true)!!

        val wrapper = ChainWrappers.queryChain(commentService.dao)
            .eq("article_id", originalComment.articleId)
            .eq("original_id", request.originalId)

        val page = commentService.page(request, wrapper)

        val voPage = PageUtils.copyPage<BlogComment, BlogSubCommentVO>(page) {
            return@copyPage commentTransferService.transferSub(it)
        }

        return voPage
    }

    override fun pageArticleComment(request: BlogCommentQueryRequest): Page<BlogArticleCommentVO> {
        validateArticle(request.articleId)

        val wrapper = ChainWrappers.queryChain(commentService.dao)
            .eq("article_id", request.articleId)
            .eq("original_id", "")

        val page = commentService.page(request, wrapper)

        val voPage = PageUtils.copyPage<BlogComment, BlogArticleCommentVO>(page) {
            return@copyPage commentTransferService.transferArticle(it)
        }

        return voPage
    }

    override fun page(request: BlogCommentQueryRequest): Page<Any> {
        val wrapper = ChainWrappers.queryChain(commentService.dao)

        val page = commentService.page(request, wrapper)

        val voPage = PageUtils.copyPage(page) {
            if (it.originalId.isNullOrBlank()) {
                return@copyPage commentTransferService.transferArticle(it)
            } else {
                return@copyPage commentTransferService.transferSub(it)
            }
        }

        return voPage
    }

    /**
     * 校验文章是否存在
     */
    private fun validateArticle(articleId: String?): BlogArticle {
        if (articleId.isNullOrBlank()) {
            throw BaseBlogException(StatusCode.PARAM_IS_BLANK)
        }
        return articleService.findById(articleId) ?: throw BlogArticleNotFoundException()
    }

    /**
     * 校验评论是否存在
     */
    private fun validateComment(originalId: String?, must: Boolean = false): BlogComment? {
        if (originalId.isNullOrBlank()) {
            if (must) {
                throw BaseBlogException(StatusCode.PARAM_IS_BLANK)
            } else {
                return null
            }
        }
        return commentService.findById(originalId) ?: throw BlogCommentNotFoundException()
    }

}