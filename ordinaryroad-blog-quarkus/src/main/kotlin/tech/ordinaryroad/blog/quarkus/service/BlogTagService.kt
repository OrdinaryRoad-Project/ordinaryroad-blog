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

import cn.hutool.core.util.ReUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import tech.ordinaryroad.blog.quarkus.constant.ReConstants
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogTagDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogTag
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogTypeNotValidException
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import java.util.*
import javax.enterprise.context.ApplicationScoped

/**
 * Service-Tag
 *
 * @author mjz
 * @date 2022/9/1
 */
@ApplicationScoped
class BlogTagService : BaseService<BlogTagDAO, BlogTag>() {

    override fun getEntityClass(): Class<BlogTag> {
        return BlogTag::class.java
    }

    //region 业务相关
    /**
     * 根据分类名称获取标签列表，不存在则创建
     */
    fun getListByNamesAndCreateBy(tagNames: List<String>?, createBy: String): List<BlogTag> {
        val blogTags = ArrayList<BlogTag>()
        if (!tagNames.isNullOrEmpty()) {
            tagNames.forEach {
                if (!ReUtil.isMatch(ReConstants.PATTERN_TAG_NAME, it)) {
                    BaseBlogException(ReConstants.PATTERN_TAG_NAME_DESCRIPTION).throws()
                }
                val wrapper = Wrappers.query<BlogTag>()
                    .eq("name", it)
                var tag = super.dao.selectOne(wrapper)
                if (Objects.isNull(tag)) {
                    tag = super.create(BlogTag().apply {
                        name = it
                    })
                }
                blogTags.add(tag)
            }
        }

        return blogTags
    }

    fun getIdListByNamesAndCreateBy(tagNames: List<String>?, createBy: String): List<String> {
        val idListByNamesAndCreateBy = getListByNamesAndCreateBy(tagNames, createBy)
        return idListByNamesAndCreateBy.map(BlogTag::getUuid)
    }

    /**
     * 管理员恢复分类
     */
    fun restore(id: String) {
        // 只能恢复已经删除的
        if (super.dao.restore(id) <= 0) {
            BlogTypeNotValidException().throws()
        }
    }
    //endregion

    //region SQL相关
    fun existByName(name: String): Boolean {
        return super.dao.countByName(name) > 0
    }
    //endregion

}