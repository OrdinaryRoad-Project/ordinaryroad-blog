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
      access-key="blog:user"
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
            v-model="searchParams.roleName"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('roleDataTable.roleName')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.roleCode"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('roleDataTable.roleCode')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>
      </template>

      <template #[`item.roleName`]="{ item }">
        <or-user-role-chip :role="item" :small="false" />
      </template>

      <template #[`item.enabled`]="{ item }">
        <v-switch
          v-model="item.enabled"
          readonly
          :disabled="isBuiltInRole(item)||!$access.hasDeveloperOrAdminRole()"
          inset
          @click="toggleEnabled(item)"
        />
      </template>

      <template #actions="{ item }">
        <v-btn
          v-if="!isBuiltInRole(item)"
          icon
          color="accent"
          class="me-2"
          @click="$refs.dataTable.editItem(item)"
        >
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
        <v-btn
          icon
          color="accent"
          @click="$router.push({ name: 'dashboard-system-role-users-roleCode', params: { roleCode: item.roleCode, item } })"
        >
          <v-icon>mdi-account</v-icon>
        </v-btn>
      </template>
    </or-base-data-table>
    <or-base-dialog
      ref="roleDialog"
      loading
      :title="$t(action+'What',[$t('titles.form.role')])"
      @onConfirm="createOrUpdate"
    >
      <or-blog-role-save-form
        ref="roleForm"
        :preset="selectedItem"
        @update="onItemUpdate"
      />
    </or-base-dialog>
  </v-container>
</template>

<script>
export default {
  name: 'OrBlogRoleDataTable',
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
      roleName: null,
      roleCode: null
    },
    selectedIndex: -1,
    editedItem: {
      uuid: null,
      roleName: null,
      roleCode: null
    },
    selectedItem: {
      uuid: null,
      roleName: null,
      roleCode: null
    },
    defaultItem: {
      uuid: null,
      roleName: null,
      roleCode: null
    }
  }),
  computed: {
    // 放在这为了支持国际化，如果放在data下切换语言不会更新
    headers () {
      const headers = [
        {
          text: this.$t('roleDataTable.roleName'),
          value: 'roleName',
          width: 150
        },
        {
          text: this.$t('roleDataTable.roleCode'),
          value: 'roleCode',
          width: 300
        },
        {
          text: this.$t('roleDataTable.enabled'),
          value: 'enabled',
          width: 100
        }
      ]
      return headers
    },
    action () {
      return this.selectedIndex === -1 ? 'create' : 'update'
    },
    isBuiltInRole () {
      return (role) => {
        const builtInRoleCodes = ['DEVELOPER', 'ADMIN', 'AUDITOR', 'SSSSSSVIP']
        return builtInRoleCodes.includes(role.roleCode)
      }
    }
  },
  watch: {},
  created () {
  },
  mounted () {
  },
  methods: {
    toggleEnabled (item) {
      if (this.isBuiltInRole(item) || !this.$access.hasDeveloperOrAdminRole()) {
        return
      }
      this.selectedItem = Object.assign({}, item)
      const action = this.selectedIndex === -1 ? 'disable' : 'enable'
      this.$dialog({
        title: this.$t('areYouSureToDoWhat', [this.$t(action)]),
        loading: true,
        persistent: false
      })
        .then((dialog) => {
          if (dialog.isConfirm) {
            this.$apis.blog.role.updateEnabled(item.uuid, !item.enabled)
              .then(() => {
                this.$refs.dataTable.getItems()
                dialog.cancel()
              })
              .catch(() => {
                dialog.cancel()
              })
          }
        })
    },
    onItemUpdate (item) {
      this.editedItem = item
    },
    createOrUpdate () {
      if (this.$or.util.objectEquals(this.editedItem, this.selectedItem)) {
        this.$refs.roleDialog.close()
        // 有变动才进行创建或更新
      } else if (this.$refs.roleForm.validate()) {
        const action = this.selectedIndex === -1 ? 'create' : 'update'
        this.$apis.blog.role[action](this.editedItem)
          .then(() => {
            this.$refs.roleDialog.close()
            this.$refs.dataTable.getItems()
          })
          .catch(() => {
            // 取消loading
            this.$refs.roleDialog.cancelLoading()
          })
      } else {
        // 取消loading
        this.$refs.roleDialog.cancelLoading()
      }
    },
    onResetSearch () {
    },
    onInsertItem () {
      // 创建角色
      this.selectedIndex = -1
      this.selectedItem = Object.assign({}, this.defaultItem)
      this.$refs.roleDialog.show()
    },
    onDeleteItem ({ item, index }) {
      // 删除角色
      this.selectedItem = Object.assign({}, item)
    },
    onEditItem ({ item, index }) {
      // 编辑角色
      this.selectedIndex = index
      this.selectedItem = Object.assign({}, item)
      this.$refs.roleDialog.show()
    },
    onGetItems ({
      options,
      offset,
      limit,
      sortBy,
      sortDesc
    }) {
      // 分页查询角色
      this.$apis.blog.role.page(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
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
