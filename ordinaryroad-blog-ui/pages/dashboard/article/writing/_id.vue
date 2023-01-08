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
    <base-material-card
      :title="$t('article.actions.writing')"
    >
      <template #after-heading>
        <v-toolbar flat>
          <v-menu
            v-model="articleInheritsMenuModel"
            :disabled="!article.firstId"
            offset-y
            :close-on-content-click="false"
            :close-on-click="false"
          >
            <template #activator="{ on, attrs }">
              <v-btn
                :disabled="!article.firstId"
                depressed
                v-bind="attrs"
                v-on="on"
              >
                <v-icon left>
                  mdi-history
                </v-icon>
                {{ $t('article.actions.historicVersion') }}
              </v-btn>
            </template>
            <v-card>
              <v-card-title>
                <span>{{ $t('article.actions.selectionOfHistoricalVersions') }}</span>
                <v-spacer />
                <v-btn icon @click="articleInheritsMenuModel=false">
                  <v-icon>mdi-close</v-icon>
                </v-btn>
              </v-card-title>
              <or-blog-article-data-table
                ref="articleDataTable"
                show-status-column
                show-base-headers-when-selecting
                :preset-status="['PUBLISH', 'INHERIT','INHERIT_AUTO', 'INHERIT_PUBLISH']"
                :preset-first-id="article.firstId"
                select-return-object
                show-select
                single-select
                @itemsSelected="onSelectArticleInherit"
              >
                <template #actionsTop>
                  <v-btn
                    outlined
                    color="primary"
                    dark
                    @click="articleInheritsMenuModel=false"
                  >
                    <v-icon left>
                      mdi-close
                    </v-icon>
                    {{ $t('close') }}
                  </v-btn>
                </template>
              </or-blog-article-data-table>
            </v-card>
          </v-menu>
          <v-spacer />
          <v-btn
            :loading="draftSaving"
            :disabled="!articleValid"
            depressed
            @click="saveDraft"
          >
            {{ $t('article.actions.saveDraft') }}
          </v-btn>
          <v-btn
            class="ms-2"
            :disabled="!articleValid"
            color="primary"
            @click="publish"
          >
            {{ $t('article.actions.publish') }}
          </v-btn>
        </v-toolbar>
      </template>
      <v-form
        ref="form"
        v-model="formValid"
      >
        <v-container>
          <v-row no-gutters>
            <v-col cols="12">
              <v-file-input
                :loading="fileUploading"
                truncate-length="100"
                accept="image/*"
                prepend-icon=""
                :rules="[$rules.maxFileSize10MB]"
                outlined
                show-size
                append-outer-icon="mdi-eye"
                :label="$t('article.coverImage')"
                @change="onPictureSelected"
                @click:append-outer="onPictureClicked"
              />
            </v-col>
            <v-row no-gutters>
              <v-select
                v-model="article.original"
                :rules="[$rules.required]"
                class="flex-grow-0"
                style="width: 100px;"
                outlined
                hide-details
                :items="[{text: $t('article.originalOptions.original'),value:true},{text:$t('article.originalOptions.reprint'),value:false}]"
              />
              <v-text-field
                v-model="article.title"
                :counter="article.title.length>=60"
                :rules="[$rules.notBlank,$rules.max100Chars]"
                outlined
                :label="$t('article.title')"
                @focusin="$store.dispatch('app/setSearchInputHotKeyEnabled', false)"
                @focusout="$store.dispatch('app/setSearchInputHotKeyEnabled', true)"
              />
            </v-row>
            <v-col cols="12">
              <v-textarea
                v-model="article.summary"
                rows="1"
                :counter="article.summary.length>=400"
                :rules="[$rules.max500Chars]"
                auto-grow
                outlined
                :label="$t('article.summary')"
                @focusin="$store.dispatch('app/setSearchInputHotKeyEnabled', false)"
                @focusout="$store.dispatch('app/setSearchInputHotKeyEnabled', true)"
              />
            </v-col>
            <v-col
              id="input-vditor"
              cols="12"
            >
              <v-input
                :error-messages="!firstValid && contentEmpty ? [$t('rules.contentNotEmpty')] : null"
              >
                <or-md-vditor
                  ref="vditor"
                  :lang="$i18n.locale"
                  :placeholder="$t('article.startWritingHint')"
                  :dark="$vuetify.theme.dark"
                  :read-only="false"
                  :transfer-content.sync="article.content"
                  @focus="$store.dispatch('app/setSearchInputHotKeyEnabled', false)"
                  @blur="$store.dispatch('app/setSearchInputHotKeyEnabled', true)"
                  @after="onVditorAfter"
                />
              </v-input>
            </v-col>

            <!-- 分类 -->
            <v-col sm="12" md="6" lg="6" xl="6" class="mt-2 mb-2">
              <v-combobox
                v-model="article.typeName"
                outlined
                :disabled="typeOptions.loading"
                :loading="typeOptions.loading"
                flat
                :items="typeOptions.items"
                chips
                clearable
                :label="$t('titles.form.type')"
                prepend-inner-icon="mdi-view-list"
                solo
                hide-details
                @focusin="$store.dispatch('app/setSearchInputHotKeyEnabled', false)"
                @focusout="$store.dispatch('app/setSearchInputHotKeyEnabled', true)"
              >
                <template #selection="{ attrs, item, select, selected }">
                  <v-chip
                    label
                    v-bind="attrs"
                    :input-value="selected"
                    close
                    @click="select"
                    @click:close="removeType"
                  >
                    <strong>{{ item }}</strong>
                  </v-chip>
                </template>
              </v-combobox>
            </v-col>
            <!-- 标签 -->
            <v-col sm="12" md="6" lg="6" xl="6" class="mt-2 mb-2">
              <or-base-menu
                offset-y
                min-width="40%"
                eager
                :close-on-content-click="false"
              >
                <template #activator="{ on, attrs }">
                  <v-combobox
                    ref="tagCombobox"
                    v-model="tagOptions.selectedItems"
                    outlined
                    :disabled="tagOptions.loading"
                    flat
                    chips
                    v-bind="attrs"
                    return-object
                    :rules="[$rules.max10Size]"
                    clearable
                    :items="tagOptions.items"
                    :label="$t('titles.form.tag')"
                    prepend-inner-icon="mdi-tag-multiple"
                    solo
                    multiple
                    hide-details="auto"
                    @focusin="$store.dispatch('app/setSearchInputHotKeyEnabled', false)"
                    @focusout="$store.dispatch('app/setSearchInputHotKeyEnabled', true)"
                    @input="onSelectedItemsInput"
                    v-on="on"
                  >
                    <template #selection="{ attrs, item, select, selected }">
                      <v-chip
                        v-bind="attrs"
                        :input-value="selected"
                        close
                        @click="select"
                        @click:close="removeTag(item)"
                      >
                        <strong>{{ typeof item === 'string' ? item : item.name }}</strong>
                      </v-chip>
                    </template>
                  </v-combobox>
                </template>
                <v-sheet class="pa-2">
                  <or-blog-tag-data-table
                    ref="tagDataTable"
                    select-return-object
                    show-select
                    @itemsSelected="onTagNamesSelected"
                    @currentItems="onCurrentItems"
                  />
                </v-sheet>
              </or-base-menu>
            </v-col>

            <v-col cols="4">
              <v-checkbox
                v-model="article.canComment"
                :label="$t('article.canComment')"
              />
            </v-col>
            <v-col cols="4">
              <v-checkbox
                v-model="article.canReward"
                :label="$t('article.canReward')"
              />
            </v-col>
          </v-row>
        </v-container>
      </v-form>
    </base-material-card>
  </div>
</template>

<script>

export default {
  data: () => ({
    contentEmpty: true,
    draftSaving: false,
    fileUploading: false,
    formValid: false,

    articleInheritsMenuModel: false,

    /**
     * 文章内容延迟校验
     */
    firstValid: true,

    article: {
      status: 'DRAFT',
      coverImage: '',
      title: '',
      summary: '',
      content: '',
      canComment: true,
      canReward: false,
      original: false,
      typeName: '',
      tagNames: []
    },
    /**
     * 仅用于设置Vditor
     */
    articleContent: '',
    typeOptions: {
      loading: true,
      items: []
    },
    tagOptions: {
      loading: true,
      items: [],
      selectedItems: []
    }
  }),
  computed: {
    fileFormValid () {
      return true
    },
    articleValid () {
      const selectedItems = this.tagOptions.selectedItems
      if (selectedItems.length > 10) {
        return false
      }
      return !this.contentEmpty && this.formValid && this.fileFormValid
    }
  },
  watch: {
    '$route.params.id' (val) {
      if (val === undefined) {
        this.$router.go(0)
      }
    },
    article: {
      handler (val) {
        const contentEmpty = !val || !val.content || val.content.length === 0 || val.content === '\n'
        if (!contentEmpty) {
          if (this.firstValid) {
            this.firstValid = false
            this.contentEmpty = false
            return
          }
        }
        this.contentEmpty = contentEmpty
      },
      deep: true,
      immediate: true
    }
  },
  created () {
    const id = (this.$route.params.id || '').trim()
    if (id !== 'new') {
      this.$apis.blog.article.findOwnWritingById(id)
        .then((data) => {
          this.initData(data)
        })
        .catch(() => {
          process.client && this.$dialog({
            persistent: true,
            content: this.$i18n.t('status.article.notFound'),
            confirmText: this.$i18n.t('retry'),
            cancelText: this.$i18n.t('back')
          }).then(({ isConfirm }) => {
            if (isConfirm) {
              this.$router.go(0)
            } else {
              this.$router.back()
            }
          })
        })
    }
  },
  mounted () {
    /**
     const _this = this
     window.onbeforeunload = function (e) {
        if (_this.$route.name === 'dashboard-article-id') {
          e = e || window.event
          // 兼容IE8和Firefox 4之前的版本
          if (e) {
            e.returnValue = '编辑器内容将被丢弃，请注意保存'
          }
          // Chrome, Safari, Firefox 4+, Opera 12+ , IE 9+
          return '编辑器内容将被丢弃，请注意保存'
        } else {
          window.onbeforeunload = null
        }
      }
     **/
    this.$apis.blog.type.findAllOwn()
      .then((data) => {
        this.typeOptions.items = data.map(item => item.name)
        this.typeOptions.loading = false
      })
      .catch(() => {
        this.typeOptions.loading = false
      })
    // TODO 加载最火的标签
    this.tagOptions.items = []
    this.tagOptions.loading = false
    const inputs = this.$refs.tagCombobox.$el.getElementsByTagName('input')
    if (inputs && inputs[0]) {
      const input = inputs[0]
      input.addEventListener('input', () => {
        const inputValue = input.value
        // TODO 搜索标签
        console.log('onInput', inputValue)
      })
    }
  },
  methods: {
    initData (data) {
      // 只能编辑草稿或已发布的
      if (data.status !== 'DRAFT' && data.status !== 'PUBLISH') {
        this.$router.replace('/dashboard/article/writing')
      } else {
        this.article = data
        this.articleContent = data.content
        this.tagOptions.selectedItems = data.tagNames
        this.$nextTick(() => {
          try {
            this.$refs.vditor && this.$refs.vditor.setValue(data.content)
          } catch (ignore) {
          }
        })
      }
    },
    onVditorAfter () {
      // 防止数据比编辑器先加载出来
      this.$refs.vditor.setValue(this.articleContent)
    },
    onCurrentItems (items) {
      this.onSelectedItemsInput(this.tagOptions.selectedItems)
    },
    /**
     * 退格删除选择项或者会车添加选择项时更新DataTable的UI
     * @param tags
     */
    onSelectedItemsInput (tags) {
      this.onSelectedItemsInputWithEmit(tags)
    },
    onSelectedItemsInputWithEmit (tags, emit = true) {
      try {
        const dataTableTags = this.$refs.tagDataTable.$refs.dataTable.$data.dataTableParams.items
        dataTableTags.forEach((tag) => {
          let founded = false
          for (let i = 0; i < tags.length; i++) {
            const selectedTag = tags[i]
            if (typeof selectedTag === 'string') {
              if (selectedTag === tag.name) {
                founded = true
                break
              }
            } else if (selectedTag.uuid === tag.uuid) {
              founded = true
              break
            }
          }
          this.$refs.tagDataTable.$refs.dataTable.select({ item: tag, value: founded, emit })
        })
      } catch (ignore) {
        // ignore
      }
    },
    removeType () {
      this.article.typeName = ''
    },
    /**
     * 更新selectedItems
     * @param tags DataTable选中的项目
     */
    onTagNamesSelected (tags) {
      const tagNames = tags.map(tag => tag.name)
      const items = [...tags]
      this.tagOptions.selectedItems.forEach((item) => {
        if (typeof item === 'string' && !tagNames.includes(item)) {
          items.push(item)
        }
      })
      this.tagOptions.selectedItems = items
    },
    /**
     * 点击Chip删除按钮，更新SelectedItem和DataTable选中的项目
     * @param item 点击删除按钮删除的Item String | Object
     */
    removeTag (item) {
      this.tagOptions.selectedItems.splice(this.tagOptions.selectedItems.indexOf(item), 1)
      this.tagOptions.selectedItems = [...this.tagOptions.selectedItems]
      this.onSelectedItemsInput(this.tagOptions.selectedItems)
    },
    changeCurrentArticle (newArticle) {
      this.article = newArticle
      this.articleContent = this.article.content
      this.$refs.vditor.setValue(this.articleContent)
      this.tagOptions.selectedItems = this.article.tagNames
      this.onCurrentItems(null)
    },
    /**
     * 选中历史版本
     */
    onSelectArticleInherit (items) {
      if (items.length === 1) {
        this.$dialog({
          content: this.$t('article.actions.selectHistoricHint')
        }).then((dialog) => {
          if (dialog.isConfirm) {
            this.articleInheritsMenuModel = false
            const newArticle = items[0]
            this.changeCurrentArticle(newArticle)
          }
          this.$refs.articleDataTable.unSelectItem(items[0])
        })
      }
    },
    /**
     * 选中图片
     * @param file File
     */
    onPictureSelected (file) {
      if (file == null) {
        this.article.coverImage = ''
      } else if (this.fileFormValid) {
        this.fileUploading = true
        this.$apis.blog.upload(file)
          .then((data) => {
            this.article.coverImage = data
            this.fileUploading = false
          })
          .catch(() => {
            this.fileUploading = false
          })
      }
    },
    updateArticleTagNames () {
      this.article.tagNames = []
      this.tagOptions.selectedItems.forEach((item) => {
        if (typeof item === 'string') {
          this.article.tagNames.push(item)
        } else {
          this.article.tagNames.push(item.name)
        }
      })
    },
    publish () {
      if (this.formValid && !this.contentEmpty) {
        if (this.article.status !== 'DRAFT' && this.article.status !== 'PUBLISH') {
          this.$snackbar.error(this.$t('article.actions.onlyEditDraftOrPublishHint'))
          return
        }
        this.updateArticleTagNames()
        this.$dialog({
          persistent: false,
          loading: true,
          content: this.$t('areYouSureToDoWhat', [this.$t('article.actions.publish')])
        }).then((dialog) => {
          if (dialog.isConfirm) {
            this.$apis.blog.article.publish(this.article)
              .then((data) => {
                this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('article.actions.publish')]))
                setTimeout(() => {
                  this.$router.replace('/dashboard/article/status/PENDING')
                  dialog.cancel()
                }, 300)
              })
              .catch(() => {
                dialog.cancel()
              })
          }
        })
      } else {
        this.$snackbar.error('请检查输入')
      }
    },
    saveDraft () {
      if (this.formValid && !this.contentEmpty) {
        this.updateArticleTagNames()
        this.draftSaving = true
        this.$apis.blog.article.saveDraft(this.article)
          .then((data) => {
            this.article = data
            this.draftSaving = false
            this.$snackbar.success(this.$t('whatSuccessfully', [this.$t('article.actions.saveDraft')]))
          })
          .catch(() => {
            this.draftSaving = false
          })
      } else {
        this.$snackbar.error('请检查输入')
      }
    },
    onPictureClicked () {
      if (this.article.coverImage && this.article.coverImage !== '') {
        window.open(this.$apis.blog.getFileUrl(this.article.coverImage), '_blank')
      }
    }
  }
}
</script>

<style>
#input-vditor .v-input__slot {
  display: block !important;
}
</style>
