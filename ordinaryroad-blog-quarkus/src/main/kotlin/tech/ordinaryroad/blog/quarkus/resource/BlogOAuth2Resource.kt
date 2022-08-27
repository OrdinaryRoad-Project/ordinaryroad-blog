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
import org.eclipse.microprofile.rest.client.inject.RestClient
import tech.ordinaryroad.blog.quarkus.client.gitee.oauth2.GiteeOAuth2Service
import tech.ordinaryroad.blog.quarkus.client.github.oauth2.GithubOAuth2Service
import tech.ordinaryroad.blog.quarkus.client.ordinaryroad.auth.AuthService
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserInfoDTO
import tech.ordinaryroad.blog.quarkus.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.facade.BlogRoleFacade
import tech.ordinaryroad.blog.quarkus.facade.BlogUserFacade
import tech.ordinaryroad.blog.quarkus.request.OAuth2CallbackRequest
import tech.ordinaryroad.blog.quarkus.service.BlogOAuthUserService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.service.transfer.BlogDtoService
import javax.inject.Inject
import javax.ws.rs.BeanParam
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

@Path("oauth2")
class BlogOAuth2Resource {

    @RestClient
    lateinit var orAuthService: AuthService

    @RestClient
    lateinit var githubOAuth2Service: GithubOAuth2Service

    @RestClient
    lateinit var giteeOAuth2Service: GiteeOAuth2Service

    @Inject
    protected lateinit var oAuthUserService: BlogOAuthUserService

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var roleFacade: BlogRoleFacade

    @Inject
    protected lateinit var dtoService: BlogDtoService

    @Inject
    protected lateinit var userFacade: BlogUserFacade

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
        val authorization = request.authorization
        val provider = request.provider
        val device = request.device

        var openid = ""
        // 是否成功
        var success = false

        // 成功时的返回体
        val responseObject = JsonObject()

        // 通过API获取的 OAuth User
        val responseOAuthUser = BlogOAuthUser().apply {
            setProvider(provider)
        }

        when (provider) {
            "ordinaryroad" -> {
                openid = request.openid
                // 获取OR用户信息
                val userinfo = orAuthService.userinfo(authorization)
                if (userinfo.getBoolean("success")) {
                    success = true
                    // username avatar email
                    val jsonObject = userinfo.getJsonObject("data")
                    responseOAuthUser.apply {
                        setOpenid(openid)
                        username = jsonObject.getString("username")
                        avatar = jsonObject.getString("avatar")
                        email = jsonObject.getString("email")
                    }
                }
            }

            "github" -> {
                // 获取GitHub用户信息
                val response = githubOAuth2Service.user(authorization)
                if (response.status == Status.OK.statusCode) {
                    success = true
                    val userinfo = response.readEntity(JsonObject::class.java)
                    openid = userinfo.getNumber("id").toString()
                    // username avatar email
                    responseOAuthUser.apply {
                        setOpenid(openid)
                        username = userinfo.getString("name")
                        avatar = userinfo.getString("avatar_url")
                        email = userinfo.getString("email")
                    }
                }
            }

            "gitee" -> {
                // 获取Gitee用户信息
                val response = giteeOAuth2Service.user(authorization)
//                val response = giteeOAuth2Service.user("token ${authorization.split(" ")[1]}")
                if (response.status == Status.OK.statusCode) {
                    success = true
                    val userinfo = response.readEntity(JsonObject::class.java)
                    openid = userinfo.getNumber("id").toString()
                    // username avatar email
                    responseOAuthUser.apply {
                        setOpenid(openid)
                        username = userinfo.getString("name")
                        avatar = userinfo.getString("avatar_url")
                        email = userinfo.getString("email")
                    }
                }
            }

            "qq" -> {
                // TODO 调用QQ的getUserinfo接口
                success = true
                val username = "用户名——QQ"
                val avatar =
                    "https://img1.baidu.com/it/u=2940939222,1035762201&fm=253&app=138&size=w931&n=0&f=JPEG&fmt=auto?sec=1656954000&t=e9d10fdfde1d55bee41507fcbee3b66d"

                responseOAuthUser.apply {
                    setUsername(username)
                    setAvatar(avatar)
                }
            }

            else -> {
            }
        }

        if (success) {
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

            if (provider == "ordinaryroad") {
                userFacade.updateRoles(userId, arrayListOf("SSSSSSVIP"))
            }

            StpUtil.login(userId, SaLoginModel().apply {
                setDevice(device)
                setIsLastingCookie(true)
            })

            val roleDtoList = roleFacade.findAllByUserId(userId)

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
