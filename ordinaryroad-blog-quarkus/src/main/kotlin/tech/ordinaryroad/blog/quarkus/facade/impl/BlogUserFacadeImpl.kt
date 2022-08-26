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

package tech.ordinaryroad.blog.quarkus.facade.impl

import tech.ordinaryroad.blog.quarkus.entity.BlogUserRoles
import tech.ordinaryroad.blog.quarkus.exception.BlogUserNotFoundException
import tech.ordinaryroad.blog.quarkus.facade.BlogUserFacade
import tech.ordinaryroad.blog.quarkus.service.BlogUserRolesService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.service.transfer.BlogUserTransferService
import tech.ordinaryroad.blog.quarkus.vo.BlogUserVO
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class BlogUserFacadeImpl : BlogUserFacade {

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var userRolesService: BlogUserRolesService

    @Inject
    protected lateinit var userTransferService: BlogUserTransferService

    override fun findById(id: String): BlogUserVO {
        val blogUser = userService.findById(id)

        if (blogUser == null) {
            throw BlogUserNotFoundException()
        } else {
            return userTransferService.transfer(blogUser)
        }
    }

    override fun updateRoles(id: String, roleIdList: List<String>) {
        val oldRoleIdSet = userRolesService.findAllByUserId(id)
            .stream()
            .map(BlogUserRoles::getRoleId)
            .collect(Collectors.toSet())

        val intersectRoleIdSet = roleIdList.intersect(oldRoleIdSet)

        val roleIdListToAdd = arrayListOf<String>()
        val roleIdListToDelete = arrayListOf<String>()
        oldRoleIdSet.forEach {
            if (!intersectRoleIdSet.contains(it)) {
                roleIdListToDelete.add(it)
            }
        }
        roleIdList.forEach {
            if (!intersectRoleIdSet.contains(it)) {
                roleIdListToAdd.add(it)
            }
        }

        userRolesService.deleteByIdList(roleIdListToDelete)
        roleIdListToAdd.forEach {
            userRolesService.create(BlogUserRoles().apply {
                userId = id
                roleId = it
            })
        }
    }

}