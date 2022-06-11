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
  <v-card class="mx-auto">
    <v-card-text>
      <v-progress-circular indeterminate color="primary" class="me-2" />
      授权通过，正在获取用户信息...
    </v-card-text>
  </v-card>
</template>

<script>
export default {
  layout: 'empty',
  asyncData ({
    store, route, redirect
  }) {
    const oAuth2State = store.getters['user/getOAuth2State']
    console.log('oAuth2State', oAuth2State)
    if (oAuth2State == null) {
      // TODO 返回登录页面
      console.log('返回登录页面')
      redirect('/user/login')
    } else {
      const stateFromServer = route.query.state
      if (decodeURIComponent(stateFromServer) !== decodeURIComponent(oAuth2State)) {
        // TODO state不一致
        console.log('state不一致', decodeURIComponent(stateFromServer), decodeURIComponent(oAuth2State))
      } else {
        const redirectFromLogin = decodeURIComponent(oAuth2State).split(',')[1]
        return {
          code: 'CODE',
          redirect: redirectFromLogin
        }
      }
    }
  },
  data () {
    return {
      code: '',
      redirect: ''
    }
  },
  mounted () {
    // TODO Code换取用户信息

    console.log(this.$store.getters['user/getTokenInfo'])
    this.$store.commit('user/SET_TOKEN_INFO', { satoken: 'TEST_TOKEN' })
    console.log(this.$store.getters['user/getTokenInfo'])

    console.log(this.$store.getters['user/getUserInfo'])
    this.$store.commit('user/SET_USER_INFO', { username: 'TEST_USER_INFO' })
    console.log(this.$store.getters['user/getUserInfo'])

    this.$store.commit('user/REMOVE_OAUTH2_STATE')
    console.log('跳转到', this.redirect)
    this.$router.replace(this.redirect)
  }
}
</script>

<style scoped>

</style>
