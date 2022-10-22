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
  <div style="width: 100%;">
    <or-no-more-data v-if="noMoreData" />
    <v-btn
      v-else
      text
      :loading="loading"
      block
      depressed
      @click="startLoading(false)"
    >
      <v-icon v-if="showDownIcon" left>
        mdi-chevron-down
      </v-icon>
      加载更多
    </v-btn>
  </div>
</template>

<script>
export default {
  name: 'OrLoadMoreFooter',
  props: {
    /**
     * 是否显示向下的图标
     */
    showDownIcon: {
      type: Boolean,
      default: true
    },
    noMoreData: {
      type: Boolean,
      required: true
    }
  },
  data: () => ({
    loading: false
  }),
  methods: {
    startLoading (onlyAnimation = false) {
      this.loading = true
      !onlyAnimation && this.$emit('loadMore')
    },
    finishLoad () {
      this.loading = false
    }
  }
}
</script>

<style scoped>

</style>
