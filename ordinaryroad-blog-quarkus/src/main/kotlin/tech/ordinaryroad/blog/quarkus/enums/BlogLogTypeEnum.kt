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

package tech.ordinaryroad.blog.quarkus.enums

import cn.hutool.core.text.AntPathMatcher
import cn.hutool.json.JSONObject
import com.baomidou.mybatisplus.annotation.IEnum

/**
 *
 *
 * @author mjz
 * @date 2022/9/10
 */
enum class BlogLogTypeEnum(
    var description: String,
    var code: Int,
    var pathPattern: String,
    var method: String = "POST"
) : IEnum<Int> {

    //region BlogOAuth2Resource
    OAUTH2_AUTHORIZE("获取授权链接", 1000, "/oauth2/authorize", "GET"),
    OAUTH2_CALLBACK("授权成功回调", 1001, "/oauth2/callback/*", "GET"),
    //endregion

    //region BlogResource
    BLOG_LOGOUT("登出", 2001, "/common/logout", "GET"),
    BLOG_UPLOAD("用户上传文件", 2002, "/common/upload", "POST"),
    //endregion

    //region BlogUserResource
    BLOG_USER_UPDATE_AVATAR("用户更新头像", 3000, "/user/avatar", "PUT"),
    BLOG_USER_UPDATE_USERNAME("用户更新用户名", 3001, "/user/username", "PUT"),
    //endregion

    //region BlogRoleResource
    BLOG_ROLE_UPDATE_AVATAR("用户更新头像", 4000, "/user/avatar", "PUT"),
    //endregion

    //region BlogOAuthUserResource
    BLOG_OAUTH_USER_DELETE("用户解绑第三方账号", 5000, "/oauth_user", "DELETE"),
    //endregion

    //region BlogArticleResource
    BLOG_ARTICLE_PUBLISH("用户发布文章", 10000, "/article/publish", "POST"),
    BLOG_ARTICLE_SAVE_DRAFT("用户手动保存草稿", 10001, "/article/draft", "POST"),
    BLOG_ARTICLE_MOVE_TO_TRASH("用户移动至废纸篓", 10002, "/article/move_to_trash/*", "POST"),
    BLOG_ARTICLE_RECOVER_FROM_TRASH("用户从废纸篓恢复", 10003, "/article/recover_from_trash/*", "POST"),
    //endregion

    //region BlogTypeResource
    BLOG_TYPE_CREATE("用户创建分类", 20000, "/type/create", "POST"),
    BLOG_TYPE_DELETE("用户删除自己的分类", 20001, "/type/delete/own/*", "DELETE"),
    BLOG_TYPE_UPDATE("用户更新自己的分类", 20002, "/type/update/own"),
    //endregion

    //region BlogTagResource
    BLOG_TAG_CREATE("用户创建标签", 30000, "/tag/create"),
    //endregion

    //region BlogCommentResource
    BLOG_COMMENT_POST("用户发布评论", 40000, "/comment/post"),
    //endregion
    ;

    override fun getValue(): Int {
        return this.code
    }

    override fun toString(): String {
        return JSONObject().apply {
            set("description", description)
            set("code", code)
            set("pathPattern", pathPattern)
            set("method", method)
        }.toString()
    }


    companion object {
        private val antPathMatcher = AntPathMatcher()

        private val blogLogTypeEnumMap = HashMap<String, BlogLogTypeEnum>().apply {
            put(OAUTH2_AUTHORIZE.pathPattern, OAUTH2_AUTHORIZE)
            put(OAUTH2_CALLBACK.pathPattern, OAUTH2_CALLBACK)
            put(BLOG_LOGOUT.pathPattern, BLOG_LOGOUT)
            put(BLOG_UPLOAD.pathPattern, BLOG_UPLOAD)
            put(BLOG_USER_UPDATE_AVATAR.pathPattern, BLOG_USER_UPDATE_AVATAR)
            put(BLOG_USER_UPDATE_USERNAME.pathPattern, BLOG_USER_UPDATE_USERNAME)
            put(BLOG_ROLE_UPDATE_AVATAR.pathPattern, BLOG_ROLE_UPDATE_AVATAR)
            put(BLOG_OAUTH_USER_DELETE.pathPattern, BLOG_OAUTH_USER_DELETE)
            put(BLOG_ARTICLE_PUBLISH.pathPattern, BLOG_ARTICLE_PUBLISH)
            put(BLOG_ARTICLE_SAVE_DRAFT.pathPattern, BLOG_ARTICLE_SAVE_DRAFT)
            put(BLOG_ARTICLE_MOVE_TO_TRASH.pathPattern, BLOG_ARTICLE_MOVE_TO_TRASH)
            put(BLOG_ARTICLE_RECOVER_FROM_TRASH.pathPattern, BLOG_ARTICLE_RECOVER_FROM_TRASH)
            put(BLOG_TYPE_CREATE.pathPattern, BLOG_TYPE_CREATE)
            put(BLOG_TYPE_DELETE.pathPattern, BLOG_TYPE_DELETE)
            put(BLOG_TYPE_UPDATE.pathPattern, BLOG_TYPE_UPDATE)
            put(BLOG_TAG_CREATE.pathPattern, BLOG_TAG_CREATE)
            put(BLOG_COMMENT_POST.pathPattern, BLOG_COMMENT_POST)
        }

        fun getAllTypes(): Collection<BlogLogTypeEnum> {
            return blogLogTypeEnumMap.values
        }

        fun get(path: String, method: String): BlogLogTypeEnum? {
            var blogLogTypeEnum: BlogLogTypeEnum? = null

            if (blogLogTypeEnumMap.containsKey(path)) {
                blogLogTypeEnumMap[path]?.let {
                    if (it.method.equals(method, true)) {
                        blogLogTypeEnum = it
                    }
                }
            } else {
                blogLogTypeEnumMap.forEach { (pathPattern, value) ->
                    if (blogLogTypeEnum == null) {
                        if (value.method.equals(method, true) && antPathMatcher.match(pathPattern, path)) {
                            blogLogTypeEnum = value
                        }
                    }
                }
            }
            return blogLogTypeEnum
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val get = get("/oauth2/callback/ordinaryroad", "GET")
            println(get)
        }
    }

}