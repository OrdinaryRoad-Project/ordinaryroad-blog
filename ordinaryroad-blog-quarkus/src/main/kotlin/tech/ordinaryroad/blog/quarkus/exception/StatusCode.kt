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

package tech.ordinaryroad.blog.quarkus.exception

import tech.ordinaryroad.commons.base.cons.IStatusCode

enum class StatusCode(
    private val code: Int,
    private val message: String
) : IStatusCode {
    BLOG_ARTICLE_NOT_FOUND(404, "文章不存在"),
    BLOG_ARTICLE_NOT_VALID(400, "文章无效"),
    BLOG_ARTICLE_CANNOT_COMMENT(400, "文章不允许评论"),
    BLOG_ARTICLE_RECOVER_FROM_TRASH_CONFLICT(400, "本地存在未发布草稿"),
    BLOG_COMMENT_NOT_FOUND(404, "评论不存在"),
    BLOG_COMMENT_NOT_VALID(400, "评论无效"),
    BLOG_USER_NOT_FOUND(404, "用户不存在"),
    BLOG_TYPE_NOT_FOUND(404, "分类不存在"),
    BLOG_TYPE_NOT_VALID(400, "分类无效"),
    BLOG_TYPE_NAME_ALREADY_EXIST(400, "分类名已存在"),
    BLOG_TAG_NOT_FOUND(404, "标签不存在"),
    BLOG_TAG_NOT_VALID(400, "标签无效"),
    BLOG_TAG_NAME_ALREADY_EXIST(400, "标签名已存在"),
    BLOG_ROLE_NOT_FOUND(404, "角色不存在"),
    BLOG_ROLE_NOT_VALID(400, "角色无效"),
    ;

    override fun getCode(): Int {
        return this.code
    }

    override fun getMessage(): String {
        return this.message
    }
}