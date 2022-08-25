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

package tech.ordinaryroad.blog.quarkus.service

import io.quarkus.mailer.Mail
import io.quarkus.mailer.Mailer
import tech.ordinaryroad.blog.quarkus.entity.BlogOAuthUser
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Blog消息推送
 *
 * @author mjz
 * @date 2022/8/24
 */
@ApplicationScoped
class BlogPushService {

    @Inject
    protected lateinit var mailer: Mailer

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var oauthUserService: BlogOAuthUserService

    fun comment(title: String, content: String, notifier: String) {
        val allOAuthUser = oauthUserService.findAllByUserId(notifier)
        val emailSet = allOAuthUser.stream().map(BlogOAuthUser::getEmail).collect(Collectors.toSet())
        val mail = Mail().apply {
            subject = title
            text = content
            to = emailSet.toList()
        }
        mailer.send(mail)
    }

}