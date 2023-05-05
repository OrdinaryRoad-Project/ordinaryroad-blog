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
  <div class="text-center" style="margin-top: 5%">
    <p>{{ tipString }}</p>
    <v-btn text class="ma-1" :to="{path:'/friend_link/apply'}">
      <div class="d-flex align-center justify-center text--primary">
        <v-card-title class="pa-0 font-weight-bold">
          立即申请
        </v-card-title>
        <v-icon large>
          mdi-chevron-right
        </v-icon>
      </div>
    </v-btn>
  </div>
</template>

<script>
export default {
  name: 'EmptyFriendLinks',
  props: {
    tipString: {
      type: String,
      default: '暂无友链，快联系我申请吧！'
    }
  }
}
</script>

<style scoped>

</style>
