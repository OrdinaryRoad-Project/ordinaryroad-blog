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

package tech.ordinaryroad.blog.quarkus

import cn.dev33.satoken.stp.StpUtil
import cn.hutool.core.util.StrUtil
import cn.hutool.json.JSONUtil
import io.quarkus.arc.Priority
import io.quarkus.vertx.http.runtime.CurrentVertxRequest
import org.jboss.logging.Logger
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogLog
import tech.ordinaryroad.blog.quarkus.enums.BlogLogTypeEnum
import tech.ordinaryroad.blog.quarkus.service.BlogLogService
import tech.ordinaryroad.blog.quarkus.util.BlogUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import javax.enterprise.inject.spi.CDI
import javax.ws.rs.Priorities
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider


/**
 *
 * 日志过滤器
 *
 * @author mjz
 * @date 2022/8/29
 */
@Priority(Priorities.USER + 1000)
@Provider
class BlogLogFilter : ContainerRequestFilter, ContainerResponseFilter {

    companion object {
        private const val BODY_MAX_LENGTH = 10000000
    }

    private val log = Logger.getLogger(BlogLogFilter::class.java.name)

    private var tlStartTimestamp = ThreadLocal<Long>()
    private var tlBlogLog = ThreadLocal<BlogLog>()

    @Volatile
    private var currentVertxRequest: CurrentVertxRequest? = null

    fun currentVertxRequest(): CurrentVertxRequest {
        if (currentVertxRequest == null) {
            currentVertxRequest = BlogUtils.currentVertxRequest()
        }
        return currentVertxRequest!!
    }

    @Volatile
    private var logService: BlogLogService? = null

    fun blogLogService(): BlogLogService {
        if (logService == null) {
            logService = CDI.current().select(BlogLogService::class.java).get()!!
        }
        return logService!!
    }

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext) {
        tlStartTimestamp.set(System.currentTimeMillis())
        log.debug("==========request start==========")
        val loginIdDefaultNull = StpUtil.getLoginIdDefaultNull() as String?
        log.debug("current userId: $loginIdDefaultNull")

        val entityStream = requestContext.entityStream
        val out = ByteArrayOutputStream(entityStream.available())
        entityStream.copyTo(out)
        val data = out.toByteArray()

        val headers = requestContext.headers
        val path = requestContext.uriInfo.path
        val method = requestContext.method

        val blogLog = BlogLog()
        val blogLogTypeEnum = BlogLogTypeEnum.get(path, method)
        blogLog.type = blogLogTypeEnum
        blogLog.ip = BlogUtils.getClientIp(this.currentVertxRequest())
        blogLog.path = path
        blogLog.method = method
        blogLog.headers = JSONUtil.toJsonStr(headers)
        blogLog.cookies = JSONUtil.toJsonStr(requestContext.cookies)
        blogLog.pathParams = JSONUtil.toJsonStr(requestContext.uriInfo.pathParameters)
        blogLog.queryParams = JSONUtil.toJsonStr(requestContext.uriInfo.queryParameters)
        if (blogLogTypeEnum != BlogLogTypeEnum.BLOG_UPLOAD) {
            // 长度最大一百万
            blogLog.request = StrUtil.subWithLength(String(data, StandardCharsets.UTF_8), 0, BODY_MAX_LENGTH)
        }

        log.debug("ip: ${blogLog.ip}")
        log.debug("path: ${blogLog.path}")
        log.debug("method: ${blogLog.method}")
        log.debug("headers: ${blogLog.headers}")
        log.debug("cookies: ${blogLog.cookies}")
        log.debug("path params: ${blogLog.pathParams}")
        log.debug("query params: ${blogLog.queryParams}")
        log.debug("request body: ${blogLog.request}")

        requestContext.entityStream = ByteArrayInputStream(data)
        tlBlogLog.set(blogLog)
    }

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val blogLog = tlBlogLog.get() ?: return

        blogLog.status = responseContext.statusInfo?.toEnum() ?: Response.Status.NOT_IMPLEMENTED
        blogLog.responseHeaders = JSONUtil.toJsonStr(responseContext.headers)
        blogLog.responseCookies = JSONUtil.toJsonStr(responseContext.cookies)
        // 长度最大一百万
        blogLog.response = StrUtil.subWithLength(JSONUtil.toJsonStr(responseContext.entity), 0, BODY_MAX_LENGTH)

        log.debug("response status: ${blogLog.status}")
        log.debug("response headers: ${blogLog.headers}")
        log.debug("response cookies: ${blogLog.cookies}")
        log.debug("response body: ${blogLog.response}")

        val consumedTime = System.currentTimeMillis() - tlStartTimestamp.get()
        blogLog.consumedTime = consumedTime

        if (blogLog.type != null) {
            // save
            blogLogService().create(blogLog)
        } else {
            if (requestContext.method != "GET") {
                log.warn("blogLog.type is null when GET")
            } else {
                log.debug("skip GET method")
            }
        }

        tlStartTimestamp.remove()
        tlBlogLog.remove()
        log.debug("consumed time: $consumedTime")
        log.debug("==========request end==========")
    }

}