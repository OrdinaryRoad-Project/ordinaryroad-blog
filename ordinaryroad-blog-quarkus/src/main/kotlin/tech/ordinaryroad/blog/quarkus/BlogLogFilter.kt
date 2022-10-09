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
@Priority(Priorities.USER + 1)
@Provider
class BlogLogFilter : ContainerRequestFilter, ContainerResponseFilter {

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
        log.info("==========request start==========")
        val loginIdDefaultNull = StpUtil.getLoginIdDefaultNull() as String?
        log.info("current userId: $loginIdDefaultNull")

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
        blogLog.request = String(data, StandardCharsets.UTF_8)

        log.info("ip: ${blogLog.ip}")
        log.info("path: ${blogLog.path}")
        log.info("method: ${blogLog.method}")
        log.info("headers: ${blogLog.headers}")
        log.info("cookies: ${blogLog.cookies}")
        log.info("path params: ${blogLog.pathParams}")
        log.info("query params: ${blogLog.queryParams}")
        log.info("request body: ${blogLog.request}")

        requestContext.entityStream = ByteArrayInputStream(data)
        tlBlogLog.set(blogLog)
    }

    @Throws(IOException::class)
    override fun filter(requestContext: ContainerRequestContext, responseContext: ContainerResponseContext) {
        val blogLog = tlBlogLog.get() ?: return

        blogLog.status = responseContext.statusInfo?.toEnum() ?: Response.Status.NOT_IMPLEMENTED
        blogLog.responseHeaders = JSONUtil.toJsonStr(responseContext.headers)
        blogLog.responseCookies = JSONUtil.toJsonStr(responseContext.cookies)
        blogLog.response = JSONUtil.toJsonStr(responseContext.entity)

        log.info("response status: ${blogLog.status}")
        log.info("response headers: ${blogLog.headers}")
        log.info("response cookies: ${blogLog.cookies}")
        log.info("response body: ${blogLog.response}")

        val consumedTime = System.currentTimeMillis() - tlStartTimestamp.get()
        blogLog.consumedTime = consumedTime

        if (blogLog.type != null) {
            // save
            blogLogService().create(blogLog)
        } else {
            if (requestContext.method != "GET") {
                log.warn("blogLog.type is null")
            } else {
                log.info("skip GET method")
            }
        }

        tlStartTimestamp.remove()
        tlBlogLog.remove()
        log.info("consumed time: $consumedTime")
        log.info("==========request end==========")
    }

}