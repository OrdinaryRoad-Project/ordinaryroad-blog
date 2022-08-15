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

import { removeOAuth2State, removeTokenInfo, setOAuth2State, setTokenInfo } from 'static/js/utils/cookie/vuex/user'

export default {
  REMOVE_OAUTH2_STATE (state) {
    state.oAuth2State = null
    removeOAuth2State()
  },
  SET_OAUTH2_STATE (state, oAuth2State) {
    state.oAuth2State = oAuth2State
    setOAuth2State(oAuth2State)
  },
  REMOVE_TOKEN_INFO (state) {
    state.tokenInfo = null
    removeTokenInfo()
  },
  SET_TOKEN_INFO (state, tokenInfo) {
    state.tokenInfo = tokenInfo
    setTokenInfo({ value: tokenInfo })
  },
  REMOVE_USER_INFO (state) {
    state.userInfo = null
  },
  SET_USER_INFO (state, userInfo) {
    state.userInfo = userInfo
  },
  UPDATE_USER_INFO_AVATAR (state, avatar) {
    state.userInfo.user.avatar = avatar
  },
  UPDATE_USER_INFO_USERNAME (state, username) {
    state.userInfo.user.username = username
  },
  UPDATE_USER_INFO_EMAIL (state, email) {
    state.userInfo.user.email = email
  }
}
