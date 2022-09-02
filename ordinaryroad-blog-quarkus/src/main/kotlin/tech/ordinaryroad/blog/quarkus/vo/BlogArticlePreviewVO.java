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

package tech.ordinaryroad.blog.quarkus.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 博客文章预览VO类
 */
@JsonInclude
@JsonPropertyOrder
@RegisterForReflection
public class BlogArticlePreviewVO {

    private String uuid;

    private String coverImage;

    private String title;

    private String summary;

    private Boolean original;

    private BlogUserVO user;

    private LocalDateTime createdTime;

    private LocalDateTime updateTime;

    private BlogTypeVO type;

    private List<BlogTagVO> tags;

    private Integer pv;

    public BlogArticlePreviewVO() {
    }

    public BlogArticlePreviewVO(String uuid, String coverImage, String title, String summary, Boolean original, BlogUserVO user, LocalDateTime createdTime, LocalDateTime updateTime, BlogTypeVO type, List<BlogTagVO> tags, Integer pv) {
        this.uuid = uuid;
        this.coverImage = coverImage;
        this.title = title;
        this.summary = summary;
        this.original = original;
        this.user = user;
        this.createdTime = createdTime;
        this.updateTime = updateTime;
        this.type = type;
        this.tags = tags;
        this.pv = pv;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getOriginal() {
        return original;
    }

    public void setOriginal(Boolean original) {
        this.original = original;
    }

    public BlogUserVO getUser() {
        return user;
    }

    public void setUser(BlogUserVO user) {
        this.user = user;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public BlogTypeVO getType() {
        return type;
    }

    public void setType(BlogTypeVO type) {
        this.type = type;
    }

    public List<BlogTagVO> getTags() {
        return tags;
    }

    public void setTags(List<BlogTagVO> tags) {
        this.tags = tags;
    }

    public Integer getPv() {
        return pv;
    }

    public void setPv(Integer pv) {
        this.pv = pv;
    }
}
