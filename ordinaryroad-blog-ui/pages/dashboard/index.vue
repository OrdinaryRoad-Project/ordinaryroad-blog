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
    <base-material-card :title="$t('userMenuTitles.dashboard')">
      <v-card flat>
        <v-card-title>我的数据</v-card-title>
        <v-container fluid>
          <v-row>
            <v-col
              v-for="countOption in countOptions"
              :key="countOption.to"
              cols="12"
              sm="12"
              md="6"
              lg="3"
              xl="3"
            >
              <v-hover :disabled="!countOption.to">
                <template #default="{ hover }">
                  <v-card
                    :outlined="!hover"
                    :elevation="hover?4:0"
                    class="transition-swing"
                    :to="countOption.to"
                  >
                    <v-card-title>
                      <v-icon
                        v-if="countOption.icon"
                        left
                        color="primary"
                      >
                        {{ countOption.icon }}
                      </v-icon>
                      {{ countOption.title }}
                      <v-spacer />
                      <v-slide-x-reverse-transition>
                        <span
                          v-if="!hover"
                          class="text-h4 font-weight-bold primary--text"
                          style="position: absolute; right: 16px"
                        >{{ countOption.loading ? '-' : countOption.data }}</span>
                        <v-icon
                          v-else
                          style="position: absolute; right: 16px"
                          size="24"
                          class="ma-2"
                        >
                          mdi-chevron-right
                        </v-icon>
                      </v-slide-x-reverse-transition>
                    </v-card-title>
                  </v-card>
                </template>
              </v-hover>
            </v-col>
          </v-row>

          <v-row>
            <v-col
              cols="8"
              sm="6"
              md="12"
              lg="12"
              xl="8"
              order="10"
              order-md="0"
            >
              <or-blog-article-daily-posts-chart
                :vertical="$vuetify.breakpoint.smAndDown"
              />
            </v-col>
            <v-col
              cols="12"
              sm="12"
              md="12"
              lg="6"
              xl="5"
            >
              <or-blog-type-top-n-chart />
            </v-col>
            <v-col
              cols="12"
              sm="12"
              md="12"
              lg="6"
              xl="5"
            >
              <or-blog-article-top-n-comments-chart />
            </v-col>
            <v-col
              cols="12"
              sm="12"
              md="12"
              lg="6"
              xl="5"
            >
              <or-blog-article-top-n-liked-chart />
            </v-col>
            <v-col
              cols="12"
              sm="12"
              md="12"
              lg="6"
              xl="5"
            >
              <or-blog-article-top-n-browsed-chart />
            </v-col>
          </v-row>
        </v-container>
      </v-card>
    </base-material-card>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data: () => ({
    countOptions: [],
    articlesCountOptions: {
      title: '已发布文章',
      to: '/dashboard/article/status/PUBLISH',
      icon: 'mdi-file-document-multiple',
      loading: true,
      data: null
    },
    articlesCountBrowsedOptions: {
      title: '已浏览文章',
      to: '/dashboard/article/browsed',
      icon: 'mdi-history',
      loading: true,
      data: null
    },
    articlesCountLikedOptions: {
      title: '已点赞文章',
      to: '/dashboard/article/liked',
      icon: 'mdi-thumb-up',
      loading: true,
      data: null
    },
    typesCountOptions: {
      title: '分类',
      to: '/dashboard/type',
      icon: 'mdi-view-list',
      loading: true,
      data: null
    },
    commentsCountOptions: {
      title: '评论发表数',
      to: null,
      icon: null,
      loading: true,
      data: null
    }
  }),
  computed: {
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    })
  },
  created () {
    this.countOptions.push(this.articlesCountOptions, this.articlesCountBrowsedOptions, this.articlesCountLikedOptions, this.typesCountOptions, this.commentsCountOptions)
    this.countArticles()
    this.countArticlesBrowsed()
    this.countArticlesLiked()
    this.countTypes()
    this.countComments()
  },
  methods: {
    countArticles () {
      this.$apis.blog.article.count(this.userInfo.user.uuid)
        .then((data) => {
          this.articlesCountOptions.loading = false
          this.articlesCountOptions.data = data
        })
        .catch(() => {
          this.articlesCountOptions.loading = false
        })
    },
    countArticlesBrowsed () {
      this.$apis.blog.article.countBrowsed(this.userInfo.user.uuid)
        .then((data) => {
          this.articlesCountBrowsedOptions.loading = false
          this.articlesCountBrowsedOptions.data = data
        })
        .catch(() => {
          this.articlesCountBrowsedOptions.loading = false
        })
    },
    countArticlesLiked () {
      this.$apis.blog.article.countLiked(this.userInfo.user.uuid)
        .then((data) => {
          this.articlesCountLikedOptions.loading = false
          this.articlesCountLikedOptions.data = data
        })
        .catch(() => {
          this.articlesCountLikedOptions.loading = false
        })
    },
    countTypes () {
      this.$apis.blog.type.count(this.userInfo.user.uuid)
        .then((data) => {
          this.typesCountOptions.loading = false
          this.typesCountOptions.data = data
        })
        .catch(() => {
          this.typesCountOptions.loading = false
        })
    },
    countComments () {
      this.$apis.blog.comment.count(this.userInfo.user.uuid)
        .then((data) => {
          this.commentsCountOptions.loading = false
          this.commentsCountOptions.data = data
        })
        .catch(() => {
          this.commentsCountOptions.loading = false
        })
    }
  }
}
</script>

<style scoped>

</style>
