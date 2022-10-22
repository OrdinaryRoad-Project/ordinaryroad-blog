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

import blogApis from './blog/index'

export default function ({
  $axios,
  $config,
  app
}, inject) {
  // 初始化axios
  blogApis.initAxios($axios, $config)
  const $apis = {
    blog: blogApis.apis,
    statusColor (item) {
      if (['OK', 'NO_CONTENT'].includes(item.status)) {
        return 'success'
      } else if (['BAD_REQUEST', 'INTERNAL_SERVER_ERROR', 'REQUEST_TIMEOUT'].includes(item.status)) {
        return 'error'
      } else if (['UNAUTHORIZED', 'FORBIDDEN', 'METHOD_NOT_ALLOWED', 'NOT_FOUND',
        'REQUEST_ENTITY_TOO_LARGE', 'REQUEST_URI_TOO_LONG', 'UNSUPPORTED_MEDIA_TYPE'].includes(item.status)) {
        return 'warning'
      } else {
        return null
      }
    }
  }
  // $apis
  inject('apis', $apis)
}
