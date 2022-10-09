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
      v-if="userPageItems==null"
    >
      加载失败
    </v-row>
    <v-list v-else>
      <v-list-item
        v-for="item in userPageItems.records"
        :key="item.uuid"
        target="_blank"
        :to="`/${item.uuid}`"
      >
        <v-list-item-avatar>
          <or-avatar
            :username="item.username"
            :avatar="$apis.blog.getFileUrl(item.avatar)"
          />
        </v-list-item-avatar>
        <v-list-item-title>{{ item.username }}</v-list-item-title>
      </v-list-item>
    </v-list>

    <or-load-more-footer
      ref="loadMoreFooter"
      class="mt-4"
      :no-more-data="loadMoreOptions.noMoreData"
      @loadMore="getUsers()"
    />
  </v-container>
</template>

<script>

export default {
  name: 'OrBlogUserList',
  props: {},
  data: () => ({
    loadMoreOptions: {
      loading: false,
      noMoreData: false
    },
    userPageItems: {
      records: [],
      current: 1
    },

    username: null
  }),
  computed: {},
  watch: {
    'userPageItems.total' () {
      this.$emit('update:total', this.userPageItems.total)
    }
  },
  mounted () {
  },
  created () {
  },
  methods: {
    getUsers (loadMore = true) {
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
      this.$emit('update:loading', true)
      const page = loadMore ? this.userPageItems.current + 1 : 1
      this.$apis.blog.user.page(page, 20, { username: this.username })
        .then((data) => {
          if (loadMore) {
            this.$refs.loadMoreFooter.finishLoad()
            const newRecords = this.userPageItems.records.concat(data.records)
            this.userPageItems = {
              ...data,
              records: newRecords
            }
            if (this.userPageItems.current === this.userPageItems.pages) {
              this.loadMoreOptions.noMoreData = true
            }
            setTimeout(() => {
              this.loadMoreOptions.loading = false
            }, 1000)
          } else {
            this.userPageItems = data
            this.loadMoreOptions = {
              loading: false,
              noMoreData: data.pages === 0 || data.current === data.pages
            }
            this.$emit('loadFinish')
          }
          this.$emit('update:loading', false)
        })
        .catch(() => {
          if (loadMore) {
            this.$refs.loadMoreFooter.finishLoad()
            setTimeout(() => {
              this.loadMoreOptions.loading = false
            }, 1000)
          }
          this.$emit('update:loading', false)
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
