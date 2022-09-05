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
    <v-row
      v-if="articlePageItems==null"
    >
      加载失败，点我重试
    </v-row>
    <vue-masonry-wall
      v-else-if="articlePageItems.records.length > 0"
      :items="articlePageItems.records"
      :options="options"
      :ssr="{columns: 2}"
      @append="autoLoadMore&&appendArticles()"
    >
      <template #default="{item}">
        <or-blog-article-item :item="item" />
      </template>
    </vue-masonry-wall>

    <or-load-more-footer
      ref="loadMoreFooter"
      class="mt-4"
      :no-more-data="loadMoreOptions.noMoreData"
      @loadMore="!autoLoadMore&&appendArticles()"
    />
  </v-container>
</template>

<script>
import VueMasonryWall from 'vue-masonry-wall'

export default {
  name: 'OrBlogArticleList',
  components: {
    VueMasonryWall
  },
  props: {
    createBy: {
      type: String,
      default: null
    },
    autoLoadMore: {
      type: Boolean,
      default: false
    }
  },
  data: () => ({
    loadMoreOptions: {
      loading: false,
      noMoreData: false
    },
    articlePageItems: {
      records: [],
      current: 1
    },

    options: {
      width: 500,
      padding: {
        // 1: 1,
        default: 1
      }
    }
  }),
  watch: {
    'articlePageItems.total' () {
      this.$emit('update:total', this.articlePageItems.total)
    }
  },
  mounted () {
  },
  created () {
    this.$apis.blog.article.pagePublish(1, { createBy: this.createBy })
      .then((data) => {
        this.articlePageItems = data
        this.loadMoreOptions = {
          loading: false,
          noMoreData: data.pages === 0 || data.current === data.pages
        }
      })
  },
  methods: {
    appendArticles () {
      if (this.loadMoreOptions.noMoreData) {
        console.log('没有更多数据啦')
        return
      }
      if (this.loadMoreOptions.loading) {
        console.log('正在加载，请等待')
        return
      }
      // TODO 加载更多
      this.$refs.loadMoreFooter.startLoading(true)
      this.loadMoreOptions.loading = true
      console.log('开始加载', this.articlePageItems.current + 1)
      this.$apis.blog.article.pagePublish(this.articlePageItems.current + 1, { createBy: this.createBy })
        .then((data) => {
          this.$refs.loadMoreFooter.finishLoad()
          console.log('加载完成', data.current)
          const newRecords = this.articlePageItems.records.concat(data.records)
          this.articlePageItems = {
            ...data,
            records: newRecords
          }
          if (this.articlePageItems.current === this.articlePageItems.pages) {
            this.loadMoreOptions.noMoreData = true
            console.log('没有更多数据啦')
          }
          setTimeout(() => {
            this.loadMoreOptions.loading = false
          }, 1000)
        })
        .catch(() => {
          this.$refs.loadMoreFooter.finishLoad()
          setTimeout(() => {
            this.loadMoreOptions.loading = false
          }, 1000)
        })
    }
  }
}
</script>

<style scoped>

img {
  object-fit: cover;
  width: 100%;
  height: 100%;
  line-height: 0;
  display: block;
}
</style>
