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
    route,
    redirect
  }) {
    const stateFromServer = route.query.state
    const redirectFromLogin = decodeURIComponent(stateFromServer).split('_')[1]
    const provider = decodeURIComponent(stateFromServer).split('_')[2]

    try {
      const code = route.query.code

      // 调用callback接口，换取用户信息和token
      const data = await $apis.blog.oauth2.callback(provider, code, stateFromServer)
      return {
        success: true,
        token: data.token,
        userInfo: data.userInfo,
        redirect: redirectFromLogin
      }
    } catch (e) {
      return {
        redirect: redirectFromLogin,
        msg: e
      }
    }
  },
  data () {
    return {
      success: false,
      redirect: '',
      msg: '失败',
      token: null,
      userInfo: null
    }
  },
  mounted () {
    if (this.success) {
      this.$store.commit('user/SET_TOKEN_INFO', this.token)
      this.$store.commit('user/SET_USER_INFO', this.userInfo)
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
