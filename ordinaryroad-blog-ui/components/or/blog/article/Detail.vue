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
      ref="appBar"
      :hide-on-scroll="percentOfRead>0.0"
      clipped-left
      app
      dark
      :elevation="showScrollToTopFab?4:0"
      :color="showScrollToTopFab?'primary':'transparent'"
    >
      <!-- 开关目录按钮 TODO目录可以调节宽度-->
      <v-tooltip
        v-if="catalogue.length>0"
        :disabled="$vuetify.breakpoint.smAndDown&&drawer"
        bottom
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
        <span>{{ drawer ? $t('closeCatalogue') : $t('catalogue') }}</span>
      </v-tooltip>

      <!-- 标题 -->
      <v-toolbar-title
        style="cursor: pointer;"
        :style="{color:showScrollToTopFab||!drawer||$vuetify.theme.dark?'white':'black'}"
        @click="onClickToolbarTitle"
      >
        {{ showScrollToTopFab ? blogArticle.title : $t('appName') }}
      </v-toolbar-title>

      <v-spacer />

      <!-- 编辑 -->
      <v-tooltip bottom>
        <template #activator="{on,attrs}">
          <v-btn
            v-if="!readOnly&&userInfo&&blogArticle.user.uuid===userInfo.user.uuid"
            v-bind="attrs"
            icon
            :to="`/dashboard/article/writing/${blogArticle.uuid}`"
            v-on="on"
          >
            <v-icon>mdi-pencil</v-icon>
          </v-btn>
        </template>
        {{ $t('article.actions.edit') }}
      </v-tooltip>

      <!-- 搜索 -->
      <or-search
        disable-hot-key
        :focused.sync="searchInputFocused"
        :auto-expand="false"
        solo-inverted
        @onSubmit="onSearchSubmit"
      />

      <!-- 用户信息 -->
      <or-user-info-menu
        v-if="!$vuetify.breakpoint.smAndDown||!searchInputFocused"
        :transparent="!showScrollToTopFab"
        start-writing-color="transparent"
        login-color="white"
      />

      <!-- 在其他设备上阅读 -->
      <v-menu
        v-if="!$vuetify.breakpoint.smAndDown"
        origin="top center"
        transition="scale-transition"
      >
        <template #activator="{ on: menu, attrs }">
          <v-tooltip bottom>
            <template #activator="{ on: tooltip }">
              <v-btn
                id="readOnOtherDeviceMenuBtn"
                icon
                dark
                v-bind="attrs"
                v-on="{ ...tooltip, ...menu }"
              >
                <v-icon>
                  mdi-cellphone-link
                </v-icon>
              </v-btn>
            </template>
            <span>{{ $t('continueReadingOnOtherDevices') }}</span>
          </v-tooltip>
        </template>
        <div style="width: 150px;height: 150px">
          <vue-qr
            :margin="10"
            :text="vueQrUrl"
            style="width: 100%;height: 100%"
          />
        </div>
      </v-menu>

      <!-- 设置 -->
      <v-tooltip bottom>
        <template #activator="{ on, attrs }">
          <v-btn
            icon
            v-bind="attrs"
            v-on="on"
            @click.stop="$store.dispatch('app/toggleRightDrawerModel')"
          >
            <v-icon>mdi-dots-horizontal</v-icon>
          </v-btn>
        </template>
        <span>{{ $t('more') }}</span>
      </v-tooltip>
    </v-app-bar>

    <!--目录-->
    <v-navigation-drawer
      v-if="catalogue.length>0"
      v-model="drawer"
      width="280"
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
          <div :id="`label_h_${item.id}`" :blog-toc-id="item.name" v-html="item.html" />
        </template>
      </v-treeview>
    </v-navigation-drawer>

    <!-- 设置 -->
    <or-settings-drawer show-i18n-setting>
      <template #other-list-item>
        <v-list-item v-if="$vuetify.breakpoint.smAndDown" @click="$refs.qrDialog.show()">
          <v-list-item-content>
            <v-list-item-title>{{ $t('continueReadingOnOtherDevices') }}</v-list-item-title>
          </v-list-item-content>
          <or-base-dialog
            ref="qrDialog"
            :title="$t('continueReadingOnOtherDevices')"
          >
            <div
              class="d-flex justify-center align-center"
            >
              <vue-qr
                :margin="10"
                :text="vueQrUrl"
                :size="150"
              />
            </div>
          </or-base-dialog>
        </v-list-item>
      </template>
    </or-settings-drawer>

    <!-- 封面 -->
    <v-img
      :src="$apis.blog.getFileUrl(blogArticle.coverImage,'https://tuapi.eees.cc/api.php?category={biying,fengjing}&type=302')"
      style="height: 400px; margin-top: -70px"
      gradient="rgba(0,0,0,.20),rgba(0,0,0,.20)"
    >
      <!-- 封面占位符 -->
      <template #placeholder>
        <div class="d-flex or-article-cover-img">
          <v-skeleton-loader type="image" class="flex-grow-1" tile />
        </div>
      </template>

      <!-- 文章标题 -->
      <v-card-title
        class="display-1 text-center justify-center white--text font-weight-bold"
        style="position: absolute; left: 0; right: 0; bottom: 60px; top: 0"
      >
        {{ blogArticle.title }}
      </v-card-title>

      <v-list-item
        dark
        class="d-flex flex-wrap align-center justify-center"
        style="position: absolute; left: 0; right: 0; bottom: 100px"
      >
        <!-- 作者信息 -->
        <div class="d-flex align-center mx-4">
          <!-- 头像 -->
          <or-blog-user-avatar
            :user="blogArticle.user"
            avatar-class="v-list-item__avatar"
          />

          <v-list-item-content>
            <!-- 用户名 -->
            <v-hover>
              <template #default="{ hover }">
                <v-list-item-title
                  class="font-weight-medium transition-swing"
                  :class="hover?'primary--text':null"
                >
                  <span style="cursor: pointer" @click="onClickUsername">{{ blogArticle.user.username }}</span>
                </v-list-item-title>
              </template>
            </v-hover>
            <!-- todo 个性签名 -->
            <v-list-item-subtitle v-if="false">
              世间种种平凡都不平凡
            </v-list-item-subtitle>
          </v-list-item-content>
        </div>

        <!-- 时间信息 -->
        <div class="text-caption">
          <!-- 创建时间 -->
          <div>
            <strong>{{ $t('createAt', [$t('punctuation.colonWithSuffixSpace')]) }}</strong>{{
              blogArticle.createdTime
            }}
          </div>
          <!-- 更新时间 -->
          <div v-if="blogArticle.updateTime">
            <strong>{{ $t('updateAt', [$t('punctuation.colonWithSuffixSpace')]) }}</strong>{{ blogArticle.updateTime }}
          </div>
        </div>
      </v-list-item>

      <v-list-item
        dark
        dense
        class="d-flex flex-wrap align-center justify-center"
        style="position: absolute; left: 0; right: 0; bottom: 60px"
      >
        <!-- IP归属地 -->
        <div v-if="blogArticle.ip" class="d-flex align-center me-2">
          <v-icon left small>
            mdi-map-marker
          </v-icon>
          <span>{{
            blogArticle.ip.country === '中国' ? blogArticle.ip.province : blogArticle.ip.country === '0' ? '未知' : blogArticle.ip.country
          }}</span>
        </div>
        <!-- 点赞量 -->
        <div class="d-flex align-center me-2">
          <v-icon left small>
            mdi-thumb-up
          </v-icon>
          <span>{{ blogArticle.likesCount }}</span>
        </div>
        <!-- 浏览量uv -->
        <div class="d-flex align-center me-2">
          <v-icon left small>
            mdi-account-eye
          </v-icon>
          <span>{{ blogArticle.uv }}</span>
        </div>
        <!-- 浏览量pv -->
        <div class="d-flex align-center me-2">
          <v-icon left small>
            mdi-eye
          </v-icon>
          <span>{{ blogArticle.pv }}</span>
        </div>
        <!-- 评论数 -->
        <v-hover>
          <template #default="{ hover }">
            <div
              class="d-flex align-center"
              style="cursor: pointer"
              @click="$vuetify.goTo($refs.commentsContainer)"
            >
              <v-icon left small :color="hover?'primary':null">
                mdi-comment-text
              </v-icon>
              <span
                class="transition-swing"
                :class="hover?'primary--text':null"
              >{{ blogArticle.commentsCount }}</span>
            </div>
          </template>
        </v-hover>
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
            class="my-2"
            :dark="$vuetify.theme.dark"
            :pre-set-content="blogArticle.summary"
          />
        </div>

        <div class="d-flex flex-wrap align-center mx-2">
          <!-- 分类 -->
          <div
            v-if="blogArticle.type && blogArticle.type !== ''"
            class="d-inline-flex align-center me-2"
          >
            <v-icon class="pa-2" small>
              mdi-view-list
            </v-icon>
            <v-chip
              small
              label
              @click="onClickType(blogArticle.type)"
            >
              {{ blogArticle.type.name }}
            </v-chip>
          </div>

          <!-- 标签 -->
          <div
            v-if="blogArticle.tags && blogArticle.tags.length && blogArticle.tags.length > 0"
            class="d-inline-flex align-center"
          >
            <v-icon class="pa-2" small>
              mdi-tag-multiple
            </v-icon>
            <div>
              <v-chip
                v-for="(tag,index) in blogArticle.tags"
                :key="tag.uuid"
                small
                :class="index!==blogArticle.tags.length-1?'me-2':null"
                @click="onClickTag(tag)"
                @keypress.enter="onClickTag(tag)"
              >
                {{ tag.name }}
              </v-chip>
            </div>
          </div>
        </div>

        <!-- 阅读时长创建时间修改时间 -->
        <div>
          <v-alert
            text
            dense
            class="text-break mx-5 my-2"
            border="left"
            colored-border
            dismissible
            type="info"
          >
            <strong class="font-weight-bold">{{ $t('article.detail.readingTimeHint.prefix') }}</strong>{{
              $t('article.detail.readingTimeHint.hint')
            }}<span class="accent--text">{{ watchTimeString }}</span>
          </v-alert>
        </div>

        <!-- 内容 -->
        <div v-if="!articleVditorFinished" class="ma-5">
          <div v-show="false">
            {{ blogArticle.content }}
          </div>
          <v-skeleton-loader
            v-for="i in skeletonLoaderCount"
            :key="i"
            :type="i%3===1?'heading':'paragraph'"
            :class="i%3===1?'my-3':null"
          />
        </div>
        <v-fade-transition class="transition-fast-in-fast-out">
          <or-md-vditor
            v-show="articleVditorFinished"
            id="content"
            :headings-offset-top="appBarHeight"
            :preview-toc.sync="toc"
            :current-toc-index.sync="currentTocIndex"
            :dark="$vuetify.theme.dark"
            :pre-set-content="blogArticle.content"
            @after="onVditorAfter"
          />
        </v-fade-transition>

        <div id="likeFab" />

        <!--转载声明-->
        <v-alert border="left" text class="text-break mx-5 mt-2" colored-border color="#e00000">
          <div v-if="blogArticle.original">
            <strong>{{ $t('article.detail.copyrightStatement.author') }}</strong>{{ blogArticle.user.username }}<br>
          </div>
          <span>
            <strong>{{ $t('article.detail.copyrightStatement.link') }}</strong><or-link
              :href="currentUrl"
              target="_self"
            >{{ currentUrl }}</or-link>
          </span>
          <br>
          <div v-if="blogArticle.original">
            <strong>{{
              $t('article.detail.copyrightStatement.title')
            }}</strong>{{ $t('article.detail.copyrightStatement.statementPrefix', [blogArticle.user.username]) }}
            <or-link href="https://creativecommons.org/licenses/by-sa/4.0/">
              CC BY-SA 4.0
            </or-link>
            {{ $t('article.detail.copyrightStatement.statementSuffix') }}
          </div>
        </v-alert>

        <div class="d-flex justify-space-around align-center mb-4">
          <!-- 点赞 -->
          <v-badge
            :value="blogArticle.likesCount&&(readOnly||hoverLikeButton)"
            color="accent"
            offset-x="18"
            offset-y="18"
            overlap
            bordered
            :content="blogArticle.likesCount"
            transition="slide-y-reverse-transition"
          >
            <v-hover v-model="hoverLikeButton">
              <v-btn
                :disabled="readOnly"
                fab
                :color="likeOptions.liked?'primary':null"
                :loading="likeOptions.loading"
                @click="onClickLike"
              >
                <v-icon>mdi-thumb-up</v-icon>
              </v-btn>
            </v-hover>
          </v-badge>

          <!-- 赞赏 -->
          <div v-if="false&&blogArticle.canReward">
            <v-tooltip bottom>
              <template #activator="{ on }">
                <div v-on="on">
                  <v-menu origin="center" nudge-left="45" nudge-top="75" transition="scale-transition">
                    <template #activator="{ on }">
                      <v-btn fab v-on="on">
                        <v-icon>mdi-currency-cny</v-icon>
                      </v-btn>
                    </template>
                    <v-img style="width: 150px" :src="require('~/static/vuetify-logo.svg')" />
                  </v-menu>
                </div>
              </template>
              <span>赞赏</span>
            </v-tooltip>
          </div>
        </div>

        <!-- 文章切换，上/下一篇 -->
        <v-divider />
        <or-blog-article-pre-and-next-bar :pre-and-next-article="preAndNextArticle" />

        <!-- 评论 -->
        <v-divider />
        <v-card id="comments">
          <v-card-title>
            {{ $t('commentCount', [`${articleComments.total ? $t('parentheses', [articleComments.total]) : ''}`]) }}
          </v-card-title>
          <div ref="commentsContainer" />
          <v-container
            v-if="!readOnly"
            class="mb-2"
          >
            <div v-if="blogArticle.canComment">
              <!-- 撰写评论 -->
              <v-row
                v-if="$access.isLogged()"
                no-gutters
              >
                <or-blog-user-avatar
                  avatar-class="mb-auto"
                  disable-menu
                  :user="userInfo.user"
                />
                <div class="flex-grow-1">
                  <or-md-vditor
                    ref="commentVditor"
                    :transfer-content.sync="commentOptions.content"
                    :placeholder="$t('comment.actions.placeholder')"
                    class="mx-2"
                    :dark="$vuetify.theme.dark"
                    comment-mode
                    :read-only="false"
                  />
                </div>
                <v-btn
                  color="primary"
                  class="mt-auto"
                  :disabled="!commentContentValid"
                  :loading="commentOptions.posting"
                  @click="postComment"
                >
                  {{ $t('comment.actions.send') }}
                </v-btn>
              </v-row>
              <!-- 登录提示 -->
              <v-sheet
                v-else
                rounded
                height="60"
                class="d-flex justify-center align-center mx-2"
                outlined
              >
                <v-btn
                  small
                  color="primary"
                  @click="$router.push({path:'/user/login',query:{redirect:$route.path}})"
                >
                  {{ $t('login') }}
                </v-btn>
              </v-sheet>
            </div>
            <!-- 关闭评论提示 -->
            <v-sheet
              v-else
              rounded
              class="d-flex justify-center align-center mx-2 py-2"
              outlined
            >
              <span class="pa-2">{{ $t('article.cannotCommentHint') }}</span>
            </v-sheet>

            <!-- 回复提示 -->
            <v-row
              class="d-block mt-6"
              no-gutters
            >
              <v-alert
                v-model="commentOptions.showAlert"
                transition="slide-y-reverse-transition"
                dense
                dismissible
                color="secondary"
                colored-border
                border="left"
                elevation="2"
              >
                {{ $t('comment.actions.reply') }}{{
                  commentOptions.parentComment ? `${commentOptions.parentComment.user.username}：${commentOptions.parentComment.content}` : ''
                }}
              </v-alert>
            </v-row>
          </v-container>
          <or-blog-comment-list
            ref="commentList"
            :article-id="blogArticle.uuid"
            :article-comments="articleComments"
            @clickReply="onClickReply"
          />

          <!-- 审核按钮 -->
          <v-card-actions v-if="auditMode||offendMode">
            <v-spacer />
            <v-btn
              v-if="auditMode&&$access.hasAuditorRole()"
              text
              color="warning"
              @click="$refs.auditFailedDialog.show()"
            >
              {{ $t('article.actions.auditFailed') }}
            </v-btn>
            <v-btn
              v-if="auditMode&&$access.hasAuditorRole()"
              color="success"
              @click="onClickAuditApproved"
            >
              {{ $t('article.actions.auditApproved') }}
            </v-btn>
            <v-btn
              v-if="offendMode"
              color="primary"
              @click="$refs.articleAppealDialog.show()"
            >
              {{ $t('article.actions.articleAppeal') }}
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-card>
    </v-card>

    <!-- Footer -->
    <or-footer />

    <!-- 恢复阅读位置日夜间模式 -->
    <v-snackbar v-model="snackbar">
      {{ $t('article.restoreReadingStatusHint') }}
      <v-btn color="pink" text @click="$vuetify.goTo(0)">
        {{ $t('article.top') }}
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
    <or-blog-article-input-reason-dialog
      ref="auditFailedDialog"
      title="打回"
      @onConfirm="onClickAuditFailed"
    />
    <or-blog-article-input-reason-dialog
      ref="articleAppealDialog"
      title="申诉"
      @onConfirm="onClickArticleAppeal"
    />
  </v-container>
</template>

<script>
import VueQr from 'vue-qr'
import { mapGetters } from 'vuex'

export default {
  name: 'OrBlogArticleDetail',
  components: { VueQr },
  props: {
    article: {
      type: Object,
      required: true
    },
    preAndNextArticle: {
      type: Object,
      default: () => ({})
    },
    presetArticleComments: {
      type: Object,
      default: () => ({
        records: []
      })
    }
  },
  data: () => ({
    appBarHidden: false,
    articleVditorFinished: false,
    hoverLikeButton: false,
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
    searchInputFocused: false,

    likeOptions: {
      loading: false,

      liked: null
    },

    commentOptions: {
      posting: false,

      content: '',
      parentId: '',
      showAlert: false
    },

    articleComments: {}
  }),
  computed: {
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    }),
    skeletonLoaderCount () {
      const totalSeconds = this.article.content.length / 8
      return Math.max(Math.round(Math.random() * (totalSeconds / 15) + totalSeconds / 30) * (this.$vuetify.breakpoint.smAndDown ? 2 : 1), 1)
    },
    auditMode () {
      return this.article.status === 'UNDER_REVIEW'
    },
    offendMode () {
      return this.article.status === 'OFFEND'
    },
    readOnly () {
      return this.auditMode || this.offendMode
    },
    commentContentValid () {
      return this.commentOptions.content && this.commentOptions.content !== '' && this.commentOptions.content !== '\n'
    },
    vueQrUrl () {
      return this.currentUrl + '?p=' + this.realPercentOfRead + '&t=' + this.$store.getters['app/getSelectedThemeOption']
    },
    currentUrl () {
      return process.client ? window.location.origin + window.location.pathname : ''
    },
    watchTimeString () {
      // 估算阅读时长
      const totalSeconds = this.article.content.length / 8
      return this.$or.util.formatSeconds(totalSeconds, {
        aMoment: this.$t('time.aMoment'),
        second: this.$t('time.seconds'),
        minute: this.$t('time.minutes'),
        hour: this.$t('time.hours'),
        day: this.$t('time.days')
      })
    },
    appBarHeight () {
      return this.appBarHidden ? 0 : (this.$vuetify.breakpoint.smAndDown ? 56 : 64)
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
      this.drawer = !this.$vuetify.breakpoint.mdAndDown

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
        currentItem.html = currentItem.headHtml
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
          const headDivElement = document.getElementById('vditorAnchor-' + headId)
          // console.log(headDivElement)
          if (headDivElement) {
            this.$vuetify.goTo(headDivElement)
            this.$router.replace(this.$route.path + '#' + headId)
          }
        }
      })
    },
    currentTocIndex (val) {
      // console.log('currentTocIndex', val)
      this.active = []
      this.active.push(val)

      /* hash定位不是在屏幕最顶部，有冲突
      const element = document.getElementById(`label_h_${val}`)
      console.log(element)
      if (element) {
        this.$router.replace(this.$route.path + '#' + element.getAttribute('blog-toc-id'))
      } */
    }
  },
  created () {
    this.blogArticle = this.article
    this.articleComments = this.presetArticleComments
    if (this.$access.isLogged()) {
      this.updateLiked()
    }
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
    onSearchSubmit (searchInput) {
      window.open(`/search/${searchInput}`, '_blank')
    },
    onClickArticleAppeal (reason) {
      if (this.$refs.articleAppealDialog.validate()) {
        this.$apis.blog.article.articleAppeal(this.blogArticle.uuid, reason)
          .then((data) => {
            this.$snackbar.success(this.$t('operationSucceeded'))
            this.$refs.articleAppealDialog.close()
            this.$router.replace('/dashboard/article/status/ON_APPEAL')
          })
          .catch(() => {
            this.$refs.articleAppealDialog.cancelLoading()
          })
      } else {
        this.$refs.articleAppealDialog.cancelLoading()
      }
    },
    onClickAuditApproved () {
      this.$dialog({
        persistent: false,
        content: '确定通过？',
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.auditApproved(this.blogArticle.uuid)
            .then((data) => {
              this.$indexnow.updateArticle(this.blogArticle)
              this.$snackbar.success(this.$t('operationSucceeded'))
              dialog.cancel()
              this.$router.replace('/dashboard/article/status/UNDER_REVIEW')
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onClickAuditFailed (reason) {
      if (this.$refs.auditFailedDialog.validate()) {
        this.$apis.blog.article.auditFailed(this.blogArticle.uuid, reason)
          .then((data) => {
            this.$snackbar.success(this.$t('operationSucceeded'))
            this.$refs.auditFailedDialog.close()
            this.$router.replace('/dashboard/article/status/UNDER_REVIEW')
          })
          .catch(() => {
            this.$refs.auditFailedDialog.cancelLoading()
          })
      } else {
        this.$refs.auditFailedDialog.cancelLoading()
      }
    },
    updateLiked () {
      this.$apis.blog.article.getLiked(this.blogArticle.uuid)
        .then((data) => {
          this.likeOptions.liked = data
        })
    },
    onClickLike () {
      if (!this.$access.checkLogin()) {
        return
      }
      if (this.likeOptions.liked === null) {
        // 未获取到是否点赞
        return
      }

      let promise
      if (this.likeOptions.liked) {
        promise = this.$apis.blog.article.unlikesArticle(this.blogArticle.firstId)
      } else {
        promise = this.$apis.blog.article.likesArticle(this.blogArticle.firstId)
      }
      promise.then(() => {
        this.likeOptions.loading = false
        this.likeOptions.liked = !this.likeOptions.liked
        if (this.likeOptions.liked) {
          this.blogArticle.likesCount++
        } else {
          this.blogArticle.likesCount--
        }
        this.$snackbar.success(this.$t('whatSuccessfully', [this.likeOptions.liked ? this.$t('article.actions.like') : this.$t('article.actions.unlike')]))
      })
        .catch(() => {
          this.likeOptions.loading = false
        })
    },
    onClickUsername () {
      window.open(`/${this.blogArticle.user.uid}`, '_blank')
    },
    onClickToolbarTitle () {
      if (this.showScrollToTopFab) {
        this.$vuetify.goTo(0)
      } else {
        this.$router.push('/')
      }
    },
    onClickType (type) {
      window.open(`/${this.blogArticle.user.uid}/type/${type.uuid}`, '_blank')
    },
    onClickTag (tag) {
      window.open(`/search/${tag.name}`, '_blank')
    },
    onClickReply ({
      originalComment,
      parentComment
    }) {
      if (this.readOnly) {
        return
      }

      this.commentOptions.parentComment = parentComment
      this.commentOptions.parentId = parentComment.uuid
      this.commentOptions.showAlert = true
      this.$vuetify.goTo(this.$refs.commentsContainer)
      this.$refs.commentVditor?.focus()
    },
    postComment () {
      if (!this.$access.checkLogin()) {
        return
      }
      if (this.commentContentValid) {
        if (!this.commentOptions.showAlert) {
          this.commentOptions.parentId = ''
        }
        this.commentOptions.posting = true
        this.$apis.blog.comment.post({
          ...this.commentOptions,
          articleId: this.blogArticle.firstId
        })
          .then((data) => {
            this.commentOptions.content = ''
            this.commentOptions.parentId = ''
            this.commentOptions.showAlert = false
            this.$refs.commentVditor.setValue('')
            this.$refs.commentList.addComment(data)
            this.blogArticle.commentsCount++
            this.articleComments.total++
            this.commentOptions.posting = false
          })
          .catch(() => {
            this.commentOptions.posting = false
          })
      }
    },
    onVditorAfter () {
      this.articleVditorFinished = true
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
      const cardOffsetTop = document.getElementById('card').offsetTop - this.appBarHeight
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
      this.realPercentOfRead = (scrollTop) / (totalHeight - window.innerHeight - (this.appBarHeight))

      // App Bar是否隐藏
      this.appBarHidden = this.$refs.appBar?.$el.style.transform.includes('-') ?? false

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
          if (tN >= 0 && tN < 5) {
            this.$store.dispatch('app/setSelectedThemeOption', {
              value: tN,
              $vuetify: this.$vuetify
            })
            this.snackbar = true
          }
        }

        // 恢复阅读位置
        const hash = this.$route.hash
        if (!(hash && hash.length > 1)) {
          const realPercentOfRead = this.$route.query.p
          if (realPercentOfRead !== undefined) {
            const pN = parseFloat(realPercentOfRead)
            if (pN >= 0 && pN <= 100) {
              const totalHeight = document.body.scrollHeight
              // console.log(pN, totalHeight, window.innerHeight, totalHeight - window.innerHeight)
              // 获取准确位置
              this.$vuetify.goTo(pN * (totalHeight - window.innerHeight + this.appBarHeight))
              if (!this.snackbar) {
                this.snackbar = true
              }
            }
          }
        } else {
          const hashValue = decodeURIComponent(hash).slice(1)
          const elementById = document.getElementById(hashValue)
          // 非正文的标题
          if (elementById) {
            this.$vuetify.goTo(elementById)
          } else {
            // vditor生成的锚点
            const elementById = document.getElementById('vditorAnchor-' + hashValue)
            // console.log(elementById)
            if (elementById) {
              this.$vuetify.goTo(elementById)
            } else {
              // this.toc.forEach((item) => {
              //   if (item.headString === hashValue) {
              //     const headDivElement = document.getElementById('h-' + item.id)
              //     if (headDivElement) {
              //       this.$vuetify.goTo(headDivElement)
              //     }
              //   }
              // })
            }
          }
        }
      }, 1000)
    }
  }
}
</script>

<style>
.or-article-cover-img{
  height: 100%;
}

.or-article-cover-img .v-skeleton-loader__image{
  height: 100% !important;
}
</style>
