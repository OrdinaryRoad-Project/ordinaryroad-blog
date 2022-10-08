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
    <v-row>
      <v-col
        md="8"
        cols="12"
        order="2"
        order-md="1"
        class="pa-0"
      >
        <v-tabs
          v-model="tabModel"
          class="sticky-top"
          style="z-index: 2"
          :style="`top :${$vuetify.breakpoint.smAndDown?'56px':'64px'} !important;`"
        >
          <v-tab @click="onClickTab()">
            {{ $t('overview') }}
          </v-tab>
          <v-tab @click="onClickTab('article')">
            {{ $t('articleCount', [`${totalArticle ? $t('parentheses', [totalArticle]) : ''}`]) }}
          </v-tab>
        </v-tabs>

        <v-tabs-items v-model="tabModel">
          <v-tab-item>
            <or-blog-user-overview
              :user-id="blogUser.uuid"
            />
          </v-tab-item>
          <v-tab-item>
            <or-blog-user-articles
              :user-id="blogUser.uuid"
              :total.sync="totalArticle"
            />
          </v-tab-item>
        </v-tabs-items>
      </v-col>

      <v-col
        md="4"
        cols="12"
        order="1"
        order-md="2"
      >
        <div
          style="top: 108px !important;"
          class="sticky-top"
        >
          <!-- 用户基本资料 -->
          <base-material-card :avatar="$apis.blog.getFileUrl(blogUser.avatar)">
            <template #avatar>
              <or-avatar
                :username="blogUser.username"
                avatar-class="v-card--material__avatar elevation-6"
                size="128"
                :avatar="$apis.blog.getFileUrl(blogUser.avatar)"
              />
            </template>
            <v-list>
              <v-list-item>
                <v-list-item-content>
                  <v-list-item-title>
                    <span class="font-weight-bold">{{ blogUser.username }}</span>
                    <or-user-roles :roles="blogUser.roles" />
                  </v-list-item-title>
                </v-list-item-content>
              </v-list-item>
            </v-list>
            <!--          <v-divider inset />-->
          </base-material-card>

          <div class="mt-14" />
          <!-- 用户创建的分类 -->
          <base-material-card :title="$t('typeCount',[`${totalTypes?$t('parentheses',[totalTypes]):''}`])">
            <or-blog-type-treeview
              :total.sync="totalTypes"
            />
          </base-material-card>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<script>

export default {
  props: {},
  asyncData ({
    route,
    redirect,
    $apis
  }) {
    const tabItems = ['overview', 'article']
    const userId = route.params.userId
    const tab = route.query.tab
    const indexOf = tab ? tabItems.indexOf(tab) : 0
    return $apis.blog.user.findById(userId)
      .then((data) => {
        return {
          tabModel: indexOf === -1 ? 0 : indexOf,
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
      totalArticle: null,
      totalTypes: null,

      tabModel: 0
    }
  },
  watch: {
    tabModel (val) {
      // ignore
      switch (val) {
        case 0:
          break
        case 1:
          break
        default:
      }
    }
  },
  mounted () {
  },
  methods: {
    onClickTab (tab) {
      this.$router.replace({ path: this.$route.path, query: { tab } })
    }
  }
}
</script>

<style scoped>

</style>
