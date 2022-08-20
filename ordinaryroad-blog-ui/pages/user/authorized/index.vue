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
    <v-card class="mx-auto">
      <v-card-text>
        <v-progress-circular indeterminate color="primary" class="me-2" />
        授权通过，正在获取用户信息...
      </v-card-text>
    </v-card>
  </v-container>
</template>

<script>
export default {
  layout: 'empty',
  async asyncData ({
    $apis,
    store,
    route,
    redirect
  }) {
    const oAuth2State = store.getters['user/getOAuth2State']
    console.log(oAuth2State)
    if (oAuth2State == null) {
      // 返回登录页面
      redirect('/user/login')
    } else {
      const stateFromServer = route.query.state
      const redirectFromLogin = decodeURIComponent(oAuth2State).split('_')[1]
      const provider = decodeURIComponent(oAuth2State).split('_')[2]
      try {
        if (decodeURIComponent(stateFromServer) !== decodeURIComponent(oAuth2State)) {
          // state不一致
          throw new Error('state不一致')
        } else {
          const code = route.query.code
          const tokenData = await $apis.oauth.token(provider, code)
          // console.log('tokenData', tokenData)
          // OrdinaryRoad Error
          if (tokenData.success !== undefined && !tokenData.success) {
            throw new Error(tokenData.msg)
          }
          // GitHub Error
          if (tokenData.error !== undefined && tokenData.error !== '') {
            throw new Error(tokenData.error_description)
          }

          const openid = tokenData.openid || ''
          const accessToken = tokenData.access_token
          const tokenType = tokenData.token_type

          // 调用callback接口，换取用户信息和token
          const data = await $apis.blog.oauth2.callback(provider, `${tokenType} ${accessToken}`, openid)
          return {
            success: true,
            token: data.token,
            // userinfoDTO (user, ...)
            userinfo: data.userinfo,
            redirect: redirectFromLogin
          }
        }
      } catch (e) {
        console.log(e)
        return {
          redirect: redirectFromLogin,
          msg: e.message
        }
      }
    }
  },
  data () {
    return {
      success: false,
      redirect: '',
      msg: '失败',
      token: null,
      userinfo: null
    }
  },
  mounted () {
    if (this.success) {
      this.$store.commit('user/REMOVE_OAUTH2_STATE')
      this.$store.commit('user/SET_TOKEN_INFO', this.token)
      this.$store.commit('user/SET_USER_INFO', this.userinfo)
      this.$router.replace(this.redirect)
    } else {
      this.$dialog({
        persistent: true,
        title: '系统提示',
        content: '失败，请重试。\n' + this.msg,
        confirmText: '确定'
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$router.replace(`/user/login?redirect=${this.redirect}`)
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
