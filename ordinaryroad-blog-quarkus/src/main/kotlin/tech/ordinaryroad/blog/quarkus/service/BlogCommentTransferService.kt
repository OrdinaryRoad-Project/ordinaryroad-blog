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

import tech.ordinaryroad.blog.quarkus.entity.BlogComment
import tech.ordinaryroad.blog.quarkus.facade.BlogArticleFacade
import tech.ordinaryroad.blog.quarkus.facade.BlogCommentFacade
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogCommentMapStruct
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogUserMapStruct
import tech.ordinaryroad.blog.quarkus.request.BlogCommentQueryRequest
import tech.ordinaryroad.blog.quarkus.vo.BlogArticleCommentVO
import tech.ordinaryroad.blog.quarkus.vo.BlogSubCommentVO
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * 评论转换服务类
 */
@ApplicationScoped
class BlogCommentTransferService {

    val blogCommentMapStruct = BlogCommentMapStruct.INSTANCE
    val blogUserMapStruct = BlogUserMapStruct.INSTANCE

    @Inject
    protected lateinit var commentFacade: BlogCommentFacade

    @Inject
    protected lateinit var articleFacade: BlogArticleFacade

    @Inject
    protected lateinit var commentService: BlogCommentService

    @Inject
    protected lateinit var userService: BlogUserService

    fun transferArticle(comment: BlogComment): BlogArticleCommentVO {
        return blogCommentMapStruct.do2ArticleVo(comment).apply {
            val blogUser = userService.findById(comment.createBy)
            user = blogUserMapStruct.do2Vo(blogUser)

            replies = commentFacade.pageSubComment(BlogCommentQueryRequest().apply {
                articleId = comment.articleId
                originalId = comment.uuid
                size = 5
                sortBy = listOf("created_time")
                sortDesc = listOf(true)
            })
        }
    }

    fun transferSub(comment: BlogComment): BlogSubCommentVO {
        return blogCommentMapStruct.do2SubVo(comment).apply {
            user = blogUserMapStruct.do2Vo(userService.findById(comment.createBy))

            if (!comment.parentId.isNullOrBlank()) {
                val parentComment = commentService.findById(comment.parentId)
                parent = blogCommentMapStruct.do2SubVo(parentComment).apply {
                    user = blogUserMapStruct.do2Vo(userService.findById(parentComment.createBy))
                }
            }
        }
    }

}
