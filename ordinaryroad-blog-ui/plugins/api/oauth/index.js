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

import { urlEncode } from '@/plugins/ordinaryroad/utils'

let $axios = null
let $config = null

export default {
  initAxios (axios, config) {
    $axios = $axios || axios
    $config = $config || config
  },
  apis: {
    token: (provider, code) => {
      const data = {
        client_id: $config.OAUTH2[provider].CLIENT_ID,
        client_secret: $config.OAUTH2[provider].CLIENT_SECRET,
        code
      }
      const accessTokenEndpoint = `${$config.OAUTH2[provider].ACCESS_TOKEN_ENDPOINT}`
      if (provider === 'ordinaryroad') {
        return $axios({
          url: `${accessTokenEndpoint}?grant_type=authorization_code${urlEncode(data)}`,
          method: 'get'
        })
      } else if (provider === 'github') {
        return $axios({
          url: `${accessTokenEndpoint}?1=1${urlEncode(data)}`,
          headers: {
            Accept: 'application/json'
          },
          method: 'post'
        })
      } else {
        return $axios({
          url: `${accessTokenEndpoint}&redirect_uri=${$config.OAUTH2.REDIRECT_URI}`,
          method: 'post',
          data
        })
      }
    }
  }
}
