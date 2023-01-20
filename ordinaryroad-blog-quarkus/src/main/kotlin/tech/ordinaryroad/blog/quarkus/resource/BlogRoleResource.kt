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
import com.baomidou.mybatisplus.core.metadata.IPage
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import io.vertx.core.json.JsonObject
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.constant.SaTokenConstants
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogRole
import tech.ordinaryroad.blog.quarkus.dto.BlogRoleDTO
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogRoleCannotModifyBuiltInException
import tech.ordinaryroad.blog.quarkus.exception.BlogRoleNameOrCodeAlreadyExistException
import tech.ordinaryroad.blog.quarkus.request.BlogRoleQueryRequest
import tech.ordinaryroad.blog.quarkus.request.BlogRoleSaveRequest
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogRoleService
import tech.ordinaryroad.blog.quarkus.service.BlogValidateService
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import java.util.*
import java.util.stream.Collectors
import javax.inject.Inject
import javax.management.relation.RoleNotFoundException
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * 角色Resource
 *
 * @author mjz
 * @date 2022/8/26
 */
@Path("role")
class BlogRoleResource {

    @Inject
    protected lateinit var roleService: BlogRoleService

    @Inject
    protected lateinit var validateService: BlogValidateService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    /**
     * 创建角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    fun create(@Valid @BeanParam request: BlogRoleSaveRequest): BlogRoleDTO {
        /* 唯一性校验 */
        if (roleService.existByRoleNameOrRoleCode(request.roleName, request.roleCode)) {
            BlogRoleNameOrCodeAlreadyExistException().throws()
        }

        val create = roleService.create(
            BlogRole().apply {
                this.roleName = request.roleName
                this.roleCode = request.roleCode
            }
        )

        return dtoService.transfer(create, BlogRoleDTO::class.java)
    }

    /**
     * 更新角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @PUT
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    fun update(@Valid @BeanParam request: BlogRoleSaveRequest): BlogRoleDTO {
        if (request.uuid.isNullOrBlank()) {
            throw BadRequestException()
        }
        val role = validateService.validateRole(request.uuid, true)!!

        /* 内置角色校验 */
        if (validateService.isBuiltInRole(role)) {
            BlogRoleCannotModifyBuiltInException().throws()
        }

        /* 唯一性校验 */
        if (roleService.existByRoleNameOrRoleCode(
                if (role.roleName == request.roleName) null else request.roleName,
                if (role.roleCode == request.roleCode) null else request.roleCode
            )
        ) {
            BlogRoleNameOrCodeAlreadyExistException().throws()
        }

        val update = roleService.update(
            BlogRole().apply {
                this.uuid = role.uuid
                this.roleName = request.roleName
                this.roleCode = request.roleCode
            }
        )

        return dtoService.transfer(update, BlogRoleDTO::class.java)
    }

    /**
     * 启用/禁用角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @PUT
    @Path("{id}/enabled")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    fun updateEnabled(
        @PathParam("id") id: String,
        @RestQuery enabled: Boolean
    ): BlogRoleDTO {
        val role = validateService.validateRole(id, true)!!

        /* 内置角色校验 */
        if (validateService.isBuiltInRole(role)) {
            BlogRoleCannotModifyBuiltInException().throws()
        }

        val update = roleService.update(
            BlogRole().apply {
                this.uuid = role.uuid
                this.enabled = enabled
            }
        )

        return dtoService.transfer(update, BlogRoleDTO::class.java)
    }

    /**
     * 更新角色对应的用户
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @PUT
    @Path("{id}/users")
    @Transactional
    fun updateUsers(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String,
        request: JsonObject
    ) {
        val userUuids = request.getJsonArray("userUuids").list as List<String>
        roleService.updateUsers(id, userUuids)
    }

    /**
     * 分页查询角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun page(@Valid @BeanParam request: BlogRoleQueryRequest): IPage<BlogRoleDTO> {
        val wrapper = ChainWrappers.queryChain(roleService.dao)
            .like(!request.roleName.isNullOrBlank(), "role_name", "%" + request.roleName + "%")
            .like(!request.roleCode.isNullOrBlank(), "role_code", "%" + request.roleCode + "%")

        val page = roleService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage<BlogRole, BlogRoleDTO>(page).apply {
            records = page.records.stream()
                .map { dtoService.transfer(it, BlogRoleDTO::class.java) }
                .collect(Collectors.toList())
        }
        return dtoPage
    }

    /**
     * 查询所有角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    fun findAll(@Valid @BeanParam request: BlogRoleQueryRequest): List<BlogRoleDTO> {
        val wrapper = ChainWrappers.queryChain(roleService.dao)
            .eq("enabled", true)
            .like(!request.roleName.isNullOrBlank(), "role_name", "%" + request.roleName + "%")
            .like(!request.roleCode.isNullOrBlank(), "role_code", "%" + request.roleCode + "%")

        val all = roleService.findAll(request, wrapper)

        return all.stream().map { dtoService.transfer(it, BlogRoleDTO::class.java) }.collect(Collectors.toList())
    }

    /**
     * 根据用户id查询用户的所有角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("user/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun findAllByUserUuid(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ): List<BlogRoleDTO> {
        val all = roleService.findAllByUserId(id)
        return all.stream().map { dtoService.transfer(it, BlogRoleDTO::class.java) }.collect(Collectors.toList())
    }

    /**
     * 根据唯一键查询角色
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER, SaTokenConstants.ROLE_ADMIN, mode = SaMode.OR)
    @GET
    @Path("unique")
    @Produces(MediaType.APPLICATION_JSON)
    fun findByUniqueColumn(@Valid @BeanParam request: BlogRoleQueryRequest): BlogRoleDTO {
        var blogRole: BlogRole? = null
        val roleCode = request.roleCode
        val roleName = request.roleName
        if (!roleCode.isNullOrBlank()) {
            blogRole = roleService.findByRoleCode(roleCode)
        }
        if (blogRole == null && !roleName.isNullOrBlank()) {
            blogRole = roleService.findByRoleName(roleName)
        }
        if (blogRole == null) {
            throw RoleNotFoundException()
        } else {
            return dtoService.transfer(blogRole, BlogRoleDTO::class.java)
        }
    }

}