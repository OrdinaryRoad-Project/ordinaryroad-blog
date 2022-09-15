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

package tech.ordinaryroad.blog.quarkus.dal.entity;

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.quarkus.runtime.annotations.RegisterForReflection;
import tech.ordinaryroad.blog.quarkus.enums.BlogLogTypeEnum;
import tech.ordinaryroad.commons.mybatis.quarkus.model.BaseDO;

import javax.ws.rs.core.Response;

/**
 * 博客日志
 * 路径
 * IP
 * 请求：header、cookie、queryParam、body
 * 响应：header、cookie、body
 * 耗时
 * 类型：
 * 系统：登录、登出、文件上传
 * 业务：新增、删除、修改、查询
 * 操作人
 */
@RegisterForReflection
public class BlogLog extends BaseDO {

    private static final long serialVersionUID = 1074001601264806792L;

    /**
     * IP
     */
    private String ip;

    /**
     * 请求路径
     */
    private String path;

    /**
     * 请求方法
     */
    private String method;

    /**
     * 请求头
     */
    private String headers;

    /**
     * 请求Cookie
     */
    private String cookies;

    /**
     * 路径参数
     */
    private String pathParams;

    /**
     * 查询参数
     */
    private String queryParams;

    /**
     * 请求体
     */
    private String request;

    /**
     * 响应状态
     */
    private Response.Status status;

    /**
     * 响应头
     */
    private String responseHeaders;

    /**
     * 响应Cookie
     */
    private String responseCookies;

    /**
     * 响应体
     */
    private String response;

    /**
     * 耗时
     */
    private long consumedTime;

    /**
     * 类型
     */
    private BlogLogTypeEnum type;

    /**
     * 是否删除
     */
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getHeaders() {
        return headers;
    }

    public void setHeaders(String headers) {
        this.headers = headers;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }

    public String getPathParams() {
        return pathParams;
    }

    public void setPathParams(String pathParams) {
        this.pathParams = pathParams;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(String queryParams) {
        this.queryParams = queryParams;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public Response.Status getStatus() {
        return status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
    }

    public String getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(String responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getResponseCookies() {
        return responseCookies;
    }

    public void setResponseCookies(String responseCookies) {
        this.responseCookies = responseCookies;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public long getConsumedTime() {
        return consumedTime;
    }

    public void setConsumedTime(long consumedTime) {
        this.consumedTime = consumedTime;
    }

    public BlogLogTypeEnum getType() {
        return type;
    }

    public void setType(BlogLogTypeEnum type) {
        this.type = type;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
