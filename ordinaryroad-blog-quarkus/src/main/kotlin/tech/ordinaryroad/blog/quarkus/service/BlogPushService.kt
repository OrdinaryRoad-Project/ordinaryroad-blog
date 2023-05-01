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

import io.quarkus.mailer.MailTemplate.MailTemplateInstance
import io.quarkus.qute.CheckedTemplate
import org.eclipse.microprofile.config.ConfigProvider
import tech.ordinaryroad.blog.quarkus.dal.entity.*
import java.time.Duration
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

    /**
     * https://www.likecs.com/ask-195922.html
     *  @JvmStatic
     */
    @CheckedTemplate
    internal object Templates {

        @JvmStatic
        external fun articleStatusChanged(
            title: String?,
            articleHref: String?,
            articleTitle: String?,
            actionString: String?
        ): MailTemplateInstance

        @JvmStatic
        external fun newComment(
            title: String?,
            fromUserId: String?,
            fromUsername: String?,
            actionString: String?,
            content: String?,
            toUserId: String?,
            articleId: String?,
            articleTitle: String?
        ): MailTemplateInstance

        @JvmStatic
        external fun userLikesArticle(
            title: String?,
            fromUserId: String?,
            fromUsername: String?,
            actionString: String?,
            content: String?,
            toUserId: String?,
            articleId: String?,
            articleTitle: String?
        ): MailTemplateInstance

        @JvmStatic
        external fun applyForFriendLink(title: String?, friendLink: BlogFriendLink?): MailTemplateInstance

        @JvmStatic
        external fun applyForFriendLinkApproved(title: String?, friendLink: BlogFriendLink?): MailTemplateInstance

        @JvmStatic
        external fun applyForFriendLinkDisapproved(
            title: String?,
            reason: String?,
            friendLink: BlogFriendLink?
        ): MailTemplateInstance
    }

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var articleService: BlogArticleService

    fun comment(
        fromUser: BlogUser,
        content: String,
        article: BlogArticle,
        parentComment: BlogComment?
    ) {
        val toUser: BlogUser
        val title: String
        val actionString: String
        if (parentComment == null) {
            title = "新评论提醒"
            actionString = "对我的文章发表了评论"
            toUser = userService.findById(article.createBy)
        } else {
            title = "新回复提醒"
            actionString = "回复了我的评论"
            toUser = userService.findById(parentComment.createBy)
        }

        if (fromUser.uuid == toUser.uuid) {
            // 跳过本人操作
            return
        }

        if (toUser.email.isNullOrBlank()) {
            return
        }

        Templates.newComment(
            title,
            fromUser.username,
            fromUser.uid,
            actionString,
            content,
            article.firstId,
            article.title,
            toUser.uid
        )
            .to(toUser.email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    /**
     * 点赞通知
     */
    fun likesArticle(
        fromUser: BlogUser,
        blogUserLikedArticle: BlogUserLikedArticle,
    ) {
        val article = articleService.findById(blogUserLikedArticle.articleId)
        val toUser = userService.findById(article.createBy)

        val title = "文章被点赞提醒"
        val actionString = "点赞了我的文章"
        val content = article.title

        if (fromUser.uuid == toUser.uuid) {
            // 跳过本人操作
            return
        }

        if (toUser.email.isNullOrBlank()) {
            return
        }

        Templates.userLikesArticle(
            title,
            fromUser.username,
            fromUser.uid,
            actionString,
            content,
            article.firstId,
            article.title,
            toUser.uid
        )
            .to(toUser.email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    /**
     * 文章开始审核通知
     */
    fun startAuditingArticle(article: BlogArticle) {
        // val userId = StpUtil.getLoginIdAsString()
        // val fromUser = userService.findById(userId)
        val toUser = userService.findById(article.createBy)

        val title = "文章开始审核通知"

        Templates.articleStatusChanged(
            title,
            "https://blog.ordinaryroad.tech/dashboard/article/auditing/${article.uuid}",
            article.title,
            "已经开始审核"
        )
            .to(toUser.email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    /**
     * 文章审核通过通知
     */
    fun articleAuditApproved(article: BlogArticle) {
        // val userId = StpUtil.getLoginIdAsString()
        // val fromUser = userService.findById(userId)
        val toUser = userService.findById(article.createBy)

        val title = "文章审核通过通知"

        Templates.articleStatusChanged(
            title,
            "https://blog.ordinaryroad.tech/${toUser.uid}/article/${article.uuid}",
            article.title,
            "审核通过 已经开放浏览"
        )
            .to(toUser.email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    /**
     * 文章审核失败通知
     */
    fun articleAuditFailed(article: BlogArticle, reason: String) {
        // val userId = StpUtil.getLoginIdAsString()
        // val fromUser = userService.findById(userId)
        val toUser = userService.findById(article.createBy)

        val title = "文章审核失败通知"

        Templates.articleStatusChanged(
            title,
            "https://blog.ordinaryroad.tech/dashboard/article/writing/${article.uuid}",
            article.title,
            "审核失败 原因：$reason"
        )
            .to(toUser.email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    /**
     * 文章违规通知
     */
    fun articleViolation(article: BlogArticle, reason: String) {
        // val userId = StpUtil.getLoginIdAsString()
        // val fromUser = userService.findById(userId)
        val toUser = userService.findById(article.createBy)

        val title = "文章违规通知"

        Templates.articleStatusChanged(
            title,
            "https://blog.ordinaryroad.tech/dashboard/article/status/OFFEND",
            article.title,
            "违规 原因：$reason"
        )
            .to(toUser.email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    /**
     * 通知开发者有新的友链申请
     */
    fun applyForFriendLink(friendLink: BlogFriendLink) {
        val title = "友链申请通知"
        Templates.applyForFriendLink(title, friendLink)
            .to(ConfigProvider.getConfig().getValue("quarkus.mailer.from", String::class.java))
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    fun friendLinkApproved(friendLink: BlogFriendLink) {
        val email = friendLink.email
        if (email.isNullOrBlank()) {
            return
        }
        val title = "友链申请结果通知"
        Templates.applyForFriendLinkApproved(title, friendLink)
            .to(email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }

    fun friendLinkDisapproved(reason: String, friendLink: BlogFriendLink) {
        val email = friendLink.email
        if (email.isNullOrBlank()) {
            return
        }
        val title = "友链申请结果通知"
        Templates.applyForFriendLinkDisapproved(title, reason, friendLink)
            .to(email)
            .subject(title)
            .send()
            .await()
            .atMost(Duration.ofMinutes(1))
    }
}