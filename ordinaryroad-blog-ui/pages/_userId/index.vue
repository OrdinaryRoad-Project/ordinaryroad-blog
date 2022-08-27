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
      >
        <base-material-card :title="$t('articleCount',[`${totalArticle?$t('parentheses',[totalArticle]):''}`])">
          <or-blog-article-list
            :total.sync="totalArticle"
            :create-by="blogUser.uuid"
          />
        </base-material-card>
      </v-col>

      <v-col
        md="4"
        cols="12"
        order="1"
        order-md="2"
      >
        <base-material-card
          :avatar="$apis.blog.getFileUrl(blogUser.avatar)"
        >
          <template #avatar>
            <or-avatar
              :username="blogUser.username"
              avatar-class="v-card--material__avatar elevation-6"
              size="128"
              :avatar="$apis.blog.getFileUrl(blogUser.avatar)"
              editable
              @selectAvatar="onAvatarSelect"
            />
          </template>
          <v-list>
            <v-list-item>
              <v-list-item-content>
                <v-list-item-title>
                  <span class="font-weight-bold">{{ blogUser.username }}</span>
                  <or-user-roles v-if="blogUser.roles.length>0" class="ms-2" :roles="blogUser.roles" />
                </v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
          <!--          <v-divider inset />-->
        </base-material-card>
      </v-col>
    </v-row>
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
  },
  methods: {
    onAvatarSelect (e) {
      console.log(e)
    }
  }
}
</script>

<style scoped>

</style>
