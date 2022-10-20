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

package tech.ordinaryroad.blog.quarkus.dal.dao.result;

import com.baomidou.mybatisplus.annotation.TableField;
import org.apache.ibatis.type.JdbcType;
import tech.ordinaryroad.commons.mybatis.quarkus.model.BaseDO;
import tech.ordinaryroad.commons.mybatis.quarkus.type.StringListTypeHandler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户已浏览文章
 *
 * @author mjz
 * @date 2022/9/23
 */
public class BlogArticleUserBrowsed extends BaseDO {

    private static final long serialVersionUID = 6774417142338139691L;

    /**
     * 标题
     */
    private String title;

    /**
     * 封面地址
     */
    private String coverImage;

    /**
     * 摘要
     */
    private String summary;

    /**
     * 内容
     */
    private String content;

    /**
     * 是否允许打赏
     */
    private Boolean canReward;

    /**
     * 是否为原创，原创时加转载声明
     */
    private Boolean original;

    /**
     * 文章firstId
     */
    private String firstId;

    /**
     * 分类Id
     */
    private String typeId;

    /**
     * 标签Id列表
     */
    @TableField(
            jdbcType = JdbcType.BLOB,
            typeHandler = StringListTypeHandler.class
    )
    private List<String> tagIds;

    /**
     * 首次浏览时间
     */
    private LocalDateTime firstBrowsedTime;


    /**
     * 上次浏览时间
     */
    private LocalDateTime lastBrowsedTime;

    /**
     * 累计浏览次数
     */
    private Long count;

    public BlogArticleUserBrowsed() {
    }

    public BlogArticleUserBrowsed(String title, String coverImage, String summary, String content, Boolean canReward, Boolean original, String firstId, String typeId, List<String> tagIds, LocalDateTime firstBrowsedTime, LocalDateTime lastBrowsedTime, Long count) {
        this.title = title;
        this.coverImage = coverImage;
        this.summary = summary;
        this.content = content;
        this.canReward = canReward;
        this.original = original;
        this.firstId = firstId;
        this.typeId = typeId;
        this.tagIds = tagIds;
        this.firstBrowsedTime = firstBrowsedTime;
        this.lastBrowsedTime = lastBrowsedTime;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getCanReward() {
        return canReward;
    }

    public void setCanReward(Boolean canReward) {
        this.canReward = canReward;
    }

    public Boolean getOriginal() {
        return original;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }

    public String getFirstId() {
        return firstId;
    }

    public void setFirstId(String firstId) {
        this.firstId = firstId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    public LocalDateTime getFirstBrowsedTime() {
        return firstBrowsedTime;
    }

    public void setFirstBrowsedTime(LocalDateTime firstBrowsedTime) {
        this.firstBrowsedTime = firstBrowsedTime;
    }

    public LocalDateTime getLastBrowsedTime() {
        return lastBrowsedTime;
    }

    public void setLastBrowsedTime(LocalDateTime lastBrowsedTime) {
        this.lastBrowsedTime = lastBrowsedTime;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
