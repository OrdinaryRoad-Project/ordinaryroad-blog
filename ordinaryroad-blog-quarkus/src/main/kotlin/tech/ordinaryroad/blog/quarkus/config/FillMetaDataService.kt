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

import cn.dev33.satoken.stp.StpUtil
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import tech.ordinaryroad.blog.quarkus.dal.entity.BaseBlogDO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogUser
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.util.BlogUtils
import tech.ordinaryroad.commons.mybatis.quarkus.model.BaseDO
import tech.ordinaryroad.commons.mybatis.quarkus.service.IFillMetaFieldService
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import kotlin.math.max

@ApplicationScoped
class FillMetaDataService : IFillMetaFieldService {

    @Inject
    protected lateinit var userService: BlogUserService

    override fun generateCreateBy(): String? {
        return StpUtil.getLoginIdDefaultNull()?.toString()
    }

    override fun generateUpdateBy(): String? {
        return StpUtil.getLoginIdDefaultNull()?.toString()
    }

    override fun <T : BaseDO?> beforeInsert(t: T) {
        if (t is BaseBlogDO) {
            t.ip = BlogUtils.getClientIp()
        }

        if (t is BlogUser) {
            val wrapper = Wrappers.query<BlogUser>()
            wrapper.orderBy(true, false, "uid")
            val blogUser = userService.dao.selectOne(wrapper)
            var uid = 10000L
            if (blogUser != null) {
                uid = max(blogUser.uid, uid)
            }
            t.uid = uid + 1
        }
    }

}
