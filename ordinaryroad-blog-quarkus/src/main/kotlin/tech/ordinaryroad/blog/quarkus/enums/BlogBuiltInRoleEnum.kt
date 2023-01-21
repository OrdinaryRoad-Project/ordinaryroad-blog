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

package tech.ordinaryroad.blog.quarkus.enums

import tech.ordinaryroad.blog.quarkus.constant.SaTokenConstants

/**
 * 内置角色
 *
 * @author mjz
 * @date 2023/1/19
 */
enum class BlogBuiltInRoleEnum(
    val roleCode: String,
    val roleName: String,
) {
    DEVELOPER(SaTokenConstants.ROLE_DEVELOPER, "站长"),
    ADMIN(SaTokenConstants.ROLE_ADMIN, "管理员"),
    AUDITOR(SaTokenConstants.ROLE_AUDITOR, "审核员"),
    SSSSSSVIP(SaTokenConstants.ROLE_SSSSSSVIP, "SSSSSSVIP"),
    ;

    companion object {
        fun getByRoleCode(roleCode: String?): BlogBuiltInRoleEnum? {
            if (roleCode.isNullOrBlank()) {
                return null
            }
            for (value in values()) {
                if (value.roleCode == roleCode) {
                    return value
                }
            }
            return null
        }
    }
}