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

package tech.ordinaryroad.blog.quarkus.resource

import cn.dev33.satoken.annotation.SaCheckLogin
import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.dto.BlogCommentDTO
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogUserNotFoundException
import tech.ordinaryroad.blog.quarkus.request.BlogCommentPostRequest
import tech.ordinaryroad.blog.quarkus.request.BlogCommentQueryRequest
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticleCommentVO
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogSubCommentVO
import tech.ordinaryroad.blog.quarkus.service.BlogCommentService
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("comment")
class BlogCommentResource {

    @Inject
    protected lateinit var commentService: BlogCommentService

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    //region 已测试
    /**
     * 用户发布评论
     */
    @SaCheckLogin
    @POST
    @Path("post")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun post(@Valid request: BlogCommentPostRequest): Response {
        return commentService.post(request)
    }

    /**
     * 用户删除自己的评论
     */
    @SaCheckLogin
    @DELETE
    @Path("delete/own/{id}")
    @Transactional
    fun deleteOwn(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        commentService.deleteOwn(id)
    }

    /**
     * 用户分页查询所有自己的评论
     */
    @SaCheckLogin
    @GET
    @Path("page/own/{page}/{size}")
    fun pageOwn(@Valid @BeanParam request: BlogCommentQueryRequest): Page<BlogCommentDTO> {
        val userId = StpUtil.getLoginIdAsString()

        val wrapper = ChainWrappers.queryChain(commentService.dao)
            .like(!request.content.isNullOrBlank(), "content", "%" + request.content + "%")
            .eq("create_by", userId)

        val page = commentService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogCommentDTO::class.java)
        }

        return dtoPage
    }

    /**
     * 分页查询原始评论的子评论
     */
    @GET
    @Path("page/sub/{originalId}/{page}/{size}")
    fun pageSubComment(@BeanParam request: BlogCommentQueryRequest): Page<BlogSubCommentVO> {
        return commentService.pageSubComment(request)
    }

    /**
     * 分页查询文章的评论，（子评论最多五条）
     */
    @GET
    @Path("page/article/{articleId}/{page}/{size}")
    fun pageArticleComment(@BeanParam request: BlogCommentQueryRequest): Page<BlogArticleCommentVO> {
        return commentService.pageArticleComment(request)
    }

    /**
     * 用户分页查询所有评论
     */
    @GET
    @Path("page/{page}/{size}")
    fun page(@BeanParam request: BlogCommentQueryRequest): Page<Any> {
        throw BadRequestException()

        return commentService.page(request)
    }

    /**
     * 获取评论发表数
     */
    @GET
    @Path("count")
    fun countPost(
        @Valid @Size(
            max = 32,
            message = "userId长度不能大于32"
        ) @DefaultValue("") @RestQuery userId: String
    ): Long {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        return commentService.countByUserId(userId)
    }
    //region 开发中
    //endregion
}
