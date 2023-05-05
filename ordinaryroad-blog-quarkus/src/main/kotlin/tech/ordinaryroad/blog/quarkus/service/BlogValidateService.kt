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

import cn.dev33.satoken.stp.StpUtil
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogComment
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogFriendLink
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogRole
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.blog.quarkus.enums.BlogBuiltInRoleEnum
import tech.ordinaryroad.blog.quarkus.exception.*
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * 校验服务类
 *
 * @author mjz
 * @date 2022/12/9
 */
@ApplicationScoped
class BlogValidateService {

    @Inject
    protected lateinit var articleService: BlogArticleService

    @Inject
    protected lateinit var roleService: BlogRoleService

    @Inject
    protected lateinit var commentService: BlogCommentService

    @Inject
    protected lateinit var friendLinkService: BlogFriendLinkService

    /**
     * 根据文章的firstId校验文章是否存在已发布的
     */
    fun validatePublishByArticleFirstId(articleFirstId: String?): BlogArticle {
        if (articleFirstId.isNullOrBlank()) {
            BlogArticleNotValidException().throws()
        }
        return articleService.findFirstOrLastByFirstIdAndStatus(articleFirstId!!, BlogArticleStatus.PUBLISH, false)
            ?: throw BlogArticleNotFoundException()
    }

    /**
     * 校验文章的最初版本
     */
    fun validateFirstVersionArticle(articleId: String?): BlogArticle {
        if (articleId.isNullOrBlank()) {
            BlogArticleNotValidException().throws()
        }
        return articleService.getFirstById(articleId!!) ?: throw BlogArticleNotFoundException()
    }

    /**
     * 校验文章是否属于自己
     *
     * @param id 文章Id
     * @return BlogArticle 文章
     */
    fun validateOwnArticle(id: String): BlogArticle {
        val userId = StpUtil.getLoginIdAsString()
        if (id.isBlank()) {
            BlogArticleNotFoundException().throws()
        }
        val blogArticle = articleService.findById(id) ?: throw BlogArticleNotFoundException()
        if (blogArticle.createBy != userId) {
            BlogArticleNotValidException().throws()
        }
        return blogArticle
    }

    /**
     * 校验评论是否属于自己
     *
     * @param id 评论Id
     * @return BlogComment 评论
     */
    fun validateOwnComment(id: String?): BlogComment {
        if (id.isNullOrBlank()) {
            throw BlogCommentNotFoundException()
        }

        val blogComment = commentService.findById(id) ?: throw BlogCommentNotFoundException()
        if (blogComment.createBy != StpUtil.getLoginIdAsString()) {
            BlogCommentNotValidException().throws()
        }
        return blogComment
    }

    /**
     * 校验评论是否存在
     */
    fun validateComment(id: String?, must: Boolean = false): BlogComment? {
        if (id.isNullOrBlank()) {
            if (must) {
                BlogCommentNotValidException().throws()
            } else {
                return null
            }
        }
        return commentService.findById(id) ?: throw BlogCommentNotFoundException()
    }

    /**
     * 校验角色是否存在
     */
    fun validateRole(id: String?, must: Boolean = false): BlogRole? {
        if (id.isNullOrBlank()) {
            if (must) {
                BlogRoleNotValidException().throws()
            } else {
                return null
            }
        }
        return roleService.findById(id) ?: throw BlogRoleNotFoundException()
    }

    fun isBuiltInRole(role: BlogRole): Boolean {
        return BlogBuiltInRoleEnum.getByRoleCode(role.roleCode) != null
    }

    /**
     * 校验友链是否存在
     */
    fun validateFriendLink(id: String?, must: Boolean): BlogFriendLink? {
        if (id.isNullOrBlank()) {
            if (must) {
                BlogFriendLinkNotValidException().throws()
            } else {
                return null
            }
        }
        return friendLinkService.findById(id) ?: throw BlogFriendLinkNotFoundException()
    }

}