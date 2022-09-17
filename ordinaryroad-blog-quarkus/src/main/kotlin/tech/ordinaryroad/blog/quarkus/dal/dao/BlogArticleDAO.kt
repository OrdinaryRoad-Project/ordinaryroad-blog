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

package tech.ordinaryroad.blog.quarkus.dal.dao

import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.enums.BlogArticleStatus
import tech.ordinaryroad.commons.mybatis.quarkus.mapper.IBaseMapper

@Mapper
interface BlogArticleDAO : IBaseMapper<BlogArticle> {

    /**
     * 根据状态查询所有最初版本的草稿
     */
    @Select("SELECT * FROM blog_article AS BA WHERE BA.first_id = BA.uuid AND `status` = #{status} AND `create_by` = #{createBy}")
    fun selectAllFirstArticleByStateAndCreateBy(status: BlogArticleStatus, createBy: String): List<BlogArticle>

//    fun selectAllPublishByType(page:IPage,typeName)

    /**
     * 查询评论数前十的文章
     * @param n N
     * @param userId 用户Id
     */
    fun getTopNByUserId(@Param("n") n: Int, @Param("userId") userId: String): List<Map<String, String>>

}
