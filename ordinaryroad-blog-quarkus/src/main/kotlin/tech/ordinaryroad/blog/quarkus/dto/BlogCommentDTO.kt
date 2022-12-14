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

package tech.ordinaryroad.blog.quarkus.dto

import cn.hutool.core.util.StrUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.quarkus.runtime.annotations.RegisterForReflection
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogComment

/**
 * 评论DTO类
 *
 * @author mjz
 * @date 2022/12/14
 */
@JsonInclude
@JsonPropertyOrder
@RegisterForReflection
data class BlogCommentDTO(
    var content: String = StrUtil.EMPTY,
    var articleId: String = StrUtil.EMPTY,
    var parentId: String = StrUtil.EMPTY,
    var originalId: String = StrUtil.EMPTY,
) : BaseBlogModelDTO<BlogComment>() {
    override fun parse(baseDo: BlogComment) {
        content = baseDo.content
        articleId = baseDo.articleId
        parentId = baseDo.parentId
        originalId = baseDo.originalId
    }

    companion object {
        private const val serialVersionUID: Long = -3701560342094602092L
    }
}