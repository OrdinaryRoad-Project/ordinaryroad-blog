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
    <or-base-data-iterator
      ref="dataIterator"
      :sort-by="sortBy"
      :sort-desc="sortDesc"
      :single-select="singleSelect"
      selectable-key="selectable"
      :hide-default-footer="hideDefaultFooter"
      :item-keys="itemKeys"
      @getItems="onGetItems"
    >
      <template #searchFormBody>
        <v-col
          cols="6"
          lg="3"
          md="4"
        >
          <v-text-field
            v-model.trim="searchParams.title"
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
            v-model.trim="searchParams.summary"
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
            v-model.trim="searchParams.content"
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
        <span />
      </template>

      <template #default="{ props }">
        <slot name="default" :props="props" />
      </template>

      <!-- { items, originalItemsLength, pagination, options, groupedItems, updateOptions, sort, sortArray, group } -->
      <template
        v-if="hideDefaultFooter"
        #footer="props"
      >
        <slot name="footer" :props="props" />
      </template>

      <!-- { pageStart, pageStop, itemsLength } -->
      <template
        v-if="hideDefaultFooter"
        #[`footer.page-text`]="props"
      >
        <slot name="footer.page-text" :props="props" />
      </template>
    </or-base-data-iterator>
  </v-container>
</template>

<script>

import { mapGetters } from 'vuex'

export default {
  name: 'OrBlogArticleDataIterator',
  props: {
    sortBy: {
      type: Array,
      default: () => []
    },
    sortDesc: {
      type: Array,
      default: () => []
    },
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
    hideDefaultFooter: {
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
      original: null
    },
    selectedIndex: -1,
    selectedItem: null
  }),
  computed: {
    ...mapGetters('user', {
      userInfo: 'getUserInfo'
    }),

    itemKeys () {
      const itemKeys = [
        {
          text: this.$t('article.coverImage'),
          value: 'coverImage'
        },
        {
          text: this.$t('article.title'),
          value: 'title'
        },
        {
          text: this.$t('article.summary'),
          value: 'summary'
        },
        {
          text: this.$t('article.typeName'),
          value: 'typeName',
          sortable: false
        },
        {
          text: this.$t('article.tagNames'),
          value: 'tagNames',
          sortable: false
        },
        {
          text: this.$t('article.canReward'),
          value: 'canReward'
        },
        {
          text: this.$t('article.original'),
          value: 'original'
        }
      ]

      return itemKeys
    }
  },
  watch: {},
  created () {
  },
  mounted () {
    // this.$refs.dataIterator.getItems()
  },
  methods: {
    onResetSearch () {
    },
    onInsertItem () {
      this.selectedIndex = -1
      this.selectedItem = null
    },
    onEditItem ({
      item,
      index
    }) {
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
      this.$emit('getItems', { options, offset, limit, sortBy, sortDesc })
    },
    onItemsSelected (items) {
      this.$emit('itemsSelected', items)
    },
    unSelectItem (item) {
      this.$refs.dataIterator.select({
        item,
        value: false,
        emit: true
      })
    }
  }
}
</script>
