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
  <v-container fluid class="pa-0">
    <!-- 顶部栏 -->
    <v-app-bar
      clipped-left
      app
      dark
      :elevation="showScrollToTopFab?4:0"
      :color="showScrollToTopFab?'primary':'transparent'"
    >
      <!-- 开关目录按钮 -->
      <v-tooltip
        v-if="catalogue.length>0"
        right
      >
        <template #activator="{ on }">
          <v-btn
            :light="!$vuetify.theme.dark&&drawer&&!showScrollToTopFab"
            icon
            v-on="on"
            @click="drawer = !drawer"
          >
            <v-icon v-if="drawer">
              mdi-close
            </v-icon>
            <v-icon v-else>
              mdi-table-of-contents
            </v-icon>
          </v-btn>
        </template>
        <span>{{ drawer ? '关闭目录' : '目录' }}</span>
      </v-tooltip>

      <!-- 标题 -->
      <v-toolbar-title>{{ showScrollToTopFab ? blogArticle.title : null }}</v-toolbar-title>

      <v-spacer />

      <!-- 在其他设备上阅读 -->
      <v-menu
        v-if="$vuetify.breakpoint.smAndDown"
        origin="top center"
        offset-overflow
        absolute
        transition="scale-transition"
      >
        <template #activator="{ on }">
          <v-btn icon dark v-on="on">
            <v-icon>mdi-cellphone-link</v-icon>
          </v-btn>
        </template>
        <div style="width: 150px;height: 150px">
          <vue-qr
            :margin="10"
            :text="vueQrUrl"
            style="width: 100%;height: 100%"
          />
        </div>
      </v-menu>
      <v-tooltip v-else bottom>
        <template #activator="{ on }">
          <v-menu
            origin="top center"
            offset-overflow
            absolute
            transition="scale-transition"
            v-on="on"
          >
            <template #activator="{ on }">
              <v-btn icon dark v-on="on">
                <v-icon>mdi-cellphone-link</v-icon>
              </v-btn>
            </template>
            <div style="width: 150px;height: 150px">
              <vue-qr
                :margin="10"
                :text="vueQrUrl"
                style="width: 100%;height: 100%"
              />
            </div>
          </v-menu>
        </template>
        <span>在其他设备上继续阅读</span>
      </v-tooltip>

      <v-btn icon @click.stop="$store.dispatch('app/toggleRightDrawerModel')">
        <v-icon>mdi-cog</v-icon>
      </v-btn>
    </v-app-bar>

    <!--目录-->
    <v-navigation-drawer
      v-if="catalogue.length>0"
      v-model="drawer"
      clipped
      app
    >
      <div id="article-outline" />
      <v-treeview
        v-if="catalogue.length!==0"
        id="treeViewOutline"
        open-all
        dense
        :items="catalogue"
        activatable
        transition
        shaped
        :active="active"
        hoverable
        class="me-2"
      >
        <template #label="{item}">
          <div :id="`label_blog-toc-id_${item.id}`" :blog-toc-id="item.id">
            {{ item.name }}
          </div>
        </template>
      </v-treeview>
    </v-navigation-drawer>

    <!-- 设置 -->
    <or-settings-drawer show-i18n-setting />

    <!-- 封面 -->
    <v-img
      :src="$apis.blog.getFileUrl(blogArticle.coverImage,'https://api.ixiaowai.cn/gqapi/gqapi.php')"
      style="height: 400px; margin-top: -70px"
      gradient="rgba(0,0,0,.15),rgba(0,0,0,.15)"
    >
      <!-- 封面占位符 -->
      <template #placeholder>
        <v-skeleton-loader type="image" />
        <v-skeleton-loader type="image" />
      </template>

      <!-- 文章标题 -->
      <v-card-title
        class="display-1 text-center justify-center white--text font-weight-bold"
        style="position: absolute; left: 0; right: 0; bottom: 60px; top: 0"
      >
        {{ blogArticle.title }}
      </v-card-title>

      <!-- 作者信息 -->
      <v-list-item
        dark
        class="d-flex justify-center"
        style="position: absolute; left: 0; right: 0; bottom: 100px"
      >
        <!-- 头像 -->
        <v-list-item-avatar>
          <v-img
            style="border:1px solid;"
            alt=""
            :src="$apis.blog.getFileUrl(blogArticle.user.avatar)"
          >
            <template #placeholder>
              <v-skeleton-loader type="image" />
            </template>
          </v-img>
        </v-list-item-avatar>

        <div>
          <!-- 用户名 -->
          <v-list-item-title
            class="font-weight-medium"
          >
            {{ blogArticle.user.username }}
          </v-list-item-title>

          <!-- todo 个性签名 -->
          <v-list-item-subtitle>世间种种平凡都不平凡</v-list-item-subtitle>
        </div>

        <div class="ms-12 text-caption">
          <!-- 创建时间 -->
          <div>
            <strong>创建于：</strong>{{ blogArticle.createdTime }}
          </div>
          <!-- 更新时间 -->
          <div v-if="blogArticle.updateTime">
            <strong>修改于：</strong>{{ blogArticle.updateTime }}
          </div>
        </div>

        <!-- 浏览量 -->
        <div v-if="false" class="ms-2">
          <v-icon>mdi-eye</v-icon>
          <span class="ms-1">999</span>
        </div>
      </v-list-item>
    </v-img>

    <!--文章详情-->
    <v-card
      id="card"
      :tile="$vuetify.breakpoint.smAndDown"
      :raised="!$vuetify.breakpoint.smAndDown"
      :flat="$vuetify.breakpoint.smAndDown"
      :max-width="$vuetify.breakpoint.smAndDown?`1000px`:`80%`"
      class="mx-auto"
      elevation="8"
      style="margin-top: -60px; margin-bottom: 60px"
    >
      <v-card flat class="pt-1">
        <!-- 摘要 -->
        <div v-if="blogArticle.summary && blogArticle.summary !== ''">
          <or-md-vditor
            class="mt-2"
            :dark="$vuetify.theme.dark"
            :pre-set-content="blogArticle.summary"
          />
        </div>

        <!-- 阅读时长创建时间修改时间 -->
        <div>
          <v-alert
            text
            dense
            class="text-break mx-5 mt-2"
            border="left"
            colored-border
            type="info"
          >
            <strong class="font-weight-bold">友情提示：</strong>
            此篇文章大约需要阅读<span style="color: #82b1ff">{{ watchTimeString }}</span>。
          </v-alert>
        </div>

        <!-- 内容 -->
        <or-md-vditor
          :headings-offset-top="$vuetify.breakpoint.smAndDown?56:64"
          :preview-toc.sync="toc"
          :current-toc-index.sync="currentTocIndex"
          :dark="$vuetify.theme.dark"
          :pre-set-content="blogArticle.content"
          @after="onVditorAfter"
        />

        <br>

        <!-- 赞赏 -->
        <div id="likeFab" />
        <div v-if="blogArticle.canReward" class="d-flex justify-center pt-5">
          <v-tooltip bottom>
            <template #activator="{ on }">
              <div v-on="on">
                <v-menu origin="center" nudge-left="45" nudge-top="75" transition="scale-transition">
                  <template #activator="{ on }">
                    <v-btn fab color="accent" v-on="on">
                      <v-icon>mdi-thumb-up-outline</v-icon>
                    </v-btn>
                  </template>
                  <v-img style="width: 150px" :src="require('~/static/vuetify-logo.svg')" />
                </v-menu>
              </div>
            </template>
            <span>赞赏</span>
          </v-tooltip>
        </div>

        <!--转载声明-->
        <div class="pb-1">
          <v-alert border="left" text class="text-break mx-5" colored-border color="#e00000">
            <div v-if="blogArticle.original">
              <strong>本文作者：</strong>{{ blogArticle.user.username }}<br>
            </div>
            <strong>本文链接：</strong><a :href="currentUrl" target="_blank">{{ currentUrl }}</a><br>
            <div v-if="blogArticle.original">
              <strong>版权声明：</strong>本文为OrdinaryRoad博客博主{{ blogArticle.user.username }}的原创文章，遵循<a
                target="_blank"
                href="https://creativecommons.org/licenses/by-sa/4.0/"
              >CC BY-SA 4.0</a>许可协议，转载请附上本文链接及本声明。
            </div>
          </v-alert>
        </div>

        <!-- TODO 分类 -->

        <!-- TODO 标签 -->

        <!-- TODO 评论 -->
        <v-card>
          <v-card-title>评论（{{ articleComments.total }}）</v-card-title>
          <div class="mx-2">
            <div class="d-flex align-end">
              <or-md-vditor
                ref="commentVditor"
                :transfer-content.sync="commentOptions.content"
                placeholder="写一条友善的发言吧（支持Markdown）"
                class="flex-grow-1 mx-2"
                :dark="$vuetify.theme.dark"
                pre-set-content=""
                comment-mode
                :read-only="false"
              />
              <v-btn
                color="primary"
                class="ml-auto"
                :disabled="!commentContentValid"
                :loading="commentOptions.posting"
                @click="postComment"
              >
                发送
              </v-btn>
            </div>
            <v-alert
              v-model="commentOptions.showAlert"
              class="mt-2 mx-2"
              transition="slide-y-reverse-transition"
              dense
              dismissible
              color="secondary"
              colored-border
              border="left"
              elevation="2"
            >
              回复{{
                commentOptions.parentComment ? `${commentOptions.parentComment.user.username}：${commentOptions.parentComment.content}` : ''
              }}
            </v-alert>
          </div>
          <or-blog-comment-list
            ref="commentList"
            :article-id="blogArticle.uuid"
            :article-comments="articleComments"
            @clickReply="onClickReply"
          />
        </v-card>

        <!-- TODO 上/下一篇 -->
      </v-card>
    </v-card>

    <!-- Footer -->
    <or-footer />

    <!-- 恢复阅读位置日夜间模式 -->
    <v-snackbar v-model="snackbar">
      已自动恢复阅读位置、日/夜间模式参数
      <v-btn color="pink" text @click="$vuetify.goTo(0)">
        从头开始
      </v-btn>
    </v-snackbar>

    <!-- 滚动到顶部 -->
    <v-fab-transition>
      <v-btn
        v-show="showScrollToTopFab"
        fab
        class="ma-2"
        color="accent"
        fixed
        right
        :small="$vuetify.breakpoint.smAndDown"
        bottom
        @click="$vuetify.goTo(0)"
      >
        <v-progress-circular
          :size="$vuetify.breakpoint.smAndDown?40:56"
          width="2"
          rotate="-90"
          :value="percentOfRead * 100"
        >
          <v-icon
            :small="$vuetify.breakpoint.smAndDown"
          >
            mdi-chevron-up
          </v-icon>
        </v-progress-circular>
      </v-btn>
    </v-fab-transition>
  </v-container>
</template>

<script>
import VueQr from 'vue-qr'

export default {
  name: 'OrBlogArticleDetail',
  components: { VueQr },
  props: {
    article: {
      type: Object,
      required: true
    },
    articleComments: {
      type: Object,
      required: true
    }
  },
  data: () => ({
    drawer: false,
    showScrollToTopFab: false,
    snackbar: false,
    percentOfRead: 0.0,
    realPercentOfRead: 0.0,
    catalogue: [],
    blogArticle: {},
    toc: [],
    active: [],
    currentTocIndex: null,

    commentOptions: {
      posting: false,

      content: '',
      parentId: '',
      showAlert: false
    }
  }),
  computed: {
    commentContentValid () {
      return this.commentOptions.content && this.commentOptions.content !== '' && this.commentOptions.content !== '\n'
    },
    vueQrUrl () {
      return this.currentUrl + '?p=' + this.realPercentOfRead + '&t=' + this.$store.getters['app/getSelectedThemeOption']
    },
    currentUrl () {
      return process.client ? window.location.href : ''
    },
    watchTimeString () {
      // 估算阅读时长
      const totalSeconds = this.article.content.length / 8
      const formatSeconds = this.$util.formatSeconds(totalSeconds)
      return formatSeconds === '' ? '一瞬间~' : formatSeconds
    }
  },
  watch: {
    /* 生成treeview目录 */
    toc (val) {
      // console.log('目录下标处理前', val)
      if (!val || val.length === 0) {
        this.catalogue = []
        this.drawer = false
        return
      }
      this.drawer = !this.$vuetify.breakpoint.smAndDown

      // 处理连续的
      const tocIndexContinuously = []
      if (val.length >= 1) {
        tocIndexContinuously.push({
          ...val[0],
          index: 0
        })
      }
      // console.log(tocIndexContinuously)
      if (val.length > 1) {
        for (let i = 1; i < val.length; i++) {
          const preItem = val[i - 1]
          const currentItem = val[i]
          const newPreItem = tocIndexContinuously[i - 1]

          const preIndex = preItem.index
          const currentIndex = currentItem.index
          const newPreIndex = newPreItem.index

          if (currentIndex === preIndex) {
            // 相等 newPreIndex
            tocIndexContinuously.push({
              ...currentItem,
              index: newPreIndex
            })
          } else if (currentIndex > preIndex) {
            // 大于 newPreIndex+1
            tocIndexContinuously.push({
              ...currentItem,
              index: newPreIndex + 1
            })
          } else {
            // 小于 往前找直到 currentIndex > preIndex，没找到则为0
            let found = false
            for (let j = i - 1; j >= 0; j--) {
              const tempPreItem = val[j]
              if (currentIndex > tempPreItem.index) {
                tocIndexContinuously.push({
                  ...currentItem,
                  index: tocIndexContinuously[j].index + 1
                })
                found = true
                break
              }
            }
            if (!found) {
              tocIndexContinuously.push({
                ...currentItem,
                index: 0
              })
            }
          }
        }
      }

      // console.log('目录下标处理后', tocIndexContinuously)

      /**
       * <code>
       *   [
       *      {
       *         id: headCnt++,
       *         name: headString,
       *         children: []
       *      },
       *      {
       *         id: headCnt++,
       *         name: headString,
       *         children: []
       *      }
       *   ]
       * </code>
       * @type {*[]}
       */
      const headings = JSON.parse(JSON.stringify(tocIndexContinuously))
      const headCatalogue = {
        id: -1,
        name: 'HEAD',
        children: []
      }
      const catalogues = []
      catalogues.push(headCatalogue)

      for (let i = 0; i < headings.length; i++) {
        const currentItem = headings[i]
        currentItem.name = currentItem.headString
        currentItem.children = []
        if (i === 0) {
          currentItem.parent = headCatalogue
          currentItem.parent.children.push(currentItem)
        } else {
          let preItem = headings[i - 1]
          if (currentItem.index === preItem.index) {
            // 等于放在同一层
            currentItem.parent = preItem.parent
            preItem.parent.children.push(currentItem)
          } else if (currentItem.index > preItem.index) {
            // 大于放在下一层
            currentItem.parent = preItem
            preItem.children.push(currentItem)
          } else {
            // 小于，一直往前找，直到index相等（因为处理过了，所以肯定存在相等的）
            while (preItem.index !== currentItem.index) {
              preItem = preItem.parent
            }
            currentItem.parent = preItem.parent
            preItem.parent.children.push(currentItem)
          }
        }
      }

      this.catalogue = headCatalogue.children.concat()

      /* 监听目录点击事件 */
      this.$nextTick(function () {
        document.getElementById('treeViewOutline').onclick = (event) => {
          let elementTemp = event.target
          if (elementTemp.classList.contains('v-treeview-node__root')) {
            elementTemp = elementTemp.querySelector('.v-treeview-node__label')
          }
          // console.log(elementTemp)
          const elementsByTagName = elementTemp.getElementsByTagName('div')
          if (elementsByTagName[0]) {
            elementTemp = elementsByTagName[0]
            // console.log(elementsByTagName[0])
          }
          const headId = elementTemp.getAttribute('blog-toc-id')
          // console.log(headId)
          const headDivElement = document.getElementById('blog-toc-id-' + headId)
          if (headDivElement) {
            this.$vuetify.goTo(headDivElement)
          }
        }
      })
    },
    currentTocIndex (val) {
      // console.log('currentTocIndex', val)
      this.active = []
      this.active.push(val)
      // const element = document.getElementById(`label_blog-toc-id_${val}`)
    }
  },
  created () {
    this.blogArticle = this.article
  },
  mounted () {
    // 监听滚动事件，然后用handleScroll这个方法进行相应的处理
    window.addEventListener('scroll', this.handleScroll)
  },
  destroyed () {
    // 移除滚动事件
    window.removeEventListener('scroll', this.handleScroll, false)
  },
  methods: {
    onClickReply ({
      originalComment,
      parentComment
    }) {
      this.commentOptions.parentComment = parentComment
      this.commentOptions.parentId = parentComment.uuid
      this.commentOptions.showAlert = true
    },
    postComment () {
      if (!this.$access.checkLogin()) {
        return
      }
      if (this.commentContentValid) {
        this.commentOptions.posting = true
        this.$apis.blog.comment.post({
          ...this.commentOptions,
          articleId: this.blogArticle.uuid
        })
          .then((data) => {
            this.commentOptions.content = ''
            this.commentOptions.parentId = ''
            this.commentOptions.showAlert = false
            this.$refs.commentVditor.setValue('')
            this.$refs.commentList.addComment(data)
            this.commentOptions.posting = false
          })
          .catch(() => {
            this.commentOptions.posting = false
          })
      }
    },
    onVditorAfter () {
      // 恢复主题和阅读位置
      this.restoreLastReadProfile()
    },
    handleScroll () {
      const totalHeight = document.body.scrollHeight
      // console.log("this.totalHeight", totalHeight)
      // 滚动条偏移量
      const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop

      // 是否显示滑倒顶部的FAB
      this.showScrollToTopFab = scrollTop > 0

      // 文章开始偏移量
      const cardOffsetTop = document.getElementById('card').offsetTop
      // 文章结束偏移量
      const likeFabOffsetTop = document.getElementById('likeFab').offsetTop

      // console.log('document.body.scrollHeight', document.body.scrollHeight, 'document.body.clientHeight', document.body.clientHeight, 'innerHeight', window.innerHeight, 'scrollTop', scrollTop, 'cardOffsetTop', cardOffsetTop, 'likeFabOffsetTop', likeFabOffsetTop, 'totalHeight', totalHeight, 'window.screen.availHeight', window.screen.availHeight)
      // 计算实际阅读百分比
      if (scrollTop >= cardOffsetTop) {
        if (scrollTop + cardOffsetTop <= likeFabOffsetTop - cardOffsetTop) {
          this.percentOfRead = ((scrollTop - cardOffsetTop) / (likeFabOffsetTop - cardOffsetTop - cardOffsetTop - cardOffsetTop)).toFixed(2)
        } else {
          this.percentOfRead = 1.0
        }
      } else {
        this.percentOfRead = 0.0
      }
      // 计算网页百分比
      this.realPercentOfRead = scrollTop / (totalHeight - window.innerHeight)

      // console.log("this.percentOfRead", this.percentOfRead, "this.realPercentOfRead", this.realPercentOfRead)
    },
    /**
     * 恢复上一次阅读参数
     */
    restoreLastReadProfile () {
      setTimeout(() => {
        // 恢复主题
        const selectedThemeOption = this.$route.query.t
        if (selectedThemeOption !== undefined) {
          const tN = parseInt(selectedThemeOption)
          if (tN >= 0 && tN < 4) {
            this.$store.dispatch('app/setSelectedThemeOption', {
              value: tN,
              $vuetify: this.$vuetify
            })
            this.snackbar = true
          }
        }

        // 恢复阅读位置
        const realPercentOfRead = this.$route.query.p
        if (realPercentOfRead !== undefined) {
          const pN = parseFloat(realPercentOfRead)
          if (pN >= 0 && pN <= 100) {
            const totalHeight = document.body.scrollHeight
            // console.log(pN, totalHeight, window.innerHeight, totalHeight - window.innerHeight)
            // 获取准确位置
            this.$vuetify.goTo(pN * (totalHeight - window.innerHeight))
            if (!this.snackbar) {
              this.snackbar = true
            }
          }
        }
      }, 2000)
    }
  }
}
</script>

<style scoped>
</style>
