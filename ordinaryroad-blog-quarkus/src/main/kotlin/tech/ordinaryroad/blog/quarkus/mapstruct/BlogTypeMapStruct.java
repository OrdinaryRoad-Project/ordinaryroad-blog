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

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogType;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogTypeInfoVO;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogTypeVO;
import tech.ordinaryroad.blog.quarkus.service.BlogArticleService;

import javax.enterprise.inject.spi.CDI;

@Mapper
public interface BlogTypeMapStruct extends BaseBlogMapStruct {

    BlogTypeMapStruct INSTANCE = Mappers.getMapper(BlogTypeMapStruct.class);

    @Mapping(source = "createBy", target = "user")
    BlogTypeVO transfer(BlogType type);

    @Mapping(source = "createBy", target = "user")
    BlogTypeInfoVO transferInfo(BlogType type);

    @AfterMapping
    default void afterMapping(BlogType type, @MappingTarget BlogTypeInfoVO target) {
        BlogArticleService articleService = CDI.current().select(BlogArticleService.class).get();
        target.setArticleCount(articleService.countPublishByTypeId(type.getUuid()));
    }

}