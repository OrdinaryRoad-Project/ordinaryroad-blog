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

package tech.ordinaryroad.blog.quarkus.chain.oauth2.provider

import cn.hutool.json.JSONUtil
import com.xkcoding.http.support.HttpHeader
import me.zhyd.oauth.cache.AuthStateCache
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.config.AuthSource
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthToken
import me.zhyd.oauth.model.AuthUser
import me.zhyd.oauth.request.AuthDefaultRequest
import me.zhyd.oauth.utils.HttpUtils
import me.zhyd.oauth.utils.UrlBuilder

/**
 *
 *
 * @author mjz
 * @date 2022/9/6
 */
class OrdinaryRoadOAuth2Request : AuthDefaultRequest {

    /**
     * JustAuth框架反射要用
     */
    constructor(config: AuthConfig?) : super(config, null)
    constructor(config: AuthConfig?, source: AuthSource?) : super(config, source)
    constructor(config: AuthConfig?, source: AuthSource?, authStateCache: AuthStateCache?) : super(
        config,
        source,
        authStateCache
    )

    override fun authorize(state: String?): String {
        return UrlBuilder.fromBaseUrl(super.authorize(state))
            .queryParam("scope", super.getScopes(",", true, config.scopes))
            .build()
    }

    override fun getAccessToken(authCallback: AuthCallback): AuthToken {
        val accessTokenObject = JSONUtil.parseObj(super.doPostAuthorizationCode(authCallback.code))

        if (!accessTokenObject.getBool("success", true)) {
            throw AuthException(accessTokenObject.getStr("msg"))
        }

        return AuthToken.builder()
            .accessToken(accessTokenObject.getStr("access_token"))
            .refreshToken(accessTokenObject.getStr("refresh_token"))
            .expireIn(accessTokenObject.getInt("expires_in"))
            .refreshTokenExpireIn(accessTokenObject.getInt("refresh_expires_in"))
            .scope(accessTokenObject.getStr("scope"))
            .openId(accessTokenObject.getStr("openid"))
            .tokenType(accessTokenObject.getStr("token_type"))
            .build()
    }

    override fun getUserInfo(authToken: AuthToken): AuthUser {
        val responseJsonString = HttpUtils(config.httpConfig)
            .get(source.userInfo(), emptyMap(), HttpHeader().apply {
                add("Authorization", authToken.tokenType + " " + authToken.accessToken)
            }, false)
            .body

        val userInfo = JSONUtil.parseObj(responseJsonString)

        if (!userInfo.getBool("success")) {
            throw AuthException(userInfo.getStr("msg"))
        }

        // username avatar email
        val jsonObject = userInfo.getJSONObject("data")
        return AuthUser.builder()
            .uuid(authToken.openId)
            .username(jsonObject.getStr("username"))
            .avatar(jsonObject.getStr("avatar"))
            .email(jsonObject.getStr("email"))
            .build()
    }

}