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
  <v-sheet class="d-flex mx-5">
    <!-- 头像 -->
    <or-avatar
      class="mt-1"
      :avatar="$apis.blog.getFileUrl(blogComment.user.avatar)"
      :username="blogComment.user.username"
    />
    <a :id="`comment-${blogComment.uuid}`" class="target-fix" />
    <v-sheet class="flex-grow-1 ms-2 mt-1 bottom-toolbar-controller">
      <div class="d-flex">
        <div class="d-flex align-center">
          <!-- 用户名 -->
          <nuxt-link
            class="text-subtitle-1 font-weight-bold me-2"
            target="_blank"
            :to="`/${blogComment.user.uuid}`"
          >
            {{ blogComment.user.username }}
          </nuxt-link>

          <!-- 角色 -->
          <span v-if="blogComment.user.roles.length>0" class="me-2">
            <v-chip
              v-for="role in blogComment.user.roles"
              :key="role.roleCode"
              small
              :color="!(role.roleCode.indexOf('DEVELOPER') > 0 || role.roleCode.indexOf('ADMIN') > 0 || role.roleCode.indexOf('VIP') > 0) ? null : 'primary'"
              :outlined="!(role.roleCode.indexOf('DEVELOPER') > 0 || role.roleCode.indexOf('ADMIN') > 0 || role.roleCode.indexOf('VIP') > 0)"
              label
            >{{ role.roleName }}</v-chip>
          </span>

          <!-- 时间 -->
          <span class="text-body-2"> {{ blogComment.createdTime }}</span>
        </div>

        <!-- 更多操作 -->
        <div class="ml-auto bottom-toolbar-hover-hide-item">
          <span>
            <v-btn
              v-if="blogComment.parent"
              small
              :href="`#comment-${blogComment.parent.uuid}`"
              text
            >查看原评论</v-btn>
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
      </div>

      <!--评论内容-->
      <or-md-vditor
        :dark="$vuetify.theme.dark"
        :pre-set-content="commentContent"
        comment-mode
        class="mb-1"
      />

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
    </v-sheet>
  </v-sheet>
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
      blogComment: {}
    }
  },
  computed: {
    commentContent () {
      let prefix = ''
      if (this.blogComment.parent && this.blogComment.parent.uuid !== this.blogComment.originalId) {
        prefix = `回复[@${this.blogComment.parent.user.username}](/${this.blogComment.parent.user.uuid})`
      }
      return `${prefix} ${this.blogComment.content}`
    }
  },
  created () {
    this.blogComment = this.item
  },
  methods: {
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

.bottom-toolbar-hover-hide-item {
  transition: 0.3s;
  opacity: 0;
}

.bottom-toolbar-controller:hover .bottom-toolbar-hover-hide-item {
  opacity: 1;
}
</style>
