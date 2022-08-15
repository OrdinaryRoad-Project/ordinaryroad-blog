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
    <v-toolbar flat>
      <v-app-bar-nav-icon @click="$store.dispatch('app/toggleDashboardDrawerModel')" />
      <v-toolbar-title>
        <v-breadcrumbs :items="items" />
      </v-toolbar-title>
    </v-toolbar>
    <nuxt-child />
  </div>
</template>

<script>

export default {
  middleware: ['userInfo'],

  data: () => ({}),

  computed: {
    items () {
      const path = this.$route.path
      const split = path.split('/')
      const items = []
      for (let i = 0; i < split.length; i++) {
        // 因为第0个是空
        if (i === 0) {
          continue
        }
        if (i === 1) {
          const string = split[i]
          items.push({
            text: `/${string}`,
            disable: false,
            href: `/${string}`
          })
        } else {
          const preItem = items[i - 1 - 1]
          const string = preItem.href + '/' + split[i]
          items.push({
            text: string,
            disable: false,
            href: string
          })
        }
      }
      if (items.length !== 0) {
        items[items.length - 1].disabled = true
      }
      return items
    }
  },
  methods: {}
}
</script>

<style scoped>

</style>
