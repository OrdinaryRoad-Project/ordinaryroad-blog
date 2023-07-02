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
  <v-form ref="form">
    <v-text-field
      v-model="model.name"
      :rules="[$or.rules.notBlank,$or.rules.max50Chars]"
      :label="$t('friendLink.name')"
      @keydown.enter="$emit('submit')"
    />
    <v-text-field
      v-model="model.description"
      :rules="[$or.rules.notBlank,$or.rules.max100Chars]"
      :label="$t('friendLink.description')"
      @keydown.enter="$emit('submit')"
    />
    <v-text-field
      v-model="model.url"
      :rules="[$or.rules.notBlank,$or.rules.max500Chars]"
      :label="$t('friendLink.url')"
      @keydown.enter="$emit('submit')"
    />
    <v-text-field
      v-model="model.logo"
      :rules="[$or.rules.notBlank,$or.rules.max500Chars]"
      :label="$t('friendLink.logo')"
      @keydown.enter="$emit('submit')"
    />
    <v-text-field
      v-model="model.email"
      :rules="[$or.rules.max500Chars]"
      :label="$t('friendLink.email')"
      @keydown.enter="$emit('submit')"
    />
    <or-file-field
      ref="snapshotFileField"
      v-model="model.snapshotUrl"
      :loading="snapshotParams.uploading"
      :rules="[$or.rules.maxFileSize10MB]"
      :text-rules="[$or.rules.max500Chars]"
      :label="$t('friendLink.snapshotUrl')"
      accept="image/*"
      error-when-file-not-valid="文件过大，请重新选择"
      @keydown-enter="$emit('submit')"
      @change="onPictureSelected"
    />
  </v-form>
</template>

<script>
export default {
  name: 'OrBlogFriendLinkSaveForm',
  props: {
    preset: {
      type: Object,
      default: () => ({
        name: null
      })
    }
  },
  data: () => ({
    snapshotParams: {
      uploading: false
    },
    model: {}
  }),
  watch: {
    preset: {
      handler (val) {
        this.model = Object.assign({}, val)
      },
      deep: true,
      immediate: true
    },
    model: {
      handler (val) {
        this.$emit('update', val)
      },
      deep: true,
      immediate: true
    }
  },
  mounted () {
  },
  methods: {
    onPictureSelected (file) {
      this.snapshotParams.uploading = true
      this.$apis.blog.upload(file)
        .then((data) => {
          this.model.snapshotUrl = data
          this.snapshotParams.uploading = false
        })
        .catch(() => {
          this.snapshotParams.uploading = false
        })
    },
    validate () {
      return this.$refs.form.validate() && this.$refs.snapshotFileField.validate()
    }
  }
}
</script>

<style scoped>
</style>
