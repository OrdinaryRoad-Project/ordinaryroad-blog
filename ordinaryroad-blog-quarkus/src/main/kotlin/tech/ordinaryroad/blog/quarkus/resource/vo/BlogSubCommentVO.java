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

package tech.ordinaryroad.blog.quarkus.resource.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.time.LocalDateTime;

/**
 * 子评论VO类
 */
@JsonInclude
@JsonPropertyOrder
@RegisterForReflection
public class BlogSubCommentVO {

    private String uuid;

    private String content;

    private BlogIpRegionVO ip;

    private BlogSubCommentVO parent;

    private BlogUserVO user;

    private LocalDateTime createdTime;

    private String originalId;

    public BlogSubCommentVO() {
    }

    public BlogSubCommentVO(String uuid, String content, BlogIpRegionVO ip, BlogSubCommentVO parent, BlogUserVO user, LocalDateTime createdTime, String originalId) {
        this.uuid = uuid;
        this.content = content;
        this.ip = ip;
        this.parent = parent;
        this.user = user;
        this.createdTime = createdTime;
        this.originalId = originalId;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BlogIpRegionVO getIp() {
        return ip;
    }

    public void setIp(BlogIpRegionVO ip) {
        this.ip = ip;
    }

    public BlogSubCommentVO getParent() {
        return parent;
    }

    public void setParent(BlogSubCommentVO parent) {
        this.parent = parent;
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

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }
}
