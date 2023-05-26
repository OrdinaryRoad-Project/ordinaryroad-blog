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
      hide-update-headers
      :single-select="singleSelect"
      :select-return-object="selectReturnObject"
      :show-select="showSelect"
      :show-base-headers-when-selecting="showBaseHeadersWhenSelecting"
      :show-actions-when-selecting="showActionsWhenSelecting"
      :preset-selected-items="presetSelectedItems"
      :table-headers="headers"
      access-key="blog:tag"
      @getItems="onGetItems"
      @insertItem="onInsertItem"
      @deleteItem="onDeleteItem"
      @editItem="onEditItem"
      @itemsSelected="onItemsSelected"
      @resetSearch="onResetSearch"
      @currentItems="onCurrentItems"
    >
      <template #searchFormBody>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.name"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('tag.name')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>
      </template>

      <template #[`item.name`]="{ item }">
        <v-chip>
          {{ item.name }}
        </v-chip>
      </template>

      <template #actions="{ item }">
        <v-btn
          :to="`/search/${item.name}`"
          target="_blank"
          icon
          color="primary"
          class="mx-2"
        >
          <v-icon>mdi-file-document-multiple</v-icon>
        </v-btn>
      </template>
    </or-base-data-table>
    <or-base-dialog
      ref="tagDialog"
      loading
      :title="$t('createWhat',[$t('titles.form.tag')])"
      @onConfirm="create"
    >
      <or-blog-tag-save-form
        ref="tagForm"
        :preset="selectedItem"
        @update="onItemUpdate"
      />
    </or-base-dialog>
  </v-container>
</template>

<script>
export default {
  name: 'OrBlogTagDataTable',
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
      name: '',
      deleted: false
    },
    selectedItem: {
      uuid: null,
      name: '',
      deleted: false
    },
    defaultItem: {
      uuid: null,
      name: '',
      deleted: false
    }
  }),
  computed: {
    // 放在这为了支持国际化，如果放在data下切换语言不会更新
    headers () {
      const headers = [
        {
          text: this.$t('tag.name'),
          value: 'name',
          width: 100
        }
      ]
      return headers
    }
  },
  watch: {},
  created () {
  },
  mounted () {
  },
  methods: {
    onCurrentItems (items) {
      this.$emit('currentItems', items)
    },
    onItemUpdate (item) {
      this.editedItem = item
    },
    create () {
      if (this.$or.util.objectEquals(this.editedItem, this.selectedItem)) {
        this.$refs.tagDialog.close()
        // 有变动才进行创建或更新
      } else if (this.$refs.tagForm.validate()) {
        this.$apis.blog.tag.create(this.editedItem)
          .then(() => {
            this.$refs.tagDialog.close()
            this.$refs.dataTable.getItems()
          })
          .catch(() => {
            // 取消loading
            this.$refs.tagDialog.cancelLoading()
          })
      } else {
        // 取消loading
        this.$refs.tagDialog.cancelLoading()
      }
    },
    onResetSearch () {
    },
    onInsertItem () {
      // 创建标签
      this.selectedIndex = -1
      this.selectedItem = Object.assign({}, this.defaultItem)
      this.$refs.tagDialog.show()
    },
    onDeleteItem ({ item, index }) {
      // 删除标签
      this.$refs.dataTable.deleteFailed()
    },
    onEditItem ({ item, index }) {
      // 编辑标签
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
      // 分页查询标签
      this.$apis.blog.tag.page(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
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
