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
    <v-row no-gutters>
      <!-- 推荐文章 -->
      <v-col
        cols="12"
        lg="7"
      >
        <v-card
          class="mt-3 ms-3"
          :class="{
            'me-3':$vuetify.breakpoint.mdAndDown,
            'me-1':!$vuetify.breakpoint.mdAndDown
          }"
          hover
        >
          <v-carousel
            cycle
            :height="$vuetify.breakpoint.smAndDown?'250px':'458px'"
            interval="5000"
            show-arrows-on-hover
          >
            <v-carousel-item
              v-for="article in recommendArticles"
              :key="article.uuid"
              :href="`/${article.user.uid}/article/${article.uuid}`"
              target="_blank"
              :src="articleCoverImageUrl(article)"
            >
              <!-- 标题 -->
              <v-card-title
                class="white--text text-justify tow-lines-text font-weight-bold"
                style="background: linear-gradient(to top, rgba(0, 0, 0, .0), rgba(0, 0, 0, .2), rgba(0, 0, 0, .4))"
                :class="{
                  'text-subtitle-1':$vuetify.breakpoint.smAndDown,
                }"
              >
                {{ article.title }}
              </v-card-title>
            </v-carousel-item>
          </v-carousel>
        </v-card>
      </v-col>

      <v-col
        cols="12"
        lg="5"
        class="d-flex flex-column"
      >
        <!-- 每日壁纸 -->
        <v-card
          hover
          height="250px"
          :href="dailyBing?.copyrightlink"
          target="_blank"
          class="mb-1 mt-3 me-3"
          :class="{
            'ms-3':$vuetify.breakpoint.mdAndDown,
            'ms-1':!$vuetify.breakpoint.mdAndDown
          }"
        >
          <v-img
            class="fill-height"
            :src="`https://cn.bing.com${dailyBing?.url}`"
            gradient="rgba(0,0,0,.20),rgba(0,0,0,.20)"
          >
            <template #placeholder>
              <div class="d-flex or-index-bing-cover-img">
                <v-skeleton-loader type="image" class="flex-grow-1" tile/>
              </div>
            </template>
            <div class="d-flex fill-height flex-column justify-space-between white--text">
              <v-card-title>{{ dailyBing?.title }}</v-card-title>
              <small class="px-4 pb-2">{{ dailyBing?.copyright }}</small>
            </div>
          </v-img>
        </v-card>

        <!-- 一言 -->
        <v-hover v-model="hoverYiyan">
          <v-card
            hover
            height="200px"
            class="mt-1 mb-3 me-3"
            :class="{
              'ms-3':$vuetify.breakpoint.mdAndDown,
              'ms-1':!$vuetify.breakpoint.mdAndDown
            }"
          >
            <v-card-title>
              一言
              <v-spacer />
              <v-fade-transition>
                <v-btn
                  v-if="hoverYiyan&&yiyanList.length===maxYiyanListLength"
                  icon
                  small
                  @click.stop="refreshYiyan"
                >
                  <v-icon>mdi-refresh</v-icon>
                </v-btn>
              </v-fade-transition>
            </v-card-title>
            <v-card-text>
              <div class="text-sm-h4 text-h5 font-weight-bold text--primary">
                {{ obj.output }}
                <span class="easy-typed-cursor">|</span>
              </div>
            </v-card-text>
          </v-card>
        </v-hover>
      </v-col>
    </v-row>

    <div
      class="sticky-top"
      :class="{'mx-3':!$vuetify.breakpoint.smAndDown}"
      style="z-index: 2"
      :style="`top :${$vuetify.breakpoint.smAndDown?'56px':'64px'} !important;`"
    >
      <v-tabs
        v-model="tabModel"
        show-arrows
        @change="onTabChange"
      >
        <v-tab>
          {{ $t('all') }}{{ articleTotal ? $t('parenthesesWithSpace', [articleTotal]) : '' }}
        </v-tab>
        <v-tab v-for="tag in tags" :key="tag.uuid">
          {{ tag.name }}{{ $t('parenthesesWithSpace', [tag.article_count]) }}
        </v-tab>
      </v-tabs>
    </div>

    <or-blog-article-list
      ref="list"
      style="z-index: 1"
      auto-load-more
      :total.sync="localArticleTotal"
    />
  </div>
</template>

<script>
export default {
  components: {},
  async asyncData ({ $apis, $axios }) {
    const tags = await $apis.blog.tag.getTopN()
    const recommendArticles = await $apis.blog.article.getRecommendedArticles()
    return {
      tags,
      recommendArticles
    }
  },
  data: () => ({
    tabFirstChange: true,
    tabModel: null,
    articleTotal: null,
    tags: [],
    recommendArticles: [],
    dailyBing: null,

    hoverYiyan: false,
    typed: null,
    currentYiyanIndex: 0,
    yiyanList: [],
    maxYiyanListLength: 5,
    obj: {
      output: '',
      isEnd: false,
      speed: 150,
      singleBack: false,
      sleep: 5000,
      type: 'rollback',
      backSpeed: 80,
      sentencePause: false
    }
  }),
  head () {
    return {
      title: this.$t('appName'),
      titleTemplate: '%s',
      meta: [
        { name: 'sogou_site_verification', content: 'zBDjR0e24C' }
      ]
    }
  },
  computed: {
    localArticleTotal: {
      get () {
        return this.articleTotal
      },
      set (val) {
        if (this.tabModel === 0) {
          this.articleTotal = val
        }
      }
    },
    articleCoverImageUrl () {
      const defaultUrl = `/img/covers/bg${Math.floor(Math.random() * 5) + 1}.jpg`
      return (item) => {
        return this.$apis.blog.getFileUrl(item.coverImage, defaultUrl)
      }
    }
  },
  mounted () {
    this.fetchYiyanData()
    this.fetchDailyBing()
  },
  created () {
  },
  methods: {
    fetchDailyBing() {
      this.$axios.get('/bing/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN')
        .then((data) => {
          this.dailyBing = data.images[0]
        })
        .catch(() => {
          // ignore
        })
    },
    refreshYiyan() {
      this.yiyanList = []
      this.currentYiyanIndex = 0
    },
    fetchYiyanData () {
      if (this.yiyanList.length < this.maxYiyanListLength) {
        // 一言Api进行打字机循环输出效果
        this.$axios.get('https://v1.hitokoto.cn', {
          headers: {
            Origin: 'https://v1.hitokoto.cn',
            Referer: 'https://v1.hitokoto.cn'
          }
        })
          .then(({hitokoto}) => {
            this.yiyanList.push(hitokoto)
            this.initTyped(hitokoto, () => {
              this.fetchYiyanData()
            })
          })
          .catch(() => {
            this.obj.output = '暂时无法访问一言API'
          })
      } else {
        this.initTyped(this.yiyanList[(this.currentYiyanIndex++) % this.maxYiyanListLength], () => {
          this.fetchYiyanData()
        })
      }
    },
    initTyped (input, fn, hooks) {
      const obj = this.obj
      if (input instanceof Array) {
        input = [...input]
      }
      this.typed = new this.$easytyper(obj, input, fn, hooks)
    },
    onTabChange (e) {
      let tagName = ''
      if (e !== 0) {
        const tag = this.tags[e - 1]
        tagName = tag.name
      }
      if (this.tabFirstChange) {
        this.tabFirstChange = false
      } else {
        this.$refs.list.tagName = tagName
        this.$refs.list.getArticles(false)
      }
    }
  }
}
</script>

<style lang="stylus">
.easy-typed-cursor
  opacity: 1
  -webkit-animation: blink 0.7s infinite
  -moz-animation: blink 0.7s infinite
  animation: blink 0.7s infinite

@keyframes blink
  0%
    opacity: 1
  50%
    opacity: 0
  100%
    opacity: 1

@-webkit-keyframes blink
  0%
    opacity: 1
  50%
    opacity: 0
  100%
    opacity: 1

@-moz-keyframes blink
  0%
    opacity: 1
  50%
    opacity: 0
  100%
    opacity: 1

.or-index-bing-cover-img
  height: 100%

  .v-skeleton-loader__image
    height: 100% !important;
</style>
