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

import { urlEncode } from 'ordinaryroad-vuetify/src/utils'

let $axios = null

export default {
  initAxios (axios) {
    $axios = $axios || axios
  },
  apis: {
    create: ({ name }) => {
      const data = { name }
      return $axios({
        url: '/blog/tag/create',
        method: 'post',
        data
      })
    },
    page: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/tag/page/${page}/${size}?${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    getTopN (params = { n: 10, userId: null }) {
      return $axios({
        url: `/blog/tag/top?${urlEncode(params)}`,
        method: 'get'
      })
    }
  }
}
