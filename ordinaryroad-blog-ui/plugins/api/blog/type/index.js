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

import { urlEncode } from '~/plugins/ordinaryroad/utils'

let $axios = null

export default {
  initAxios (axios) {
    $axios = $axios || axios
  },
  apis: {
    create: ({ name }) => {
      const data = { name }
      return $axios({
        url: '/blog/type/create',
        method: 'post',
        data
      })
    },
    deleteOwn: (id) => {
      return $axios({
        url: `/blog/type/delete/own/${id}`,
        method: 'delete'
      })
    },
    restoreOwn: (id) => {
      return $axios({
        url: `/blog/type/restore/own/${id}`,
        method: 'post'
      })
    },
    updateOwn: ({ uuid, name }) => {
      const data = { uuid, name }
      return $axios({
        url: '/blog/type/update/own',
        method: 'post',
        data
      })
    },
    findAllOwn () {
      return $axios({
        url: '/blog/type/find/all/own',
        method: 'get'
      })
    },
    pageOwn: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/type/page/own/${page}/${size}?1=1${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    pageInfo: (page, size, { sortBy, sortDesc, createBy }) => {
      const searchParams = { sortBy, sortDesc, createBy }
      return $axios({
        url: `/blog/type/page/info/${page}/${size}?1=1${urlEncode(searchParams)}`,
        method: 'get'
      })
    },
    getTopN ({ n = 10, userId }) {
      const params = { n, userId }
      return $axios({
        url: `/blog/type/top?1=1${urlEncode(params)}`,
        method: 'get'
      })
    },
    count: (userId = '') => {
      return $axios({
        url: `/blog/type/count?userId=${userId}`,
        method: 'get'
      })
    },
    findById: (id = '') => {
      return $axios({
        url: `/blog/type/${id}`,
        method: 'get'
      })
    }
  }
}
