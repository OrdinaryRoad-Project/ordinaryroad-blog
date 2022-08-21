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
import cn.hutool.core.io.FileUtil
import org.eclipse.microprofile.rest.client.inject.RestClient
import org.jboss.resteasy.reactive.MultipartForm
import org.jboss.resteasy.reactive.RestHeader
import tech.ordinaryroad.blog.quarkus.client.ordinaryroad.upms.OrUpmsApi
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO.Companion.transfer
import tech.ordinaryroad.blog.quarkus.dto.BlogUserinfoDTO
import tech.ordinaryroad.blog.quarkus.request.FileUploadRequest
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.commons.base.exception.BaseException
import javax.inject.Inject
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("common")
class BlogResource {

    @RestClient
    lateinit var orUpmsApi: OrUpmsApi

    @Inject
    protected lateinit var userService: BlogUserService

    /**
     * 获取用户信息 username avatar email
     */
    @GET
    @Path("userinfo")
    @Produces(MediaType.APPLICATION_JSON)
    fun userinfo(): BlogUserinfoDTO {
        val userId = StpUtil.getLoginIdAsString()

        val user = userService.findById(userId)!!

        val userinfoDTO = BlogUserinfoDTO(user.transfer())

        return userinfoDTO
    }

    @POST
    @Path("upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    fun upload(
        @MultipartForm request: FileUploadRequest,
        @RestHeader("or-blog-token") token: String
    ): Response {
        // 报错
        // StpUtil.checkLogin()

        // TODO 统一异常处理
        StpUtil.getLoginIdByToken(token) ?: throw BaseException("UserId 为空")

        val fileUpload = request.file

        val fileName = fileUpload.fileName()

        val srcFilePath = fileUpload.uploadedFile()
        val srcFile = srcFilePath.toFile()

        val renamedFile = FileUtil.rename(srcFile, fileName, true)

        val jsonObject =
            orUpmsApi.upload(tech.ordinaryroad.blog.quarkus.client.ordinaryroad.upms.FileUploadRequest().apply {
                clientId = "ordinaryroad-blog"
                clientSecret = "g8DxQweDHm4CAtda"
                file = renamedFile
            })

        return if (jsonObject.getBoolean("success")) {
            Response.ok(jsonObject.getString("data")).build()
        } else {
            Response.status(Response.Status.BAD_REQUEST).build()
        }
    }

    @GET
    @Path("loginTest")
    fun loginTest() {
        // StpUtil.login("41acab7734074dae9da0f40aca5294ca")
    }

    @GET
    @Path("logout")
    fun logout() {
        StpUtil.logout()
    }

}
