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
      <div style="position: relative; overflow: hidden;">
        <!-- 封面 -->
        <img
          alt=""
          :style="{aspectRatio: (-1.2/1000*Math.min(item.uv,1000)+1.6)}"
          class="or-cover-image"
          :src="articleCoverImageUrl(item)"
        >

        <!-- 封面上层 -->
        <div
          class="or-title"
          :class="{
            'pb-1':!$vuetify.breakpoint.smAndDown
          }"
        >
          <v-card-subtitle
            class="d-flex flex-wrap pt-0 align-center"
            :class="{
              'pb-1':!$vuetify.breakpoint.smAndDown,
              'px-2':$vuetify.breakpoint.smAndDown,
              'pb-0':$vuetify.breakpoint.smAndDown,
              'text-caption':$vuetify.breakpoint.smAndDown,
            }"
          >
            <div class="d-inline-flex">
              <!-- 创建时间 -->
              <span class="d-inline-flex align-center me-3">
                <v-icon dark small left>
                  mdi-history
                </v-icon>
                {{ $dayjs(item.createdTime).fromNow() }}
              </span>
              <!-- 浏览量uv -->
              <span v-if="false" class="d-inline-flex align-center me-3">
                <v-icon left small>
                  mdi-account-eye
                </v-icon>
                <span>{{ item.uv }}</span>
              </span>
              <!-- 浏览量pv -->
              <span class="d-inline-flex align-center me-3">
                <v-icon left small>
                  mdi-eye
                </v-icon>
                <span>{{ item.pv }}</span>
              </span>
            </div>

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
          <v-card-title
            class="py-0 text-justify tow-lines-text font-weight-bold"
            :class="{
              'text-subtitle-1':$vuetify.breakpoint.smAndDown,
              'px-2':$vuetify.breakpoint.smAndDown
            }"
          >
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
                class="py-0 my-0 text-justify four-lines-text"
              >
                {{ item.summary }}
              </v-card-subtitle>
            </div>
          </v-expand-transition>

          <v-divider
            :class="`mt-${$vuetify.breakpoint.smAndDown?0:2}`"
          />

          <!-- 作者信息和操作按钮栏 -->
          <v-list-item
            :dense="$vuetify.breakpoint.smAndDown"
            :class="{
              'px-2':$vuetify.breakpoint.smAndDown
            }"
          >
            <v-list-item-avatar
              :size="$vuetify.breakpoint.smAndDown?34:48"
              :class="{
                'my-1':$vuetify.breakpoint.smAndDown,
                'me-2':$vuetify.breakpoint.smAndDown
              }"
            >
              <!-- 头像 -->
              <or-blog-user-avatar
                :user="item.user"
                :size="$vuetify.breakpoint.smAndDown?34:48"
              />
            </v-list-item-avatar>

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

            <v-list-item-action class="ms-1 my-1">
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

        <!-- 是否原创 -->
        <div v-if="item.original" class="primary or-original-label">
          <span>{{ $t('article.originalOptions.original') }}</span>
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
    articleCoverImageUrl () {
      return (item) => {
        // return this.$apis.blog.getFileUrl(item.coverImage, 'https://api.ixiaowai.cn/gqapi/gqapi.php')
        // return 'https://tuapi.eees.cc/fengjing/m/1661427654614.jpg'
        return this.$apis.blog.getFileUrl(item.coverImage, 'https://tuapi.eees.cc/api.php?category={biying,fengjing}&type=302')
      }
    }
  },
  watch: {},
  created () {
  },
  methods: {
    onClickUsername () {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uid}` })
      window.open(href, '_blank')
    },
    onClickArticle () {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uid}/article/${this.item.firstId}` })
      window.open(href, '_blank')
    },
    onClickArticleComments () {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uid}/article/${this.item.firstId}#comments` })
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
  background-color: darkgrey;
  object-fit: cover;
  height: 100%;
  width: 100%;
  max-height: 400px;
  display: block;
  margin: 0 auto;
}

.or-title {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  color: white;
  padding-top: 40px;
  background: linear-gradient(to top, rgba(0, 0, 0, .4), rgba(0, 0, 0, .35), rgba(0, 0, 0, .25), rgba(0, 0, 0, 0.0));
}
</style>
