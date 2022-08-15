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
  <v-hover close-delay="100">
    <template #default="{ hover }">
      <v-card
          dark
          :elevation="hover?24:0"
          class="transition-swing"
          @click="onClickArticle(item)"
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
            <span>原创</span>
          </div>

          <!-- 封面上层 -->
          <div class="or-title px-4 pb-1">
            <!-- 创建时间 -->
            <v-card-subtitle class="pt-0 pb-1 text-justify">
              <v-icon dark small>
                mdi-calendar
              </v-icon>
              {{ item.createdTime }}
            </v-card-subtitle>

            <!-- 标题 -->
            <v-card-title class="py-0 text-justify tow-lines-text">
              {{ item.title }}
            </v-card-title>

            <!-- 摘要 -->
            <template v-if="item.summary !== undefined && item.summary !== ''">
              <v-expand-transition>
                <v-card-subtitle
                    v-if="hover"
                    class="py-0 my-0 text-justify font-weight-light four-lines-text transition-fast-in-fast-out"
                >
                  {{ item.summary }}
                </v-card-subtitle>
              </v-expand-transition>
            </template>

            <v-divider class="mt-2"/>

            <!-- 作者信息和操作按钮栏 -->
            <v-list-item>
              <!-- 头像 -->
              <v-list-item-avatar>
                <v-img
                    style="border:1px solid;"
                    alt=""
                    :src="$apis.blog.getFileUrl(item.user.avatar)"
                >
                  <template #placeholder>
                    <v-skeleton-loader type="image"/>
                  </template>
                </v-img>
              </v-list-item-avatar>
              <!-- 用户名 -->
              <v-list-item-content>
                <v-list-item-title
                    class="font-weight-medium"
                    @click="onClickUsername(item)"
                >
                  {{ item.user.username }}
                </v-list-item-title>
                <!-- todo 个性签名 -->
                <v-list-item-subtitle>世间种种平凡都不平凡</v-list-item-subtitle>
              </v-list-item-content>

              <v-list-item-action>
                <v-row
                    no-gutters
                    align="center"
                    justify="end"
                >
                  <v-col v-if="false" class="d-inline-flex align-center">
                    <v-btn icon>
                      <v-icon>mdi-thumb-up-outline</v-icon>
                    </v-btn>
                    <span>123</span>
                  </v-col>

                  <v-col v-if="false" class="d-inline-flex align-center mx-2">
                    <v-btn icon>
                      <v-icon>mdi-thumb-down-outline</v-icon>
                    </v-btn>
                  </v-col>

                  <v-col v-if="false" class="d-inline-flex align-center me-2">
                    <v-icon>mdi-eye</v-icon>
                    <span class="ms-1">{{ item.pv }}</span>
                  </v-col>

                  <v-col v-if="false" class="d-inline-flex align-center">
                    <v-menu>
                      <template #activator="{ on, attrs }">
                        <v-btn
                            v-bind="attrs"
                            icon
                            v-on="on"
                        >
                          <v-icon>
                            mdi-chevron-down
                          </v-icon>
                        </v-btn>
                      </template>
                      <v-list>
                        <v-list-item>
                          <v-list-item-title>操作1</v-list-item-title>
                        </v-list-item>
                        <v-list-item>
                          <v-list-item-title>操作2</v-list-item-title>
                        </v-list-item>
                      </v-list>
                    </v-menu>
                  </v-col>
                </v-row>
              </v-list-item-action>
            </v-list-item>
          </div>
        </div>
      </v-card>
    </template>
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
      summaryShow: false
    }
  },
  watch: {},
  created: function () {
  },
  methods: {
    onClickUsername (item) {
      const { href } = this.$router.resolve({ path: `/${this.item.user.uuid}` })
      window.open(href, '_blank')
    },
    onClickArticle (item) {
      const { href } = this.$router.resolve({ path: `/${item.user.uuid}/article/detail/${item.uuid}` })
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
