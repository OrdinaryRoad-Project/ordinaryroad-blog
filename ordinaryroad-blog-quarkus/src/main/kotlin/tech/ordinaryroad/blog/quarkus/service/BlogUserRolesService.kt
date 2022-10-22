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
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogUserRolesDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUserRoles
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class BlogUserRolesService : BaseService<BlogUserRolesDAO, BlogUserRoles>() {

    override fun getEntityClass(): Class<BlogUserRoles> {
        return BlogUserRoles::class.java
    }

    /**
     * 根据用户Id查询所有关联关系
     */
    fun findAllByUserId(userId: String): List<BlogUserRoles> {
        val wrapper = Wrappers.query<BlogUserRoles>()
        wrapper.eq("user_id", userId)
        return super.dao.selectList(wrapper)
    }

    /**
     * 根据角色Id和用户Id查询关联关系
     */
    fun findByRoleIdAndUserId(roleId: String, userId: String): BlogUserRoles? {
        val wrapper = Wrappers.query<BlogUserRoles>()
        wrapper.eq("role_id", roleId)
        wrapper.eq("user_id", userId)
        return super.dao.selectOne(wrapper)
    }

    /**
     * 根据角色Id和用户Id删除关联关系
     */
    fun deleteByRoleIdsAndUserId(roleIds: List<String>, userId: String): Int {
        if (roleIds.isEmpty()) {
            return 0
        }
        val wrapper = Wrappers.query<BlogUserRoles>()
        wrapper.`in`("role_id", roleIds)
        wrapper.eq("user_id", userId)
        return super.dao.delete(wrapper)
    }
}
