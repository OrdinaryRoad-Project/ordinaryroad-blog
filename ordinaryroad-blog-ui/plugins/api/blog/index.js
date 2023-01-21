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

import oauth2Apis from './oauth2'
import oauthUserApis from './oauth_user'
import articleApis from './article'
import commentApis from './comment'
import userApis from './user'
import typeApis from './type'
import tagApis from './tag'
import logApis from './log'
import roleApis from './role'

let $axios = null
let $config = null

export default {
  initAxios (axios, config) {
    $axios = $axios || axios
    $config = $config || config
    oauth2Apis.initAxios(axios)
    oauthUserApis.initAxios(axios)
    articleApis.initAxios(axios)
    commentApis.initAxios(axios)
    userApis.initAxios(axios)
    typeApis.initAxios(axios)
    tagApis.initAxios(axios)
    logApis.initAxios(axios)
    roleApis.initAxios(axios)
  },
  apis: {
    oauth2: oauth2Apis.apis,
    oauth_user: oauthUserApis.apis,
    article: articleApis.apis,
    comment: commentApis.apis,
    user: userApis.apis,
    type: typeApis.apis,
    tag: tagApis.apis,
    log: logApis.apis,
    role: roleApis.apis,
    /**
     * 获取文件全路径
     * @param url
     * @param defaultUrl 默认url
     * @returns {string|*}
     */
    getFileUrl: (url, defaultUrl = '') => {
      if (!url || url === '') {
        return defaultUrl
      } else if (url.startsWith('/ordinaryroad-')) {
        return `${$config.FILE_DOWNLOAD_BASE_URL}${url}`
      } else {
        return url
      }
    },
    logout: () => {
      return $axios.get('/blog/common/logout')
    },
    upload: (file) => {
      const data = new FormData()
      data.append('file', file)
      return $axios({
        url: '/blog/common/upload',
        method: 'post',
        data
      })
    }
  }
}
