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

import com.baomidou.mybatisplus.core.toolkit.Wrappers
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogFriendLinkDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogFriendLink
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogFriendLinkNotValidException
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import javax.enterprise.context.ApplicationScoped

/**
 * Service-FriendLink
 *
 * @author mjz
 * @date 2023/2/25
 */
@ApplicationScoped
class BlogFriendLinkService : BaseService<BlogFriendLinkDAO, BlogFriendLink>() {

    override fun getEntityClass(): Class<BlogFriendLink> {
        return BlogFriendLink::class.java
    }

    //region 业务相关
    /**
     * 管理员恢复分类
     */
    fun restore(id: String) {
        // 只能恢复已经删除的
        if (super.dao.restore(id) <= 0) {
            BlogFriendLinkNotValidException().throws()
        }
    }
    //endregion

    //region SQL相关
    fun existByUrl(url: String): Boolean {
        val wrapper = Wrappers.query<BlogFriendLink>()
            .eq("url", url)
        return super.dao.exists(wrapper)
    }
    //endregion

}