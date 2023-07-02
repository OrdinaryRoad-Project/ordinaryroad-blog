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
  <!-- 悬浮预览网站 -->
  <v-menu
    open-delay="500"
    open-on-hover
    :disabled="!item.snapshotUrl||item.TYPE==='APPLY'"
  >
    <template #activator="{ attrs, on }">
      <v-card
        v-bind="attrs"
        hover
        class="d-flex ma-1"
        :href="item.TYPE==='APPLY'?'/friend_link/apply':item.url"
        :target="item.TYPE==='APPLY'?'_self':'_blank'"
        v-on="on"
      >
        <v-img
          v-if="item.TYPE==='APPLY'"
          :gradient="$vuetify.theme.dark?'rgba(0,0,0,0.2),rgba(0,0,0,0.2)':''"
          class="align-center"
          aspect-ratio="1"
        >
          <div class="d-flex align-center justify-center text--primary">
            <v-card-title class="pa-0 font-weight-bold">
              {{ $t('friendLink.applyNow') }}
            </v-card-title>
            <v-icon large>
              mdi-chevron-right
            </v-icon>
          </div>
        </v-img>
        <v-img
          v-else
          :gradient="$vuetify.theme.dark?'rgba(0,0,0,0.2),rgba(0,0,0,0.2)':''"
          :src="item.logo"
          class="align-end"
          aspect-ratio="1"
        >
          <div style="background-color: rgba(0,0,0,0.25); ">
            <v-card-text
              class="ma-0 white--text pt-0 pb-0 ps-2 pe-2 title"
              style="overflow: hidden;text-overflow:ellipsis;white-space: nowrap"
            >
              {{ item.name }}
            </v-card-text>
            <v-card-text
              class="ma-0 white--text pt-0 pb-1 ps-2 pe-2"
              style="overflow: hidden;text-overflow:ellipsis;white-space: nowrap"
            >
              {{ item.description }}
            </v-card-text>
          </div>
        </v-img>
      </v-card>
    </template>
    <v-card
      :href="item.url"
      target="_blank"
    >
      <v-img
        min-width="100%"
        :width="$vuetify.breakpoint.width/4"
        :src="$apis.blog.getFileUrl(item.snapshotUrl)"
        aspect-ratio="1"
      />
    </v-card>
  </v-menu>
</template>

<script>
export default {
  name: 'OrBlogFriendLinkItem',
  props: {
    item: {
      type: Object,
      required: true
    }
  }
}
</script>

<style scoped>

</style>
