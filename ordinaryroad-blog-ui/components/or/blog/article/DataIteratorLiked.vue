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
  <v-container>
    <or-blog-article-data-iterator
      ref="articleIterator"
      @getItems="onGetItems"
    >
      <template #default="{ props }">
        <v-container>
          <v-row
            class="d-flex flex-wrap"
            dense
          >
            <v-col
              v-for="item in props.items"
              :key="item.uuid"
              cols="12"
              sm="12"
              md="12"
              lg="6"
              xl="4"
            >
              <v-hover open-delay="200">
                <template #default="{ hover }">
                  <v-card>
                    <or-blog-article-item :item="item">
                      <template #actions>
                        <v-menu open-on-hover>
                          <template #activator="{ on, attrs }">
                            <v-btn
                              small
                              class="ms-3"
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
                            <v-list-item @click="unlikesArticle(item)">
                              <v-list-item-content>
                                <v-list-item-title>{{ $t('article.actions.unlike') }}</v-list-item-title>
                              </v-list-item-content>
                            </v-list-item>
                            <v-list-item
                              v-if="item.user.uuid===userInfo.user.uuid"
                              :to="`/dashboard/article/${item.uuid}`"
                            >
                              <v-list-item-content>
                                <v-list-item-title>{{ $t('article.actions.edit') }}</v-list-item-title>
                              </v-list-item-content>
                            </v-list-item>
                          </v-list>
                        </v-menu>
                      </template>
                    </or-blog-article-item>
                    <v-expand-transition>
                      <v-overlay
                        v-if="hover"
                        class="transition-swing"
                        absolute
                        :opacity="0"
                        style="height: 60px; z-index: 0; border-radius: 0"
                      >
                        <v-alert
                          dense
                          class="d-inline-flex my-0 elevation-4"
                          type="info"
                        >
                          点赞时间： {{ $dayjs(item.likedTime).format() }}
                        </v-alert>
                      </v-overlay>
                    </v-expand-transition>
                  </v-card>
                </template>
              </v-hover>
            </v-col>
          </v-row>
        </v-container>
      </template>
    </or-blog-article-data-iterator>
    <v-snackbar
      v-model="snackbarModel"
      bottom
      color="success"
      @input="onSnackbarInputChange"
    >
      取消点赞成功
      <template #action>
        <v-btn icon @click="likesArticle(selectedItem)">
          <v-icon>mdi-restore</v-icon>
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'OrBlogArticleDataIteratorLiked',
  props: {},
  data: () => ({
    snackbarModel: null,
    /**
     * 恢复点赞是否成功
     */
    canceled: false,
    selectedIndex: -1,
    selectedItem: null
  }),
  computed: {
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    })
  },
  watch: {},
  created () {
  },
  mounted () {
  },
  methods: {
    onSnackbarInputChange (val) {
      if (!val) {
        if (this.canceled) {
          // 手动点击恢复，重置为false
          this.canceled = false
        } else {
          // 延迟刷新表格
          this.$refs.articleIterator.$refs.dataIterator.getItems()
        }
      }
    },
    likesArticle () {
      this.$apis.blog.article.likesArticle(this.selectedItem.uuid)
        .then(() => {
          this.$snackbar.success('已撤销')
          this.canceled = true
          this.snackbarModel = false
          this.$refs.articleIterator.$refs.dataIterator.getItems()
        })
    },
    unlikesArticle (item) {
      this.selectedItem = Object.assign({}, item)
      // 删除点赞记
      this.$dialog({
        content: `确定取消点赞 ${item.title}？`,
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.unlikesArticle(item.uuid)
            .then(() => {
              this.snackbarModel = true
              dialog.cancel()
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onGetItems ({
      options,
      offset,
      limit,
      sortBy,
      sortDesc
    }) {
      this.$apis.blog.article.pageOwnLiked(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.$refs.articleIterator.searchParams)
        .then((result) => {
          this.$refs.articleIterator.$refs.dataIterator.loadSuccessfully(result.records, result.total)
        })
        .catch(() => {
          this.$refs.articleIterator.$refs.dataIterator.loadFinish()
        })
    }
  }
}
</script>
