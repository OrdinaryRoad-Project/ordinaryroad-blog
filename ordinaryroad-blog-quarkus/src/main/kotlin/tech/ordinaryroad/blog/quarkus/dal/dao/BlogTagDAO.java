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

package tech.ordinaryroad.blog.quarkus.dal.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogTag;
import tech.ordinaryroad.commons.mybatis.quarkus.mapper.IBaseMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 标签DAO
 *
 * @author mjz
 * @date 2022/9/1
 */
@Mapper
public interface BlogTagDAO extends IBaseMapper<BlogTag> {

    @Update("UPDATE blog_tag SET `deleted` = 0 WHERE `uuid` = #{uuid}")
    int restore(String uuid);

    List<String> selectIdByNameIn(@Param("nameList") Collection<String> nameList);

    int countByName(@Param("name") String name);

    List<String> selectNameListByUuidIn(@Param("uuidList") Collection<String> uuidList);

    /**
     * 查询文章数前十的标签
     *
     * @param n      N
     * @param userId 用户Id
     */
    List<Map<String, String>> getTopNByUserId(@Param("n") Integer n, @Param("userId") String userId);

}
