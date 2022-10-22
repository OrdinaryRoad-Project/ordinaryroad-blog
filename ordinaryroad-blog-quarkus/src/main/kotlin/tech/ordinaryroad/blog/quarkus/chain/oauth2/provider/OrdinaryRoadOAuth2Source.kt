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

import me.zhyd.oauth.config.AuthSource
import me.zhyd.oauth.request.AuthDefaultRequest
import tech.ordinaryroad.blog.quarkus.properties.OAuth2Properties
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * OrdinaryRoadOAuth2Provider
 *
 * @author mjz
 * @date 2022/9/5
 */
@ApplicationScoped
class OrdinaryRoadOAuth2Source : AuthSource {

    @Inject
    protected lateinit var oAuth2Properties: OAuth2Properties

    override fun authorize(): String {
        return oAuth2Properties.providers()["ordinaryroad"]!!["authorize-endpoint"]!!
    }

    override fun accessToken(): String {
        return oAuth2Properties.providers()["ordinaryroad"]!!["access-token-endpoint"]!!
    }

    override fun userInfo(): String {
        return oAuth2Properties.providers()["ordinaryroad"]!!["user-info-endpoint"]!!
    }

    override fun getTargetClass(): Class<out AuthDefaultRequest> {
        return OrdinaryRoadOAuth2Request::class.java
    }

    override fun getName(): String {
        return NAME
    }

    companion object {
        const val NAME = "ordinaryroad"
    }

}