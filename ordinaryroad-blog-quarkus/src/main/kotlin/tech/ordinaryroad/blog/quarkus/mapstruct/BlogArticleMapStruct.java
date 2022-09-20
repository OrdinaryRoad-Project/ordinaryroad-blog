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

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.vertx.core.json.JsonObject;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle;
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogTag;
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogType;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticleDetailVO;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticlePreviewVO;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogTagVO;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogTypeVO;
import tech.ordinaryroad.blog.quarkus.service.*;

import javax.enterprise.inject.spi.CDI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mapper
public interface BlogArticleMapStruct extends BaseBlogMapStruct {

    BlogArticleMapStruct INSTANCE = Mappers.getMapper(BlogArticleMapStruct.class);

    @Mapping(source = "typeId", target = "type")
    @Mapping(source = "tagIds", target = "tags")
    @Mapping(source = "createBy", target = "user")
    BlogArticlePreviewVO transferPreview(BlogArticle article);

    @Mapping(source = "typeId", target = "type")
    @Mapping(source = "tagIds", target = "tags")
    @Mapping(source = "createBy", target = "user")
    BlogArticleDetailVO transferDetail(BlogArticle article);

    /**
     * 填充 创建时间、更新时间，浏览量，点赞量 字段
     *
     * @param article From
     * @param object  To
     */
    @AfterMapping
    default void afterMapping(BlogArticle article, @MappingTarget Object object) {
        String articleUuid = article.getUuid();
        BlogArticlePreviewVO vo = (BlogArticlePreviewVO) object;

        BlogArticleService articleService = CDI.current().select(BlogArticleService.class).get();
        JsonObject times = articleService.getPublishCreatedTimeAndUpdateTimeById(articleUuid);
        vo.setCreatedTime((LocalDateTime) times.getValue("createdTime"));
        Object updateTime = times.getValue("updateTime");
        if (Objects.nonNull(updateTime)) {
            vo.setUpdateTime((LocalDateTime) updateTime);
        }

        BlogUserBrowsedArticleService userBrowsedArticleService = CDI.current().select(BlogUserBrowsedArticleService.class).get();
        vo.setPv(userBrowsedArticleService.getBrowsedCount(articleUuid));

        BlogUserLikedArticleService userLikedArticleService = CDI.current().select(BlogUserLikedArticleService.class).get();
        vo.setLikesCount(userLikedArticleService.getLikesCount(articleUuid));
    }

    default BlogTypeVO string2TypeVO(String type) {
        if (StrUtil.isBlank(type)) {
            return null;
        }
        BlogTypeService typeService = CDI.current().select(BlogTypeService.class).get();
        BlogType blogType = typeService.findById(type);
        if (blogType.getDeleted()) {
            return null;
        }
        return BlogTypeMapStruct.INSTANCE.transfer(blogType);
    }

    default List<BlogTagVO> stringList2TypeVOList(List<String> tagIdList) {
        if (CollUtil.isEmpty(tagIdList)) {
            return null;
        }
        BlogTagService tagService = CDI.current().select(BlogTagService.class).get();
        List<BlogTag> tagList = tagService.findIds(BlogTag.class, tagIdList);

        List<BlogTagVO> tagVOList = tagList.stream()
                .map(BlogTagMapStruct.INSTANCE::transfer)
                .collect(Collectors.toList());

        return tagVOList;
    }

}