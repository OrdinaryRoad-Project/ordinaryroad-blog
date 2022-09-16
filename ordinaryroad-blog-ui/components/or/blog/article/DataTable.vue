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
  <v-container>
    <or-base-data-table
      ref="dataTable"
      :sort-by="[]"
      :sort-desc="[]"
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
            v-model="searchParams.canReward"
            clearable
            :items="[{label:'是',value:'true'},{label:'否',value:'false'}]"
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
            :items="[{label:'是',value:'true'},{label:'否',value:'false'}]"
            dense
            outlined
            item-text="label"
            item-value="value"
            hide-details="auto"
            :label="$t('article.original')"
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
          :text="item.status!=='PUBLISH'"
          :href="`/${item.createUserId}/article/detail/${item.uuid}`"
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
      <template #[`item.canReward`]="{ item }">
        <v-simple-checkbox v-model="item.canReward" disabled />
      </template>
      <template #[`item.original`]="{ item }">
        <v-simple-checkbox v-model="item.original" disabled />
      </template>

      <!-- 替换默认的操作按钮 -->
      <template #actions="{ item }">
        <v-btn
          v-if="['DRAFT', 'PUBLISH'].includes(item.status)"
          icon
          color="accent"
          class="mr-2"
          @click="$refs.dataTable.editItem(item)"
        >
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
        <v-btn
          v-if="['DRAFT', 'PUBLISH'].includes(item.status)"
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
      </template>
    </or-base-data-table>
  </v-container>
</template>

<script>

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
      canReward: null,
      original: null,
      status: 'DRAFT'
    },
    selectedIndex: -1,
    articleStatusOptions: {
      items: [
        {
          label: '草稿',
          value: 'DRAFT',
          remark: '手动保存的草稿'
        },
        {
          label: '已发布',
          value: 'PUBLISH',
          remark: '已发布可供公开查看的文章'
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
      this.$router.replace(`/dashboard/article/box/${status}`)
    },
    onResetSearch () {
    },
    onInsertItem () {
      this.selectedIndex = -1
      this.$router.push('/dashboard/article')
    },
    onDeleteItem (item) {
      this.$dialog({
        content: '确定移动到垃圾箱？',
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.moveToTrash(item.uuid)
            .then(() => {
              this.$snackbar.success('操作成功')
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
        content: '确定恢复该文章？',
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.article.recoverFromTrash(item.uuid)
            .then(() => {
              this.$snackbar.success('已恢复至草稿箱')
              this.$refs.dataTable.getItems()
              dialog.cancel()
            })
            .catch(() => {
              dialog.cancel()
            })
        }
      })
    },
    onEditItem ({
      item,
      index
    }) {
      this.selectedIndex = index
      // TODO 编辑
      this.$router.push(`/dashboard/article/${item.uuid}`)
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

      this.$apis.blog.article.pageOwn(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
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
