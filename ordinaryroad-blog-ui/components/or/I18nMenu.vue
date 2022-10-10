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
  <v-menu offset-y open-on-hover>
    <template #activator="{ on, attrs }">
      <v-btn
        depressed
        v-bind="attrs"
        v-on="on"
      >
        <v-icon>mdi-translate</v-icon>
        <v-icon>mdi-chevron-down</v-icon>
      </v-btn>
    </template>
    <v-list>
      <v-list-item
        v-for="(localeOption, index) in localeOptions"
        :key="index"
        @click.stop="setLang({
          value: locales[localeOptions.indexOf(localeOption)],
          $i18n, $vuetify, $dayjs
        })"
      >
        <v-list-item-content>
          <v-list-item-title>{{ localeOption }}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'OrI18nMenu',
  computed: {
    ...mapGetters('i18n', {
      localeOptions: 'getLocaleOptions',
      locales: 'getLocales'
    })
  },
  methods: {
    ...mapActions('i18n', {
      setLang: 'setLang'
    })
  }
}
</script>

<style scoped>

</style>
