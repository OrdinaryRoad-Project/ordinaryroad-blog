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

    <!-- 搜索 -->
    <or-search
      v-if="$route.name!=='search-input'"
    />

    <!-- 用户信息 -->
    <or-user-info-menu />

    <!-- 国际化 -->
    <or-i18n-menu v-if="!$vuetify.breakpoint.smAndDown" />
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
  data: () => ({}),
  computed: {
    ...mapGetters('app', {
      selectedThemeOption: 'getSelectedThemeOption',
      themeOptions: 'getThemeOptions',
      titleKey: 'getTitleKey'
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
    },

    showSearch () {
      return this.$vuetify.breakpoint.mdAndUp
    }

  },
  methods: {
    ...mapActions('app', {
      setSelectedThemeOption: 'setSelectedThemeOption'
    }),

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
