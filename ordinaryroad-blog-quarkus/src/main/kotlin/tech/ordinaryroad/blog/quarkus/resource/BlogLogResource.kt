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

package tech.ordinaryroad.blog.quarkus.resource

import cn.dev33.satoken.annotation.SaCheckRole
import cn.dev33.satoken.annotation.SaMode
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import org.jboss.resteasy.reactive.RestPath
import tech.ordinaryroad.blog.quarkus.constant.SaTokenConstants
import tech.ordinaryroad.blog.quarkus.dto.BlogLogDTO
import tech.ordinaryroad.blog.quarkus.enums.BlogLogTypeEnum
import tech.ordinaryroad.blog.quarkus.request.BlogLogQueryRequest
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogLogTypeEnumVO
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogLogService
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType


@Path("log")
class BlogLogResource {

    @Inject
    protected lateinit var logService: BlogLogService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    /**
     * 管理员分页查询日志
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun page(@BeanParam request: BlogLogQueryRequest): Page<BlogLogDTO> {
        val wrapper = ChainWrappers.queryChain(logService.dao)
            .eq(!request.type.isNullOrBlank(), "type", request.type)
            .eq(request.type.isNullOrBlank() && !request.method.isNullOrBlank(), "method", request.method)
            .eq(!request.status.isNullOrBlank(), "status", request.status)

        val page = logService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogLogDTO::class.java)
        }

        return dtoPage
    }

    /**
     * 管理员删除日志
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @DELETE
    @Path("delete/{id}")
    @Transactional
    fun delete(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        logService.delete(id)
    }

    /**
     * 查询所有类型
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("all/types")
    fun findAllTypes(): List<BlogLogTypeEnumVO> {
        val list = ArrayList<BlogLogTypeEnumVO>()

        val enums = BlogLogTypeEnum.values()

        enums.forEach {
            val logTypeEnumVO = BlogLogTypeEnumVO()
            logTypeEnumVO.description = it.description
            logTypeEnumVO.code = it.code
            logTypeEnumVO.method = it.method
            list.add(logTypeEnumVO)
        }

        return list
    }

    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("all/status")
    fun findAllStatus(): List<String> {
        return logService.dao.selectDistinctStatus()
    }

}
