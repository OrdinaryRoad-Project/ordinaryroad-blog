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

package tech.ordinaryroad.blog.quarkus.enums

import cn.hutool.core.util.EnumUtil
import com.baomidou.mybatisplus.annotation.IEnum

/**
 * 文章状态
 *
 * https://blog.csdn.net/yiyuzz/article/details/126121615
 */
enum class BlogArticleStatus : IEnum<String> {
    /**
     * 草稿
     *
     * 每个用户可以有多个文章的草稿
     * 但每篇文章只能有一个草稿
     */
    DRAFT,

    /**
     * 自动保存的草稿
     *
     * 每个用户可以有多个文章的自动保存的草稿
     * 但每篇文章只能有一个自动保存的草稿
     */
    DRAFT_AUTO,

    /**
     * 历史版本，保存或自动保存时将草稿保存为历史版本
     *
     * 每篇文章可以有多个历史版本
     */
    INHERIT,

    /**
     * 自动保存的历史版本
     *
     */
    INHERIT_AUTO,

    /**
     * 已发布的历史版本
     */
    INHERIT_PUBLISH,

    /**
     * 已发布
     */
    PUBLISH,

    /**
     * 待审核
     */
    PENDING,

    /**
     * 审核中
     */
    UNDER_REVIEW,

    /**
     * 回收站
     */
    TRASH,

    /**
     * 文章违规
     */
    OFFEND,

    /**
     * 申诉中
     */
    ON_APPEAL,
    ;

    override fun getValue(): String {
        return EnumUtil.toString(this)
    }

    override fun toString(): String {
        return EnumUtil.toString(this)
    }

}

