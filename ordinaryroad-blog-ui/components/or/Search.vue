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
          id="orSearchInput"
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
    observer: null,
    /**
     * 观察器的配置（需要观察什么变动）
     */
    observerConfig: { attributes: false, childList: true, subtree: true },

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
  created () {
  },
  mounted () {
    // 创建一个观察器实例并传入回调函数
    this.observer = new MutationObserver(this.mutationObserverCallback)
    // 以上述配置开始观察目标节点
    this.observer.observe(document, this.observerConfig)

    // 之后，可停止观察
    // observer.disconnect()

    this.initFocusEvents()

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
    /**
     * 当观察到变动时执行的回调函数
     */
    mutationObserverCallback (mutationsList, observer) {
      // Use traditional 'for loops' for IE 11
      for (const mutation of mutationsList) {
        if (mutation.type === 'childList') {
          // A child node has been added or removed.
          // console.log(mutation)
          // console.log(mutation.addedNodes)
          mutation.addedNodes.forEach((value, key, parent) => {
            // console.log(value)
            const editableElements = this.getEditableElements(value)
            if (editableElements && editableElements.length) {
              // console.log(value)
              // console.log('editableElements', editableElements)
              for (const element of editableElements) {
                element.addEventListener('focus', this.onFocusOtherEditableElements)
                element.addEventListener('blur', this.onBlurOtherEditableElements)
              }
            }
          })
        }
      }
    },
    /**
     * 兼容其他输入框正常输入'/'
     */
    initFocusEvents () {
      const editableElements = this.getEditableElements(document)
      // console.log(editableElements)
      if (editableElements && editableElements.length) {
        for (const element of editableElements) {
          element.addEventListener('focus', this.onFocusOtherEditableElements)
          element.addEventListener('blur', this.onBlurOtherEditableElements)
          // console.log('element', element)
        }
      }
    },
    /**
     * 获取可编辑的元素
     * @param element
     */
    getEditableElements (element) {
      if (!element) {
        return []
      }
      const inputs = [...element.getElementsByTagName('input')].filter((input) => {
        return input.id !== 'orSearchInput' && input.type === 'text' && input.getAttribute('readonly') !== 'readonly'
      })
      const textareas = element.getElementsByTagName('textarea')
      return [...inputs, ...textareas]
    },

    onFocusOtherEditableElements (ev) {
      // console.log('onFocus', ev)
      this.$store.dispatch('app/setSearchInputHotKeyEnabled', false)
    },

    onBlurOtherEditableElements (ev) {
      // console.log('onBlur', ev)
      this.$store.dispatch('app/setSearchInputHotKeyEnabled', true)
    },

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
      this.$emit('onSubmit', this.searchInput)
    }
  }
}
</script>

<style scoped>

</style>
