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
      hide-update-headers
      :sort-desc="[]"
      :single-select="singleSelect"
      :select-return-object="selectReturnObject"
      :show-select="showSelect"
      :show-base-headers-when-selecting="showBaseHeadersWhenSelecting"
      :show-actions-when-selecting="showActionsWhenSelecting"
      :preset-selected-items="presetSelectedItems"
      :table-headers="headers"
      access-key="blog:comment"
      @getItems="onGetItems"
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
            v-model="searchParams.content"
            dense
            outlined
            clearable
            hide-details="auto"
            maxlength="200"
            :label="$t('comment.content')"
          />
        </v-col>
        <v-col
          v-if="$access.hasAuditorRole()"
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
            :label="$t('comment.onlyViewOwn')"
          />
        </v-col>
      </template>

      <template #actionsTop>
        <span />
      </template>

      <template #[`item.content`]="{ item }">
        <span class="tow-lines-text">{{ item.content }}</span>
      </template>

      <template #[`item.article`]="{ item }">
        <or-link
          hover-able
          :href="`/${item.article.creatorUid}/article/${item.article.firstId}`"
        >
          {{ item.article.title }}
        </or-link>
      </template>

      <template #actions="{ item }">
        <v-btn
          icon
          color="primary"
          @click="onClickContent(item)"
        >
          <v-icon>mdi-eye</v-icon>
        </v-btn>
        <v-btn
          v-if="item.creatorId===userInfo.user.uuid||$access.hasAuditorRole()"
          icon
          color="error"
          @click="onDeleteItem(item)"
        >
          <v-icon>mdi-delete-forever</v-icon>
        </v-btn>
      </template>
    </or-base-data-table>
    <or-base-dialog
      ref="commentDialog"
      title="评论"
      @onConfirm="$refs.commentDialog.close()"
    >
      <or-md-vditor
        comment-mode
        :lang="$i18n.locale"
        :dark="$vuetify.theme.dark"
        :pre-set-content="selectedItem?.content ?? ''"
      />
    </or-base-dialog>
  </v-container>
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'OrBlogCommentDataTable',
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
      content: null,
      own: null
    },
    selectedIndex: -1,
    selectedItem: null
  }),
  computed: {
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    }),
    // 放在这为了支持国际化，如果放在data下切换语言不会更新
    headers () {
      const headers = [
        {
          text: this.$t('comment.content'),
          value: 'content',
          width: 400
        },
        {
          text: this.$t('comment.article'),
          value: 'article',
          width: 200
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
    onClickContent (item) {
      this.selectedItem = Object.assign({}, item)
      this.$refs.commentDialog.show()
    },
    onResetSearch () {
    },
    onDeleteItem (item) {
      this.$dialog({
        persistent: false,
        content: this.$t('areYouSureToDoWhat', [this.$t('comment.actions.deleteForever')]),
        loading: true
      }).then((dialog) => {
        if (dialog.isConfirm) {
          this.$apis.blog.comment.delete(item.uuid)
            .then(() => {
              this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('comment.actions.deleteForever')]))
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
    },
    onGetItems ({
      options,
      offset,
      limit,
      sortBy,
      sortDesc
    }) {
      this.$apis.blog.comment.page(offset / limit + 1, options.itemsPerPage, sortBy, sortDesc, this.searchParams)
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
