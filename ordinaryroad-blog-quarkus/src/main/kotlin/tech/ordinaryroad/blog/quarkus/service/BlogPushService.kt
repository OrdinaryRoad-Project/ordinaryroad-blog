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
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogComment
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUserLikedArticle
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
    protected lateinit var articleService: BlogArticleService

    fun comment(
        fromUser: BlogUser,
        content: String,
        article: BlogArticle,
        parentComment: BlogComment?
    ) {
        val toUser: BlogUser
        val title: String
        val notifier: String
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
        val emailSet = setOf(toUser.email)

        var contentTemplate = NEW_COMMENT_TEMPLATE
        contentTemplate = contentTemplate.replace("{title}", title)
        contentTemplate = contentTemplate.replace("{fromUsername}", fromUser.username)
        contentTemplate = contentTemplate.replace("{fromUserId}", fromUser.uuid)
        contentTemplate = contentTemplate.replace("{actionString}", actionString)
        contentTemplate = contentTemplate.replace("{content}", content)
        contentTemplate = contentTemplate.replace("{articleId}", article.uuid)
        contentTemplate = contentTemplate.replace("{articleTitle}", article.title)
        contentTemplate = contentTemplate.replace("{toUserId}", toUser.uuid)

        mailer.send(
            Mail().apply {
                to = emailSet.toList()
                subject = title
                html = contentTemplate
            }
        )
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

        var contentTemplate = USER_LIKES_ARTICLE_TEMPLATE
        contentTemplate = contentTemplate.replace("{title}", title)
        contentTemplate = contentTemplate.replace("{fromUsername}", fromUser.username)
        contentTemplate = contentTemplate.replace("{fromUserId}", fromUser.uuid)
        contentTemplate = contentTemplate.replace("{actionString}", actionString)
        contentTemplate = contentTemplate.replace("{content}", content)
        contentTemplate = contentTemplate.replace("{articleId}", article.uuid)
        contentTemplate = contentTemplate.replace("{articleTitle}", article.title)
        contentTemplate = contentTemplate.replace("{toUserId}", toUser.uuid)

        if (fromUser.uuid == toUser.uuid) {
            // 跳过本人操作
            return
        }

        if (toUser.email.isNullOrBlank()) {
            return
        }
        val emailSet = setOf(toUser.email)

        mailer.send(
            Mail().apply {
                to = emailSet.toList()
                subject = title
                html = contentTemplate
            }
        )
    }

    /**
     * src/main/resources/templates/new-comment.html
     */
    private final val NEW_COMMENT_TEMPLATE = "<!DOCTYPE html>\n" +
            "<html lang=\"zh\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>{title}</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<a href=\"https://blog.ordinaryroad.tech/{fromUserId}\">{fromUsername}</a>{actionString}\n" +
            "<div>\n" +
            "    {content}\n" +
            "</div>\n" +
            "<div>\n" +
            "    <a href=\"https://blog.ordinaryroad.tech/{toUserId}/article/{articleId}#comments\">{articleTitle}</a>\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "</html>"


    /**
     * src/main/resources/templates/user-likes-article.html
     */
    private final val USER_LIKES_ARTICLE_TEMPLATE = "<!DOCTYPE html>\n" +
            "<html lang=\"zh\">\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>{title}</title>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<a href=\"https://blog.ordinaryroad.tech/{fromUserId}\">{fromUsername}</a>{actionString}\n" +
            "<div>\n" +
            "    {content}\n" +
            "</div>\n" +
            "<div>\n" +
            "    <a href=\"https://blog.ordinaryroad.tech/{toUserId}/article/{articleId}\">{articleTitle}</a>\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "</html>"
}