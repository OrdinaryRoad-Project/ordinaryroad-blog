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
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.chain.oauth2.OAuth2ProviderChain
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.OrdinaryRoadOAuth2Source
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUserOAuthUsers
import tech.ordinaryroad.blog.quarkus.dto.BlogRoleDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserInfoDTO
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.service.*
import java.time.LocalDate
import java.util.stream.Collectors
import javax.inject.Inject
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType


@Path("oauth2")
class BlogOAuth2Resource {

    @Inject
    protected lateinit var userOAuthUsersService: BlogUserOAuthUsersService

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

    @GET
    @Path("authorize")
    @Produces(MediaType.TEXT_PLAIN)
    fun authorize(@RestQuery provider: String, @RestQuery state: String): String {
        return oAuth2ProviderChain.authorize(provider, state)
    }

    /**
     * OAuth授权成功后的回调
     * 1. 根据Provider获取userinfo
     * 2. 更新BlogOAuthUser表
     * 3. 判断当前是否已登录
     * 3.1 已登录则获取当前BlogUser
     * 3.2 未登录判断是否关联了主账号BlogUser，找不到则创建主账号并更新关联关系表，然后StpUtil.login(BlogUser.id)
     * 4. 返回userinfo和token
     */
    @GET
    @Path("callback/{provider}")
    @Produces(MediaType.APPLICATION_JSON)
    fun callback(@RestPath provider: String, @RestQuery code: String, @RestQuery state: String): JsonObject {
        // 回掉方式 login|add|update
        val type = state.split("_")[3]
        when (type) {
            "login" -> {
                // 已经登录则需要退出登录
                if (StpUtil.isLogin()) {
                    throw BaseBlogException("请先退出登录")
                }
            }

            "add", "update" -> {
                // 未登录则需要登录
                if (!StpUtil.isLogin()) {
                    throw BaseBlogException("请先登录")
                }
            }

            else -> {
                throw BaseBlogException("暂不支持的操作")
            }
        }

        val device = "PC"

        // 成功时的返回体
        val responseObject = JsonObject()

        // 获取OAuth2的UserInfo
        val responseOAuthUser = oAuth2ProviderChain.userInfo(provider, code, state)
        val openid = responseOAuthUser.openid
        // openid和provider确定唯一用户
        var oAuthUser = oAuthUserService.findByOpenidAndProvider(openid, provider)

        // 主账号
        var user: BlogUser? = null
        when (type) {
            "login" -> {
                if (oAuthUser == null) {
                    // 不存在则创建，用于创建关联关系
                    oAuthUser = oAuthUserService.create(responseOAuthUser)!!
                }

                // 根据OAuth2用户Id查询Blog主账号
                user = userService.findByOAuthUserId(oAuthUser.uuid)
                if (user == null) {
                    // 不存在则创建
                    user = userService.create(
                        BlogUser().apply {
                            username = oAuthUser!!.username ?: "U_${IdUtil.fastSimpleUUID()}"
                            avatar = oAuthUser!!.avatar
                            email = oAuthUser!!.email
                        }, oAuthUser
                    )
                }
            }

            "add" -> {
                if (oAuthUser == null) {
                    // 不存在则创建，用于创建关联关系
                    oAuthUser = oAuthUserService.create(responseOAuthUser)!!
                }

                user = userService.findById(StpUtil.getLoginIdAsString())
                userService.findByOAuthUserId(oAuthUser.uuid)?.let {
                    throw BaseBlogException("该账号已注册，请先注销账号（开发中...）")
                }
                userOAuthUsersService.create(
                    BlogUserOAuthUsers().apply {
                        userId = user!!.uuid
                        oAuthUserId = oAuthUser.uuid
                    })
            }

            "update" -> {
                if (oAuthUser == null) {
                    BaseBlogException("参数无效").throws()
                }
                responseOAuthUser.uuid = oAuthUser!!.uuid

                // 根据OAuth2用户Id查询Blog主账号
                user = userService.findByOAuthUserId(oAuthUser.uuid)
                if (user == null || user.uuid != StpUtil.getLoginIdAsString()) {
                    // 更新时需要有正确的关联关系
                    BaseBlogException("参数无效").throws()
                }
                oAuthUserService.update(responseOAuthUser)
            }

            else -> {
                BaseBlogException("不支持的操作").throws()
            }
        }
        val userId = user!!.uuid

        // 2023年之前通过ordinaryroad账号登录赋予SSSSSSVIP角色
        if (LocalDate.now().isBefore(LocalDate.of(2023, 1, 1))) {
            if (provider == OrdinaryRoadOAuth2Source.NAME) {
                if (roleService.findAllByUserId(userId).isEmpty()) {
                    val role = roleService.findByRoleCode("SSSSSSVIP")!!
                    userService.updateRoles(userId, arrayListOf(role.uuid))
                }
            }
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

        responseObject.put("userInfo", BlogUserInfoDTO(userDto, roleDtoList))

        val tokenValue = StpUtil.getTokenValue()
        responseObject.put("token", tokenValue)
        return responseObject
    }

}
