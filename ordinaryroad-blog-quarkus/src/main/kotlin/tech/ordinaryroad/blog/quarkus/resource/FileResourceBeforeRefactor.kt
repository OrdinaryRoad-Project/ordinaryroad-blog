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

package tech.ordinaryroad.blog.quarkus.resource

import cn.hutool.core.util.URLUtil
import org.jboss.resteasy.reactive.RestQuery
import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.Size
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.core.Response

/**
 * 兼容重构前的文件下载
 *
 * @author mjz
 * @date 2023/5/23
 */
@Path("file")
class FileResourceBeforeRefactor {

    /**
     * 重构前的请求路径：/file/download?uuid=ef2bef5da02e4f898aed8d9bf26e47593f5b&filename=android.jpg
     * 重构后的请求路径：/upms/file/download/ordinaryroad-blog/before-refactor/picture/ef2bef5da02e4f898aed8d9bf26e47593f5b/android.jpg
     */
    @GET
    @Path("download")
    fun download(
        @Valid @Size(max = 36, message = "uuid长度不能大于36")
        @RestQuery uuid: String,
        @Valid @Size(max = 500, message = "filename长度不能大于500")
        @RestQuery filename: String
    ): Response {
        return Response.status(Response.Status.MOVED_PERMANENTLY)
            .location(URI.create(URLUtil.encode("https://api.ordinaryroad.tech/upms/file/download/ordinaryroad-blog/before-refactor/picture/${uuid}/${filename}")))
            .build()
    }

}