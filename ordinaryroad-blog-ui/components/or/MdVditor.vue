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
  <div ref="vditor" />
</template>

<script>
import Vditor from 'vditor'
import 'vditor/src/assets/less/index.less'

export default {
  name: 'OrMdVditor',
  components: {},
  props: {
    placeholder: {
      type: String,
      default: ''
    },
    commentMode: {
      type: Boolean,
      default: false
    },
    readOnly: {
      type: Boolean,
      default: true
    },
    preSetContent: {
      type: String,
      required: true
    },
    dark: {
      type: Boolean,
      default: false
    },
    /**
     * 计算标题出现时到顶部的偏移量
     */
    headingsOffsetTop: {
      type: Number,
      default: 0
    }
  },
  data: () => ({
    scrollingOptions: {
      scrollEndTimer: null,
      currentScrollTop: 0
    },
    instance: undefined,
    headingElements: []
  }),
  watch: {
    dark (val) {
      try {
        if (this.readOnly) {
          this.updatePreviewerTheme(val)
        } else {
          this.updateEditorTheme(val)
        }
      } catch (e) {
      }
    }
  },
  created () {
  },
  // 相当于created中的nextTick，此时dom元素已经渲染完成
  mounted () {
    // 当查看博客，新建博客的时候触发mounted方法
    if (this.readOnly) {
      this.initPreviewer(this.preSetContent)
    } else {
      this.initEditor()
    }
  },
  beforeDestroy () {
  },
  destroyed () {
    window.removeEventListener('scroll', this.handleScroll, false)
  },
  methods: {
    setPlaceholder (val) {
      if (!this.readOnly) {
        const byClassName = this.$refs.vditor.getElementsByClassName('vditor-reset')
        if (byClassName && byClassName.length > 0) {
          byClassName[0].setAttribute('placeholder', val)
        }
      }
    },
    setValue (val) {
      // console.log('watch:preSetContent', this.preSetContent)
      // // 当编辑博客的时候触发watch和mounted方法，但是只有在watch中才有值，因为prop异步
      if (this.readOnly) {
        // this.initPreviewer(val)
      } else {
        const current = this.instance.getValue()
        // console.log(current === val)
        if (current !== val) {
          this.instance.setValue(val || '')
          this.$nextTick(() => {
            if (!this.commentMode) {
              // this.instance.focus()
            }
          })
        }
        // this.initEditor(null)
      }
    },
    updateEditorTheme (dark) {
      if (this.instance) {
        let editorTheme = 'classic'
        let theme = 'light'
        let codeTheme = 'github'
        if (dark) {
          editorTheme = 'dark'
          theme = 'dark'
          codeTheme = 'dracula'
        }
        this.instance.setTheme(editorTheme, theme, codeTheme)
      }
    },
    updatePreviewerTheme (dark) {
      // TODO 夜间模式链接颜色不对
      let theme = 'light'
      let codeTheme = 'github'
      if (dark) {
        theme = 'dark'
        codeTheme = 'dracula'
      }
      Vditor.setCodeTheme(codeTheme)
      Vditor.setContentTheme(theme, 'https://unpkg.com/vditor@3.8.15/dist/css/content-theme')
    },
    initEditor () {
      this.instance = new Vditor(this.$refs.vditor, {
        placeholder: this.placeholder,
        toolbarConfig: {
          hide: this.commentMode,
          pin: true
        },
        // https://b3log.org/vditor/demo/advanced-hint.html
        hint: {
          emojiTail: '输入英文冒号 <code>:</code> 使用<a href="https://github.com/88250/lute/blob/master/parse/emoji_map.go?utm_source=ld246.com" target="_blank">更多表情</a>',
          // https://github.com/88250/lute/blob/master/parse/emoji_map.go?utm_source=ld246.com
          emoji: {
            '+1': '👍',
            '-1': '👎',
            heart: '❤',
            cold_sweat: '😰',
            sad: '💔',
            'or-logo': 'https://cdn.jsdelivr.net/npm/vditor@1.3.1/dist/images/emoji/j.png'
          },
          extend: [
            {
              key: '@',
              hint (value) {
                console.log(value)
                return [
                  {
                    html: '<a> 1 </a>',
                    value: '@1'
                  }
                ]
              }
            }
          ]
        },
        minHeight: this.commentMode ? 50 : 600,
        value: this.preSetContent || '',
        mode: 'wysiwyg',
        theme: this.dark ? 'dark' : 'classic',
        icon: 'material',
        counter: {
          type: 'text',
          enable: true
        },
        cache: {
          enable: false
        },
        preview: {
          hljs: {
            style: this.dark ? 'dracula' : 'github',
            lineNumber: true
          },
          theme: {
            current: this.dark ? 'dark' : 'light'
          }
        },
        upload: {
          max: 30 * 1024 * 1024,
          url: '/api/blog/common/upload',
          // 跨站点访问控制。默认值: false
          withCredentials: true,
          headers: {},
          // 是否允许多文件上传。默认值：true
          multiple: false,
          fieldName: 'file',
          setHeaders: () => {
            return {
              or_blog_token: this.$store.getters['user/getTokenInfo']
            }
          },
          // linkToImgUrl: '/api/blog/upload/{url}',
          // linkToImgFormat: (responseText) => {
          //   const response = JSON.parse(responseText)
          //   response.code = 0
          //   response.msg = ''
          //   const url = response.data.url
          //   response.data.url = this.$constants.OSS_INFO.download + url
          //   // console.log(response)
          //   return JSON.stringify(response)
          // },
          format: (files, responseText) => {
            // console.log(files, responseText)
            const file = files[0]
            const response = {}
            response.code = 0
            response.msg = ''
            response.data = { succMap: {} }
            // const succMap = response.data.succMap
            // const succMapNew = {}
            response.data.succMap[file.name] = `${this.$config.FILE_DOWNLOAD_BASE_URL}${responseText}`
            // console.log(response)
            return JSON.stringify(response)
          }
        },
        resize: {
          enable: true
        },
        outline: {
          enable: !this.commentMode
        },
        after: () => {
          this.updateEditorTheme(this.dark)
          this.$nextTick(() => {
            if (this.commentMode) {
              // 评论的时候设置padding
            } else {
              // this.instance.focus()
            }
          })
          this.$emit('after', 'edit')
        },
        input: (value) => {
          this.$emit('update:transferContent', value)
        }
      })
    },
    initPreviewer (content) {
      const previewElement = this.$refs.vditor
      // const renderHeading = false
      // const textTemp = ''
      if (this.commentMode) {
        previewElement.style.paddingLeft = '0'
        previewElement.style.paddingRight = '0'
      } else {
        previewElement.style.paddingLeft = '20px'
        previewElement.style.paddingRight = '20px'
      }
      let mode = 'light'
      let theme = 'light'
      let codeTheme = 'github'
      if (this.dark) {
        mode = 'dark'
        theme = 'dark'
        codeTheme = 'dracula'
      }
      Vditor.preview(previewElement, content,
        {
          markdown: {
            fixTermTypo: true
          },
          mode,
          hljs: {
            style: codeTheme,
            lineNumber: true
          },
          theme: {
            current: theme
          },
          speech: {
            enable: true
          },
          renderers: {
            // renderText: (node, entering) => {
            //   if (entering) {
            //     if (textTemp === '' && renderHeading) {
            //       textTemp = node.TokensStr()
            //       renderHeading = false
            //     }
            //     return ['', Lute.WalkContinue]
            //   } else {
            //     return [node.TokensStr(), Lute.WalkContinue]
            //   }
            // },
            // renderHeading: (node, entering) => {
            //   if (entering) {
            //     renderHeading = true
            //     return [`<div><h${node.__internal_object__.HeadingLevel}>`, Lute.WalkContinue];
            //   } else {
            //     let string = `</h${node.__internal_object__.HeadingLevel}><div name="${textTemp}"></div></div>`;
            //     textTemp = '';
            //     return [string, Lute.WalkContinue];
            //   }
            // }
          },
          after: () => {
            this.updatePreviewerTheme(this.dark)
            previewElement.addEventListener('click', (event) => {
              if (event.target.tagName === 'IMG') {
                Vditor.previewImage(event.target, null, this.$vuetify.theme.dark ? 'dark' : 'light')
              }
            })

            if (this.commentMode) {
              // 评论不需要目录
              return
            }
            // 无效
            // Vditor.outlineRender(
            //   previewElement,
            //   document.getElementById('outline')
            // )
            const headingElements = []
            const toc = []
            // console.log(previewElement.querySelectorAll('h1,h2,h3,h4,h5,h6'));
            const elementNodeListOf = previewElement.querySelectorAll('h1,h2,h3,h4,h5,h6')
            if (elementNodeListOf.length) {
              for (let i = 0; i < elementNodeListOf.length; i++) {
                const element = elementNodeListOf[i]
                headingElements.push(element)
                element.setAttribute('id', 'blog-toc-id' + '-' + i)
                // console.log(element.tagName.charAt(1))
                // console.log(element.textContent)
                // console.log('\n')
                toc.push({
                  id: i,
                  index: element.tagName.charAt(1) - 1,
                  headString: element.textContent
                })
              }
              this.$emit('update:previewToc', toc)
              // console.log('toc', toc)

              this.$emit('update:currentTocIndex', 0)
              // console.log('MdVditor 需要激活的目录', 0)

              this.headingElements = headingElements

              window.addEventListener('scroll', this.handleScroll)
            }

            this.$emit('after', 'preview')
          }
        }
      )
    },
    handleScroll () {
      const headingElements = this.headingElements
      // 发生滚动重置计时器
      clearTimeout(this.scrollingOptions.scrollEndTimer)
      this.scrollingOptions.currentScrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
      // 100ms后调用函数判断t1,t2是否一致
      this.scrollingOptions.scrollEndTimer = setTimeout(() => {
        const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
        if (this.scrollingOptions.currentScrollTop === scrollTop) {
          // console.log("滚动结束")
          for (let i = 0; i < headingElements.length; i++) {
            // 找到 屏幕上 离offset最近的 元素
            const offset = this.headingsOffsetTop
            const element = headingElements[i]
            const currentTop = element.getBoundingClientRect().top
            if (currentTop >= offset) {
              if (currentTop === offset) {
                this.$emit('update:currentTocIndex', i)
                // console.log('MdVditor 滑动后需要激活的目录', i)
              } else if (i > 1) {
                const preElement = headingElements[i - 1]
                const preTop = preElement.getBoundingClientRect().headingsOffsetTop
                const distance = currentTop - preTop
                if (distance < offset) {
                  // 间距过小，直接设置当前的位置
                  this.$emit('update:currentTocIndex', i)
                  // console.log('MdVditor 滑动后需要激活的目录 distance < offset', i)
                } else if (currentTop - offset < 3) {
                  // 当前紧靠offset位置
                  this.$emit('update:currentTocIndex', i)
                  // console.log('MdVditor 滑动后需要激活的目录 currentTop - offset < 3', i)
                } else {
                  // 上一部分还没完全遮住
                  this.$emit('update:currentTocIndex', i - 1)
                  // console.log('MdVditor 滑动后需要激活的目录', i - 1)
                }
              } else {
                this.$emit('update:currentTocIndex', 0)
                // console.log('MdVditor 滑动后需要激活的目录', 0)
              }
              break
            }
          }
        }
      }, 100)
    }
  }
}
</script>

<style>
.vditor-img {
  z-index: 100;
  /*margin: 80px;*/
}

.vditor-toolbar--hide {
  display: none;
}
</style>
