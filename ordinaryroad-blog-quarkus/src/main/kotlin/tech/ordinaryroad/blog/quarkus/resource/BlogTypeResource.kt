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

import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogType
import tech.ordinaryroad.blog.quarkus.dto.BlogTypeDTO
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogUserNotFoundException
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogTypeMapStruct
import tech.ordinaryroad.blog.quarkus.request.BlogTypeQueryRequest
import tech.ordinaryroad.blog.quarkus.request.BlogTypeSaveRequest
import tech.ordinaryroad.blog.quarkus.request.BlogTypeUpdateRequest
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogTypeInfoVO
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogTypeService
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.Max
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * 分类Resource
 *
 * @author mjz
 * @date 2022/8/30
 */
@Path("type")
class BlogTypeResource {

    val blogTypeMapStruct = BlogTypeMapStruct.INSTANCE

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var typeService: BlogTypeService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    private val typeMapStruct = BlogTypeMapStruct.INSTANCE

    /**
     * 获取文章数前N的分类
     */
    @GET
    @Path("top")
    fun getTopN(
        @Valid @Size(max = 32, message = "userId长度不能大于32") @DefaultValue("") @RestQuery userId: String,
        @Valid @Max(value = 50, message = "n不能大于50") @DefaultValue("10") @RestQuery n: Int
    ): List<Map<String, String>> {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        return typeService.dao.getTopNByUserId(n, userId)
    }

    /**
     * 用户创建分类
     */
    @POST
    @Path("create")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun create(@Valid request: BlogTypeSaveRequest): BlogTypeDTO {
        /* 登录校验 */
        StpUtil.checkLogin()

        val blogType = BlogType().apply {
            name = request.name
        }

        val create = typeService.create(blogType)

        return dtoService.transfer(create, BlogTypeDTO::class.java)
    }

    /**
     * 用户删除自己的分类
     */
    @DELETE
    @Path("delete/own/{id}")
    @Transactional
    fun deleteOwn(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        /* 登录校验 */
        StpUtil.checkLogin()

        typeService.deleteOwn(id)
    }

    /**
     * 用户更新自己的分类
     */
    @POST
    @Path("update/own")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun updateOwn(@Valid request: BlogTypeUpdateRequest): BlogTypeDTO {
        /* 登录校验 */
        StpUtil.checkLogin()

        val blogType = BlogType().apply {
            uuid = request.uuid
            name = request.name
        }

        val create = typeService.updateOwn(blogType)

        return dtoService.transfer(create, BlogTypeDTO::class.java)
    }

    /**
     * 用户查询自己的所有分类
     */
    @GET
    @Path("find/all/own")
    @Produces(MediaType.APPLICATION_JSON)
    fun findAllOwn(): List<BlogTypeDTO> {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val list = typeService.findAllByCreateBy(userId)

        val dtoList = list.map {
            return@map dtoService.transfer(it, BlogTypeDTO::class.java)
        }

        return dtoList
    }

    /**
     * 用户分页查询自己的分类
     */
    @GET
    @Path("page/own/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageOwn(@BeanParam request: BlogTypeQueryRequest): Page<BlogTypeDTO> {
        /* 登录校验 */
        StpUtil.checkLogin()

        val userId = StpUtil.getLoginIdAsString()

        val wrapper = ChainWrappers.queryChain(typeService.dao)
            .like(!request.name.isNullOrBlank(), "name", "%" + request.name + "%")
            .eq("create_by", userId)

        val page = typeService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogTypeDTO::class.java)
        }

        return dtoPage
    }

    /**
     * 分页查询分类信息（包含文章个数）
     */
    @GET
    @Path("page/info/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageInfo(@BeanParam request: BlogTypeQueryRequest): Page<BlogTypeInfoVO> {
        /* 登录校验 */
        val wrapper = ChainWrappers.queryChain(typeService.dao)
            .like(!request.name.isNullOrBlank(), "name", "%" + request.name + "%")
            .eq(!request.createBy.isNullOrBlank(), "create_by", request.createBy)

        val page = typeService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            typeMapStruct.transferInfo(item)
        }

        return dtoPage
    }

    /**
     * 获取分类个数
     */
    @GET
    @Path("count")
    fun count(
        @Valid @Size(
            max = 32,
            message = "userId长度不能大于32"
        ) @DefaultValue("") @RestQuery userId: String
    ): Long {
        if (userId.isNotBlank()) {
            if (userService.findById(userId) == null) {
                BlogUserNotFoundException().throws()
            }
        }
        val wrapper = Wrappers.query<BlogType>()
            .eq(userId.isNotBlank(), "create_by", userId)
        return typeService.dao.selectCount(wrapper)
    }

}