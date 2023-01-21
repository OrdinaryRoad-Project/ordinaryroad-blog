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
package tech.ordinaryroad.blog.quarkus.dto

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.quarkus.runtime.annotations.RegisterForReflection
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogRole

/**
 * 博客角色DTO类
 */
@JsonInclude
@JsonPropertyOrder
@RegisterForReflection
data class BlogRoleDTO(
    var roleName: String = StrUtil.EMPTY,
    var roleCode: String = StrUtil.EMPTY,
    var enabled: Boolean = true,
) : BaseBlogModelDTO<BlogRole>() {

    override fun parse(baseDo: BlogRole) {
        roleName = baseDo.roleName
        roleCode = baseDo.roleCode
        enabled = baseDo.enabled
    }

    companion object {
        private const val serialVersionUID: Long = -425690715347674015L
    }

}
