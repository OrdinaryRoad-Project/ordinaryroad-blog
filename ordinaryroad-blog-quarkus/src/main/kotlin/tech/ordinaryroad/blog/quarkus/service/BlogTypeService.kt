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
import cn.hutool.core.util.StrUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogTypeDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogType
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogTypeNotFoundException
import tech.ordinaryroad.blog.quarkus.exception.BlogTypeNotValidException
import tech.ordinaryroad.blog.quarkus.resource.BlogArticleResource
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * Service-Type
 *
 * @author mjz
 * @date 2022/8/29
 */
@ApplicationScoped
class BlogTypeService : BaseService<BlogTypeDAO, BlogType>() {

    override fun getEntityClass(): Class<BlogType> {
        return BlogType::class.java
    }

    //region 业务相关
    /**
     * 根据分类名称获取自己的分类，不存在则创建
     */
    fun getOwnOrCreateByNameAndCreateBy(name: String?, createBy: String): BlogType? {
        var blogType: BlogType? = null
        if (!name.isNullOrBlank()) {
            blogType = this.findByNameAndCreateBy(name, createBy)
            if (Objects.isNull(blogType)) {
                // 创建
                blogType = super.create(BlogType().apply {
                    this.name = name
                })
            }
        }

        return blogType
    }

    fun getOwnIdByNameAndCreateBy(name: String?, createBy: String): String {
        val ownByNameAndCreateBy = getOwnOrCreateByNameAndCreateBy(name, createBy)
        return if (ownByNameAndCreateBy == null) {
            StrUtil.EMPTY
        } else {
            ownByNameAndCreateBy.uuid
        }
    }

    /**
     * 删除自己的分类
     */
    fun deleteOwn(id: String) {
        val type = validateOwn(id)

        // 只能删除未被删除的
        if (!type.deleted) {
            super.delete(id)
        } else {
            BlogTypeNotValidException().throws()
        }
    }

    /**
     * 管理员恢复分类
     */
    fun restore(id: String) {
        // TODO 暂不支持管理员恢复分类
        BlogArticleResource.throwBadRequest()

        val type = validateOwn(id)

        // 只能恢复已经删除的
        if (type.deleted) {
            super.dao.restore(id)
        } else {
            BlogTypeNotValidException().throws()
        }
    }

    /**
     * 更新自己的分类
     */
    fun updateOwn(blogType: BlogType): BlogType {
        val type = validateOwn(blogType.uuid)

        // 只能更新未删除的
        if (type.deleted) {
            BlogTypeNotValidException().throws()
        }

        return super.update(blogType)
    }

    /**
     * 校验分类是否属于自己
     *
     * @param id 分类Id
     * @return BlogType 分类
     */
    private fun validateOwn(id: String): BlogType {
        val userId = StpUtil.getLoginIdAsString()
        if (id.isBlank()) {
            BlogTypeNotFoundException().throws()
        }
        val blogType = findById(id) ?: throw BlogTypeNotFoundException()
        if (blogType.createBy != userId) {
            BlogTypeNotValidException().throws()
        }
        return blogType
    }
    //endregion

    //region SQL相关
    /**
     * 根据创建者查询所有分类
     */
    fun findAllByCreateBy(createBy: String): List<BlogType> {
        val wrapper = Wrappers.query<BlogType>()
            .eq("create_by", createBy)
        return super.dao.selectList(wrapper)
    }

    /**
     * 根据名称和创建者查询
     */
    fun findByNameAndCreateBy(typeName: String, createBy: String): BlogType? {
        val wrapper = Wrappers.query<BlogType>()
            .eq("name", typeName)
            .eq("create_by", createBy)
        return super.dao.selectOne(wrapper)
    }

    /**
     * 根据名称和创建者查询是否存在
     */
    fun existByNameAndCreateBy(typeName: String, createBy: String): Boolean {
        val wrapper = Wrappers.query<BlogType>()
            .eq("name", typeName)
            .eq("create_by", createBy)
        return super.dao.exists(wrapper)
    }
    //endregion

}