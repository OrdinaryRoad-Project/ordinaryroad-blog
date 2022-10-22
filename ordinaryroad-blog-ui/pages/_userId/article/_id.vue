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
    <or-blog-article-detail :article="article" :preset-article-comments="articleComments" />
  </v-container>
</template>

<script>

export default {
  props: {
    user: {
      type: Object,
      required: true
    }
  },
  async asyncData ({
    route,
    $apis,
    store,
    redirect,
    error
  }) {
    const tokenValue = store.getters['user/getTokenValue']
    // 判断文章是否存在
    const userId = route.params.userId || ''
    const id = route.params.id || ''
    if (id && id.trim() !== '') {
      try {
        const article = await $apis.blog.article.findPublishById(tokenValue, id)
        if (!article) {
          error({ statusCode: 404, message: '文章不存在' })
        }
        if (article.firstId !== id) {
          redirect(`/${userId}/article/${article.firstId}`)
        }
        if (article.user.uid !== userId) {
          redirect(`/${userId}`)
        }

        const articleComments = await $apis.blog.comment.pageArticle(id, 1)
        return {
          article,
          articleComments
        }
      } catch {
        error({ statusCode: 404, message: '文章不存在' })
      }
    } else {
      error({ statusCode: 404, message: '文章不存在' })
    }
  },
  data () {
    return {
      article: null,
      articleComments: {}
    }
  },
  head () {
    return {
      title: this.article.title,
      titleTemplate: `%s - ${this.$t('appName')}`,
      meta: [
        { name: 'keywords', content: this.keywords },
        { hid: 'description', name: 'description', content: this.article.summary }
      ]
    }
  },
  computed: {
    keywords () {
      let keywords = ''
      if (this.article.type) {
        keywords += this.article.type.name
      }
      if (this.article.tags) {
        this.article.tags.forEach((item) => {
          keywords += ','
          keywords += item.name
        })
      }
      return keywords
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
