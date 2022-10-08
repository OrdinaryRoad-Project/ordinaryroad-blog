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

package tech.ordinaryroad.blog.quarkus.exception.mapper;

import io.quarkus.vertx.http.runtime.CurrentVertxRequest;
import org.jboss.logging.Logger;
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException;
import tech.ordinaryroad.commons.base.cons.IStatusCode;
import tech.ordinaryroad.commons.base.cons.StatusCode;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER + 1001)
public class BlogExceptionMapper implements ExceptionMapper<BaseBlogException> {

    private static final Logger log = Logger.getLogger(BlogExceptionMapper.class.getName());

    private volatile CurrentVertxRequest currentVertxRequest;

    CurrentVertxRequest currentVertxRequest() {
        if (currentVertxRequest == null) {
            currentVertxRequest = CDI.current().select(CurrentVertxRequest.class).get();
        }
        return currentVertxRequest;
    }

    @Override
    public Response toResponse(BaseBlogException exception) {
        log.error("BaseBlogException", exception);
        IStatusCode statusCode = exception.getStatusCode();
        int status = statusCode.getCode();
        if (statusCode.getCode() == StatusCode.COMMON_EXCEPTION.getCode()) {
            status = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
        return Response.status(status, statusCode.getMessage())
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(exception)
                .build();
    }

}
