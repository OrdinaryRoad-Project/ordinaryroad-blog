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
    <v-text-field
      v-model="model.snapshotUrl"
      :loading="snapshotParams.loading"
      :disabled="snapshotParams.loading"
      clearable
      :rules="[$or.rules.max500Chars]"
      :error-messages="snapshotParams.error"
      :label="$t('friendLink.snapshotUrl')"
      @keydown.enter="$emit('submit')"
    >
      <template #append-outer>
        <v-form ref="snapshotFileForm">
          <v-file-input
            hide-input
            hide-details
            :rules="[maxFileSize10MB]"
            accept="image/*"
            class="pt-0 mt-0 or-friend-link-file-input"
            @change="onPictureSelected"
          />
        </v-form>
      </template>
    </v-text-field>
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
    maxFileSize10MB: value => (!value || value.size <= 100) || 'File size should be less than or equal to  MB.',
    snapshotParams: {
      error: null,
      loading: false
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
      if (file == null) {
        return
      }
      if (!this.$refs.snapshotFileForm.validate()) {
        this.snapshotParams.error = '文件过大，请重新选择'
        setTimeout(() => {
          this.snapshotParams.error = null
          this.$refs.snapshotFileForm.resetValidation()
        }, 2000)
      } else {
        this.snapshotParams.loading = true
        this.$apis.blog.upload(file)
          .then((data) => {
            this.model.snapshotUrl = data
            this.snapshotParams.loading = false
          })
          .catch(() => {
            this.snapshotParams.loading = false
          })
      }
    },
    validate () {
      return this.$refs.form.validate()
    }
  }
}
</script>

<style>
.or-friend-link-file-input .v-input__prepend-outer{
  margin-top: 0 !important;
  margin-bottom: 0 !important;
}
</style>
