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
    create ({ roleName, roleCode }) {
      const data = { roleName, roleCode }
      return $axios({
        url: `/blog/role/create?${urlEncode(data)}`,
        method: 'post'
      })
    },
    update ({ uuid, roleName, roleCode }) {
      const data = { roleName, roleCode }
      return $axios({
        url: `/blog/role/${uuid}?${urlEncode(data)}`,
        method: 'put'
      })
    },
    updateEnabled (id, enabled) {
      return $axios({
        url: `/blog/role/${id}/enabled?${urlEncode(enabled, 'enabled')}`,
        method: 'put'
      })
    },
    updateUsers ({ uuid, userUuids }) {
      const data = { userUuids }
      return $axios({
        url: `/blog/role/${uuid}/users`,
        method: 'put',
        data
      })
    },
    page: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/role/page/${page}/${size}?${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    findAllByUserUuid: (id) => {
      return $axios({
        url: `/blog/role/user/${id}`,
        method: 'get'
      })
    },
    findAll ({ roleCode }) {
      const data = { roleCode }
      return $axios({
        url: `/blog/role/all?${urlEncode(data)}`,
        method: 'get'
      })
    }
  }
}
