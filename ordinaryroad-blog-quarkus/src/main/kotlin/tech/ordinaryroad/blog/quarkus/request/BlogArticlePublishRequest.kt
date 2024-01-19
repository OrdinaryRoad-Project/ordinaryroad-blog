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

import tech.ordinaryroad.blog.quarkus.constant.ReConstants
import tech.ordinaryroad.commons.core.quarkus.base.request.BaseRequest
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

class BlogArticlePublishRequest : BaseRequest() {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100")
    var title: String = ""

    @Size(max = 1000, message = "图片地址长度不能超过1000")
    var coverImage: String? = null

    @Size(max = 500, message = "标题长度不能超过500")
    var summary: String? = null

    @NotBlank(message = "内容不能为空")
    var content: String = ""

    var canComment: Boolean? = null

    var canReward: Boolean? = null

    var original: Boolean? = null

    @Size(max = 32, message = "First Id长度不能超过32")
    var firstId: String? = null

    @Pattern(regexp = ReConstants.PATTERN_TYPE_NAME, message = ReConstants.PATTERN_TYPE_NAME_DESCRIPTION)
    @Size(max = 100, message = "分类名称长度不能超过100")
    var typeName: String? = null

    @Size(max = 10, message = "标签个数不能超过10")
    var tagNames: List<String> = emptyList()

    companion object {
        private const val serialVersionUID: Long = 4440826574287058339L
    }

}
