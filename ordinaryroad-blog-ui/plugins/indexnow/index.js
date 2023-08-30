/*
 * MIT License
 *
 * Copyright (c) 2021 苗锦洲
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import Vue from 'vue'

export default function ({
  $axios,
  $config,
  app
}, inject) {
  const host = $config.DOMAIN.replace('https://', '')

  // 初始化时过滤掉未启用的
  const searchEnginesEnabled = $config.INDEX_NOW.SEARCH_ENGINES.filter((value) => {
    return value.enabled
  })

  Vue.prototype.$indexnow = {
    updateUrls: async (urlList) => {
      for (const searchEngine of searchEnginesEnabled) {
        await $axios({
          method: 'post',
          url: `/indexnow/${searchEngine.name}`,
          data: {
            host,
            key: $config.INDEX_NOW.KEY,
            // 支持相对路径，自动添加网站DOMAIN
            urlList: urlList.map((url) => {
              if (url.startsWith('/')) {
                return $config.DOMAIN + url
              } else {
                return url
              }
            })
          }
        })
      }
    },
    updateArticle (article) {
      return this.updateUrls([`/${article.creatorUid || article.user.uid}/article/${article.firstId}`])
    }
  }
  inject('indexnow', Vue.prototype.$indexnow)
}
