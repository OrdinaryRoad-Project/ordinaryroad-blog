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
      hide-default-footer
      @getItems="onGetItems"
    >
      <template #default="{ props }">
        <v-container class="ma-0 pa-0">
          <v-timeline
            align-top
            :dense="$vuetify.breakpoint.mdAndDown"
          >
            <v-timeline-item
              v-for="(item) in props.items"
              :key="item.uuid"
            >
              <template #opposite>
                <span v-if="!$vuetify.breakpoint.mdAndDown">
                  {{ $t('article.browsedTime', [item.lastBrowsedTime]) }}
                  <br>
                  {{
                    item.firstBrowsedTime !== item.lastBrowsedTime ? $t('article.firstBrowsedTime', [item.firstBrowsedTime]) : null
                  }}</span>
              </template>

              <v-card-title v-if="$vuetify.breakpoint.mdAndDown">
                {{ item.browsedTime }}
              </v-card-title>
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
                      <v-list-item @click="unBrowsesArticle(item)">
                        <v-list-item-content>
                          <v-list-item-title>{{ $t('delete') }}</v-list-item-title>
                        </v-list-item-content>
                      </v-list-item>
                      <v-list-item
                        v-if="item.user.uuid===userInfo.user.uuid"
                        :to="`/dashboard/article/writing/${item.uuid}`"
                      >
                        <v-list-item-content>
                          <v-list-item-title>{{ $t('article.actions.edit') }}</v-list-item-title>
                        </v-list-item-content>
                      </v-list-item>
                    </v-list>
                  </v-menu>
                </template>
              </or-blog-article-item>
            </v-timeline-item>
          </v-timeline>
          <or-load-more-footer
            ref="loadMoreFooter"
            :no-more-data="$refs.articleIterator.$refs.dataIterator.dataIteratorParams.totalItems<=$refs.articleIterator.$refs.dataIterator.dataIteratorParams.items.length"
            @loadMore="onLoadMore"
          />
        </v-container>
      </template>
    </or-blog-article-data-iterator>
  </v-container>
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'OrBlogArticleDataIteratorBrowsed',
  props: {},
  data: () => ({
    selectedIndex: -1,
    selectedItem: null,
    noMoreData: false
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
    onLoadMore () {
      this.$refs.articleIterator.$refs.dataIterator.options.page = this.$refs.articleIterator.$refs.dataIterator.options.page + 1
    },
    unBrowsesArticle (item) {
      this.selectedItem = Object.assign({}, item)
      // 删除点赞记
      this.$dialog({
        content: `确定删除文章 ${item.title} 的浏览记录？`,
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.unBrowsesArticle(item.uuid)
            .then(() => {
              this.$snackbar.success('删除成功')

              const items = this.$refs.articleIterator.$refs.dataIterator.dataIteratorParams.items
              this.$refs.articleIterator.$refs.dataIterator.loadSuccessfully(
                this.$util.remove(items, 'uuid', item.uuid),
                this.$refs.articleIterator.$refs.dataIterator.dataIteratorParams.totalItems - 1
              )

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
      this.$apis.blog.article.pageOwnBrowsed(options.page, options.itemsPerPage, sortBy, sortDesc, this.$refs.articleIterator.searchParams)
        .then((result) => {
          const records = result.records
          if (options.page > 1) {
            records.unshift(...this.$refs.articleIterator.$refs.dataIterator.dataIteratorParams.items)
          }
          this.$refs.articleIterator.$refs.dataIterator.loadSuccessfully(records, result.total)
          this.$refs.loadMoreFooter.finishLoad()
        })
        .catch(() => {
          this.$refs.articleIterator.$refs.dataIterator.loadFinish()
          this.$refs.loadMoreFooter.finishLoad()
        })
    }
  }
}
</script>
