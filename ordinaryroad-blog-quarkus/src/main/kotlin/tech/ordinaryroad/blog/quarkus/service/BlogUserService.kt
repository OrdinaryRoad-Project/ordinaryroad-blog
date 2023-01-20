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
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogUserDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogOAuthUser
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUserOAuthUsers
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUserRoles
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogRoleNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogRoleNotValidException
import tech.ordinaryroad.blog.quarkus.util.BlogUtils.differ
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class BlogUserService : BaseService<BlogUserDAO, BlogUser>() {

    override fun getEntityClass(): Class<BlogUser> {
        return BlogUser::class.java
    }

    @Inject
    protected lateinit var userOAuthUsersService: BlogUserOAuthUsersService

    @Inject
    protected lateinit var userRolesService: BlogUserRolesService

    @Inject
    protected lateinit var roleService: BlogRoleService

    //region 业务相关
    fun updateRoles(id: String, roleIdList: List<String>) {
        val oldRoleIdList = userRolesService.findAllByUserId(id)
            .stream()
            .map(BlogUserRoles::getRoleId)
            .collect(Collectors.toList())

        val lists = oldRoleIdList.differ(roleIdList)

        val roleIdListToDelete = lists[0]
        val roleIdListToAdd = lists[1]

        userRolesService.deleteByRoleIdsAndUserId(roleIdListToDelete, id)
        roleIdListToAdd.forEach {
            val role = roleService.findById(it)
            if (role == null) {
                BlogRoleNotFoundException().throws()
            }
            if (!role.enabled) {
                BlogRoleNotValidException().throws()
            }
            userRolesService.create(BlogUserRoles().apply {
                userId = id
                roleId = it
            })
        }
    }
    //endregion


    //region SQL相关
    /**
     * 创建主账号，并关联OAuth用户
     */
    fun create(user: BlogUser, oAuthUser: BlogOAuthUser): BlogUser {
        val create = super.create(user)
        userOAuthUsersService.create(
            BlogUserOAuthUsers().apply {
                userId = create.uuid
                oAuthUserId = oAuthUser.uuid
            })
        return create
    }

    /**
     * 根据OAuthUser Id查询主账号BlogUser
     */
    fun findByOAuthUserId(oAuthUserId: String): BlogUser? {
        val userId = userOAuthUsersService.findUserIdByOAuthUserId(oAuthUserId)
        userId == null && return null
        return super.findById(userId)
    }

    /**
     * 根据uid查询用户
     */
    fun findByUid(uid: Long): BlogUser? {
        val wrapper = Wrappers.query<BlogUser>()
        wrapper.eq("uid", uid)
        return super.dao.selectOne(wrapper)
    }

    /**
     * 查询所有拥有某个角色的用户
     */
    fun findAllByRoleUuid(id: String): List<BlogUser> {
        return super.dao.selectAllByRoleUuid(id)
    }
    //endregion

}
