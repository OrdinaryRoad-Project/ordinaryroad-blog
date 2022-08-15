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
  <v-container fluid class="pa-0">
    <or-blog-article-detail :article="article" :article-comments="articleComments" />
  </v-container>
</template>

<script>
export default {
  layout: 'empty',
  async asyncData ({
    route,
    $apis
  }) {
    // 判断文章是否存在
    const id = route.params.id || ''
    if (id && id.trim() !== '') {
      const article = await $apis.blog.article.findPublishById(id)
      const articleComments = await $apis.blog.comment.pageArticle(id, 1)
      return {
        article,
        articleComments
      }
    }
  },
  data () {
    return {
      article: null,
      articleComments: {}
    }
  },
  mounted () {
    if (this.article == null) {
      this.$dialog({
        persistent: true,
        content: this.$i18n.t('status.article.notFound'),
        confirmText: this.$i18n.t('retry'),
        cancelText: this.$i18n.t('back')
      }).then(({ isConfirm }) => {
        if (isConfirm) {
          this.$router.go(0)
        } else {
          this.$router.back()
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
