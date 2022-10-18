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
  <base-material-card avatar="_required">
    <template #avatar>
      <or-avatar
        :username="user.username"
        avatar-class="v-card--material__avatar elevation-6"
        size="128"
        :avatar="$apis.blog.getFileUrl(user.avatar)"
      />
    </template>
    <v-list class="pa-0">
      <!-- 身份信息 -->
      <v-list-item>
        <v-list-item-content>
          <v-list-item-title
            class="d-flex justify-center align-center flex-wrap"
          >
            <span
              v-if="usernameLinkDisabled"
              class="font-weight-bold"
            >
              {{ user.username }}
            </span>
            <v-hover v-else>
              <template #default="{ hover }">
                <span
                  style="cursor: pointer"
                  :class="hover?'primary--text':null"
                  class="font-weight-bold transition-swing"
                  @click="onClickUsername(user)"
                >{{ user.username }}</span>
              </template>
            </v-hover>
            <or-user-roles
              class="ms-2"
              :roles="user.roles"
            />
          </v-list-item-title>
        </v-list-item-content>
      </v-list-item>

      <v-list-item>
        <v-list-item-content>
          <v-row justify="center">
            <v-col>
              <or-link
                hide-icon
                :hover-able="!usernameLinkDisabled"
                :text="usernameLinkDisabled"
                :href="`/${user.uuid}?tab=article`"
              >
                <v-row justify="center" class="text-h6">
                  {{ $t('user.basicInfo.articlesCount') }}
                </v-row>
                <v-row justify="center" class="text-subtitle-1">
                  {{ articlesCountOptions.loading ? '-' : articlesCountOptions.data }}
                </v-row>
              </or-link>
            </v-col>
            <v-col>
              <v-row justify="center" class="text-h6">
                {{ $t('user.basicInfo.commentsCount') }}
              </v-row>
              <v-row justify="center" class="text-subtitle-1">
                {{ commentsCountOptions.loading ? '-' : commentsCountOptions.data }}
              </v-row>
            </v-col>
          </v-row>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </base-material-card>
</template>

<script>
export default {
  name: 'OrBlogUserBasicInfo',
  props: {
    user: {
      type: Object,
      required: true
    },
    usernameLinkDisabled: {
      type: Boolean,
      default: false
    }
  },
  data: () => ({
    articlesCountOptions: {
      loading: true,
      data: null
    },
    commentsCountOptions: {
      loading: true,
      data: null
    }
  }),
  created () {
    this.$apis.blog.article.count(this.user.uuid)
      .then((data) => {
        this.articlesCountOptions.loading = false
        this.articlesCountOptions.data = data
      })
      .catch(() => {
        this.articlesCountOptions.loading = false
      })
    this.$apis.blog.comment.count(this.user.uuid)
      .then((data) => {
        this.commentsCountOptions.loading = false
        this.commentsCountOptions.data = data
      })
      .catch(() => {
        this.commentsCountOptions.loading = false
      })
  },
  methods: {
    onClickUsername (user) {
      window.open(`/${user.uuid}`, '_blank')
    }
  }
}
</script>

<style scoped>

</style>
