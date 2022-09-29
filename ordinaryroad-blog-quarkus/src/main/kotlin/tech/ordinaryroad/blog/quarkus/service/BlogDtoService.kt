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

import cn.hutool.core.util.ReflectUtil
import tech.ordinaryroad.blog.quarkus.dto.BaseBlogModelDTO
import tech.ordinaryroad.commons.mybatis.quarkus.model.BaseDO
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * DTO服务类
 */
@ApplicationScoped
class BlogDtoService {

    @Inject
    protected lateinit var userService: BlogUserService

    /**
     * do转dto DTO必须要有非空构造
     */
    fun <DO : BaseDO, DTO : BaseBlogModelDTO<DO>> transfer(baseDo: DO, clazz: Class<DTO>): DTO {
        val dto = ReflectUtil.newInstance(clazz)

        dto.parse(baseDo)

        dto.uuid = baseDo.uuid
        dto.createdTime = baseDo.createdTime
        val createBy = baseDo.createBy
        if (!createBy.isNullOrEmpty()) {
            userService.findById(createBy)?.let {
                dto.createBy = it.username
                dto.creatorId = createBy
            }
        }
        val updateBy = baseDo.updateBy
        if (!updateBy.isNullOrEmpty()) {
            userService.findById(updateBy)?.let {
                dto.updateBy = it.username
            }
        }
        dto.updateTime = baseDo.updateTime

        return dto
    }

}
