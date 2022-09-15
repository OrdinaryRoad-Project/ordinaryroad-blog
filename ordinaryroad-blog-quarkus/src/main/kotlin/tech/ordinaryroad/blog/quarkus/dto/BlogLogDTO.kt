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
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.quarkus.runtime.annotations.RegisterForReflection
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogLog
import tech.ordinaryroad.blog.quarkus.enums.BlogLogTypeEnum
import javax.ws.rs.core.Response

/**
 * 日志DTO类
 *
 * @author mjz
 * @date 2022/9/13
 */
@JsonInclude
@JsonPropertyOrder
@RegisterForReflection
data class BlogLogDTO(
    var ip: String = StrUtil.EMPTY,
    var path: String = StrUtil.EMPTY,
    var method: String = StrUtil.EMPTY,
    var headers: String = StrUtil.EMPTY,
    var cookies: String = StrUtil.EMPTY,
    var pathParams: String = StrUtil.EMPTY,
    var queryParams: String = StrUtil.EMPTY,
    var request: String = StrUtil.EMPTY,
    var status: Response.Status? = null,
    var responseHeaders: String = StrUtil.EMPTY,
    var responseCookies: String = StrUtil.EMPTY,
    var response: String = StrUtil.EMPTY,
    var consumedTime: Long = 0,
    @get:JsonSerialize(using = ToStringSerializer::class)
    var type: BlogLogTypeEnum? = null
) : BaseBlogModelDTO<BlogLog>() {

    override fun parse(baseDo: BlogLog) {
        ip = StrUtil.nullToEmpty(baseDo.ip)
        path = StrUtil.nullToEmpty(baseDo.path)
        method = StrUtil.nullToEmpty(baseDo.method)
        headers = StrUtil.nullToEmpty(baseDo.headers)
        cookies = StrUtil.nullToEmpty(baseDo.cookies)
        pathParams = StrUtil.nullToEmpty(baseDo.pathParams)
        queryParams = StrUtil.nullToEmpty(baseDo.queryParams)
        request = StrUtil.nullToEmpty(baseDo.request)
        status = baseDo.status
        responseHeaders = StrUtil.nullToEmpty(baseDo.responseHeaders)
        responseCookies = StrUtil.nullToEmpty(baseDo.responseCookies)
        response = StrUtil.nullToEmpty(baseDo.response)
        consumedTime = baseDo.consumedTime
        type = baseDo.type
    }

    companion object {
        private const val serialVersionUID: Long = 2588713180069303384L
    }

}