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

import { SELECTED_THEME_OPTION_KEY } from 'static/js/utils/cookie/vuex/app'
import { OAUTH2_STATE_KEY, TOKEN_INFO_KEY } from 'static/js/utils/cookie/vuex/user'

function parseCookieString (string) {
  const cookie = {}
  const cookies = string.split('; ')
  // 遍历Cookie，取得需要的值
  cookies.forEach((e) => {
    const split = e.split('=')
    cookie[split[0]] = split[1]
  })
  return cookie
}

function getFromCookie (string, key) {
  return parseCookieString(string)[key]
}

// function getBooleanFromCookie (string, key, defaultValue) {
//   const fromCookie = getFromCookie(string, key)
//   return fromCookie ? fromCookie === 'true' : defaultValue
// }

function getNumberFromCookie (string, key, defaultValue) {
  const fromCookie = getFromCookie(string, key)
  return fromCookie ? Number(fromCookie) : defaultValue
}

function getStringFromCookie (string, key, defaultValue) {
  const fromCookie = getFromCookie(string, key)
  return fromCookie ? String(fromCookie) : defaultValue
}

function getObjectFromCookie (string, key, defaultValue) {
  const fromCookie = getFromCookie(string, key)
  return fromCookie ? JSON.parse(decodeURIComponent(fromCookie)) : defaultValue
}

export const actions = {
  async nuxtServerInit ({ commit }, { $vuetify, $apisServer, $access, req, app }) {
    const store = app.store
    // 初始化，可以获取初始值
    if (typeof req !== 'undefined' && req.headers && req.headers.cookie) {
      const cookieString = req.headers.cookie
      commit('app/SET_SELECTED_THEME_OPTION', {
        value: getNumberFromCookie(cookieString, SELECTED_THEME_OPTION_KEY, store.getters['app/getSelectedThemeOption']),
        $vuetify
      })
      commit('user/SET_OAUTH2_STATE', getStringFromCookie(cookieString, OAUTH2_STATE_KEY, store.getters['user/getOAuth2State']))

      const tokenInfo = getObjectFromCookie(cookieString, TOKEN_INFO_KEY, store.getters['user/getTokenInfo'])
      if (tokenInfo) {
        try {
          // TODO userInfo
          // const { data } = await $apisServer.blog.userInfo(tokenInfo.satoken)

          const { data } = await new Promise((resolve, reject) => {
            resolve({ data: { username: 'TEST_USERNAME_NUXT_INIT' } })
          })
          commit('user/SET_TOKEN_INFO', tokenInfo)
          commit('user/SET_USER_INFO', data)
        } catch {
          // Token无效或其他异常，不做任何操作
        }
      }
    }
  }
}
