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
      @submit="onInputSubmit(input)"
    >
      <v-text-field
        v-model.trim="input"
        append-outer-icon="mdi-magnify"
        clearable
        :placeholder="$t('search')"
        outlined
        :rules="[$rules.max100Chars]"
        @click:append-outer="onInputSubmit(input)"
      />
    </v-form>
    <v-tabs
      v-model="tabModel"
      class="sticky-top"
      style="z-index: 2"
      :style="`top :${$vuetify.breakpoint.smAndDown?'56px':'64px'} !important;`"
    >
      <v-tab>{{ $t('articleCount', [`${totalArticles ? $t('parentheses', [totalArticles]) : ''}`]) }}</v-tab>
      <v-tab>{{ $t('typeCount', [`${totalTypes ? $t('parentheses', [totalTypes]) : ''}`]) }}</v-tab>
      <v-tab>{{ $t('userCount', [`${totalUsers ? $t('parentheses', [totalUsers]) : ''}`]) }}</v-tab>
    </v-tabs>

    <v-tabs-items v-model="tabModel">
      <v-tab-item eager>
        <or-blog-article-search
          ref="articleSearch"
          :total.sync="totalArticles"
          :loading.sync="loading"
        />
      </v-tab-item>
      <v-tab-item eager>
        <or-blog-type-search
          ref="typeSearch"
          :total.sync="totalTypes"
          :loading.sync="loading"
        />
      </v-tab-item>
      <v-tab-item eager>
        <or-blog-user-search
          ref="userSearch"
          :total.sync="totalUsers"
          :loading.sync="loading"
        />
      </v-tab-item>
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
    tabModel: 0,
    input: '',
    loading: false,
    totalArticles: null,
    totalTypes: null,
    totalUsers: null
  }),
  watch: {
    tabModel () {
      this.onInputSubmit(this.input)
    }
  },
  mounted () {
    this.onInputSubmit(this.input)
  },
  methods: {
    onInputSubmit (input) {
      if (!input || input.trim() === '') {
        return
      }
      if (!this.$refs.searchForm.validate()) {
        return
      }

      switch (this.tabModel) {
        case 0:
          if (this.$refs.articleSearch) {
            if (this.$refs.articleSearch.$refs.articleList.title !== this.input) {
              this.$refs.articleSearch.$refs.articleList.title = this.input
              this.$refs.articleSearch.$refs.articleList.tagName = this.input
              this.$refs.articleSearch.$refs.articleList.getArticles(false)
            }
          }
          break
        case 1:
          if (this.$refs.typeSearch) {
            if (this.$refs.typeSearch.name !== this.input) {
              this.$refs.typeSearch.name = this.input
              this.$refs.typeSearch.getTypes(false)
            }
          }
          break
        case 2:
          if (this.$refs.userSearch) {
            if (this.$refs.userSearch.$refs.userList.username !== this.input) {
              this.$refs.userSearch.$refs.userList.username = this.input
              this.$refs.userSearch.$refs.userList.getUsers(false)
            }
          }
          break
        default:
      }
    }
  }
}
</script>

<style scoped>

</style>
