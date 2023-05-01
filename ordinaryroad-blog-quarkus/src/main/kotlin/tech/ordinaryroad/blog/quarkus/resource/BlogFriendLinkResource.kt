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
import cn.hutool.core.bean.BeanUtil
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import org.jboss.resteasy.reactive.RestPath
import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.blog.quarkus.constant.SaTokenConstants
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogFriendLink
import tech.ordinaryroad.blog.quarkus.dto.BlogFriendLinkDTO
import tech.ordinaryroad.blog.quarkus.enums.BlogFriendLinkStatusEnum
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogFriendLinkNotValidException
import tech.ordinaryroad.blog.quarkus.exception.BlogFriendLinkUrlAlreadyExistException
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogFriendLinkMapStruct
import tech.ordinaryroad.blog.quarkus.request.BlogFriendLinkQueryRequest
import tech.ordinaryroad.blog.quarkus.request.BlogFriendLinkSaveRequest
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogFriendLinkVO
import tech.ordinaryroad.blog.quarkus.service.*
import tech.ordinaryroad.commons.core.quarkus.base.request.query.BaseQueryRequest
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * 友链Resource
 *
 * @author mjz
 * @date 2023/2/25
 */
@Path("friend_link")
class BlogFriendLinkResource {

    private val friendLinkMapStruct = BlogFriendLinkMapStruct.INSTANCE

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var friendLinkService: BlogFriendLinkService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    @Inject
    protected lateinit var pushService: BlogPushService

    @Inject
    protected lateinit var validateService: BlogValidateService

    /**
     * 开发者创建友链
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @POST
    @Path("create")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun create(@Valid request: BlogFriendLinkSaveRequest): BlogFriendLinkDTO {
        val url = request.url

        /* 唯一性校验 */
        if (friendLinkService.existByUrl(url)) {
            BlogFriendLinkUrlAlreadyExistException().throws()
        }

        val blogFriendLink = BlogFriendLink().apply {
            BeanUtil.copyProperties(request, this)
            status = BlogFriendLinkStatusEnum.APPROVED
        }

        val create = friendLinkService.create(blogFriendLink)

        return dtoService.transfer(create, BlogFriendLinkDTO::class.java)
    }

    /**
     * 开发者删除友链
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @DELETE
    @Path("delete/{id}")
    @Transactional
    fun delete(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        friendLinkService.delete(id)
    }

    /**
     * 开发者更新友链
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @POST
    @Path("update/{id}")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String,
        @Valid request: BlogFriendLinkSaveRequest
    ): BlogFriendLinkDTO {
        val friendLink = validateService.validateFriendLink(id, true)!!

        /* 唯一性校验 */
        if (request.url != friendLink.url
            && friendLinkService.existByUrl(request.url)
        ) {
            BlogFriendLinkUrlAlreadyExistException().throws()
        }

        val blogFriendLink = BlogFriendLink().apply {
            BeanUtil.copyProperties(request, this)
            uuid = id
        }

        val update = friendLinkService.update(blogFriendLink)

        return dtoService.transfer(update, BlogFriendLinkDTO::class.java)
    }

    /**
     * 分页查询所有友链VO
     */
    @GET
    @Path("page/info/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun pageInfo(@Valid @BeanParam request: BaseQueryRequest): Page<BlogFriendLinkVO> {
        val wrapper = ChainWrappers.queryChain(friendLinkService.dao)
            .eq("status", BlogFriendLinkStatusEnum.APPROVED)

        val page = friendLinkService.page(request, wrapper)

        val voPage = PageUtils.copyPage(page) { item ->
            friendLinkMapStruct.transfer(item)
        }

        return voPage
    }

    /**
     * 开发者查询所有友链DTO
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @GET
    @Path("page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun page(@Valid @BeanParam request: BlogFriendLinkQueryRequest): Page<BlogFriendLinkDTO> {
        val wrapper = ChainWrappers.queryChain(friendLinkService.dao)
            .like(!request.name.isNullOrBlank(), "name", request.name)
            .like(!request.description.isNullOrBlank(), "description", request.description)
            .like(!request.url.isNullOrBlank(), "url", request.url)
            .like(!request.email.isNullOrBlank(), "email", request.email)
            .eq(request.status != null, "status", request.status)

        val page = friendLinkService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogFriendLinkDTO::class.java)
        }

        return dtoPage
    }

    /**
     * 申请友链，如果已登录create_by会附带申请者的id
     */
    @POST
    @Path("apply")
    @Transactional
    fun apply(@Valid request: BlogFriendLinkSaveRequest) {
        val url = request.url

        /* 唯一性校验 */
        if (friendLinkService.existByUrl(url)) {
            BlogFriendLinkUrlAlreadyExistException().throws()
        }

        val blogFriendLink = BlogFriendLink().apply {
            BeanUtil.copyProperties(request, this)
            this.status = BlogFriendLinkStatusEnum.PENDING
        }

        val create = friendLinkService.create(blogFriendLink)

        pushService.applyForFriendLink(create)
    }

    /**
     * 开发者通过友链申请
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @PUT
    @Path("approved/{id}")
    @Transactional
    fun approved(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String
    ) {
        // 状态判断
        val friendLink = validateService.validateFriendLink(id, true)!!
        // 只允许非APPROVED
        if (BlogFriendLinkStatusEnum.APPROVED == friendLink.status) {
            BlogFriendLinkNotValidException().throws()
        }

        val update = friendLinkService.update(BlogFriendLink().apply {
            uuid = id
            status = BlogFriendLinkStatusEnum.APPROVED
        })

        update.email?.let {
            pushService.friendLinkApproved(update)
        }
    }

    /**
     * 开发者拒绝友链申请
     */
    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @PUT
    @Path("disapproved/{id}")
    @Transactional
    fun disapproved(
        @Valid @NotBlank(message = "Id不能为空")
        @Size(max = 32, message = "id长度不能大于32") @RestPath id: String,
        @Valid @NotBlank(message = "原因不能为空")
        @Size(max = 100, message = "原因长度不能大于100") @RestQuery reason: String,
    ) {
        // 状态判断
        val friendLink = validateService.validateFriendLink(id, true)!!
        // 只允许非DISAPPROVED
        if (BlogFriendLinkStatusEnum.DISAPPROVED == friendLink.status) {
            BlogFriendLinkNotValidException().throws()
        }

        val update = friendLinkService.update(BlogFriendLink().apply {
            uuid = id
            status = BlogFriendLinkStatusEnum.DISAPPROVED
        })

        update.email?.let {
            pushService.friendLinkDisapproved(reason, update)
        }
    }

    @SaCheckRole(SaTokenConstants.ROLE_DEVELOPER)
    @GET
    @Path("all/status")
    fun findAllStatus(): List<String> {
        return BlogFriendLinkStatusEnum.values().map { it.name }
    }
}