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
  <v-app>
    <v-app-bar app>
      <v-toolbar-title>OR博客</v-toolbar-title>
      <v-spacer />
      <v-menu offset-y open-on-hover>
        <template #activator="{ on, attrs }">
          <v-btn
            large
            depressed
            v-bind="attrs"
            v-on="on"
          >
            <v-icon>mdi-account</v-icon>
            <v-icon>mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item @click="logout">
            <v-list-item-icon>
              <v-icon>mdi-logout</v-icon>
            </v-list-item-icon>
            <v-list-item-title>退出登录</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
    <v-main>
      <v-container>
        <Nuxt />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
// Utilities
import { mapActions, mapGetters } from 'vuex'
import { updateTheme } from 'static/js/utils/vuetify'

export default {
  middleware: ['userInfo'],
  data () {
    return {
      drawer: false,
      rightDrawer: false
    }
  },
  head () {
    return {
      htmlAttrs: {
        lang: this.$i18n.locale
      }
    }
  },
  computed: {
    ...mapGetters('app', {
      selectedThemeOption: 'getSelectedThemeOption',
      themeOptions: 'getThemeOptions',
      titleKey: 'getTitleKey',
      userMenuItems: 'getUserMenuItems'
    }),
    ...mapGetters('i18n', {
      localeOptions: 'getLocaleOptions',
      locales: 'getLocales'
    }),
    ...mapGetters('user', {
      userInfo: 'getUserInfo',
      avatarPath: 'getAvatarPath'
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
  mounted () {
    // dom初始化完成再初始化主题
    this.$nextTick(() => {
      updateTheme(this.selectedThemeOption, this.$vuetify)
    })
  },
  methods: {
    ...mapActions('app', {
      setSelectedThemeOption: 'setSelectedThemeOption'
    }),
    ...mapActions('i18n', {
      setLang: 'setLang'
    }),

    logout () {
      this.$dialog({
        content: this.$i18n.t('confirmLogout'),
        loading: true
      }).then((value) => {
        this.$store.dispatch('user/logout', {
          $apis: this.$apis,
          $router: this.$router,
          $route: this.$route
        }).then(() => value.cancel())
      })
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
