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
  <v-hover :disabled="!editable">
    <template #default="{ hover }">
      <v-avatar
        style="border:1px solid;"
        :size="size"
        :class="avatarClass"
        color="grey"
      >
        <v-img v-if="avatar" :src="avatar">
          <template #default>
            <v-fade-transition>
              <v-overlay
                v-if="avatarOptions.uploading"
                absolute
              >
                <v-progress-circular indeterminate />
              </v-overlay>
            </v-fade-transition>
          </template>
          <template #placeholder>
            <v-skeleton-loader type="image" />
          </template>
        </v-img>
        <span v-else class="white--text">{{ username.slice(0, 1) }}</span>

        <v-fade-transition>
          <v-overlay
            v-if="!avatarOptions.uploading&&hover"
            v-ripple
            absolute
            style="cursor: pointer"
            @click="$refs.fileInput.$refs.input.click()"
          >
            <v-file-input
              v-show="false"
              ref="fileInput"
              accept="image/*"
              @change="onChange"
            />
            <v-row
              class="fill-height ma-0"
              align="center"
              justify="center"
            >
              <v-icon>mdi-pencil</v-icon>
            </v-row>
          </v-overlay>
        </v-fade-transition>
      </v-avatar>
    </template>
  </v-hover>
</template>

<script>
export default {
  name: 'OrAvatar',
  props: {
    avatarClass: {
      type: String,
      default: ''
    },
    editable: {
      type: Boolean,
      default: false
    },
    username: {
      type: String,
      default: null
    },
    avatar: {
      type: String,
      default: null
    },
    size: {
      type: [Number, String],
      default: 48
    }
  },
  data: () => ({
    avatarOptions: {
      uploading: false
    }
  }),
  methods: {
    onChange (e) {
      this.avatarOptions.uploading = true
      this.$emit('selectAvatar', e)
    },
    finishUpload () {
      this.avatarOptions.uploading = false
    }
  }
}
</script>

<style scoped>

</style>
