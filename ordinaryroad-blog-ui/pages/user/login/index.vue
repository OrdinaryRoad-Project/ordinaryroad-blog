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
  <v-container fluid class="pa-0 fill-height">
    <v-img
      class="align-center"
      style="height: 100vh"
      src="https://api.dujin.org/bing/1920.php"
    >
      <base-material-card
        style="opacity:0.85"
        width="400"
      >
        <template #heading>
          <div class="text-center">
            <span class="d-flex justify-center">
              <h2 class="font-weight-bold mb-2 ms-9">{{ $t('loginFormTitle') }}</h2><span
                class="text-caption mb-auto ms-1"
              >{{ $config.APP_VERSION }}</span>
            </span>
            <v-btn @click="login('ordinaryroad')">
              {{ $t('usingOrNumberLoginHint') }}
            </v-btn>

            <br>

            <v-btn icon @click="login('github')">
              <v-icon>
                mdi-github
              </v-icon>
            </v-btn>
            <v-btn icon @click="login('gitee')">
              <simple-icons-gitee />
            </v-btn>
            <v-btn icon @click="login('qq')">
              <v-icon>
                mdi-qqchat
              </v-icon>
            </v-btn>

            <br>

            <div class="d-flex text-caption align-center justify-center">
              <v-simple-checkbox
                v-model="agree"
                style="scale: 80%"
                class="pa-0 ma-0"
                dark
              />
              <span>{{ $t('agree') }}<span v-if="false">《用户协议》和</span>
                <or-link class="mx-1" href="/term/privacy" hide-icon><span class="white--text">{{
                  $t('term.privacy')
                }}</span></or-link><or-link class="mx-1" href="/term/convention" hide-icon><span class="white--text">{{
                  $t('term.convention')
                }}</span></or-link></span>
            </div>
          </div>
        </template>
      </base-material-card>
    </v-img>
  </v-container>
</template>

<script>

export default {
  layout: 'empty',
  asyncData ({
    store,
    route,
    redirect
  }) {
    const redirectPath = route.query.redirect || '/'
    const userInfo = store.getters['user/getUserInfo']
    const tokenInfo = store.getters['user/getTokenInfo']
    if (tokenInfo && userInfo) {
      redirect(redirectPath)
    } else {
      return {
        redirect: redirectPath
      }
    }
  },
  data () {
    return {
      agree: false,
      redirect: '/'
    }
  },
  head () {
    return {
      title: this.$t('login'),
      titleTemplate: `%s - ${this.$t('appName')}`
    }
  },
  methods: {
    login (provider) {
      if (!this.agree) {
        this.$snackbar.info(this.$t('term.agreeHint'))
        return
      }
      const state = `${this.$dayjs().valueOf()}$${this.redirect}$${provider}$login`
      this.$dialog({
        content: this.$t('loginHint'),
        confirmText: this.$t('understandAnd', [this.$t('login')])
      })
        .then(({ isConfirm }) => {
          if (isConfirm) {
            this.$apis.blog.oauth2.authorize(provider, state)
              .then((data) => {
                window.open(data, '_self')
              })
          }
        })
    }
  }
}
</script>

<style scoped>

</style>
