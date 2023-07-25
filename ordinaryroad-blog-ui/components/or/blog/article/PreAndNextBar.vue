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
  <v-row>
    <v-col>
      <v-card
        v-if="preArticle!=null"
        flat
        tile
        class="pa-5"
        style="width: 100%;height: 100%"
        :href="`/${preArticle.user.uid}/article/${preArticle.firstId}`"
        target="_blank"
      >
        <v-icon class="d-inline">
          mdi-arrow-left
        </v-icon>
        <v-card-subtitle class="d-inline pb-0">
          {{ $t('article.detail.preAndNextArticleBar.pre') }}
        </v-card-subtitle>
        <v-card-title class="d-block py-0">
          {{ preArticle.title }}
        </v-card-title>
      </v-card>
    </v-col>
    <v-col>
      <v-card
        v-if="nextArticle!=null"
        flat
        tile
        class="pa-5 d-block"
        style="width: 100%;height: 100%;text-align: end"
        :href="`/${nextArticle.user.uid}/article/${nextArticle.firstId}`"
        target="_blank"
      >
        <v-card-subtitle class="d-inline pb-0">
          {{ $t('article.detail.preAndNextArticleBar.next') }}
        </v-card-subtitle>
        <v-icon class="d-inline">
          mdi-arrow-right
        </v-icon>
        <v-card-title class="d-block py-0">
          {{ nextArticle.title }}
        </v-card-title>
      </v-card>
    </v-col>
  </v-row>
</template>

<script>
export default {
  name: 'OrBlogArticlePreAndNextBar',
  props: {
    preAndNextArticle: {
      type: Object,
      default: () => ({})
    }
  },
  data: () => ({}),
  computed: {
    preArticle () {
      return this.preAndNextArticle.pre
    },
    nextArticle () {
      return this.preAndNextArticle.next
    }
  },
  methods: {
  }
}
</script>

<style scoped>

</style>
