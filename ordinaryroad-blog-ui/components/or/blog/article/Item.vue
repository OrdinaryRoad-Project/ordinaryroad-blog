<!--
  - MIT License
  -
  - Copyright (c) 2021 苗锦洲
  -
  - Permission is hereby granted, free of charge, to any person obtaining a copy
  - of this software and associated documentation files (the "Software"), to deal
  - in the Software without restriction, including without limitation the rights
  - to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  - copies of the Software, and to permit persons to whom the Software is
  - furnished to do so, subject to the following conditions:
  -
  - The above copyright notice and this permission notice shall be included in all
  - copies or substantial portions of the Software.
  -
  - THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  - IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  - FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  - AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  - LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  - OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  - SOFTWARE.
  -->

<template>
  <v-hover
    v-model="cardFocused"
    close-delay="100"
  >
    <v-card
      dark
      :elevation="cardFocused?24:0"
      class="transition-swing"
      @click="onClickArticle"
    >
      <!-- 图片上层的信息 -->
      <div style="position: relative; overflow: hidden">
        <!-- 封面 -->
        <img
          style="background-color: darkgrey"
          alt=""
          class="or-cover-image"
          :src="$apis.blog.getFileUrl(item.coverImage, 'https://api.ixiaowai.cn/gqapi/gqapi.php')"
        >

        <!-- 是否原创 -->
        <div v-if="item.original" class="primary or-original-label">
          <span>{{ $t('article.originalOptions.original') }}</span>
        </div>

        <!-- 封面上层 -->
        <div class="or-title px-4 pb-1">
          <v-card-subtitle class="d-flex flex-wrap pt-0 pb-1 align-center">
            <!-- 创建时间 -->
            <span class="d-inline-flex me-2">
              <v-icon dark small class="me-1">
                mdi-calendar
              </v-icon>
              {{ item.createdTime }}
            </span>

            <!-- 分类 -->
            <span v-if="item.type" class="d-inline-flex me-2">
              <v-icon dark small class="me-1">
                mdi-view-list
              </v-icon>
              {{ item.type.name }}
            </span>

            <!-- 标签 -->
            <span
              v-if="item.tags && item.tags.length && item.tags.length > 0"
              class="d-flex flex-wrap"
            >
              <span
                v-for="(tag,index) in item.tags"
                :key="tag.uuid"
                :class="index!==item.tags.length-1?'me-2':null"
                class="d-inline-flex"
              >
                <v-icon dark small class="me-1">
                  mdi-tag
                </v-icon>
                {{ tag.name }}
              </span>
            </span>
          </v-card-subtitle>

          <!-- 标题 -->
          <v-card-title class="py-0 text-justify tow-lines-text">
            {{ item.title }}
          </v-card-title>

          <!-- 悬浮显示 -->
          <v-expand-transition>
            <div
              v-if="cardFocused"
              class="transition-fast-in-fast-out"
            >
              <!-- 摘要 -->
              <v-card-subtitle
                v-if="item.summary !== undefined && item.summary !== ''"
                class="py-0 my-0 text-justify font-weight-light four-lines-text"
              >
                {{ item.summary }}
              </v-card-subtitle>
            </div>
          </v-expand-transition>

          <v-divider class="mt-2" />

          <!-- 作者信息和操作按钮栏 -->
          <v-list-item>
            <!-- 头像 -->
            <or-blog-user-avatar
              avatar-class="v-list-item__avatar"
              :user="item.user"
            />
            <!-- 用户名 -->
            <v-list-item-content>
              <v-list-item-title>
                <v-hover v-model="usernameFocused">
                  <span
                    class="font-weight-medium transition-swing"
                    :class="usernameFocused?'primary--text':null"
                    @click.stop="onClickUsername"
                  >{{ item.user.username }}</span>
                </v-hover>
              </v-list-item-title>
              <!-- todo 个性签名 -->
              <v-list-item-subtitle v-if="false">
                世间种种平凡都不平凡
              </v-list-item-subtitle>
            </v-list-item-content>

            <v-list-item-action>
              <v-row
                no-gutters
                align="center"
                justify="end"
              >
                <v-col class="d-inline-flex align-center me-3">
                  <v-icon left small>
                    mdi-thumb-up
                  </v-icon>
                  <span>{{ item.likesCount }}</span>
                </v-col>

                <v-col class="d-inline-flex align-center me-3">
                  <v-icon left small>
                    mdi-eye
                  </v-icon>
                  <span>{{ item.pv }}</span>
                </v-col>

                <v-hover v-model="commentButtonFocused">
                  <v-col class="d-inline-flex align-center" @click.stop="onClickArticleComments">
                    <v-icon left small :color="commentButtonFocused?'primary':null">
                      mdi-comment-text
                    </v-icon>
                    <span
                      class="transition-swing"
                      :class="commentButtonFocused?'primary--text':null"
                    >{{ item.commentsCount }}</span>
                  </v-col>
                </v-hover>

                <v-col class="d-inline-flex align-center">
                  <slot name="actions" />
                </v-col>
              </v-row>
            </v-list-item-action>
          </v-list-item>
        </div>
      </div>
    </v-card>
  </v-hover>
</template>

<script>
export default {
  name: 'OrBlogArticleItem',
  props: {
    /** BlogArticleVO
     * <code>
     *   {
     *     "coverImage": "xxx"
     *     "title": "xxx"
     *     "summary": "xxx"
     *     "original": true
     *     "user": {
     *       "username": "xxx",
     *       "avatar": "xxx"
     *     }
     *     "createdTime": "xxxx-xx-xxTxx:xx:xx"
     *     "updateTime": "xxxx-xx-xxTxx:xx:xx"
     *     "pv": 999
     *   }
     * </code>
     */
    item: {
      type: Object,
      required: true
    },
    showType: {
      type: Boolean,
      default: true
    },
    showTags: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      cardFocused: false,
      usernameFocused: false,
      commentButtonFocused: false,

      summaryShow: false
    }
  },
  computed: {
    articlePath () {
      return `/${this.item.user.uuid}/article/${this.item.uuid}`
    }
  },
  watch: {},
  created () {
  },
  methods: {
    onClickUsername () {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uuid}` })
      window.open(href, '_blank')
    },
    onClickArticle () {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uuid}/article/${this.item.uuid}` })
      window.open(href, '_blank')
    },
    onClickArticleComments () {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uuid}/article/${this.item.uuid}#comments` })
      window.open(href, '_blank')
    }
  }
}
</script>

<style scoped>
.or-original-label {
  position: absolute;
  top: 15px;
  right: -20px;
  width: 90px;
  height: 20px;
  text-align: center;
  line-height: 20px;
  font-size: 14px;
  color: #fff;
  transform: rotate(45deg);
}

.or-cover-image {
  object-fit: cover;
  height: 100%;
  width: 100%;
  max-height: 500px;
  min-height: 150px;
  display: block;
}

.or-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  color: white;
  padding-top: 40px;
  background: linear-gradient(to top, rgba(0, 0, 0, .4), rgba(0, 0, 0, 0));
}
</style>
