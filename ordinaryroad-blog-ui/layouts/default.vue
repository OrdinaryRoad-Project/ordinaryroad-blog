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
    <!-- 控制台侧边栏 -->
    <v-navigation-drawer
      v-if="$route.path.startsWith('/dashboard')"
      v-model="localDashboardDrawerModel"
      clipped
      app
    >
      <OrBaseTreeList
        :nav="true"
        :items="dashboardMenuItems"
      />
    </v-navigation-drawer>

    <!-- 设置 -->
    <or-settings-drawer />

    <!-- 标题 用户名 -->
    <or-header />

    <v-main>
      <v-container :fluid="$route.name==='index'" :class="$route.name==='index'?'pa-0':null">
        <nuxt />
      </v-container>
    </v-main>

    <!-- Footer -->
    <or-footer />

    <!-- 回到顶部按钮 -->
    <v-fab-transition>
      <v-btn
        v-if="scrollToTopFab"
        :small="$vuetify.breakpoint.smAndDown"
        fab
        fixed
        bottom
        right
        color="accent"
        @click="$vuetify.goTo(0)"
      >
        <v-icon :small="$vuetify.breakpoint.smAndDown">
          mdi-chevron-up
        </v-icon>
      </v-btn>
    </v-fab-transition>
  </v-app>
</template>

<script>
// Utilities
import { mapActions, mapGetters } from 'vuex'
import { updateTheme } from 'static/js/utils/vuetify'

export default {
  data () {
    return {
      scrollToTopFab: false
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
      dashboardDrawerModel: 'getDashboardDrawerModel',
      rightDrawerModel: 'getRightDrawerModel',
      titleKey: 'getTitleKey',
      userMenuItems: 'getUserMenuItems',
      dashboardMenuItems: 'getDashboardMenuItems'
    }),
    localDashboardDrawerModel: {
      get () {
        return this.dashboardDrawerModel
      },
      set (val) {
        this.setDashboardDrawerModel(val)
      }
    }
  },
  mounted () {
    this.$nextTick(() => {
      updateTheme(this.$store.getters['app/getSelectedThemeOption'], this.$vuetify)
      window.addEventListener('scroll', this.handleScroll)
    })
  },
  beforeDestroy () {
    window.removeEventListener('scroll', this.handleScroll, false)
  },
  methods: {
    ...mapActions('app', {
      setDashboardDrawerModel: 'setDashboardDrawerModel'
    }),

    logout () {
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
    },

    handleScroll (event) {
      this.scrollToTopFab = event.target.scrollingElement.scrollTop !== 0
    }
  }
}
</script>
