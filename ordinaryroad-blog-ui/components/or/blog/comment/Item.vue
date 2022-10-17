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
    <a :id="`comment-${blogComment.uuid}`" class="target-fix" />
    <v-hover v-model="focused">
      <v-row
        class="my-1"
        no-gutters
        style="flex-wrap: nowrap;"
      >
        <!-- 头像 -->
        <or-blog-user-avatar :user="blogComment.user" />
        <v-col
          class="flex-grow-1 flex-shrink-0 ms-2"
          style="min-width: 100px; max-width: 100%;"
        >
          <div style="min-height: 36px">
            <div
              class="d-flex align-center flex-wrap"
            >
              <!-- 用户名 -->
              <v-hover>
                <template #default="{ hover }">
                  <span
                    style="cursor: pointer;"
                    :class="hover?'primary--text':null"
                    class="text-subtitle-1 font-weight-bold me-2 transition-swing"
                    @click="onClickUsername(blogComment.user)"
                  >{{ blogComment.user.username }}</span>
                </template>
              </v-hover>

              <!-- 角色 -->
              <span v-if="blogComment.user.roles.length>0" class="me-2">
                <or-user-roles :roles="blogComment.user.roles" />
              </span>
            </div>

            <div
              style="min-height: 36px"
              class="d-flex align-center"
            >
              <div class="d-flex align-center mr-auto">
                <!-- IP归属地 -->
                <div v-if="blogComment.ip" class="me-2">
                  <span>{{
                    blogComment.ip.country === '中国' ? blogComment.ip.province : blogComment.ip.country === '0' ? '未知' : blogComment.ip.country
                  }}</span>
                </div>

                <!-- 时间 -->
                <span class="text-body-2 me-2"> {{
                  $dayjs(blogComment.createdTime, 'yyyy-MM-dd HH:mm:ss').fromNow()
                }}</span>
              </div>

              <!-- 更多操作 -->
              <v-fade-transition v-if="!$vuetify.breakpoint.smAndDown">
                <div
                  v-if="focused||$vuetify.breakpoint.smAndDown"
                  class="ml-auto"
                >
                  <span>
                    <v-btn
                      v-if="blogComment.parent"
                      small
                      :href="`#comment-${blogComment.parent.uuid}`"
                      text
                    >{{ $t('comment.actions.viewOriginal') }}</v-btn>
                    <!--TODO 点赞
            <v-btn icon>
              <v-icon>mdi-thumb-up</v-icon>
            </v-btn>

            <v-btn icon>
              <v-icon>mdi-thumb-down</v-icon>
            </v-btn>
            -->
                    <v-btn icon color="primary" @click="onClickReply(null,blogComment)">
                      <v-icon>mdi-reply</v-icon>
                    </v-btn>
                  </span>
                </div>
              </v-fade-transition>
            </div>
          </div>

          <!--评论内容-->
          <or-md-vditor
            :dark="$vuetify.theme.dark"
            :pre-set-content="commentContent"
            comment-mode
            class="mb-1"
          />

          <!-- 更多操作 -->
          <v-fade-transition v-if="$vuetify.breakpoint.smAndDown">
            <div class="d-flex justify-end">
              <span>
                <v-btn
                  v-if="blogComment.parent"
                  small
                  :href="`#comment-${blogComment.parent.uuid}`"
                  text
                >{{ $t('comment.actions.viewOriginal') }}</v-btn>
                <!--TODO 点赞
            <v-btn icon>
              <v-icon>mdi-thumb-up</v-icon>
            </v-btn>

            <v-btn icon>
              <v-icon>mdi-thumb-down</v-icon>
            </v-btn>
            -->
                <v-btn icon color="primary" @click="onClickReply(null,blogComment)">
                  <v-icon>mdi-reply</v-icon>
                </v-btn>
              </span>
            </div>
          </v-fade-transition>
        </v-col>
      </v-row>
    </v-hover>

    <!-- 回复 -->
    <v-expansion-panels v-if="blogComment.replies&&blogComment.replies.total>0" flat tile>
      <v-expansion-panel>
        <v-divider />
        <v-expansion-panel-header ripple>
          <div>{{ $t('comment.totalRepliesCount', [blogComment.replies.total]) }}</div>
        </v-expansion-panel-header>

        <v-expansion-panel-content>
          <v-container>
            <or-blog-comment-item
              v-for="(commentReply) in blogComment.replies.records"
              :key="commentReply.uuid"
              :item="commentReply"
              @clickReply="onClickReply(blogComment,commentReply)"
            />
            <v-row justify="center" no-gutters>
              <!-- 加载更多Footer -->
              <or-load-more-footer
                ref="loadMoreFooter"
                class="mt-4"
                :no-more-data="blogComment.replies.pages === 0 || blogComment.replies.current === blogComment.replies.pages"
                @loadMore="loadSubComments"
              />
            </v-row>
          </v-container>
        </v-expansion-panel-content>
      </v-expansion-panel>
    </v-expansion-panels>

    <v-fade-transition>
      <v-divider v-if="showDivider" />
    </v-fade-transition>
  </div>
</template>

<script>
export default {
  name: 'OrBlogCommentItem',
  props: {
    item: {
      type: Object,
      required: true
    },
    showDivider: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      focused: false,
      blogComment: {}
    }
  },
  computed: {
    commentContent () {
      let prefix = ''
      if (this.blogComment.parent && this.blogComment.parent.uuid !== this.blogComment.originalId) {
        prefix = `[@${this.blogComment.parent.user.username}](/${this.blogComment.parent.user.uuid}) `
      }
      return `${prefix}${this.blogComment.content}`
    }
  },
  created () {
    this.blogComment = this.item
  },
  methods: {
    onClickUsername (user) {
      window.open(`/${user.uuid}`, '_blank')
    },
    /**
     * 加载回复
     */
    loadSubComments () {
      this.$apis.blog.comment.pageSub(this.blogComment.uuid, this.blogComment.replies.current + 1)
        .then((data) => {
          const newRecords = this.blogComment.replies.records.concat(data.records)
          this.blogComment.replies = {
            ...data,
            records: newRecords
          }
          this.$refs.loadMoreFooter.finishLoad()
        })
        .catch(() => {
          this.$refs.loadMoreFooter.finishLoad()
        })
    },
    onClickReply (originalComment, parentComment) {
      this.$emit('clickReply', {
        originalComment,
        parentComment
      })
    }
  }
}
</script>

<style scoped>
.target-fix {
  position: relative;
  top: -68px;
  display: block;
  height: 0;
  overflow: hidden;
}
</style>
