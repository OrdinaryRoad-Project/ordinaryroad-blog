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

package tech.ordinaryroad.blog.quarkus.config

import cn.dev33.satoken.stp.StpInterface
import io.quarkus.arc.DefaultBean
import io.quarkus.arc.Unremovable
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * StpInterfaceImpl
 *
 * @author nayan
 * @date 2022/4/8 5:54 PM
 */
@DefaultBean
@Unremovable
@ApplicationScoped
class StpInterfaceImpl : StpInterface {

    @Inject
    private lateinit var userService: BlogUserService

    /**
     * 返回一个账号所拥有的权限码集合
     */
    override fun getPermissionList(loginId: Any, loginType: String): List<String> {
        return listOf()
    }

    /**
     * 返回一个账号所拥有的角色标识集合
     */
    override fun getRoleList(loginId: Any, loginType: String): List<String> {
        val user = userService.findById(loginId.toString())
        // TODO 角色列表
        return if (user.id == 1L) {
            listOf("admin", "super-admin")
        } else {
            listOf()
        }
    }
}
