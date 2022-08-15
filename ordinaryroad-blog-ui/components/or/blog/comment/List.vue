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
  <div>
    <div
      v-if="blogArticleComments!==null"
    >
      <v-container>
        <v-row
          v-for="(comment) in blogArticleComments.records"
          :key="comment.uuid"
          class="d-block"
        >
          <or-blog-comment-item
            :item="comment"
            @clickReply="onClickReply"
          />
        </v-row>
        <v-row justify="center" no-gutters class="mt-6">
          <or-load-more-footer
            ref="loadMoreFooter"
            :no-more-data="blogArticleComments.pages === 0 || blogArticleComments.current === blogArticleComments.pages"
            @loadMore="onLoadMore"
          />
        </v-row>
      </v-container>
    </div>
  </div>
</template>

<script>
export default {
  name: 'OrBlogCommentList',
  props: {
    articleId: {
      type: String,
      required: true
    },
    articleComments: {
      type: Object,
      required: true
    }
  },
  data: () => ({
    blogArticleComments: null
  }),
  created () {
    this.blogArticleComments = this.articleComments
  },
  methods: {
    onClickReply ({
      originalComment,
      parentComment
    }) {
      this.$emit('clickReply', {
        originalComment,
        parentComment
      })
    },
    addComment (comment) {
      if (comment.originalId && comment.originalId !== '') {
        // 子评论
        const query = this.$util.query(this.blogArticleComments.records, 'uuid', comment.originalId)[0]
        query.replies.records.unshift(comment)
        query.replies.total = query.replies.records.length
      } else {
        this.blogArticleComments.records.unshift(comment)
      }
    },
    onLoadMore () {
      this.$apis.blog.comment.pageArticle(this.articleId, this.blogArticleComments.current + 1)
        .then((data) => {
          const newRecords = this.blogArticleComments.records.concat(data.records)
          this.blogArticleComments = {
            ...data,
            records: newRecords
          }
          this.$refs.loadMoreFooter.finishLoad()
        })
        .catch(() => {
          this.$refs.loadMoreFooter.finishLoad()
        })
    }
  }
}
</script>

<style scoped>

</style>
