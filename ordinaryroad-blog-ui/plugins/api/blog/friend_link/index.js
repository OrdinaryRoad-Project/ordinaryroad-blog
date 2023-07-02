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
    create ({ name, description, url, logo, email, snapshotUrl }) {
      const data = { name, description, url, logo, email, snapshotUrl }
      return $axios({
        url: '/blog/friend_link/create',
        method: 'post',
        data
      })
    },
    delete (id) {
      return $axios({
        url: `/blog/friend_link/delete/${id}`,
        method: 'delete'
      })
    },
    update ({ uuid, name, description, url, logo, email, snapshotUrl }) {
      const data = { uuid, name, description, url, logo, email, snapshotUrl }
      return $axios({
        url: `/blog/friend_link/update/${uuid}`,
        method: 'post',
        data
      })
    },
    pageInfo: (page, size, sortBy, sortDesc) => {
      return $axios({
        url: `/blog/friend_link/page/info/${page}/${size}?${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    page: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/friend_link/page/${page}/${size}?${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    apply ({ name, description, url, logo, email }) {
      const data = { name, description, url, logo, email }
      return $axios({
        url: '/blog/friend_link/apply',
        method: 'post',
        data
      })
    },
    approved (id) {
      return $axios({
        url: `/blog/friend_link/approved/${id}`,
        method: 'put'
      })
    },
    disapproved (id, reason) {
      const data = { reason }
      return $axios({
        url: `/blog/friend_link/disapproved/${id}?${urlEncode(data)}`,
        method: 'put'
      })
    },
    enabled (id, enabled, notice) {
      const data = { enabled, notice }
      return $axios({
        url: `/blog/friend_link/enabled/${id}?${urlEncode(data)}`,
        method: 'put'
      })
    },
    findAllStatus () {
      return $axios({
        url: '/blog/friend_link/all/status',
        method: 'get'
      })
    }
  }
}
