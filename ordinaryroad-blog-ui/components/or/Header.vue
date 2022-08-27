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
  <v-app-bar app fixed clipped-left elevate-on-scroll>
    <v-toolbar-title style="cursor: pointer;" @click="$router.push('/')">
      {{ $t('appName') }}
    </v-toolbar-title>

    <v-spacer />

    <!-- 用户信息 -->
    <div v-if="userInfo">
      <v-menu offset-y open-on-hover>
        <template #activator="{ on, attrs }">
          <v-btn
            large
            depressed
            v-bind="attrs"
            v-on="on"
          >
            <or-avatar
              size="38"
              avatar-class="v-list-item__avatar"
              :username="userInfo.user.username"
              :avatar="$apis.blog.getFileUrl(userInfo.user.avatar)"
            />
            <v-list-item-title>
              {{ username }}
            </v-list-item-title>
            <v-icon>mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <or-base-tree-list
          :nav="false"
          :items="userMenuItems"
          @clickListItem="logout"
        />
      </v-menu>
    </div>
    <div v-else>
      <v-btn text color="primary" :to="`/user/login?redirect=${$route.path}`">
        {{ $t('login') }}
        <v-icon>mdi-login</v-icon>
      </v-btn>
    </div>

    <!-- 国际化 -->
    <or-i18n-menu />
    <!-- 设置 -->
    <v-btn icon @click="$store.dispatch('app/toggleRightDrawerModel')">
      <v-icon>mdi-cog</v-icon>
    </v-btn>
  </v-app-bar>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'OrHeader',
  computed: {
    ...mapGetters('app', {
      selectedThemeOption: 'getSelectedThemeOption',
      themeOptions: 'getThemeOptions',
      titleKey: 'getTitleKey',
      userMenuItems: 'getUserMenuItems',
      dashboardMenuItems: 'getDashboardMenuItems'
    }),
    ...mapGetters('user', {
      userInfo: 'getUserInfo',
      username: 'getUsername'
    }),
    selectedThemeOptionModel: {
      get () {
        return this.selectedThemeOption
      },
      set (val) {
        // ignore
      }
    }

  },
  methods: {
    ...mapActions('app', {
      setSelectedThemeOption: 'setSelectedThemeOption'
    }),

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
    },

    click (index) {
      this.setSelectedThemeOption({
        value: index,
        $vuetify: this.$vuetify
      })
    }
  }
}
</script>

<style scoped>

</style>
