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

package tech.ordinaryroad.blog.quarkus.dal.entity;

import io.quarkus.runtime.annotations.RegisterForReflection;
import tech.ordinaryroad.commons.mybatis.quarkus.model.BaseDO;

import java.time.LocalDateTime;

/**
 * 博客用户浏览的文章
 */
@RegisterForReflection
public class BlogUserBrowsedArticle extends BaseDO {

    private static final long serialVersionUID = -1645302105363120457L;

    private String ip;

    private String articleId;

    private LocalDateTime lastBrowsedTime;

    /**
     * 累计浏览次数
     */
    private Long count;

    private Boolean deleted;

    public BlogUserBrowsedArticle() {
    }

    public BlogUserBrowsedArticle(String ip, String articleId, LocalDateTime lastBrowsedTime, Long count, Boolean deleted) {
        this.ip = ip;
        this.articleId = articleId;
        this.lastBrowsedTime = lastBrowsedTime;
        this.count = count;
        this.deleted = deleted;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
