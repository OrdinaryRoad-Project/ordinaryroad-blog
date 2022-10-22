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
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.dto.BlogRoleDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserInfoDTO
import java.util.stream.Collectors
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * Blog
 *
 * @author mjz
 * @date 2022/8/24
 */
@ApplicationScoped
class BlogService {

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var roleService: BlogRoleService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    /**
     * 获取用户信息 username avatar email
     */
    fun userInfo(): BlogUserInfoDTO {
        val userId = StpUtil.getLoginIdAsString()

        val user = userService.findById(userId)

        val roleDtoList = roleService.findAllByUserId(userId)
            .stream()
            .map {
                return@map dtoService.transfer(it, BlogRoleDTO::class.java)
            }
            .collect(Collectors.toList())

        val userDto = dtoService.transfer(user, BlogUserDTO::class.java)

        return BlogUserInfoDTO(userDto, roleDtoList)
    }

    fun currentUser(): BlogUser {
        val userId = StpUtil.getLoginIdAsString()
        return userService.findById(userId)
    }

}