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
    <v-row v-if="typeInfoPageItems==null">
      加载失败
    </v-row>

    <or-empty v-else-if="typeInfoPageItems&&typeInfoPageItems.total===0" />

    <v-list v-else-if="typeInfoPageItems&&typeInfoPageItems.records.length>0">
      <v-list-item
        v-for="item in typeInfoPageItems.records"
        :key="item.uuid"
        :to="`/${item.user.uid}/type/${item.uuid}`"
        target="_blank"
      >
        <v-list-item-content>
          <v-list-item-title>
            {{ item.name }}{{
              item.articleCount ? $t('parenthesesWithSpace', [item.articleCount]) : ''
            }}
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>

    <or-load-more-footer
      v-if="typeInfoPageItems.total>0"
      ref="loadMoreFooter"
      class="mt-4"
      :no-more-data="loadMoreOptions.noMoreData"
      @loadMore="getTypes()"
    />
  </v-container>
</template>

<script>
export default {
  name: 'OrBlogTypeTreeview',
  props: {
    createBy: {
      type: String,
      default: null
    }
  },
  data: () => ({
    loadMoreOptions: {
      loading: false,
      noMoreData: false
    },
    typeInfoPageItems: {
      records: [],
      current: 1
    }
  }),
  watch: {
    'typeInfoPageItems.total' () {
      this.$emit('update:total', this.typeInfoPageItems.total)
    }
  },
  mounted () {
    this.getTypes(false)
  },
  methods: {
    getTypes (loadMore = true) {
      if (loadMore) {
        if (this.loadMoreOptions.noMoreData) {
          return
        }
        if (this.loadMoreOptions.loading) {
          return
        }
        // 加载更多
        this.$refs.loadMoreFooter.startLoading(true)
        this.loadMoreOptions.loading = true
      }
      const page = loadMore ? this.typeInfoPageItems.current + 1 : 1
      this.$apis.blog.type.pageInfo(page, 20, { createBy: this.createBy })
        .then((data) => {
          if (loadMore) {
            this.$refs.loadMoreFooter.finishLoad()
            const newRecords = this.typeInfoPageItems.records.concat(data.records)
            this.typeInfoPageItems = {
              ...data,
              records: newRecords
            }
            if (this.typeInfoPageItems.current === this.typeInfoPageItems.pages) {
              this.loadMoreOptions.noMoreData = true
            }
            setTimeout(() => {
              this.loadMoreOptions.loading = false
            }, 1000)
          } else {
            this.typeInfoPageItems = data
            this.loadMoreOptions = {
              loading: false,
              noMoreData: data.pages === 0 || data.current === data.pages
            }
            this.$emit('loadFinish')
          }
        })
        .catch(() => {
          this.typeInfoPageItems = null
          if (loadMore) {
            this.$refs.loadMoreFooter.finishLoad()
            setTimeout(() => {
              this.loadMoreOptions.loading = false
            }, 1000)
          }
        })
    }
  }
}
</script>

<style scoped>

</style>
