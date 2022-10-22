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
    <div>
      <v-sheet
        outlined
        rounded
        class="mb-2"
      >
        <v-card-title>{{ $t('article.pinned') }}</v-card-title>
        <vue-masonry-wall
          v-if="pinnedArticles.data&&pinnedArticles.data.length"
          :items="pinnedArticles.data"
          :options="{default:2,padding:1,width:500}"
          :ssr="{columns: 2}"
        >
          <template #default="{ item }">
            <or-blog-article-item :item="item" />
          </template>
        </vue-masonry-wall>
        <or-empty v-if="!pinnedArticles.data.length" />
      </v-sheet>

      <or-blog-article-daily-posts-chart
        :vertical="$vuetify.breakpoint.smAndDown"
        :create-by="userId"
      />
    </div>
  </v-container>
</template>

<script>
import VueMasonryWall from 'vue-masonry-wall'

export default {
  name: 'OrBlogUserOverview',
  components: {
    VueMasonryWall
  },
  props: {
    userId: {
      type: String,
      required: true
    }
  },
  data: () => ({
    pinnedArticles: {
      loading: true,
      data: []
    }
  }),
  created () {
    this.getTop6Articles()
  },
  methods: {
    getTop6Articles () {
      this.$apis.blog.article.getPinnedArticles({ userId: this.userId })
        .then((data) => {
          this.pinnedArticles.loading = false
          this.pinnedArticles.data = data
        })
        .catch(() => {
          this.pinnedArticles.loading = false
        })
    }
  }
}
</script>

<style scoped>

</style>
