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
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogRoleDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogRole
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import javax.enterprise.context.ApplicationScoped

/**
 * Service-Role
 *
 * @author mjz
 * @date 2022/8/26
 */
@ApplicationScoped
class BlogRoleService : BaseService<BlogRoleDAO, BlogRole>() {

    override fun getEntityClass(): Class<BlogRole> {
        return BlogRole::class.java
    }

    //region 业务相关
    /**
     * 根据用户Id查询所有角色
     */
    fun findAllByUserId(userId: String): List<BlogRole> {
        return super.dao.selectAllByUserId(userId)
    }
    //endregion

    //region SQL相关
    fun findByRoleCode(roleCode: String): BlogRole? {
        val wrapper = Wrappers.query<BlogRole>()
            .eq("role_code", roleCode)

        return super.dao.selectOne(wrapper)
    }

    /**
     * 根据名称和code查询是否存在
     */
    fun existByRoleNameOrRoleCode(roleName: String? = null, roleCode: String? = null): Boolean {
        if (roleName.isNullOrBlank() && roleCode.isNullOrBlank()) {
            return true
        }
        val wrapper = Wrappers.query<BlogRole>()
            .eq(!roleName.isNullOrBlank(), "role_name", roleName)
            .or()
            .eq(!roleCode.isNullOrBlank(), "role_code", roleCode)
        return super.dao.exists(wrapper)
    }
    //endregion

}