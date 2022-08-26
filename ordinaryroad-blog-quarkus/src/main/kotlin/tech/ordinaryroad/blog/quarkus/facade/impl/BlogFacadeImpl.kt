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

import cn.dev33.satoken.stp.StpUtil
import tech.ordinaryroad.blog.quarkus.dto.BlogUserDTO
import tech.ordinaryroad.blog.quarkus.dto.BlogUserInfoDTO
import tech.ordinaryroad.blog.quarkus.facade.BlogFacade
import tech.ordinaryroad.blog.quarkus.facade.BlogRoleFacade
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.service.transfer.BlogDtoService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 *
 *
 * @author mjz
 * @date 2022/8/26
 */
@ApplicationScoped
class BlogFacadeImpl : BlogFacade {

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var roleFacade: BlogRoleFacade

    @Inject
    protected lateinit var dtoService: BlogDtoService

    override fun userInfo(): BlogUserInfoDTO {
        val userId = StpUtil.getLoginIdAsString()

        val user = userService.findById(userId)

        val roleDtoList = roleFacade.findAllByUserId(userId)

        val userDto = dtoService.transfer(user, BlogUserDTO::class.java)

        return BlogUserInfoDTO(userDto, roleDtoList)
    }

}