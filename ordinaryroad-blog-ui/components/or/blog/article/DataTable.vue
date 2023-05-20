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
  <v-container fluid>
    <or-base-data-table
      ref="dataTable"
      :sort-by="['createdTime']"
      :sort-desc="[true]"
      :single-select="singleSelect"
      :select-return-object="selectReturnObject"
      :show-select="showSelect"
      :show-base-headers-when-selecting="showBaseHeadersWhenSelecting"
      :show-actions-when-selecting="showActionsWhenSelecting"
      :preset-selected-items="presetSelectedItems"
      :table-headers="headers"
      access-key="blog:article"
      @getItems="onGetItems"
      @insertItem="onInsertItem"
      @editItem="onEditItem"
      @itemsSelected="onItemsSelected"
      @resetSearch="onResetSearch"
    >
      <template #searchFormBefore>
        <v-row align="center">
          <v-col
            cols="6"
            lg="3"
            md="4"
          >
            <v-select
              v-if="!presetStatus"
              v-model="searchParams.status"
              :items="articleStatusOptions.items"
              dense
              outlined
              item-text="label"
              item-value="value"
              hide-details="auto"
              :label="$t('article.status')"
              @change="onSearchParamsStatusChange"
            >
              <template #item="{ item }">
                <div>
                  <v-list-item-title>{{ item.label }}</v-list-item-title>
                  <v-list-item-subtitle v-if="item.remark">
                    {{ item.remark }}
                  </v-list-item-subtitle>
                </div>
              </template>
            </v-select>
          </v-col>
        </v-row>
      </template>

      <template #searchFormBody>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.title"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="200"
            :label="$t('article.title')"
          />
        </v-col>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.summary"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="200"
            :label="$t('article.summary')"
          />
        </v-col>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.content"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('article.content')"
          />
        </v-col>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-select
            v-model="searchParams.canComment"
            clearable
            :items="[{label:$t('yes'),value:'true'},{label:$t('no'),value:'false'}]"
            dense
            outlined
            item-text="label"
            item-value="value"
            hide-details="auto"
            :label="$t('article.canComment')"
          />
        </v-col>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-select
            v-model="searchParams.canReward"
            clearable
            :items="[{label:$t('yes'),value:'true'},{label:$t('no'),value:'false'}]"
            dense
            outlined
            item-text="label"
            item-value="value"
            hide-details="auto"
            :label="$t('article.canReward')"
          />
        </v-col>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-select
            v-model="searchParams.original"
            clearable
            :items="[{label:$t('yes'),value:'true'},{label:$t('no'),value:'false'}]"
            dense
            outlined
            item-text="label"
            item-value="value"
            hide-details="auto"
            :label="$t('article.original')"
          />
        </v-col>
        <v-col
          v-if="['PENDING', 'UNDER_REVIEW', 'ON_APPEAL', 'PUBLISH'].includes(searchParams.status)&&$access.hasAuditorRole()"
          cols="6"
          lg="3"
          md="4"
        >
          <v-select
            v-model="searchParams.own"
            clearable
            :items="[{label:$t('yes'),value:'true'},{label:$t('no'),value:'false'}]"
            dense
            outlined
            item-text="label"
            item-value="value"
            hide-details="auto"
            :label="$t('article.onlyViewOwn')"
          />
        </v-col>
      </template>

      <template #actionsTop>
        <slot name="actionsTop">
          <v-btn
            outlined
            color="primary"
            dark
            @click="$refs.dataTable.insertItem()"
          >
            <v-icon left>
              mdi-pencil
            </v-icon>
            {{ $t('article.actions.writing') }}
          </v-btn>
        </slot>
      </template>

      <template #[`item.title`]="{ item }">
        <or-link
          :hover-able="['PUBLISH','UNDER_REVIEW','OFFEND'].includes(item.status)"
          :text="!['PUBLISH','UNDER_REVIEW','OFFEND'].includes(item.status)"
          :href="linkOnTitle(item)"
        >
          {{ item.title }}
        </or-link>
      </template>
      <template #[`item.status`]="{ item }">
        <v-chip
          label
          :color="statusColor(item)"
        >
          {{ item.status }}
        </v-chip>
      </template>
      <template #[`item.coverImage`]="{ item }">
        <a
          v-if="item.coverImage"
          :href="$apis.blog.getFileUrl(item.coverImage)"
          target="_blank"
        >
          <v-img
            width="100"
            aspect-ratio="1"
            :src="$apis.blog.getFileUrl(item.coverImage)"
          />
        </a>
        <span v-else>无</span>
      </template>
      <template #[`item.summary`]="{ item }">
        <span
          v-if="item.summary&&item.summary!==''&&item.summary!=='\n'"
        >
          {{ item.summary }}
        </span>
        <span v-else>无</span>
      </template>
      <template #[`item.typeName`]="{ item }">
        <v-chip v-if="item.typeName" label>
          {{ item.typeName }}
        </v-chip>
        <span v-else>无</span>
      </template>
      <template #[`item.tagNames`]="{ item }">
        <template v-if="item.tagNames && item.tagNames.length">
          <v-chip
            v-for="(tagName, index) in item.tagNames"
            :key="tagName"
            :class="index===item.tagNames.length-1?null:'me-1'"
          >
            {{ tagName }}
          </v-chip>
        </template>
        <span v-else>无</span>
      </template>
      <template #[`item.canComment`]="{ item }">
        <v-simple-checkbox v-model="item.canComment" disabled />
      </template>
      <template #[`item.canReward`]="{ item }">
        <v-simple-checkbox v-model="item.canReward" disabled />
      </template>
      <template #[`item.original`]="{ item }">
        <v-simple-checkbox v-model="item.original" disabled />
      </template>

      <!-- 替换默认的操作按钮 -->
      <template #actions="{ item }">
        <v-btn
          v-if="item.creatorId===userInfo.user.uuid&&['DRAFT', 'PUBLISH'].includes(item.status)"
          icon
          color="accent"
          class="mr-2"
          @click="$refs.dataTable.editItem(item)"
        >
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
        <v-btn
          v-if="['DRAFT', 'PUBLISH'].includes(item.status)&&item.creatorId===userInfo.user.uuid"
          icon
          color="error"
          @click="onDeleteItem(item)"
        >
          <v-icon>mdi-delete</v-icon>
        </v-btn>
        <v-btn
          v-if="['TRASH'].includes(item.status)"
          icon
          color="warning"
          @click="onRestoreItem(item)"
        >
          <v-icon>mdi-restore</v-icon>
        </v-btn>
        <v-btn
          v-if="['PENDING','ON_APPEAL'].includes(item.status)&&$access.hasAuditorRole()"
          text
          @click="onStartAuditing(item)"
        >
          开始审核
        </v-btn>
        <v-btn
          v-if="['UNDER_REVIEW'].includes(item.status)&&$access.hasAuditorRole()"
          text
          color="success"
          @click="onAuditApproved(item)"
        >
          {{ $t('article.actions.auditApproved') }}
        </v-btn>
        <v-btn
          v-if="['UNDER_REVIEW'].includes(item.status)&&$access.hasAuditorRole()"
          text
          color="warning"
          @click="onAuditFailed(item)"
        >
          {{ $t('article.actions.auditFailed') }}
        </v-btn>
        <v-btn
          v-if="['PUBLISH'].includes(item.status)&&$access.hasAuditorRole()"
          text
          color="error"
          @click="onArticleViolation(item)"
        >
          {{ $t('article.actions.articleViolation') }}
        </v-btn>
        <v-btn
          v-if="['OFFEND'].includes(item.status)"
          text
          color="primary"
          @click="onArticleAppeal(item)"
        >
          {{ $t('article.actions.articleAppeal') }}
        </v-btn>

        <or-base-menu
          v-if="['DRAFT'].includes(item.status)"
          offset-y
          open-on-hover
        >
          <template #activator="{ on, attrs }">
            <v-btn
              class="ml-2"
              color="primary"
              dark
              v-bind="attrs"
              icon
              v-on="on"
            >
              <v-icon>mdi-chevron-down</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item
              v-if="['DRAFT'].includes(item.status)"
              @click="onPublishArticle(item)"
            >
              <v-list-item-content>
                <v-list-item-title>{{ $t('article.actions.publish') }}</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </or-base-menu>
      </template>
    </or-base-data-table>
    <or-blog-article-input-reason-dialog
      ref="auditFailedDialog"
      :title="$t('article.actions.auditFailed')"
      @onConfirm="onClickAuditFailed"
    />
    <or-blog-article-input-reason-dialog
      ref="articleViolationDialog"
      :title="$t('article.actions.articleViolation')"
      @onConfirm="onClickArticleViolation"
    />
    <or-blog-article-input-reason-dialog
      ref="articleAppealDialog"
      :title="$t('article.actions.articleAppeal')"
      @onConfirm="onClickArticleAppeal"
    />
  </v-container>
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'OrBlogArticleDataTable',
  props: {
    /**
     * 选中返回完整Object数组，默认只返回uuid数组
     */
    selectReturnObject: {
      type: Boolean,
      default: false
    },
    singleSelect: {
      type: Boolean,
      default: false
    },
    showSelect: {
      type: Boolean,
      default: false
    },
    showActionsWhenSelecting: {
      type: Boolean,
      default: false
    },
    showBaseHeadersWhenSelecting: {
      type: Boolean,
      default: false
    },
    presetSelectedItems: {
      type: Array,
      default: () => []
    },

    /**
     * 默认选中的文章状态，会判断是否在选项里
     */
    paramStatus: {
      type: String,
      default: 'DRAFT'
    },

    /**
     * 预设要查询文章的状态，传入时直接隐藏v-select
     */
    presetStatus: {
      type: Array,
      default: null
    },
    /**
     * 预设FirstId，传入前自行校验
     */
    presetFirstId: {
      type: String,
      default: null
    },
    /**
     * 是否显示状态列
     */
    showStatusColumn: {
      type: Boolean,
      default: false
    }
  },
  data: () => ({
    searchParams: {
      title: null,
      summary: null,
      content: null,
      canComment: null,
      canReward: null,
      original: null,
      own: null,
      status: 'DRAFT'
    },
    selectedIndex: -1,
    selectedItem: null,
    articleStatusOptions: {
      items: [
        {
          label: '草稿',
          value: 'DRAFT',
          remark: '手动保存的草稿'
        },
        {
          label: '待审核',
          value: 'PENDING',
          remark: '等待审核的文章'
        },
        {
          label: '审核中',
          value: 'UNDER_REVIEW',
          remark: '正在审核中的文章'
        },
        {
          label: '已发布',
          value: 'PUBLISH',
          remark: '已发布可供公开查看的文章'
        },
        {
          label: '违规',
          value: 'OFFEND',
          remark: '违规文章'
        },
        {
          label: '申诉中',
          value: 'ON_APPEAL',
          remark: '正在申诉中的文章'
        },
        {
          label: '已删除',
          value: 'TRASH',
          remark: null
        }
      ]
    }
  }),
  computed: {
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    }),
    // 放在这为了支持国际化，如果放在data下切换语言不会更新
    headers () {
      const statusHeader = {
        text: this.$t('article.status'),
        value: 'status',
        width: 100
      }
      const headers = [
        {
          text: this.$t('article.coverImage'),
          value: 'coverImage',
          width: 100
        },
        {
          text: this.$t('article.title'),
          value: 'title',
          width: 400
        },
        {
          text: this.$t('article.summary'),
          value: 'summary',
          width: 400
        },
        {
          text: this.$t('article.typeName'),
          value: 'typeName',
          sortable: false
        },
        {
          text: this.$t('article.tagNames'),
          value: 'tagNames',
          sortable: false,
          width: 300
        },
        {
          text: this.$t('article.canComment'),
          value: 'canComment',
          width: 120
        },
        {
          text: this.$t('article.canReward'),
          value: 'canReward',
          width: 120
        },
        {
          text: this.$t('article.original'),
          value: 'original',
          width: 120
        }
      ]
      if (this.showStatusColumn) {
        headers.unshift(statusHeader)
      }
      return headers
    },
    action () {
      return this.selectedIndex === -1 ? 'create' : 'update'
    },
    statusColor () {
      return (item) => {
        if (item.status === 'PUBLISH') {
          return 'success'
        }
        return ''
      }
    },
    linkOnTitle () {
      return (item) => {
        switch (item.status) {
          case 'PUBLISH':
            return `/${item.creatorUid}/article/${item.firstId}`
          case 'UNDER_REVIEW':
            return `/dashboard/article/auditing/${item.uuid}`
          case 'OFFEND':
            return `/dashboard/article/offend/${item.uuid}`
          default:
            return ''
        }
      }
    }
  },
  watch: {
    paramStatus: {
      handler (val) {
        if (val) {
          if (this.articleStatusOptions.items.map(value => value.value).includes(val)) {
            this.searchParams.status = val
            this.$refs.dataTable && this.$refs.dataTable.resetSearch()
          }
        }
      },
      deep: true,
      immediate: true
    }
  },
  created () {
  },
  mounted () {
  },
  methods: {
    /**
     * 当用户改变文章状态下拉框时调用，替换当前页面
     * @param status 新的文章状态
     */
    onSearchParamsStatusChange (status) {
      this.$router.replace(`/dashboard/article/status/${status}`)
    },
    onResetSearch () {
    },
    onInsertItem () {
      this.selectedIndex = -1
      this.$router.push('/dashboard/article/writing/new')
    },
    onDeleteItem (item) {
      this.$dialog({
        persistent: false,
        content: this.$t('areYouSureToDoWhat', [this.$t('article.actions.moveToTrash')]),
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.moveToTrash(item.uuid)
            .then(() => {
              this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('article.actions.moveToTrash')]))
              this.$refs.dataTable.getItems()
              dialog.cancel()
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onRestoreItem (item) {
      this.$dialog({
        persistent: false,
        content: this.$t('areYouSureToDoWhat', [this.$t('article.actions.recoverFromTrash')]),
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.recoverFromTrash(item.uuid)
            .then(() => {
              this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('article.actions.recoverFromTrash')]))
              this.$refs.dataTable.getItems()
              dialog.cancel()
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onPublishArticle (item) {
      this.$dialog({
        persistent: false,
        content: this.$t('areYouSureToDoWhat', [this.$t('article.actions.publish')]),
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.publish(item)
            .then(() => {
              this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('article.actions.publish')]))
              this.$refs.dataTable.getItems()
              dialog.cancel()
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onStartAuditing (item) {
      this.$dialog({
        persistent: false,
        content: this.$t('areYouSureToDoWhat', [this.$t('article.actions.startAuditing')]),
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.startAuditing(item.uuid)
            .then(() => {
              this.$refs.dataTable.getItems()
              dialog.cancel()
              this.$router.push(`/dashboard/article/auditing/${item.uuid}`)
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onAuditApproved (item) {
      this.$dialog({
        persistent: false,
        content: this.$t('areYouSureToDoWhat', [this.$t('article.actions.auditApproved')]),
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.auditApproved(item.uuid)
            .then(() => {
              this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('article.actions.auditApproved')]))
              this.$refs.dataTable.getItems()
              dialog.cancel()
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onAuditFailed (item) {
      this.selectedItem = Object.assign({}, item)
      this.$refs.auditFailedDialog.show()
    },
    onClickAuditFailed (reason) {
      if (this.$refs.auditFailedDialog.validate()) {
        this.$apis.blog.article.auditFailed(this.selectedItem.uuid, reason)
          .then((data) => {
            this.$snackbar.success(this.$t('operationSucceeded'))
            this.$refs.dataTable.getItems()
            this.$refs.auditFailedDialog.close()
          })
          .catch(() => {
            this.$refs.auditFailedDialog.cancelLoading()
          })
      } else {
        this.$refs.auditFailedDialog.cancelLoading()
      }
    },
    onArticleViolation (item) {
      this.selectedItem = Object.assign({}, item)
      this.$refs.articleViolationDialog.show()
    },
    onClickArticleViolation (reason) {
      if (this.$refs.articleViolationDialog.validate()) {
        this.$apis.blog.article.articleViolation(this.selectedItem.uuid, reason)
          .then((data) => {
            this.$snackbar.success(this.$t('operationSucceeded'))
            this.$refs.dataTable.getItems()
            this.$refs.articleViolationDialog.close()
          })
          .catch(() => {
            this.$refs.articleViolationDialog.cancelLoading()
          })
      } else {
        this.$refs.articleViolationDialog.cancelLoading()
      }
    },
    onArticleAppeal (item) {
      this.selectedItem = Object.assign({}, item)
      this.$refs.articleAppealDialog.show()
    },
    onClickArticleAppeal (reason) {
      if (this.$refs.articleAppealDialog.validate()) {
        this.$apis.blog.article.articleAppeal(this.selectedItem.uuid, reason)
          .then((data) => {
            this.$snackbar.success(this.$t('operationSucceeded'))
            this.$refs.dataTable.getItems()
            this.$refs.articleAppealDialog.close()
          })
          .catch(() => {
            this.$refs.articleAppealDialog.cancelLoading()
          })
      } else {
        this.$refs.articleAppealDialog.cancelLoading()
      }
    },
    onEditItem ({
      item,
      index
    }) {
      this.selectedIndex = index
      // 编辑
      this.$router.push(`/dashboard/article/writing/${item.uuid}`)
    },
    onGetItems ({
      options,
      offset,
      limit,
      sortBy,
      sortDesc
    }) {
      // 填充文章状态
      if (this.presetStatus) {
        this.searchParams.status = this.presetStatus
      }
      // 填充searchParams
      this.searchParams.firstId = this.presetFirstId

      this.$apis.blog.article.page(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
        .then((result) => {
          this.$refs.dataTable.loadSuccessfully(result.records, result.total)
        })
        .catch(() => {
          this.$refs.dataTable.loadFinish()
        })
    },
    onItemsSelected (items) {
      this.$emit('itemsSelected', items)
    },
    unSelectItem (item) {
      this.$refs.dataTable.select({
        item,
        value: false,
        emit: true
      })
    }
  }
}
</script>
