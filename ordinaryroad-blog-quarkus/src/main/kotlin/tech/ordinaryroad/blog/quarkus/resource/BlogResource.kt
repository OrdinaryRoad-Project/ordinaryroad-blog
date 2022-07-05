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
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO.Companion.transfer
import tech.ordinaryroad.blog.quarkus.dto.BlogUserinfoDTO
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("")
@Produces(MediaType.APPLICATION_JSON)
class BlogResource {

    @Inject
    private lateinit var userService: BlogUserService

    /**
     * 获取用户信息 username avatar email
     */
    @GET
    @Path("userinfo")
    fun userinfo(): Response {
        val userId = StpUtil.getLoginIdAsString()

        val user = userService.findById(userId)!!

        val userinfoDTO = BlogUserinfoDTO(user.transfer())

        return Response.ok()
            .entity(userinfoDTO)
            .build()
    }

    @GET
    @Path("logout")
    fun logout() {
        StpUtil.logout()
    }

}
