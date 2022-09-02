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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import tech.ordinaryroad.blog.quarkus.dto.BlogTagDTO
import tech.ordinaryroad.blog.quarkus.entity.BlogTag
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogTagNotValidException
import tech.ordinaryroad.blog.quarkus.request.BlogTagQueryRequest
import tech.ordinaryroad.blog.quarkus.request.BlogTagSaveRequest
import tech.ordinaryroad.blog.quarkus.service.BlogDtoService
import tech.ordinaryroad.blog.quarkus.service.BlogTagService
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import javax.inject.Inject
import javax.transaction.Transactional
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType

/**
 * 标签Resource
 *
 * @author mjz
 * @date 2022/9/1
 */
@Path("tag")
class BlogTagResource {

    @Inject
    protected lateinit var tagService: BlogTagService

    @Inject
    protected lateinit var dtoService: BlogDtoService

    /**
     * 获取分类导航
     */
    fun getTagNavigation() {

    }

    /**
     * 用户创建标签
     */
    @POST
    @Path("create")
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    fun create(@Valid request: BlogTagSaveRequest): BlogTagDTO {
        /* 登录校验 */
        StpUtil.checkLogin()

        val name = request.name

        /* 唯一性校验 */
        if (tagService.existByName(name)) {
            BlogTagNotValidException().throws()
        }

        val blogTag = BlogTag().apply {
            this.name = name
        }

        val create = tagService.create(blogTag)

        return dtoService.transfer(create, BlogTagDTO::class.java)
    }

    /**
     * 分页查询所有标签
     */
    @GET
    @Path("page/{page}/{size}")
    @Produces(MediaType.APPLICATION_JSON)
    fun page(@BeanParam request: BlogTagQueryRequest): Page<BlogTagDTO> {
        /* 登录校验 */
        StpUtil.checkLogin()

        val wrapper = ChainWrappers.queryChain(tagService.dao)
            .like(!request.name.isNullOrBlank(), "name", "%" + request.name + "%")

        val page = tagService.page(request, wrapper)

        val dtoPage = PageUtils.copyPage(page) { item ->
            dtoService.transfer(item, BlogTagDTO::class.java)
        }

        return dtoPage
    }

}