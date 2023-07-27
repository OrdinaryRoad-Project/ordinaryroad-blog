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
      v-if="friendLinkPageItems==null"
    >
      加载失败
    </v-row>

    <div v-else-if="friendLinkPageItems&&friendLinkPageItems.total===0">
      <or-empty>
        <template #append>
          <v-btn
            depressed
            color="primary"
            to="/friend_link/apply"
          >
            {{ $t('friendLink.applyNow') }}
            <v-icon right>
              mdi-chevron-right
            </v-icon>
          </v-btn>
        </template>
      </or-empty>
    </div>

    <v-row v-else-if="friendLinkPageItems&&friendLinkPageItems.records.length">
      <v-col
        v-for="item in friendLinkPageItems.records"
        :key="item.uuid"
        cols="6"
        sm="4"
        md="3"
        lg="2"
        class="pa-0 child-flex"
      >
        <or-blog-friend-link-item :item="item" />
      </v-col>
    </v-row>

    <or-load-more-footer
      v-if="friendLinkPageItems.total!==0"
      ref="loadMoreFooter"
      class="mt-8"
      :no-more-data="loadMoreOptions.noMoreData"
      @loadMore="getFriendLinks()"
    />
  </v-container>
</template>

<script>
export default {
  name: 'OrBlogFriendLinkList',
  props: {
    /**
     * SEO友好，必填
     */
    presetItems: {
      type: Object,
      required: true
    }
  },
  data: () => ({
    loadMoreOptions: {
      loading: false,
      noMoreData: false
    },
    friendLinkPageItems: {
      records: [],
      current: 1
    }
  }),
  computed: {},
  watch: {
    'friendLinkPageItems.total' () {
      this.$emit('update:total', this.friendLinkPageItems.total)
    }
  },
  mounted () {
  },
  created () {
    this.friendLinkPageItems = this.presetItems
    this.loadMoreOptions.noMoreData = this.presetItems.current === this.presetItems.pages
    this.unshiftApplyItem()
  },
  methods: {
    unshiftApplyItem () {
      if (!this.friendLinkPageItems.records.length || this.friendLinkPageItems.records[0].TYPE !== 'APPLY') {
        this.friendLinkPageItems.records.unshift({ uuid: 'APPLY', TYPE: 'APPLY' })
      }
    },
    getFriendLinks (loadMore = true) {
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
      const page = loadMore ? this.friendLinkPageItems.current + 1 : 1
      this.$apis.blog.friend_link.pageInfo(page, 50)
        .then((data) => {
          if (loadMore) {
            this.$refs.loadMoreFooter.finishLoad()
            const newRecords = this.friendLinkPageItems.records.concat(data.records)
            this.friendLinkPageItems = {
              ...data,
              records: newRecords
            }
            if (this.friendLinkPageItems.current === this.friendLinkPageItems.pages) {
              this.loadMoreOptions.noMoreData = true
            }
            setTimeout(() => {
              this.loadMoreOptions.loading = false
            }, 1000)
          } else {
            this.friendLinkPageItems = data
            this.loadMoreOptions = {
              loading: false,
              noMoreData: data.pages === 0 || data.current === data.pages
            }
            this.$emit('loadFinish')
          }
          this.unshiftApplyItem()
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

</style>
