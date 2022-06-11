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
  <v-img
    class="align-center"
    height="100vh"
    src="https://api.dujin.org/bing/1920.php"
  >
    <base-material-card
      style="opacity:0.85"
      width="400"
    >
      <template #heading>
        <div class="text-center">
          <h2 class="font-weight-bold mb-2">
            {{ $t('loginFormTitle') }}
          </h2>
          <v-btn @click="login">
            使用OR账号登录
          </v-btn>
          <br>
          <br>
          <v-btn icon>
            <v-icon>
              mdi-github
            </v-icon>
          </v-btn>
          <v-btn icon>
            <v-icon>
              mdi-qqchat
            </v-icon>
          </v-btn>
        </div>
      </template>
    </base-material-card>
  </v-img>
</template>

<script>
export default {
  layout: 'empty',
  asyncData ({
    store, route, redirect,
    $config: { AUTH_BASE_URL, CLIENT_ID, REDIRECT_URI }
  }) {
    const redirectPath = route.query.redirect || '/'
    const userInfo = store.getters['user/getUserInfo']
    const tokenInfo = store.getters['user/getTokenInfo']
    if (tokenInfo && userInfo) {
      redirect(redirectPath)
    } else {
      return {
        loginUrl: `${AUTH_BASE_URL}/oauth2/authorize?response_type=code&client_id=${CLIENT_ID}&scope=openid,userinfo&redirect_uri=${REDIRECT_URI}`,
        redirect: redirectPath
      }
    }
  },
  data () {
    return {
      loginUrl: '',

      redirect: undefined
    }
  },
  methods: {
    login () {
      const oAuth2State = this.$store.getters['user/getOAuth2State']
      let state
      if (oAuth2State == null) {
        // 首次访问登录界面
        state = this.$dayjs().valueOf() + ',' + this.redirect
        this.$store.commit('user/SET_OAUTH2_STATE', state)
      } else {
        // 读取Cookie中的
        state = oAuth2State
      }
      window.open(this.loginUrl + '&state=' + state, '_self')
    }
  }
}
</script>

<style scoped>

</style>
