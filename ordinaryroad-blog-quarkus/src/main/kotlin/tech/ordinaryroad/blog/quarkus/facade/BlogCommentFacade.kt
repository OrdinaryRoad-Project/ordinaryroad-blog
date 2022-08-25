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

package tech.ordinaryroad.blog.quarkus.facade

import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import tech.ordinaryroad.blog.quarkus.request.BlogCommentPostRequest
import tech.ordinaryroad.blog.quarkus.request.BlogCommentQueryRequest
import tech.ordinaryroad.blog.quarkus.vo.BlogArticleCommentVO
import tech.ordinaryroad.blog.quarkus.vo.BlogSubCommentVO
import javax.ws.rs.core.Response

interface BlogCommentFacade {

    /**
     * 发布评论
     */
    fun post(request: BlogCommentPostRequest): Response

    /**
     * 分页查询评论的子评论
     */
    fun pageSubComment(request: BlogCommentQueryRequest): Page<BlogSubCommentVO>

    /**
     *  分页查询文章的评论，（子评论最多五条）
     */
    fun pageArticleComment(request: BlogCommentQueryRequest): Page<BlogArticleCommentVO>

    /**
     * 分页查询所有评论
     */
    fun page(request: BlogCommentQueryRequest): Page<Any>

}