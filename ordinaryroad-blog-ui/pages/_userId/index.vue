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
    <div>用户首页</div>
    <base-material-card avatar="true">
      <template #avatar>
        <or-avatar
          class="v-card--material__avatar elevation-6"
          size="128"
          :avatar="$apis.blog.getFileUrl(blogUser.avatar)"
          :username="blogUser.username"
        />
        <div class="text-center font-weight-bold">
          {{ blogUser.username }}
        </div>
      </template>
    </base-material-card>

    <base-material-card :title="`文章${totalArticle?`（${totalArticle}）`:''}`">
      <or-blog-article-list
        :total.sync="totalArticle"
        :create-by="blogUser.uuid"
      />
    </base-material-card>
  </div>
</template>

<script>
export default {
  asyncData ({
    route,
    redirect,
    $apis
  }) {
    const userId = route.params.userId
    return $apis.blog.user.findById(userId)
      .then((data) => {
        return {
          blogUser: data
        }
      })
      .catch(() => {
        redirect('/404')
      })
  },
  data () {
    return {
      blogUser: null,
      totalArticle: null
    }
  },
  mounted () {
  }
}
</script>

<style scoped>

</style>
