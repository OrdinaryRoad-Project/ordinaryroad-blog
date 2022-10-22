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

import org.jboss.resteasy.reactive.RestQuery
import tech.ordinaryroad.commons.core.quarkus.base.request.BaseRequest
import javax.validation.constraints.Max
import javax.validation.constraints.Size

/**
 *
 * 文章TopN请求
 *
 * @author mjz
 * @date 2022/10/8
 */
class BlogArticleTopNRequest : BaseRequest() {

    @Size(max = 32, message = "userId长度不能大于32")
    @RestQuery
    var userId: String = ""

    @Max(value = 50, message = "n不能大于50")
    @RestQuery
    var n: Int = 10

    companion object {
        private const val serialVersionUID: Long = -7870390888331271243L
    }

}