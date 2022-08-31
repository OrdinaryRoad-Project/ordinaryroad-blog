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

package tech.ordinaryroad.blog.quarkus.mapstruct;

import io.vertx.core.json.JsonObject;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import tech.ordinaryroad.blog.quarkus.entity.BlogArticle;
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService;
import tech.ordinaryroad.blog.quarkus.vo.BlogArticleDetailVO;
import tech.ordinaryroad.blog.quarkus.vo.BlogArticlePreviewVO;

import javax.enterprise.inject.spi.CDI;
import java.time.LocalDateTime;
import java.util.Objects;

@Mapper
public interface BlogArticleMapStruct extends BaseBlogMapStruct {

    BlogArticleMapStruct INSTANCE = Mappers.getMapper(BlogArticleMapStruct.class);

    @Mapping(source = "typeId", target = "type")
    @Mapping(source = "createBy", target = "user")
    BlogArticlePreviewVO transferPreview(BlogArticle article);

    @Mapping(source = "typeId", target = "type")
    @Mapping(source = "createBy", target = "user")
    BlogArticleDetailVO transferDetail(BlogArticle article);

    @AfterMapping
    default void fillTimes(BlogArticle article, @MappingTarget Object object) {
        BlogArticlePreviewVO vo = (BlogArticlePreviewVO) object;
        BlogArticleService articleService = CDI.current().select(BlogArticleService.class).get();
        JsonObject times = articleService.getPublishCreatedTimeAndUpdateTimeById(article.getUuid());
        vo.setCreatedTime((LocalDateTime) times.getValue("createdTime"));
        Object updateTime = times.getValue("updateTime");
        if (Objects.nonNull(updateTime)) {
            vo.setUpdateTime((LocalDateTime) updateTime);
        }
    }

}