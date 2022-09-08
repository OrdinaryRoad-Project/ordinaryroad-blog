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

import com.baomidou.mybatisplus.core.toolkit.Wrappers
import tech.ordinaryroad.blog.quarkus.dao.BlogOAuthUserDAO
import tech.ordinaryroad.blog.quarkus.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.entity.BlogUserOAuthUsers
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class BlogOAuthUserService : BaseService<BlogOAuthUserDAO, BlogOAuthUser>() {

    @Inject
    protected lateinit var userOAuthUsersService: BlogUserOAuthUsersService

    /**
     * 删除用户关联的OAuth用户和关联关系
     */
    fun removeUserOAuthUser(oAuthUserId: String, userId: String) {
        super.dao.deleteById(oAuthUserId)

        val wrapper = Wrappers.query<BlogUserOAuthUsers>()
        wrapper.eq("user_id", userId)
        wrapper.eq("oauth_user_id", oAuthUserId)
        userOAuthUsersService.dao.delete(wrapper)
    }

    fun findAllByUserId(userId: String): List<BlogOAuthUser> {
        val userOAuthUsers = userOAuthUsersService.findAllByUserId(userId)
        val oAuthUserIds = userOAuthUsers.stream().map(BlogUserOAuthUsers::getOAuthUserId)
            .collect(Collectors.toList())
        return super.findIds(BlogOAuthUser::class.java, oAuthUserIds)
    }

    fun findByOpenidAndProvider(openid: String, provider: String): BlogOAuthUser? {
        val wrapper = Wrappers.query<BlogOAuthUser>()
        wrapper.eq("openid", openid)
        wrapper.eq("provider", provider)
        return super.dao.selectOne(wrapper)
    }

    fun findByUserIdAndProvider(userId: String, provider: String): BlogOAuthUser? {
        return super.dao.selectByUserIdAndProvider(userId, provider)
    }

}
