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
  <nuxt-child :user="user" />
</template>

<script>
export default {
  layout ({ route }) {
    const routeName = route.name
    if (routeName === 'userId-article-id') {
      return 'empty'
    } else {
      return 'default'
    }
  },
  validate ({ params, query, store }) {
    return /^\d+$/.test(params.userId)
  },
  asyncData ({ route, $apis, error }) {
    const userId = Number(route.params.userId || 0)
    return $apis.blog.user.findByUid(userId)
      .then((data) => {
        return { user: data }
      })
      .catch(() => {
        error({ statusCode: 404, message: '用户不存在' })
      })
  },
  data: () => ({
    user: null
  }),
  created () {
  }
}
</script>

<style scoped>

</style>
