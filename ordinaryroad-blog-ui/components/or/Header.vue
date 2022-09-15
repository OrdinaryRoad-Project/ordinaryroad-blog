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
    <span
      v-if="$route.name!=='search-input'"
      style="position: relative;"
      class="ms-2 me-4 d-inline-flex align-center"
    >
      <v-fade-transition>
        <v-form
          v-if="$vuetify.breakpoint.mdAndUp||searchInputFocused"
          style="position: absolute; right: 0; width: 100%; max-width: 400px"
          :style="{'width':$vuetify.breakpoint.smAndDown&&!searchInputFocused?'100px':'200px'}"
          @submit="onSearchFormSubmit"
          @submit.n.native.prevent
        >
          <v-text-field
            v-model.trim="searchInput"
            dense
            :autofocus="!$vuetify.breakpoint.mdAndUp"
            class="transition-fast-in-fast-out"
            :class="searchInputFocused?'elevation-2':null"
            hide-details
            :placeholder="$t('search')"
            solo
            clearable
            flat
            maxlength="10"
            :rules="[$rules.max100Chars]"
            append-icon="mdi-magnify"
            @click:append="onClickSearchInputAppend"
            @focusin="searchInputFocused=true"
            @focusout="searchInputFocused=false"
          />
        </v-form>
      </v-fade-transition>
      <v-fade-transition>
        <v-btn
          v-if="$vuetify.breakpoint.smAndDown&&!searchInputFocused"
          small
          style="position: absolute; top: -14px; right: 0; margin-right: 10px"
          icon
          @click="searchInputFocused=true"
        >
          <v-icon>mdi-magnify</v-icon>
        </v-btn>
      </v-fade-transition>
    </span>

    <!-- 用户信息 -->
    <div v-if="userInfo">
      <v-btn
        v-if="$vuetify.breakpoint.mdAndUp"
        to="/dashboard/article"
        color="primary"
      >
        <v-icon left>
          mdi-pencil
        </v-icon>
        {{ $t('article.actions.writing') }}
      </v-btn>
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
            <v-list-item-title :style="{'max-width':$vuetify.breakpoint.mdAndUp?null:'50px' }">
              {{ username }}
            </v-list-item-title>
            <v-icon>mdi-chevron-down</v-icon>
          </v-btn>
        </template>
        <or-base-tree-list
          :nav="false"
          :items="accessibleUserMenuItems"
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
  data: () => ({
    searchInput: '',
    searchInputFocused: false
  }),
  computed: {
    ...mapGetters('app', {
      selectedThemeOption: 'getSelectedThemeOption',
      themeOptions: 'getThemeOptions',
      titleKey: 'getTitleKey',
      accessibleUserMenuItems: 'getAccessibleUserMenuItems'
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

    onClickSearchInputAppend () {
      if (this.searchInputFocused) {
        this.onSearchFormSubmit()
      } else {
        this.searchInputFocused = true
      }
    },

    onSearchFormSubmit () {
      if (this.searchInput === '') {
        return
      }
      window.open(`/search/${this.searchInput}`, '_blank')
    },

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
