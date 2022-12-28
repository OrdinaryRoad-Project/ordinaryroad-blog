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
    <or-blog-article-detail :article="article" :preset-article-comments="articleComments" />
  </div>
</template>

<script>
export default {
  async asyncData ({ route, $apis, error, store }) {
    const tokenValue = store.getters['user/getTokenValue']
    const id = route.params.id || ''
    try {
      const article = await $apis.blog.article.findOffendById(tokenValue, id)
      if (!article) {
        error({ statusCode: 404, message: '文章不存在' })
      }

      const articleComments = await $apis.blog.comment.pageArticle(article.firstId, 1)
      return {
        article,
        articleComments
      }
    } catch {
      error({ statusCode: 404, message: '文章不存在' })
    }
  },
  data: () => ({
    article: null,
    articleComments: {}
  })
}
</script>

<style scoped>

</style>
