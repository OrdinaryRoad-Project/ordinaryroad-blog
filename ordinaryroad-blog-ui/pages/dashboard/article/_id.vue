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
              <or-blog-article-data-table
                show-status-column
                show-base-headers-when-selecting
                :preset-status="['PUBLISH', 'INHERIT', 'PUBLISH_INHERIT']"
                :preset-first-id="article.firstId"
                select-return-object
                show-select
                single-select
                @itemsSelected="onSelectArticleInherit"
              />
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
              <v-form
                ref="fileForm"
                v-model="fileFormValid"
              >
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
              </v-form>
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
              />
            </v-col>
            <v-col
              id="input-vditor"
              cols="12"
            >
              <v-input
                :error-messages="contentEmpty?[$t('rules.contentNotEmpty')]:null"
              >
                <or-md-vditor
                  ref="vditor"
                  :placeholder="$t('article.startWritingHint')"
                  :dark="$vuetify.theme.dark"
                  :read-only="false"
                  :pre-set-content="articleContent"
                  :transfer-content.sync="article.content"
                />
              </v-input>
            </v-col>
            <v-row>
              <v-col sm="12" md="6" class="mt-2 mb-2">
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
                >
                  <template #selection="{ attrs, item, select, selected }">
                    <v-chip
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
              <v-col sm="12" md="6" class="mt-2 mb-2">
                <v-combobox
                  v-model="article.tagNames"
                  outlined
                  :disabled="tagOptions.loading"
                  :loading="tagOptions.loading"
                  flat
                  :items="tagOptions.items"
                  chips
                  multiple
                  solo
                  clearable
                  :label="$t('titles.form.tag')"
                  prepend-inner-icon="mdi-tag-multiple"
                  hide-details
                >
                  <template #selection="{ attrs, item, select, selected }">
                    <v-chip
                      v-bind="attrs"
                      :input-value="selected"
                      close
                      @click="select"
                      @click:close="removeTag(item)"
                    >
                      <strong>{{ item }}</strong>
                    </v-chip>
                  </template>
                </v-combobox>
              </v-col>
            </v-row>
            <v-col cols="12">
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
  asyncData ({
    route,
    $apis,
    redirect
  }) {
    const id = (route.params.id || '').trim()
    if (id !== '') {
      return $apis.blog.article.findOwnById(id)
        .then((data) => {
          return {
            article: data,
            articleContent: data.content
          }
        })
        .catch(() => {
          return {
            article: undefined
          }
        })
    } else {
      // 查询是否存在未发布的草稿，存在返回，不存在返回默认
      return $apis.blog.article.getDraft()
        .then((data) => {
          if (data) {
            return {
              article: data,
              articleContent: data.content
            }
          } else {
            return {
              article: {
                coverImage: '',
                title: '',
                summary: '',
                content: '',
                canReward: false,
                original: false,
                typeName: '',
                tagNames: []
              }
            }
          }
        })
    }
  },
  data: () => ({
    contentEmpty: true,
    draftSaving: false,
    fileUploading: false,
    formValid: false,
    fileFormValid: false,

    articleInheritsMenuModel: false,

    article: null,
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
      items: []
    }
  }),
  computed: {
    articleValid () {
      return !this.contentEmpty && this.formValid && this.fileFormValid
    }
  },
  watch: {
    article: {
      handler (val) {
        this.contentEmpty = !val || !val.content || val.content.length === 0 || val.content === '\n'
      },
      deep: true,
      immediate: true
    }
  },
  mounted () {
    if (this.article === undefined) {
      this.$dialog({
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
    } else {
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
        }, () => {
          this.typeOptions.loading = false
        })
      // TODO 加载标签
      this.tagOptions.items = ['1', '2', '3']
      this.tagOptions.loading = false
    }
  },
  methods: {
    removeType () {
      this.article.typeName = ''
    },
    removeTag (item) {
      this.article.tagNames.splice(this.article.tagNames.indexOf(item), 1)
      this.article.tagNames = [...this.article.tagNames]
    },
    /**
     * 选中历史版本
     */
    onSelectArticleInherit (items) {
      if (items.length === 1) {
        this.$dialog({
          content: '确定使用该历史版本，本地编辑器将内容将会丢失'
        }).then((dialog) => {
          if (dialog.isConfirm) {
            this.articleInheritsMenuModel = false
            this.article = items[0]
            this.articleContent = this.article.content
            this.$refs.vditor.setValue(this.articleContent)
          }
        })
      }
    },
    /**
     * 选中图片
     * @param file File
     */
    onPictureSelected (file) {
      // console.log(this.fileFormValid, this.$refs.fileForm.validate())
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
    publish () {
      if (this.formValid && !this.contentEmpty) {
        this.$dialog({
          persistent: false,
          loading: true,
          content: '确认发布？'
        }).then((dialog) => {
          if (dialog.isConfirm) {
            this.$apis.blog.article.publish(this.article)
              .then((data) => {
                dialog.cancel()
                this.$snackbar.success('文章发布成功')
                setTimeout(() => {
                  this.$router.replace(`/dashboard/article/${data.uuid}`, () => {
                    this.$router.go(0)
                  })
                }, 500)
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
        this.draftSaving = true
        this.$apis.blog.article.saveDraft(this.article)
          .then((data) => {
            this.article = data
            this.draftSaving = false
            this.$snackbar.success('草稿保存成功')
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
