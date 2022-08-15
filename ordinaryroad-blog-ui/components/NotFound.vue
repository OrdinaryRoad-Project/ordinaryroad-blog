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
  <div class="d-flex flex-column justify-center align-center" style="height: 100vh;text-align: center">
    <p class="font-weight-bold display-3">
      404
    </p><br>
    <p class="display-2">
      {{ notFoundType }}不见了哦
    </p><br>
    <p>抱歉，您访问的资源不存在。</p>
    <div>
      <v-btn class="ma-5" large @click="$router.replace({path:'/'})">
        直接返回首页
      </v-btn>
      <v-btn color="primary" large class="ma-5" @click="$router.back()">
        返回上一级/首页
      </v-btn>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  data () {
    return {
      notFoundType: '页面'
    }
  },
  watch: {
    $route: {
      handler () {
        setTimeout(() => {
          const type = this.$route.params.type
          if (type !== undefined) {
            this.notFoundType = type
          }
        }, 300)
      },
      // 首次进入页面即执行
      immediate: true
    }
  }
}
</script>

<style scoped>

</style>
