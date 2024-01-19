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
    <v-skeleton-loader
      v-if="jinrishiciOptions.loading"
      type="text"
      width="200px"
    />
    <div v-else>
      <v-hover v-model="hover">
        <v-list-item-subtitle
          style="margin-top: -3px;"
          class="d-flex align-center"
        >
          <v-tooltip bottom>
            <template #activator="{ on, attrs }">
              <span
                style="cursor: pointer;"
                v-bind="attrs"
                @click="$refs.dialog.show()"
                v-on="on"
              >
                {{ jinrishiciOptions.result.data.content }}
              </span>
            </template>
            {{ jinrishiciOptions.result.data.origin.title }}——{{ jinrishiciOptions.result.data.origin.author }}（{{ jinrishiciOptions.result.data.origin.dynasty }}）
          </v-tooltip>
          <v-fade-transition>
            <v-btn
              v-if="hover"
              icon
              x-small
              right
              @click="getJinrishici"
            >
              <v-icon>mdi-refresh</v-icon>
            </v-btn>
          </v-fade-transition>
        </v-list-item-subtitle>
      </v-hover>
    </div>
    <or-base-dialog
      ref="dialog"
      :title="`${ jinrishiciOptions.result.data.origin.title }——${ jinrishiciOptions.result.data.origin.author }（${jinrishiciOptions.result.data.origin.dynasty}）`"
      @onConfirm="$refs.dialog.close()"
    >
      <div
        v-if="jinrishiciOptions.result.data.matchTags?.length"
        class="mx-3"
      >
        <v-chip
          v-for="item in jinrishiciOptions.result.data.matchTags"
          :key="item"
          class="mx-1"
        >
          {{ item }}
        </v-chip>
      </div>
      <v-card-text>
        <span class="text-pre-line text-h6">{{ dialogContent }}</span>
      </v-card-text>
      <template #actions>
        <span />
      </template>
    </or-base-dialog>
  </div>
</template>

<script>
export default {
  name: 'OrJinrishiciSubtitle',
  data: () => ({
    hover: false,
    jinrishiciOptions: {
      result: {
        data: {
          content: null,
          origin: {
            author: null,
            dynasty: null,
            title: null
          }
        }
      },
      loading: true
    }
  }),
  computed: {
    dialogContent () {
      if (!this.jinrishiciOptions.result?.data?.origin?.content?.length) {
        return null
      }
      let s = ''
      for (let i = 0; i < this.jinrishiciOptions.result.data.origin.content.length; i++) {
        const content = `原文：${this.jinrishiciOptions.result.data.origin.content[i].trim()}`
        let translate = ''
        if (this.jinrishiciOptions.result.data.origin.translate) {
          translate = `\n译文：${this.jinrishiciOptions.result.data.origin.translate[i].trim()}`
        } else {
          translate = '\n译文：暂无'
        }
        s += `${content}${translate}`
        if (i !== this.jinrishiciOptions.result.data.origin.content.length - 1) {
          s += '\n\n'
        }
      }
      return s
    }
  },
  mounted () {
    this.getJinrishici()
  },
  methods: {
    getJinrishici () {
      this.jinrishiciOptions.loading = true

      // https://www.jinrishici.com/doc/#return
      // { "status": "success", "data": { "id": "5b8b9572e116fb3714e6fa55", "content": "小荷才露尖尖角，早有蜻蜓立上头。", "popularity": 2200000, "origin": { "title": "小池", "dynasty": "宋代", "author": "杨万里", "content": [ "泉眼无声惜细流，树阴照水爱晴柔。", "小荷才露尖尖角，早有蜻蜓立上头。" ], "translate": [ "泉眼悄然无声是因舍不得细细的水流，树荫倒映水面是喜爱晴天和风的轻柔。", "娇嫩的小荷叶刚从水面露出尖尖的角，早有一只调皮的小蜻蜓立在它的上头。" ] }, "matchTags": [ "荷花", "夏" ], "recommendedReason": "", "cacheAt": "2023-08-11T16:15:26.888975008" }, "token": "AB/O6yIbMgZ/z6IHRkCo5rGqD8R0zbMc", "ipAddress": "112.43.93.229", "warning": null }
      // { "status": "success", "data": { "id": "5b8b9572e116fb3714e73682", "content": "晴浦晚风寒，青山玉骨瘦。", "popularity": 1570, "origin": { "title": "卜算子·雪江晴月（回文，倒读《巫山一段云》）", "dynasty": "清代", "author": "董以宁", "content": [ "明月淡飞琼，阴云薄中酒。收尽盈盈舞絮飘，点点轻鸥咒。", "晴浦晚风寒，青山玉骨瘦。回看亭亭雪映窗，淡淡烟垂岫。" ], "translate": null }, "matchTags": [ "晴", "冬", "寒冷" ], "recommendedReason": "", "cacheAt": "2023-12-03T21:19:28.788325691" }, "token": "lvGhnVGs0XwgAo62hyr+9hubCQWxYsEB", "ipAddress": "112.43.92.156", "warning": null }

      const jinrishici = require('jinrishici')
      jinrishici.load((result) => {
        this.jinrishiciOptions.result = result
        this.jinrishiciOptions.loading = false
      }, (error) => {
        this.jinrishiciOptions.loading = false
        this.$snackbar.error(error)
      })
    }
  }
}
</script>

<style scoped>

</style>
