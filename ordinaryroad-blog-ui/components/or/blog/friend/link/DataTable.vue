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
      access-key="blog:friend_link"
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
            :label="$t('friendLink.name')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>

        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.description"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('friendLink.description')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>

        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.url"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('friendLink.url')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>

        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model="searchParams.email"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="100"
            :label="$t('friendLink.email')"
            @keydown.enter="$refs.dataTable.searchItems()"
          />
        </v-col>

        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-select
            v-model="searchParams.status"
            clearable
            dense
            hide-details
            :items="statusOptions.items"
            outlined
            :loading="statusOptions.loading"
            :label="$t('log.status')"
            @change="$refs.dataTable.searchItems()"
          />
        </v-col>
      </template>

      <template #actions="{ item }">
        <v-btn
          icon
          color="accent"
          class="me-2"
          @click="onEditItem({item})"
        >
          <v-icon>mdi-pencil</v-icon>
        </v-btn>
        <v-btn
          icon
          color="error"
          @click="$refs.dataTable.deleteItem(item)"
        >
          <v-icon>mdi-delete-forever</v-icon>
        </v-btn>
      </template>

      <template #moreActions="{ item }">
        <v-list-item
          v-if="item.status !== 'APPROVED'"
          @click="onClickApproved(item)"
        >
          <v-list-item-title>
            {{ $t("friendLink.actions.approved") }}
          </v-list-item-title>
        </v-list-item>
        <v-list-item
          v-if="item.status !== 'DISAPPROVED'"
          @click="onClickDisapproved(item)"
        >
          <v-list-item-title>
            {{ $t("friendLink.actions.disapproved") }}
          </v-list-item-title>
        </v-list-item>
      </template>

      <template #[`item.name`]="{ item }">
        <or-link :href="item.url" hover-able>
          {{ item.name }}
        </or-link>
      </template>

      <template #[`item.snapshotUrl`]="{ item }">
        <or-link
          v-if="item.snapshotUrl"
          :href="item.snapshotUrl"
        >
          <v-menu open-on-hover>
            <template #activator="{ attrs ,on }">
              <span
                v-bind="attrs"
                v-on="on"
              >{{ item.snapshotUrl }}</span>
            </template>
            <v-card
              :href="$apis.blog.getFileUrl(item.snapshotUrl)"
              target="_blank"
            >
              <v-img
                min-width="100%"
                :width="$vuetify.breakpoint.width/4"
                :src="$apis.blog.getFileUrl(item.snapshotUrl)"
                aspect-ratio="1"
              />
            </v-card>
          </v-menu>
        </or-link>
        <span v-else>
          无
        </span>
      </template>

      <template #[`item.enabled`]="{ item }">
        <v-switch
          v-model="item.enabled"
          readonly
          :disabled="item.status!=='APPROVED'||!$access.hasDeveloperRole()"
          inset
          @click="onToggleEnabled(item)"
        />
      </template>

      <template #[`item.status`]="{ item }">
        <v-chip label :color="$apis.statusColor(item)">
          {{ item.status }}
        </v-chip>
      </template>
    </or-base-data-table>
    <or-base-dialog
      ref="friendLinkEnabledDialog"
      loading
      :title="$t('areYouSureToDoWhat', [$t(friendLinkEnabledDialog.action)])"
      @onConfirm="toggleEnabled"
    >
      <v-checkbox
        v-model="friendLinkEnabledDialog.notice"
        :disabled="!selectedItem.email"
        label="通知站长"
      />
    </or-base-dialog>
    <or-base-dialog
      ref="friendLinkDialog"
      loading
      :title="$t(action+'What',[$t('titles.form.friendLink')])"
      @onConfirm="createOrUpdate"
    >
      <or-blog-friend-link-save-form
        ref="friendLinkForm"
        :preset="selectedItem"
        @update="onItemUpdate"
        @submit="$refs.friendLinkDialog.confirm()"
      />
    </or-base-dialog>
    <or-input-dialog
      ref="inputDialog"
      title="拒绝"
      label="理由"
      hint="请输入拒绝理由"
      :rules="[$or.rules.required,$or.rules.notBlank]"
      @onConfirm="disapproved"
    />
  </v-container>
</template>

<script>
export default {
  name: 'OrBlogFriendLinkDataTable',
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
    friendLinkEnabledDialog: {
      action: String,
      notice: false
    },
    statusOptions: {
      loading: true,
      items: []
    },
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
          text: this.$t('friendLink.enabled'),
          value: 'enabled',
          width: 100
        },
        {
          text: this.$t('friendLink.status'),
          value: 'status',
          width: 100
        },
        {
          text: this.$t('friendLink.name'),
          value: 'name',
          width: 100
        },
        {
          text: this.$t('friendLink.email'),
          value: 'email',
          width: 100
        },
        {
          text: this.$t('friendLink.description'),
          value: 'description',
          width: 200
        },
        {
          text: this.$t('friendLink.snapshotUrl'),
          value: 'snapshotUrl',
          width: 100
        },
        {
          text: this.$t('friendLink.url'),
          value: 'url',
          width: 100
        },
        {
          text: this.$t('friendLink.logo'),
          value: 'logo',
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
    this.findAllStatus()
  },
  mounted () {
  },
  methods: {
    onToggleEnabled (item) {
      if (item.status !== 'APPROVED' || !this.$access.hasDeveloperRole()) {
        return
      }
      this.selectedItem = Object.assign({}, item)
      this.friendLinkEnabledDialog.action = item.enabled ? 'disable' : 'enable'
      this.$refs.friendLinkEnabledDialog.show()
    },
    toggleEnabled () {
      this.$apis.blog.friend_link.enabled(this.selectedItem.uuid, !this.selectedItem.enabled, this.friendLinkEnabledDialog.notice)
        .then(() => {
          this.friendLinkEnabledDialog.notice = false
          this.$refs.dataTable.getItems()
          this.$refs.friendLinkEnabledDialog.close()
        })
        .catch(() => {
          this.$refs.friendLinkEnabledDialog.close()
        })
    },
    disapproved (input) {
      this.$apis.blog.friend_link.disapproved(this.editedItem.uuid, input)
        .then(() => {
          this.$refs.inputDialog.close()
          this.$refs.dataTable.getItems()
        })
        .catch(() => {
          this.$refs.inputDialog.close()
        })
    },
    onClickDisapproved (item) {
      this.editedItem = item
      this.$refs.inputDialog.show()
    },
    onClickApproved (item) {
      this.$dialog({
        content: '确定通过？',
        loading: true
      })
        .then((dialog) => {
          if (dialog.isConfirm) {
            this.$apis.blog.friend_link.approved(item.uuid)
              .then(() => {
                dialog.cancel()
                this.$refs.dataTable.getItems()
              })
              .catch(() => {
                dialog.cancel()
              })
          }
        })
    },
    findAllStatus () {
      this.$apis.blog.friend_link.findAllStatus()
        .then((data) => {
          this.statusOptions.loading = false
          this.statusOptions.items = data
        })
        .catch(() => {
          this.statusOptions.loading = false
        })
    },
    onCurrentItems (items) {
      this.$emit('currentItems', items)
    },
    onItemUpdate (item) {
      this.editedItem = item
    },
    createOrUpdate () {
      if (this.$or.util.objectEquals(this.editedItem, this.selectedItem)) {
        this.$refs.friendLinkDialog.close()
        // 有变动才进行创建或更新
      } else if (this.$refs.friendLinkForm.validate()) {
        const action = this.selectedIndex === -1 ? 'create' : 'update'
        this.$apis.blog.friend_link[action](this.editedItem)
          .then(() => {
            this.$refs.friendLinkDialog.close()
            this.$refs.dataTable.getItems()
          })
          .catch(() => {
            // 取消loading
            this.$refs.friendLinkDialog.cancelLoading()
          })
      } else {
        // 取消loading
        this.$refs.friendLinkDialog.cancelLoading()
      }
    },
    onResetSearch () {
    },
    onInsertItem () {
      // 创建友链
      this.selectedIndex = -1
      this.selectedItem = Object.assign({}, this.defaultItem)
      this.$refs.friendLinkDialog.show()
    },
    onDeleteItem ({ item, index }) {
      // 删除友链
      this.$apis.blog.friend_link.delete(item.uuid)
        .then(() => {
          this.$refs.dataTable.deleteSuccessfully()
        })
        .catch(() => {
          this.$refs.dataTable.deleteFailed()
        })
    },
    onEditItem ({ item, index }) {
      // 编辑友链
      this.selectedIndex = index
      this.selectedItem = Object.assign({}, item)
      this.$refs.friendLinkDialog.show()
    },
    onGetItems ({
      options,
      offset,
      limit,
      sortBy,
      sortDesc
    }) {
      // 分页查询友链
      this.$apis.blog.friend_link.page(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
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
