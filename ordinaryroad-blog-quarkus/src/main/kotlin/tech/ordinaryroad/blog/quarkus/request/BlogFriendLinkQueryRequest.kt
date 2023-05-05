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
import tech.ordinaryroad.blog.quarkus.enums.BlogFriendLinkStatusEnum
import tech.ordinaryroad.commons.core.quarkus.base.request.query.BaseQueryRequest

class BlogFriendLinkQueryRequest : BaseQueryRequest() {

    @RestQuery
    var name: String? = null

    @RestQuery
    var description: String? = null

    @RestQuery
    var url: String? = null

    @RestQuery
    var email: String? = null

    @RestQuery
    var status: BlogFriendLinkStatusEnum? = null

    companion object {
        private const val serialVersionUID: Long = 6578261924878008670L
    }

}