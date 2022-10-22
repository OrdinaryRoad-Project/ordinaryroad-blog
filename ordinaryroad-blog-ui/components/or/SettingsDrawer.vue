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
  <!-- 设置侧边栏 -->
  <v-navigation-drawer
    v-model="localRightDrawerModel"
    width="310"
    right
    temporary
    fixed
  >
    <v-toolbar>
      <v-toolbar-title>{{ $t('more') }}</v-toolbar-title>
      <v-spacer />
      <v-btn
        icon
        @click.stop="toggleRightDrawerModel"
      >
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </v-toolbar>

    <!-- 主题设置 -->
    <v-container>
      <div>
        <div class="text-subtitle-2 font-weight-black">
          {{ $t('theme') }}
        </div>
        <v-item-group v-model="selectedThemeOptionModel" mandatory>
          <v-container>
            <v-row>
              <v-col
                v-for="(themeOption,index) in themeOptions"
                :key="themeOption.title"
                cols="6"
                class="pa-1"
                @click="click(index)"
              >
                <v-item v-slot="{ active, toggle }">
                  <v-card
                    flat
                    :color="active ? 'primary' : $vuetify.theme.dark? 'grey darken-3':'grey lighten-3'"
                    :dark="active"
                    class="py-3 px-4 d-flex align-center justify-space-between"
                    height="50"
                    @click="toggle"
                  >
                    <span>{{ $t(themeOption.titleKey) }}</span>
                    <v-icon>{{ themeOption.icon }}</v-icon>
                  </v-card>
                </v-item>
              </v-col>
            </v-row>
          </v-container>
        </v-item-group>
      </div>
    </v-container>

    <!-- 国际化设置 -->
    <div v-if="showI18nSetting">
      <v-divider />
      <v-container>
        <div>
          <div class="text-subtitle-2 font-weight-black">
            {{ $t('language') }}
          </div>
          <v-item-group v-model="selectedLocaleOptionModel" mandatory>
            <v-container>
              <v-row>
                <v-col
                  v-for="(localeOption,index) in localeOptions"
                  :key="localeOption"
                  cols="6"
                  class="pa-1"
                  @click.stop="updateLang({
                    value: locales[index],
                    $i18n, $vuetify, $dayjs
                  })"
                >
                  <v-item v-slot="{ active, toggle }">
                    <v-card
                      flat
                      :color="active ? 'primary' : $vuetify.theme.dark? 'grey darken-3':'grey lighten-3'"
                      :dark="active"
                      class="py-3 px-4 d-flex align-center justify-space-between"
                      height="50"
                      @click="toggle"
                    >
                      <span>{{ localeOption }}</span>
                    </v-card>
                  </v-item>
                </v-col>
              </v-row>
            </v-container>
          </v-item-group>
        </div>
      </v-container>
    </div>

    <!-- 反馈 -->
    <v-divider />
    <v-container>
      <div class="text-subtitle-2 font-weight-black">
        {{ $t('other') }}
      </div>
      <v-list>
        <client-only>
          <v-list-item
            :href="feedbackUrl"
            target="_blank"
          >
            <v-list-item-content>
              <v-list-item-title>{{ $t('feedback') }}</v-list-item-title>
              <v-list-item-subtitle v-if="!$access.isLogged()">
                <span>
                  <or-link href="/user/login" target="_self">{{ $t('login') }}</or-link>{{ $t('loginToFeedbackTip') }}
                </span>
              </v-list-item-subtitle>
            </v-list-item-content>
          </v-list-item>
        </client-only>
      </v-list>
    </v-container>
  </v-navigation-drawer>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'
import { urlEncode } from '@/plugins/ordinaryroad/utils'

export default {
  name: 'OrSettingsDrawer',
  props: {
    showI18nSetting: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    ...mapGetters('app', {
      themeOptions: 'getThemeOptions',
      selectedThemeOption: 'getSelectedThemeOption',
      rightDrawerModel: 'getRightDrawerModel'
    }),
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    }),
    ...mapGetters('i18n', {
      localeOptions: 'getLocaleOptions',
      locales: 'getLocales',
      local: 'getLocale'
    }),

    localRightDrawerModel: {
      get () {
        return this.rightDrawerModel
      },
      set (val) {
        this.setRightDrawerModel(val)
      }
    },
    selectedThemeOptionModel: {
      get () {
        return this.selectedThemeOption
      },
      set (val) {
        // ignore
      }
    },
    selectedLocaleOptionModel: {
      get () {
        return this.locales.indexOf(this.local)
      },
      set (val) {
        // ignore
      }
    },

    feedbackUrl () {
      if (!process.client) {
        return ''
      }
      const customParams = {
        clientInfo: this.$util.getBrowserInfo(),
        clientVersion: this.$config.APP_VERSION
      }
      if (this.$access.isLogged()) {
        const user = this.userInfo.user
        const userParams = {
          openid: user.uuid,
          nickname: user.username,
          avatar: this.$apis.blog.getFileUrl(user.avatar)
        }
        return `https://support.qq.com/product/439619?${urlEncode(userParams).slice(1)}${urlEncode(customParams)}`
      } else {
        return `https://support.qq.com/product/439619?${urlEncode(customParams).slice(1)}`
      }
    }
  },
  methods: {
    ...mapActions('app', {
      setSelectedThemeOption: 'setSelectedThemeOption',
      toggleRightDrawerModel: 'toggleRightDrawerModel',
      setRightDrawerModel: 'setRightDrawerModel'
    }),
    ...mapActions('i18n', {
      updateLang: 'updateLang'
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
