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
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.exception.BlogUserNotFoundException
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogUserMapStruct
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.vo.BlogUserVO
import javax.inject.Inject
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("user")
class BlogUserResource {

    val userMapStruct = BlogUserMapStruct.INSTANCE

    @Inject
    protected lateinit var userService: BlogUserService

    /**
     * 查询用户
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findById(
        @Valid @NotBlank(message = "Id不能为空")
        @RestPath id: String
    ): BlogUserVO {
        val blogUser = userService.findById(id)

        if (blogUser == null) {
            throw BlogUserNotFoundException()
        } else {
            return userMapStruct.transfer(blogUser)
        }
    }


    //region 开发中（管理员）
    @PUT
    @Path("{id}")
    fun updateRoles(
        @Valid @NotBlank(message = "Id不能为空")
        @RestPath id: String,
        @RestQuery roleIds: List<String> = emptyList()
    ) {
        StpUtil.checkRoleOr("DEVELOPER", "ADMIN")

        userService.updateRoles(id, roleIds)
    }
    //endregion

}
