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

import cn.dev33.satoken.stp.SaLoginModel
import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.IdUtil
import io.vertx.core.json.JsonObject
import tech.ordinaryroad.blog.quarkus.chain.oauth2.OAuth2ProviderChain
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.OrdinaryRoadOAuth2Provider
import tech.ordinaryroad.blog.quarkus.dto.BlogRoleDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserInfoDTO
import tech.ordinaryroad.blog.quarkus.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.request.OAuth2CallbackRequest
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogOAuthUserService
import tech.ordinaryroad.blog.quarkus.service.BlogRoleService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import java.util.stream.Collectors
import javax.inject.Inject
import javax.ws.rs.BeanParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("oauth2")
class BlogOAuth2Resource {

    @Inject
    protected lateinit var oAuthUserService: BlogOAuthUserService

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var roleService: BlogRoleService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    @Inject
    protected lateinit var oAuth2ProviderChain: OAuth2ProviderChain

    /**
     * OAuth授权成功后的回调
     * 1. 根据Provider获取userinfo
     * 2. 更新BlogOAuthUser表
     * 3. 判断当前是否已登录
     * 3.1 已登录则获取当前BlogUser
     * 3.2 未登录判断是否关联了主账号BlogUser，找不到则创建主账号并更新关联关系表，然后StpUtil.login(BlogUser.id)
     * 4. 返回userinfo和token
     */
    @POST
    @Path("callback/{provider}")
    @Produces(MediaType.APPLICATION_JSON)
    fun callback(@BeanParam request: OAuth2CallbackRequest): Response {
        val device = request.device

        // 成功时的返回体
        val responseObject = JsonObject()

        val responseOAuthUser = oAuth2ProviderChain.userInfo(request)

        if (responseOAuthUser != null) {
            val openid = responseOAuthUser.openid
            val provider = responseOAuthUser.provider
            var oAuthUser = oAuthUserService.findByOpenidAndProvider(openid, provider)
            if (oAuthUser == null) {
                oAuthUser = oAuthUserService.create(responseOAuthUser)!!
            }

            var user: BlogUser?
            if (StpUtil.isLogin()) {
                val userId = StpUtil.getLoginIdAsString()
                user = userService.findById(userId)!!
            } else {
                user = userService.findByOAuthUserId(oAuthUser.uuid)
                if (user == null) {
                    user = userService.create(BlogUser().apply {
                        username = oAuthUser.username ?: "U_${IdUtil.fastSimpleUUID()}"
                        avatar = oAuthUser.avatar
                        email = oAuthUser.email
                    }, oAuthUser)
                }
            }

            val userId = user.uuid

            if (provider == OrdinaryRoadOAuth2Provider.NAME) {
                userService.updateRoles(userId, arrayListOf("SSSSSSVIP"))
            }

            StpUtil.login(userId, SaLoginModel().apply {
                setDevice(device)
                setIsLastingCookie(true)
            })

            val roleDtoList = roleService.findAllByUserId(userId)
                .stream()
                .map {
                    return@map dtoService.transfer(it, BlogRoleDTO::class.java)
                }
                .collect(Collectors.toList())

            val userDto = dtoService.transfer(user, BlogUserDTO::class.java)

            responseObject.put("userinfo", BlogUserInfoDTO(userDto, roleDtoList))

            val tokenValue = StpUtil.getTokenValue()
            responseObject.put("token", tokenValue)
            return Response.ok()
                .entity(responseObject)
                .build()
        } else {
            return Response.status(Response.Status.UNAUTHORIZED)
                .build()
        }
    }

}
