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

import org.eclipse.microprofile.rest.client.inject.RestClient
import tech.ordinaryroad.blog.quarkus.client.ordinaryroad.auth.AuthService
import tech.ordinaryroad.blog.quarkus.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.request.OAuth2CallbackRequest
import javax.enterprise.context.ApplicationScoped

/**
 * OrdinaryRoadOAuth2Provider
 *
 * @author mjz
 * @date 2022/9/5
 */
@ApplicationScoped
class OrdinaryRoadOAuth2Provider : BaseOAuth2Provider(NAME) {

    @RestClient
    protected lateinit var orAuthService: AuthService

    override fun userInfo(request: OAuth2CallbackRequest): BlogOAuthUser? {
        val userinfo = orAuthService.userinfo(request.authorization)
        if (!userinfo.getBoolean("success")) {
            return null
        }
        // username avatar email
        val jsonObject = userinfo.getJsonObject("data")
        return BlogOAuthUser().apply {
            openid = request.openid
            username = jsonObject.getString("username")
            avatar = jsonObject.getString("avatar")
            email = jsonObject.getString("email")
        }
    }

    companion object {
        const val NAME = "ordinaryroad"
    }

}