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

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogComment;
import tech.ordinaryroad.blog.quarkus.request.BlogCommentPostRequest;
import tech.ordinaryroad.blog.quarkus.request.BlogCommentQueryRequest;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticleCommentVO;
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogSubCommentVO;
import tech.ordinaryroad.blog.quarkus.service.BlogCommentService;

import javax.enterprise.inject.spi.CDI;
import java.util.Collections;

@Mapper
public interface BlogCommentMapStruct extends BaseBlogMapStruct {

    BlogCommentMapStruct INSTANCE = Mappers.getMapper(BlogCommentMapStruct.class);

    BlogComment transfer(BlogCommentPostRequest request);

    @Mapping(source = "createBy", target = "user")
    BlogArticleCommentVO transferArticle(BlogComment comment);

    @Mapping(source = "createBy", target = "user")
    @Mapping(source = "parentId", target = "parent")
    BlogSubCommentVO transferSub(BlogComment comment);

    @AfterMapping
    default void fillReplies(BlogComment comment, @MappingTarget BlogArticleCommentVO vo) {
        BlogCommentService commentService = CDI.current().select(BlogCommentService.class).get();

        BlogCommentQueryRequest blogCommentQueryRequest = new BlogCommentQueryRequest();
        blogCommentQueryRequest.setArticleId(comment.getArticleId());
        blogCommentQueryRequest.setOriginalId(comment.getUuid());
        blogCommentQueryRequest.setSize(5L);
        blogCommentQueryRequest.setSortBy(Collections.singletonList("createdTime"));
        blogCommentQueryRequest.setSortDesc(Collections.singletonList(true));
        Page<BlogSubCommentVO> replies = commentService.pageSubComment(blogCommentQueryRequest);

        vo.setReplies(replies);
    }

    default BlogSubCommentVO string2Parent(String parentId) {
        if (StrUtil.isBlank(parentId)) {
            return null;
        }

        BlogCommentService commentService = CDI.current().select(BlogCommentService.class).get();

        BlogComment parentComment = commentService.findById(parentId);
        // 防止死循环
        parentComment.setParentId(null);
        return this.transferSub(parentComment);
    }

}