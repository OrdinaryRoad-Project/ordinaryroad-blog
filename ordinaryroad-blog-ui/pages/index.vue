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
  <div>
    <or-blog-article-list auto-load-more />
  </div>
</template>

<script>
export default {
  components: {},
  asyncData ({ $apis }) {
  },
  data: () => ({}),
  mounted () {
    const key = 'notification_0'
    const hideNotification0 = localStorage.getItem(key) === 'true'
    if (!hideNotification0) {
      this.$dialog({
        persistent: true,
        title: '欢迎体验OR博客',
        content: '项目原型为我的个人博客，目前为删档内测，仅保留OR账号，💪仍在继续开发中，注册登录就送SSSSSSVIP！！！🙏感谢您的参与！\n😊也欢迎光临我的个人博客：https://ordinaryroad.top',
        confirmText: '让我康康！',
        cancelText: '下次一定～'
      }).then(({ isConfirm }) => {
        if (!isConfirm) {
          this.$snackbar.error('拿来吧你')
          setTimeout(() => {
            window.open('https://ordinaryroad.top', '_blank')
            localStorage.setItem(key, 'true')
          }, 1000)
        } else {
          window.open('https://ordinaryroad.top', '_blank')
          localStorage.setItem(key, 'true')
        }
      })
    }
  },
  created () {
  },
  methods: {}
}
</script>

<style scoped>
</style>
