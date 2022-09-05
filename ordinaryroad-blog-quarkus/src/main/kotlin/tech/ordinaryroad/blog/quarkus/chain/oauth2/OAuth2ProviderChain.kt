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
import io.quarkus.runtime.StartupEvent
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.BaseOAuth2Provider
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.GitHubOAuth2Provider
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.GiteeOAuth2Provider
import tech.ordinaryroad.blog.quarkus.chain.oauth2.provider.OrdinaryRoadOAuth2Provider
import tech.ordinaryroad.blog.quarkus.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.request.OAuth2CallbackRequest
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
    protected lateinit var ordinaryRoadOAuth2Provider: OrdinaryRoadOAuth2Provider

    @Inject
    protected lateinit var githubOAuth2Provider: GitHubOAuth2Provider

    @Inject
    protected lateinit var giteeOAuthProvider: GiteeOAuth2Provider

    private val oAuth2Providers = LinkedList<BaseOAuth2Provider>()

    fun onStart(@Observes ev: StartupEvent) {
        this.addChain(ordinaryRoadOAuth2Provider)
            .addChain(githubOAuth2Provider)
            .addChain(giteeOAuthProvider)
    }

    override fun iterator(): MutableIterator<BaseOAuth2Provider> {
        return oAuth2Providers.iterator();
    }

    override fun addChain(element: BaseOAuth2Provider?): OAuth2ProviderChain {
        element?.let {
            oAuth2Providers.add(it)
        }
        return this
    }

    override fun userInfo(request: OAuth2CallbackRequest): BlogOAuthUser? {
        val provider = request.provider
        var blogOAuthUser: BlogOAuthUser? = null
        for (oAuth2Provider in oAuth2Providers) {
            if (oAuth2Provider.name != provider) {
                continue
            }
            blogOAuthUser = oAuth2Provider.userInfo(request)
            if (blogOAuthUser != null) {
                blogOAuthUser.provider = provider
                break
            }
        }
        return blogOAuthUser
    }

}