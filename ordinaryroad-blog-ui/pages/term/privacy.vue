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
  <v-sheet tile>
    <nuxt-child />
    <v-select
      v-model="selectedPrivacyModel"
      outlined
      dense
      class="mt-8"
      hide-details
      style="position: absolute; top: 0; right: 20px; width: 150px"
      :label="$t('selectVersion')"
      :items="privacyItems"
      @input="onInput"
    />
  </v-sheet>
</template>

<script>
export default {
  layout: 'empty',
  asyncData ({ redirect }) {
    const latest = '2022-10-12'
    redirect(`/term/privacy/${latest}`)
    return {
      selectedPrivacyModel: latest
    }
  },
  data: () => ({
    selectedPrivacyModel: null,
    privacyItems: ['2022-10-12']
  }),
  head: {
    title: '隐私政策'
  },
  methods: {
    onInput (val) {
      this.$router.replace(`/term/privacy/${val}`)
    }
  }
}
</script>

<style scoped/>
