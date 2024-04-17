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
  <v-container fluid>
    <v-row
      v-if="articlePageItems==null"
    >
      加载失败
    </v-row>

    <or-empty v-else-if="articlePageItems&&articlePageItems.total===0" />

    <vue-masonry-wall
      v-else-if="articlePageItems&&articlePageItems.records.length>0"
      :items="articlePageItems.records"
      :options="options"
      :ssr="{columns: 1}"
      @append="append"
    >
      <template #default="{item}">
        <or-blog-article-item v-if="item" :item="item" />
      </template>
    </vue-masonry-wall>

    <or-load-more-footer
      v-if="articlePageItems.total!==0"
      ref="loadMoreFooter"
      class="mt-4"
      :no-more-data="loadMoreOptions.noMoreData"
      @loadMore="getArticles()"
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
    useSearchApi: {
      type: Boolean,
      default: false
    },
    createBy: {
      type: String,
      default: null
    },
    typeId: {
      type: String,
      default: null
    },
    autoLoadMore: {
      type: Boolean,
      default: false
    },
    itemWidth: {
      type: Number,
      default: 500
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

    /**
     * 最近append加载时间
     */
    lastAppendTime: 0,

    tagName: null,
    title: null,
    summary: null,
    content: null
  }),
  computed: {
    options () {
      const padding = {
        default: 4
      }
      /* if (this.$vuetify.breakpoint.xs) {
        width = 500
      } else if (this.$vuetify.breakpoint.smAndDown) {
        width = 500
      } else if (this.$vuetify.breakpoint.mdAndDown) {
        width = 500
      } else if (this.$vuetify.breakpoint.lgAndDown) {
        width = 500
      } else if (this.$vuetify.breakpoint.xl) {
        width = 500
      } */
      return { width: this.itemWidth, padding }
    }
  },
  watch: {
    'articlePageItems.total' () {
      this.$emit('update:total', this.articlePageItems.total)
    }
  },
  mounted () {
  },
  created () {
    if (this.autoLoadMore) {
      this.lastAppendTime = new Date().getTime()
      this.getArticles(false)
    }
  },
  methods: {
    getArticles (loadMore = true) {
      if (loadMore) {
        if (this.loadMoreOptions.noMoreData) {
          // console.log('没有更多数据啦')
          return
        }
        if (this.loadMoreOptions.loading) {
          // console.log('正在加载，请等待')
          return
        }
        // 加载更多
        this.$refs.loadMoreFooter.startLoading(true)
        this.loadMoreOptions.loading = true
      }
      // console.log('开始加载', this.articlePageItems.current + 1)
      this.$emit('update:loading', true)
      const page = loadMore ? this.articlePageItems.current + 1 : 1
      let promise
      if (this.useSearchApi) {
        promise = this.$apis.blog.article.searchPublish(page, {
          typeId: this.typeId,
          createBy: this.createBy,
          tagName: this.tagName,
          title: this.title,
          summary: this.summary,
          content: this.content
        })
      } else {
        promise = this.$apis.blog.article.pagePublish(page, {
          typeId: this.typeId,
          createBy: this.createBy,
          tagName: this.tagName,
          title: this.title,
          summary: this.summary,
          content: this.content
        })
      }
      promise.then((data) => {
        if (loadMore) {
          this.$refs.loadMoreFooter.finishLoad()
          // console.log('加载完成', data.current)
          const newRecords = this.articlePageItems.records.concat(data.records)
          this.articlePageItems = {
            ...data,
            records: newRecords
          }
          if (this.articlePageItems.current === this.articlePageItems.pages) {
            this.loadMoreOptions.noMoreData = true
            // console.log('没有更多数据啦')
          }
          this.loadMoreOptions.loading = false
        } else {
          this.articlePageItems = data
          this.loadMoreOptions = {
            loading: false,
            noMoreData: data.pages === 0 || data.current === data.pages
          }
          this.$emit('loadFinish')
        }
        this.$emit('update:loading', false)
        if (this.autoLoadMore) {
          this.lastAppendTime = new Date().getTime()
        }
      })
        .catch(() => {
          if (loadMore) {
            this.$refs.loadMoreFooter.finishLoad()
            this.loadMoreOptions.loading = false
          }
          this.$emit('update:loading', false)
        })
    },
    append () {
      if (this.autoLoadMore) {
        if (new Date().getTime() - this.lastAppendTime >= 1000) {
          this.getArticles()
        }
      }
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
