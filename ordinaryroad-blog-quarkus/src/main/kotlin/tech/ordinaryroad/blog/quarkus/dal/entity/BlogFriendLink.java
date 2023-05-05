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

    private static final long serialVersionUID = -3945325420469172147L;

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
     * 友链状态
     */
    private BlogFriendLinkStatusEnum status;

    /**
     * 是否删除
     */
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;

    public BlogFriendLink() {
    }

    public BlogFriendLink(String name, String description, String url, String logo, String email, BlogFriendLinkStatusEnum status, Boolean deleted) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.logo = logo;
        this.email = email;
        this.status = status;
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

    public BlogFriendLinkStatusEnum getStatus() {
        return status;
    }

    public void setStatus(BlogFriendLinkStatusEnum status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
