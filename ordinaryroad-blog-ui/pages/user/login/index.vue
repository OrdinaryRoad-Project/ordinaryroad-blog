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
            <v-btn v-if="false" icon>
              <v-icon>
                mdi-qqchat
              </v-icon>
            </v-btn>

            <br>

            <div class="text-caption">
              {{ $t('agree') }}<span v-if="false">《用户协议》和</span>
              <or-link href="/term/privacy">
                <span class="white--text">{{ $t('term.privacy') }}</span>
              </or-link>
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
      redirect: '/'
    }
  },
  methods: {
    login (provider) {
      const state = `${this.$dayjs().valueOf()}_${this.redirect}_${provider}_login`
      if (provider !== 'ordinaryroad') {
        this.$dialog({
          content: '如使用未绑定账号的第三方账号登录，登录成功后将暂时无法解绑来绑定其他账号',
          confirmText: '我知道了，继续登录'
        })
          .then(({ isConfirm }) => {
            if (isConfirm) {
              this.$apis.blog.oauth2.authorize(provider, state)
                .then((data) => {
                  window.open(data, '_self')
                })
            }
          })
      } else {
        this.$apis.blog.oauth2.authorize(provider, state)
          .then((data) => {
            window.open(data, '_self')
          })
      }
    }
  }
}
</script>

<style scoped>

</style>
