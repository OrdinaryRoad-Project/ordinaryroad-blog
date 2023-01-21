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

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.date.DatePattern
import cn.hutool.core.io.FileUtil
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.RestHeader
import tech.ordinaryroad.blog.quarkus.client.ordinaryroad.upms.OrUpmsApi
import tech.ordinaryroad.blog.quarkus.dto.BlogUserInfoDTO
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.properties.OAuth2Properties
import tech.ordinaryroad.blog.quarkus.request.BlogArticleQueryRequest
import tech.ordinaryroad.blog.quarkus.request.FileUploadRequest
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService
import tech.ordinaryroad.blog.quarkus.service.BlogService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("common")
class BlogResource {

    @Inject
    protected lateinit var oAuth2Properties: OAuth2Properties

    @RestClient
    lateinit var orUpmsApi: OrUpmsApi

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var articleService: BlogArticleService

    @Inject
    protected lateinit var blogService: BlogService

    /**
     * 获取用户信息 username avatar email
     */
    @GET
    @Path("user_info")
    @Produces(MediaType.APPLICATION_JSON)
    fun userinfo(): BlogUserInfoDTO {
        return blogService.userInfo()
    }

    /**
     * 用户上传文件
     */
    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun upload(
        @BeanParam request: FileUploadRequest,
        @RestHeader("or-blog-token") token: String
    ): Response {
        if (StpUtil.getLoginIdByToken(token) == null) {
            BlogArticleResource.throwBadRequest()
        }

        val fileUpload = request.file

        val fileName = fileUpload.fileName()

        val srcFilePath = fileUpload.uploadedFile()
        val srcFile = srcFilePath.toFile()

        val renamedFile = FileUtil.rename(srcFile, fileName, true)

        val ordinaryroadOAuth2Property = oAuth2Properties.providers()["ordinaryroad"]!!

        val jsonObject =
            orUpmsApi.upload(tech.ordinaryroad.blog.quarkus.client.ordinaryroad.upms.FileUploadRequest().apply {
                clientId = ordinaryroadOAuth2Property["client-id"]!!
                clientSecret = ordinaryroadOAuth2Property["client-secret"]!!
                file = renamedFile
            })

        return if (jsonObject.getBoolean("success")) {
            Response.ok(jsonObject.getString("data")).build()
        } else {
            Response.status(Response.Status.BAD_REQUEST).build()
        }
    }


    /**
     * 获取sitemap索引
     */
    @GET
    @Path("sitemap_index")
    @Produces(MediaType.APPLICATION_JSON)
    fun sitemapIndex(): JsonArray {
        BlogArticleResource.throwBadRequest()

        val maxCountOfEachSitemap = 50000L
        return JsonArray()
    }

    /**
     * 获取sitemap
     */
    @GET
    @Path("sitemap")
    @Produces(MediaType.APPLICATION_JSON)
    fun sitemap(): JsonArray {
        val sitemapList = JsonArray()
        val allUser = userService.findAll()
        if (!allUser.isNullOrEmpty()) {
            val blogArticleQueryRequest = BlogArticleQueryRequest()
            blogArticleQueryRequest.size = 1L.coerceAtLeast(50000L / allUser.size)
            allUser.forEach { blogUser ->
                val userSpacePath = "/${blogUser.uid}"
                val sitemap = JsonObject()
                sitemap.put("url", userSpacePath)
                sitemap.put("changefreq", "always")
                sitemap.put("priority", 0.9)
                if (blogUser.updateTime != null) {
                    sitemap.put("lastmod", blogUser.updateTime.format(DatePattern.NORM_DATETIME_FORMATTER))
                }
                sitemapList.add(sitemap)

                blogArticleQueryRequest.createBy = blogUser.uuid
                val wrapper = ChainWrappers.queryChain(articleService.dao)
                    .eq("status", BlogArticleStatus.PUBLISH)
                val page = articleService.page(blogArticleQueryRequest, wrapper)
                page.records.forEach { blogArticle ->
                    val userArticleSitemap = sitemap.copy()
                    userArticleSitemap.put("url", "$userSpacePath/article/${blogArticle.firstId}")
                    userArticleSitemap.put("changefreq", "always")
                    sitemap.put("priority", 1.0)
                    if (blogUser.updateTime != null) {
                        userArticleSitemap.put(
                            "lastmod",
                            blogArticle.createdTime.format(DatePattern.NORM_DATETIME_FORMATTER)
                        )
                    }
                    sitemapList.add(userArticleSitemap)
                }


            }
        }
        return sitemapList
    }

    @GET
    @Path("loginTest")
    fun loginTest() {
        // StpUtil.login("1600048920027762688")
    }

    @GET
    @Path("logout")
    fun logout() {
        StpUtil.logout()
    }

}
