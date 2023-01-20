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
      access-key="blog:type"
      @getItems="onGetItems"
      @insertItem="onInsertItem"
      @deleteItem="onDeleteItem"
      @editItem="onEditItem"
      @itemsSelected="onItemsSelected"
      @resetSearch="onResetSearch"
    >
      <template #searchFormBody>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.username"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('userDataTable.username')"
          />
        </v-col>
      </template>

      <template #[`item.username`]="{ item }">
        <div class="d-flex align-center">
          <!-- <or-blog-user-avatar :user="item" /> -->
          <or-avatar
            size="38"
            avatar-class="v-list-item__avatar"
            :username="item.username"
            :avatar="$apis.blog.getFileUrl(item.avatar)"
          />
          <or-link :href="`/${item.uid}`" hover-able>
            <span>{{ item.username }}</span>
          </or-link>
        </div>
      </template>

      <template #[`item.enabled`]="{ item }">
        <v-switch
          v-model="item.enabled"
          readonly
          :disabled="!$access.hasDeveloperOrAdminRole()"
          inset
          @click="toggleEnabled(item)"
        />
      </template>
    </or-base-data-table>

    <or-input-dialog
      ref="inputDialog"
      title="请输入封禁时间（秒）"
      :rules="[$rules.required,$rules.notBlank]"
      @onConfirm="disable"
    />
  </v-container>
</template>

<script>
export default {
  name: 'OrBlogUserDataTable',
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
    }
  },
  data: () => ({
    searchParams: {
      name: null
    },
    selectedIndex: -1,
    editedItem: {
      uuid: null,
      uid: null,
      username: null,
      avatar: null
    },
    selectedItem: {
      uuid: null,
      uid: null,
      username: null,
      avatar: null
    },
    defaultItem: {
      uuid: null,
      uid: null,
      username: null,
      avatar: null
    }
  }),
  computed: {
    // 放在这为了支持国际化，如果放在data下切换语言不会更新
    headers () {
      const headers = [
        {
          text: this.$t('userDataTable.uid'),
          value: 'uid',
          width: 100
        },
        {
          text: this.$t('userDataTable.username'),
          value: 'username',
          width: 300
        },
        {
          text: this.$t('userDataTable.enabled'),
          value: 'enabled',
          width: 100
        }
      ]
      return headers
    },
    action () {
      return this.selectedIndex === -1 ? 'create' : 'update'
    }
  },
  watch: {},
  created () {
  },
  mounted () {
  },
  methods: {
    disable (input) {
      const disableTime = Number(input)
      if (!this.$refs.inputDialog.validate() || !Number.isInteger(disableTime)) {
        this.$snackbar.info('请检查输入')
        this.$refs.inputDialog.cancelLoading()
      } else {
        this.$apis.blog.user.disable(this.selectedItem.uuid, disableTime)
          .then((data) => {
            this.$snackbar.success(`操作成功，剩余时间：${data}s`)
            this.$refs.inputDialog.close()
            this.$refs.dataTable.getItems()
          })
          .catch(() => {
            this.$refs.inputDialog.cancelLoading()
          })
      }
    },
    untieDisable (item) {
      this.$dialog({
        content: '确定解封该用户？',
        loading: true
      })
        .then((dialog) => {
          if (dialog.isConfirm) {
            this.$apis.blog.user.untieDisable(item.uuid)
              .then((data) => {
                this.$snackbar.success(this.$t('whatSuccessfully', ['解封']))
                dialog.cancel()
                this.$refs.dataTable.getItems()
              })
              .catch(() => {
                dialog.cancel()
              })
          }
        })
    },
    toggleEnabled (item) {
      this.selectedItem = Object.assign({}, item)
      item.enabled ? this.$refs.inputDialog.show() : this.untieDisable(item)
    },
    onItemUpdate (item) {
      this.editedItem = item
    },
    createOrUpdate () {
    },
    onResetSearch () {
    },
    onInsertItem () {
      // 创建用户
      this.selectedIndex = -1
      this.selectedItem = Object.assign({}, this.defaultItem)
    },
    onDeleteItem ({ item, index }) {
      // 删除用户
      this.selectedItem = Object.assign({}, item)
    },
    onEditItem ({ item, index }) {
      // 编辑用户
      this.selectedIndex = index
      this.selectedItem = Object.assign({}, item)
    },
    onGetItems ({
      options,
      offset,
      limit,
      sortBy,
      sortDesc
    }) {
      // 分页查询用户
      this.$apis.blog.user.page(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
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

<style scoped>

</style>
