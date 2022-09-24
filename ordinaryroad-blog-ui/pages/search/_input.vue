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
    <v-form
      ref="searchForm"
      :disabled="loading"
      @submit.native.prevent
      @submit="onInputChange(input)"
    >
      <v-text-field
        v-model.trim="input"
        append-outer-icon="mdi-magnify"
        clearable
        :placeholder="$t('search')"
        outlined
        :rules="[$rules.max100Chars]"
        @click:append-outer="onInputChange(input)"
      />
    </v-form>
    <v-tabs v-model="tab">
      <v-tab>{{ $t('articleCount', [`${totalArticles ? $t('parentheses', [totalArticles]) : ''}`]) }}</v-tab>
      <v-tab disabled>
        分类
      </v-tab>
      <v-tab disabled>
        用户
      </v-tab>
    </v-tabs>

    <v-tabs-items v-model="tab">
      <v-tab-item>
        <or-blog-article-list
          ref="articleList"
          :auto-load-more="false"
          :total.sync="totalArticles"
          @loadFinish="onArticleLoadFinish"
        />
      </v-tab-item>
      <v-tab-item />
    </v-tabs-items>
  </div>
</template>

<script>
export default {
  asyncData ({ route }) {
    return {
      input: route.params.input
    }
  },
  data: () => ({
    tab: 0,
    input: '',
    loading: false,
    totalArticles: null
  }),
  mounted () {
    this.onInputChange(this.input)
  },
  methods: {
    onArticleLoadFinish () {
      this.loading = false
    },
    onInputChange (input) {
      if (!input || input.trim() === '') {
        return
      }
      if (!this.$refs.searchForm.validate()) {
        return
      }
      this.loading = true
      this.$refs.articleList.title = input
      this.$refs.articleList.getArticles(false)
    }
  }
}
</script>

<style scoped>

</style>
