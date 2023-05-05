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
    BLOG_USER_DISABLE("用户封禁", 3002, "/user/disable", "GET"),
    BLOG_USER_DISABLE_UNTIE("用户解封", 3003, "/user/disable/untie", "GET"),
    BLOG_USER_UPDATE_ROLES("更新用户拥有的角色", 3004, "/user/*/roles", "PUT"),
    //endregion

    //region BlogRoleResource
    BLOG_ROLE_CREATE("创建角色", 4000, "/role/create", "POST"),
    BLOG_ROLE_UPDATE("更新角色", 4001, "/role/*", "PUT"),
    BLOG_ROLE_UPDATE_ENABLED("启用/禁用角色", 4002, "/role/*/enabled", "PUT"),
    BLOG_ROLE_UPDATE_USERS("更新角色对应的用户", 4003, "/role/*/users", "PUT"),
    //endregion

    //region BlogOAuthUserResource
    BLOG_OAUTH_USER_DELETE("用户解绑第三方账号", 5000, "/oauth_user", "DELETE"),
    //endregion

    //region BlogArticleResource
    BLOG_ARTICLE_PUBLISH("用户发布文章", 10000, "/article/publish", "POST"),
    BLOG_ARTICLE_SAVE_DRAFT("用户手动保存草稿", 10001, "/article/draft", "POST"),
    BLOG_ARTICLE_MOVE_TO_TRASH("用户将自己的文章移动至废纸篓", 10002, "/article/move_to_trash/*", "POST"),
    BLOG_ARTICLE_RECOVER_FROM_TRASH("用户从废纸篓恢复自己的文章", 10003, "/article/recover_from_trash/*", "POST"),
    BLOG_ARTICLE_FIND_PUBLISH("用户浏览文章", 10004, "/article/publish/*", "GET"),
    BLOG_ARTICLE_ARTICLE_APPEAL("用户申诉自己的文章", 10005, "/article/article_appeal/*", "POST"),

    BLOG_ARTICLE_LIKES("用户点赞文章", 11000, "/article/likes/*", "POST"),
    BLOG_ARTICLE_UNLIKES("用户取消点赞", 11001, "/article/unlikes/*", "POST"),
    BLOG_ARTICLE_UN_BROWSES("用户删除浏览记录", 11003, "/article/un_browses/*", "POST"),

    BLOG_ARTICLE_START_AUDITING("审核员开始审核文章", 12000, "/article/start_auditing/*", "POST"),
    BLOG_ARTICLE_AUDIT_APPROVED("审核员文章审核通过", 12001, "/article/audit_approved/*", "POST"),
    BLOG_ARTICLE_AUDIT_FAILED("审核员文章审核失败", 12002, "/article/audit_failed/*", "POST"),
    BLOG_ARTICLE_ARTICLE_VIOLATION("审核员标记文章违规", 12003, "/article/article_violation/*", "POST"),
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
    BLOG_COMMENT_DELETE("用户删除自己数据权限内的评论", 40001, "/comment/delete/*", "DELETE"),
    //endregion

    //region BlogFriendLinkResource
    BLOG_FRIEND_LINK_CREATE("创建友链", 50000, "/friend_link/create", "POST"),
    BLOG_FRIEND_LINK_DELETE("删除友链", 50001, "/friend_link/delete/*", "DELETE"),
    BLOG_FRIEND_LINK_UPDATE("更新友链", 50002, "/friend_link/update/*", "POST"),
    BLOG_FRIEND_LINK_APPLY("申请友链", 50003, "/friend_link/apply", "POST"),
    BLOG_FRIEND_LINK_APPROVED("通过友链申请", 50004, "/friend_link/approved/*", "PUT"),
    BLOG_FRIEND_LINK_DISAPPROVED("拒绝友链申请", 50005, "/friend_link/disapproved/*", "PUT"),
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
            BlogLogTypeEnum.values().forEach {
                put(it.pathPattern, it)
            }
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

            val matcher = AntPathMatcher()
            println(matcher.match("/user/*/roles*", "/user/123123123/roles?1=1"))

        }
    }

}