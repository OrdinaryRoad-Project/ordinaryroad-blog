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
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.service.BlogOAuthUserService
import tech.ordinaryroad.blog.quarkus.service.BlogUserOAuthUsersService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import javax.inject.Inject
import javax.ws.rs.DELETE
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("oauth_user")
class BlogOAuthUserResource {

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var oAuthUserService: BlogOAuthUserService

    @Inject
    protected lateinit var userOAuthUsersService: BlogUserOAuthUsersService

    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun all(): List<BlogOAuthUser> {
        val userId = StpUtil.getLoginIdAsString()
        val user = userService.findById(userId)
        val list = oAuthUserService.findAllByUserId(user.uuid)

        return list
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    fun delete(@RestQuery provider: String) {
        val userId = StpUtil.getLoginIdAsString()
        val oAuthUser = oAuthUserService.findByUserIdAndProvider(userId, provider)
        if (oAuthUser == null) {
            BaseBlogException("参数无效").throws()
        }
        // 根据OAuth2用户Id查询Blog主账号
        val oAuthUserId = oAuthUser!!.uuid
        val user = userService.findByOAuthUserId(oAuthUserId)
        if (user == null || user.uuid != userId) {
            // 删除时需要有正确的关联关系
            BaseBlogException("参数无效").throws()
        }
        val countByUserId = userOAuthUsersService.selectCountByUserId(userId)
        if (countByUserId == 1L) {
            // 必须保留一个账号
            BaseBlogException("必须保留一个账号").throws()
        } else if (countByUserId == 0L) {
            BaseBlogException("参数无效").throws()
        }

        oAuthUserService.removeUserOAuthUser(oAuthUserId, userId)
    }

}
