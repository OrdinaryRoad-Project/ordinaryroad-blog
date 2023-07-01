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
package tech.ordinaryroad.blog.quarkus.request

import tech.ordinaryroad.commons.core.quarkus.base.request.BaseRequest
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class BlogFriendLinkSaveRequest : BaseRequest() {

    @NotBlank(message = "网站名称不能为空")
    @Size(max = 50, message = "网站名称长度不能超过50")
    var name: String = ""

    @Size(max = 100, message = "网站描述长度不能超过100")
    var description: String? = null

    @NotBlank(message = "网站地址不能为空")
    @Size(max = 500, message = "网站地址长度不能超过500")
    var url: String = ""

    @NotBlank(message = "网站logo地址不能为空")
    @Size(max = 500, message = "网站logo地址长度不能超过500")
    var logo: String = ""

    @Size(max = 500, message = "站长email长度不能超过500")
    var email: String? = null

    @Size(max = 500, message = "网站快照地址长度不能超过500")
    var snapshotUrl: String = ""

    companion object {
        private const val serialVersionUID: Long = 4789157779083113050L
    }

}
