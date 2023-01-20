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

export default function ({
  $axios,
  app
}, inject) {
  // $apisServer
  inject('apisServer', {
    blog: {
      userInfo: (token) => {
        return $axios({
          url: '/blog/common/user_info',
          method: 'get',
          // 在server端调用的方法必须手动设置header，因为获取不到client的cookie
          headers: { 'or-blog-token': token }
        })
      },
      user: {
        findByUid: (token, { uid }) => {
          return $axios({
            url: `/blog/user/uid/${uid}`,
            method: 'get',
            headers: { 'or-blog-token': token }
          })
        }
      },
      role: {
        findByUniqueColumn: (token, { roleCode = null, roleName = null }) => {
          const data = { roleCode, roleName }
          return $axios({
            url: `/blog/role/unique?${urlEncode(data)}`,
            method: 'get',
            headers: { 'or-blog-token': token }
          })
        }
      }
    }
  })
}
