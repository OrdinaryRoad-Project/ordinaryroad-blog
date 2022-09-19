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
    <div v-if="userInfo">
      <v-menu offset-y open-on-hover>
        <template #activator="{ on, attrs }">
          <v-btn
            color="transparent"
            large
            depressed
            v-bind="attrs"
            v-on="on"
          >
            <or-avatar
              size="38"
              avatar-class="v-list-item__avatar"
              :username="username"
              :avatar="$apis.blog.getFileUrl(userInfo.user.avatar)"
            />
            <v-list-item-title v-if="!$vuetify.breakpoint.smAndDown">
              {{ username }}
            </v-list-item-title>
            <v-icon>mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <or-base-tree-list
          :nav="false"
          :items="accessibleUserMenuItemsModel"
          @clickListItem="logout"
        />
      </v-menu>
      <v-btn
        v-if="!$vuetify.breakpoint.smAndDown"
        to="/dashboard/article"
        depressed
        :text="transparent"
        :color="transparent?'white':startWritingColor"
      >
        <v-icon left>
          mdi-pencil
        </v-icon>
        {{ $t('article.actions.writing') }}
      </v-btn>
    </div>
    <div v-else>
      <v-btn
        text
        :to="`/user/login?redirect=${$route.path}`"
        :color="transparent?'null':loginColor"
      >
        {{ $t('login') }}
        <v-icon>mdi-login</v-icon>
      </v-btn>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'OrUserInfoMenu',
  props: {
    transparent: {
      type: Boolean,
      default: false
    },
    startWritingColor: {
      type: String,
      default: 'primary'
    },
    loginColor: {
      type: String,
      default: 'primary'
    }
  },
  computed: {
    ...mapGetters('app', {
      accessibleUserMenuItems: 'getAccessibleUserMenuItems'
    }),
    ...mapGetters('user', {
      userInfo: 'getUserInfo',
      username: 'getUsername'
    }),

    accessibleUserMenuItemsModel: {
      get () {
        const items = [...this.accessibleUserMenuItems]
        if (this.$vuetify.breakpoint.smAndDown) {
          items.unshift({
            titleKey: 'article.actions.writing',
            to: '/dashboard/article',
            icon: 'mdi-pencil'
          })
        }
        return items
      }
    }
  },
  methods: {
    logout ({ titleKey }) {
      if (titleKey === 'userMenuTitles.space') {
        window.open(`/${this.userInfo.user.uuid}`, '_blank')
      } else {
        this.$dialog({
          persistent: false,
          content: this.$i18n.t('confirmLogout'),
          loading: true
        }).then((dialog) => {
          if (dialog.isConfirm) {
            this.$store.dispatch('user/logout', {
              $apis: this.$apis,
              $router: this.$router,
              $route: this.$route
            }).then(() => dialog.cancel())
          }
        })
      }
    }
  }
}
</script>

<style scoped>

</style>
