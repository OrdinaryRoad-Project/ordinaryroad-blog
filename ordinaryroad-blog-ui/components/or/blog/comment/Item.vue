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
    <v-hover v-model="focused">
      <v-sheet class="d-flex mx-5">
        <!-- 头像 -->
        <or-blog-user-avatar
          class="mt-1"
          :user="blogComment.user"
        />
        <a :id="`comment-${blogComment.uuid}`" class="target-fix" />
        <v-sheet class="flex-grow-1 ms-2 mt-1">
          <div class="d-flex">
            <div class="d-flex align-center" style="min-height: 36px">
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

              <!-- 时间 -->
              <span class="text-body-2"> {{ $dayjs(blogComment.createdTime, 'yyyy-MM-dd HH:mm:ss').fromNow() }}</span>
            </div>

            <!-- 更多操作 -->
            <v-fade-transition>
              <div v-if="focused" class="ml-auto">
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

          <!--评论内容-->
          <or-md-vditor
            :dark="$vuetify.theme.dark"
            :pre-set-content="commentContent"
            comment-mode
            class="mb-1"
          />
        </v-sheet>
      </v-sheet>
    </v-hover>

    <!-- 回复 -->
    <v-expansion-panels v-if="blogComment.replies&&blogComment.replies.total>0" flat tile>
      <v-expansion-panel>
        <v-divider />
        <v-expansion-panel-header ripple>
          <div>
            共{{ blogComment.replies.total }}条回复
          </div>
        </v-expansion-panel-header>

        <v-expansion-panel-content>
          <v-container>
            <v-row
              v-for="(commentReply) in blogComment.replies.records"
              :key="commentReply.uuid"
              class="d-block"
            >
              <or-blog-comment-item
                :item="commentReply"
                @clickReply="onClickReply(blogComment,commentReply)"
              />
            </v-row>
            <v-row justify="center" no-gutters class="mt-6">
              <!-- 加载更多Footer -->
              <or-load-more-footer
                ref="loadMoreFooter"
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
