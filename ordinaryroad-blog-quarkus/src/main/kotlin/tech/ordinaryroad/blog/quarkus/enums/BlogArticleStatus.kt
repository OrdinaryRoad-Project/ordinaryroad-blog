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

/**
 * 文章状态
 */
enum class BlogArticleStatus {
    //region 使用中的状态
    /**
     * 草稿
     *
     * 每个用户可以有多个文章的草稿
     * 但每篇文章只能有一个草稿
     */
    DRAFT,

    /**
     * 历史版本，保存或自动保存时将草稿保存为历史版本
     *
     * 每篇文章可以有多个历史版本
     */
    INHERIT,

    /**
     * 已发布
     */
    PUBLISH,

    /**
     * 已发布的历史版本
     */
    PUBLISH_INHERIT,
    //endregion

    /**
     * 自动保存的草稿
     *
     * 每个用户可以有多个文章的自动保存的草稿
     * 但每篇文章只能有一个自动保存的草稿
     */
    AUTO_DRAFT,

    /**
     * 待审核
     */
    PENDING,

    /**
     * 回收站
     */
    TRASH,

    /**
     * 私密
     */
    PRIVATE,
    ;

    override fun toString(): String {
        return EnumUtil.toString(this)
    }

    companion object {
        val STATUS_CAN_SAVE_DRAFT = listOf<BlogArticleStatus>(BlogArticleStatus.DRAFT, BlogArticleStatus.PUBLISH)
        val STATUS_CAN_MOVE_TO_TRASH = listOf<BlogArticleStatus>(BlogArticleStatus.DRAFT, BlogArticleStatus.PUBLISH)
        val STATUS_CAN_RECOVER_FROM_TRASH = listOf<BlogArticleStatus>(BlogArticleStatus.TRASH)

        /**
         * 校验文章是否可以存为草稿
         */
        fun BlogArticleStatus.canSaveDraft(): Boolean {
            return STATUS_CAN_MOVE_TO_TRASH.contains(this)
        }

        /**
         * 校验文章是否可以移动到垃圾箱
         */
        fun BlogArticleStatus.canMoveToTrash(): Boolean {
            return STATUS_CAN_MOVE_TO_TRASH.contains(this)
        }

        /**
         * 校验文章是否可以从垃圾箱恢复
         */
        fun BlogArticleStatus.canRecoverFromTrash(): Boolean {
            return STATUS_CAN_RECOVER_FROM_TRASH.contains(this)
        }
    }


}

