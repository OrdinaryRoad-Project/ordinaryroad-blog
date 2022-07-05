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

import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import org.jboss.resteasy.annotations.jaxrs.PathParam
import tech.ordinaryroad.blog.quarkus.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.request.BlogArticleCreateRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleQueryRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleUpdateCoverImageRequest
import tech.ordinaryroad.blog.quarkus.request.BlogArticleUpdateRequest
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService
import javax.annotation.security.DenyAll
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("article")
class BlogArticleResource {

    @Inject
    lateinit var articleService: BlogArticleService

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun insert(@Valid @BeanParam request: BlogArticleCreateRequest): Response {
        val blogArticle = articleService.create(
            BlogArticle().apply {
                title = request.title
            }
        )
        return Response.ok()
            .entity(blogArticle)
            .build()
    }

    @DELETE
    @Path("/delete/{id}")
    fun deleteById(@PathParam id: String): Response {
        val success = articleService.delete(id)
        return Response.ok()
            .entity(success)
            .build()
    }

    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun update(@Valid @BeanParam request: BlogArticleUpdateRequest): Response {
        val uuid = request.uuid

        val findById = articleService.findById(uuid)
        if (findById == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        }

        val title = request.title
        val article = articleService.update(BlogArticle().apply {
            this.uuid = uuid
            this.title = title
        })
        return Response.ok()
            .entity(article)
            .build()
    }

    @PUT
    @Path("{id}/coverImage")
    @Produces(MediaType.APPLICATION_JSON)
    fun updateCoverImage(@Valid @BeanParam request: BlogArticleUpdateCoverImageRequest): Response {
        val uuid = request.uuid

        val findById = articleService.findById(uuid)
        if (findById == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        }

        val coverImage = request.coverImage
        val article = articleService.update(BlogArticle().apply {
            this.uuid = uuid
            this.coverImage = coverImage
        })
        return Response.ok()
            .entity(article)
            .build()
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findById(@PathParam id: String): Response {
        val blogArticle = articleService.findById(id)

        if (blogArticle == null) {
            return Response.status(Response.Status.NOT_FOUND)
                .build()
        } else {
            return Response.ok()
                .entity(blogArticle)
                .build()
        }
    }

    /**
     * TODO 废弃
     */
    @DenyAll
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun findAll(@Valid @BeanParam request: BlogArticleQueryRequest): Response {
        val wrapper = ChainWrappers.queryChain(articleService.dao)
        val list = articleService.findAll(request, wrapper)
        return Response.ok()
            .entity(list)
            .build()
    }

    @GET
    @Path("page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun page(@Valid @BeanParam request: BlogArticleQueryRequest): Response {
        val wrapper = ChainWrappers.queryChain(articleService.dao)
        val page = articleService.page(request, wrapper)
        return Response.ok()
            .entity(page)
            .build()
    }

}
