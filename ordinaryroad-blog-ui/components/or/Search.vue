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
  <span
    style="position: relative;"
    class="d-inline-flex align-center"
  >
    <v-fade-transition>
      <v-form
        v-if="(autoExpand&&$vuetify.breakpoint.mdAndUp)||searchInputFocused"
        style="position: absolute; right: 0; width: 100%; max-width: 400px"
        :style="{'width':$vuetify.breakpoint.smAndDown&&!searchInputFocused?'100px':'300px'}"
        @submit="onSearchFormSubmit"
        @submit.n.native.prevent
      >
        <v-text-field
          ref="input"
          v-model.trim="searchInput"
          dense
          :autofocus="!autoExpand||$vuetify.breakpoint.smAndDown"
          class="transition-fast-in-fast-out"
          :class="searchInputFocused?'elevation-2':null"
          hide-details
          :placeholder="searchInputPlaceholder"
          solo
          :solo-inverted="soloInverted"
          clearable
          flat
          maxlength="10"
          :rules="[$or.rules.max100Chars]"
          append-icon="mdi-magnify"
          @click:append="onClickSearchInputAppend"
          @focusin="searchInputFocused=true"
          @focusout="searchInputFocused=false"
        />
      </v-form>
    </v-fade-transition>
    <v-fade-transition>
      <v-btn
        v-if="(!autoExpand||$vuetify.breakpoint.smAndDown)&&!searchInputFocused"
        icon
        @click="searchInputFocused=true"
      >
        <v-icon>mdi-magnify</v-icon>
      </v-btn>
    </v-fade-transition>
  </span>
</template>

<script>
export default {
  name: 'OrSearch',
  props: {
    disableHotKey: {
      type: Boolean,
      default: false
    },
    soloInverted: {
      type: Boolean,
      default: false
    },
    autoExpand: {
      type: Boolean,
      default: true
    }
  },
  data: () => ({
    searchInput: '',
    searchInputFocused: false
  }),
  computed: {
    searchInputPlaceholder () {
      let placeholder = ''
      if (!this.$vuetify.breakpoint.smAndDown && !this.disableHotKey) {
        if (this.searchInputFocused) {
          placeholder = this.$t('focusSearchInputTipWhenFocused')
        } else {
          placeholder = this.$t('focusSearchInputTip')
        }
      } else {
        placeholder = this.$t('search')
      }
      return placeholder
    }
  },
  watch: {
    searchInputFocused (val) {
      this.$emit('update:focused', val)
    }
  },
  mounted () {
    window.addEventListener('keydown', (e) => {
      if (this.$vuetify.breakpoint.smAndDown || this.disableHotKey) {
        return
      }
      // console.log(e.code)
      if (e.key === '/') {
        if (this.searchInputFocused) {
          return
        }
        this.searchInputFocused = true
        setTimeout(() => {
          this.$refs.input.focus()
        }, 100)
      } else if (e.key === 'Escape') {
        if (!this.searchInputFocused) {
          return
        }
        this.searchInputFocused = false
        this.$refs.input.blur()
      }
    })
  },
  methods: {
    onClickSearchInputAppend () {
      if (this.searchInputFocused) {
        this.onSearchFormSubmit()
      } else {
        this.searchInputFocused = true
      }
    },

    onSearchFormSubmit () {
      if (!this.searchInput || this.searchInput.trim() === '') {
        return
      }
      window.open(`/search/${this.searchInput}`, '_blank')
    }
  }
}
</script>

<style scoped>

</style>
