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

import com.baomidou.mybatisplus.annotation.TableLogic;
import io.quarkus.runtime.annotations.RegisterForReflection;
import tech.ordinaryroad.blog.quarkus.enums.BlogFriendLinkStatusEnum;

/**
 * 博客友链
 */
@RegisterForReflection
public class BlogFriendLink extends BaseBlogDO {

    private static final long serialVersionUID = -5889222597070818404L;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 网站描述
     */
    private String description;

    /**
     * 网站地址
     */
    private String url;

    /**
     * 网站logo地址
     */
    private String logo;

    /**
     * 站长email
     */
    private String email;

    /**
     * 网站快照地址
     */
    private String snapshotUrl;

    /**
     * 友链状态
     */
    private BlogFriendLinkStatusEnum status;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否删除
     */
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;

    public BlogFriendLink() {
    }

    public BlogFriendLink(String name, String description, String url, String logo, String email, String snapshotUrl, BlogFriendLinkStatusEnum status, Boolean enabled, Boolean deleted) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.logo = logo;
        this.email = email;
        this.snapshotUrl = snapshotUrl;
        this.status = status;
        this.enabled = enabled;
        this.deleted = deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSnapshotUrl() {
        return snapshotUrl;
    }

    public void setSnapshotUrl(String snapshotUrl) {
        this.snapshotUrl = snapshotUrl;
    }

    public BlogFriendLinkStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BlogFriendLinkStatusEnum status) {
        this.status = status;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
