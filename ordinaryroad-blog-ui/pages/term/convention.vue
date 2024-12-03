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
    <v-select
      v-model="selectedConventionModel"
      outlined
      dense
      class="mt-8"
      hide-details
      style="position: absolute; top: 60px; right: 20px; width: 150px"
      :label="$t('selectVersion')"
      :items="conventionItems"
      @input="onInput"
    />
    <nuxt-child />
  </v-sheet>
</template>

<script>
export default {
  layout: 'empty',
  asyncData ({ redirect }) {
    const latest = '2024-12-03'
    redirect(`/term/convention/${latest}`)
    return {
      selectedConventionModel: latest
    }
  },
  data: () => ({
    selectedConventionModel: null,
    conventionItems: ['2024-12-03']
  }),
  head () {
    return {
      title: this.$t('term.convention'),
      titleTemplate: `%s - ${this.$t('appName')}`
    }
  },
  methods: {
    onInput (val) {
      this.$router.replace(`/term/convention/${val}`)
    }
  }
}
</script>

<style scoped />
