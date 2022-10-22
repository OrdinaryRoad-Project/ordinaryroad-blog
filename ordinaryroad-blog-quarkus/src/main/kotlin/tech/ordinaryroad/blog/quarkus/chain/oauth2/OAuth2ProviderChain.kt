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

package tech.ordinaryroad.blog.quarkus.chain.oauth2

import cn.hutool.core.lang.Chain
import cn.hutool.core.util.ReflectUtil
import io.quarkus.runtime.StartupEvent
import me.zhyd.oauth.AuthRequestBuilder
import me.zhyd.oauth.config.AuthConfig
import me.zhyd.oauth.exception.AuthException
import me.zhyd.oauth.model.AuthCallback
import me.zhyd.oauth.model.AuthResponse
import me.zhyd.oauth.model.AuthUser
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.BaseOAuth2Provider
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.OrdinaryRoadOAuth2Source
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.properties.OAuth2Properties
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes
import javax.inject.Inject


/**
 * OAuth2ProviderChain
 *
 * @author mjz
 * @date 2022/9/5
 */
@ApplicationScoped
class OAuth2ProviderChain : BaseOAuth2Provider("CHAIN_HEAD"), Chain<BaseOAuth2Provider, OAuth2ProviderChain> {

    @Inject
    protected lateinit var ordinaryRoadOAuth2Source: OrdinaryRoadOAuth2Source

    @Inject
    protected lateinit var oAuth2Properties: OAuth2Properties

    private val oAuth2Providers = LinkedList<BaseOAuth2Provider>()

    @Suppress("UNCHECKED_CAST")
    fun onStart(@Observes ev: StartupEvent) {
        oAuth2Properties.providers().forEach { (provider, properties) ->
            val authRequestBuilder = AuthRequestBuilder.builder()
            if (provider == OrdinaryRoadOAuth2Source.NAME) {
                authRequestBuilder
                    .source(provider)
                    .extendSource(ordinaryRoadOAuth2Source)
            } else {
                authRequestBuilder.source(provider)
            }
            authRequestBuilder.authConfig {
                AuthConfig.builder()
                    .clientId(properties["client-id"] as String)
                    .clientSecret(properties["client-secret"] as String)
                    .scopes((properties["scopes"] as String).split(","))
                    .redirectUri(oAuth2Properties.redirectUri())
                    .build()
            }

            val authRequest = authRequestBuilder.build()

            if (provider == OrdinaryRoadOAuth2Source.NAME) {
                ReflectUtil.setFieldValue(authRequest, "source", ordinaryRoadOAuth2Source)
            }

            this.addChain(object : BaseOAuth2Provider(provider) {
                override fun authorize(provider: String, state: String): String {
                    return authRequest.authorize(state)
                }

                override fun userInfo(provider: String, code: String, state: String): BlogOAuthUser {
                    val authResponse = authRequest.login(
                        AuthCallback.builder()
                            .code(code)
                            .state(state)
                            .build()
                    ) as AuthResponse<AuthUser>
                    if (!authResponse.ok()) {
                        throw AuthException("失败")
                    }

                    val authUser = authResponse.data
                    return BlogOAuthUser().apply {
                        openid = authUser.uuid
                        username = authUser.username
                        avatar = authUser.avatar
                        email = authUser.email
                    }
                }
            })
        }
    }

    override fun iterator(): MutableIterator<BaseOAuth2Provider> {
        return oAuth2Providers.iterator()
    }

    override fun addChain(element: BaseOAuth2Provider): OAuth2ProviderChain {
        oAuth2Providers.add(element)
        return this
    }

    override fun authorize(provider: String, state: String): String {
        for (oAuth2Provider in this.iterator()) {
            if (oAuth2Provider.name != provider) {
                continue
            }
            oAuth2Provider.authorize(provider, state).let {
                return it
            }
        }
        throw AuthException("不支持 $provider")
    }

    override fun userInfo(provider: String, code: String, state: String): BlogOAuthUser {
        for (oAuth2Provider in this.iterator()) {
            if (oAuth2Provider.name != provider) {
                continue
            }
            oAuth2Provider.userInfo(provider, code, state).let {
                it.provider = provider
                return it
            }
        }
        throw AuthException("不支持 $provider")
    }

}