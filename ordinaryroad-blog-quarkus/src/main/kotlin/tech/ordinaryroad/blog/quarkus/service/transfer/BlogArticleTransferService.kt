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

package tech.ordinaryroad.blog.quarkus.service.transfer

import tech.ordinaryroad.blog.quarkus.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.facade.BlogArticleFacade
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogArticleMapStruct
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogUserMapStruct
import tech.ordinaryroad.blog.quarkus.service.BlogUserService
import tech.ordinaryroad.blog.quarkus.vo.BlogArticleDetailVO
import tech.ordinaryroad.blog.quarkus.vo.BlogArticlePreviewVO
import java.time.LocalDateTime
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

/**
 * 文章转换服务类
 */
@ApplicationScoped
class BlogArticleTransferService {

    val blogArticleMapStruct = BlogArticleMapStruct.INSTANCE
    val blogUserMapStruct = BlogUserMapStruct.INSTANCE

    @Inject
    protected lateinit var articleFacade: BlogArticleFacade

    @Inject
    protected lateinit var userService: BlogUserService

    @Inject
    protected lateinit var userTransferService: BlogUserTransferService

    fun transferDetail(article: BlogArticle): BlogArticleDetailVO {
        return blogArticleMapStruct.do2DetailVo(article).apply {
            val times = articleFacade.getPublishCreatedTimeAndUpdateTimeById(article.uuid)
            createdTime = times.getValue("createdTime") as LocalDateTime
            updateTime = times.getValue("updateTime") as LocalDateTime?
            val blogUser = userService.findById(article.createBy)
            user = userTransferService.transfer(blogUser)
        }
    }

    fun transferPreview(article: BlogArticle): BlogArticlePreviewVO {
        return blogArticleMapStruct.do2PreviewVo(article).apply {
            val times = articleFacade.getPublishCreatedTimeAndUpdateTimeById(article.uuid)
            createdTime = times.getValue("createdTime") as LocalDateTime
            updateTime = times.getValue("updateTime") as LocalDateTime?
            val blogUser = userService.findById(article.createBy)
            user = userTransferService.transfer(blogUser)
        }
    }

}
