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
    <v-toolbar-title>
      <span style="cursor: pointer;" @click="$router.push('/')">{{ $t('appName') }}</span>
      <or-jinrishici-subtitle />
    </v-toolbar-title>

    <v-spacer />

    <!-- 搜索 -->
    <or-search
      v-if="$route.name!=='search-input'"
      :disable-hot-key="!$store.getters['app/getSearchInputHotKeyEnabled']"
      :focused.sync="searchInputFocused"
      @onSubmit="onSearchSubmit"
    />

    <!-- 用户信息 -->
    <or-user-info-menu v-if="!$vuetify.breakpoint.smAndDown||!searchInputFocused" />

    <!-- 国际化 -->
    <or-i18n-menu v-if="!$vuetify.breakpoint.smAndDown" />

    <!-- 设置 -->
    <v-tooltip bottom>
      <template #activator="{ on, attrs }">
        <v-btn
          icon
          v-bind="attrs"
          v-on="on"
          @click.stop="$store.dispatch('app/toggleRightDrawerModel')"
        >
          <v-icon>mdi-dots-horizontal</v-icon>
        </v-btn>
      </template>
      <span>{{ $t('more') }}</span>
    </v-tooltip>
  </v-app-bar>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'OrHeader',
  data: () => ({
    searchInputFocused: false
  }),
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

    onSearchSubmit (searchInput) {
      window.open(`/search/${searchInput}`, '_blank')
    }
  }
}
</script>

<style scoped>

</style>
