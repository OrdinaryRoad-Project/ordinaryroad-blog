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

export default {
  setSearchInputHotKeyEnabled ({ commit }, image) {
    commit('SET_SEARCH_INPUT_HOT_KEY_ENABLED', image)
  },
  setImage ({ commit }, image) {
    commit('SET_IMAGE', image)
  },
  setTitleKey ({ commit }, value) {
    commit('SET_TITLE_KEY', value)
  },
  setSelectedThemeOption ({ commit }, { value, $vuetify }) {
    commit('SET_SELECTED_THEME_OPTION', { value, $vuetify })
    commit('UPDATE_THEME', { value, $vuetify })
  },
  updateTheme ({ commit }, { value, $vuetify }) {
    commit('UPDATE_THEME', { value, $vuetify })
  },
  setMenuItems ({ commit }, menuItems) {
    commit('SET_MENU_ITEMS', menuItems)
  },
  setDashboardDrawerModel ({ commit }, value) {
    commit('SET_DASHBOARD_DRAWER_MODEL', value)
  },
  toggleDashboardDrawerModel ({ commit }) {
    commit('TOGGLE_DASHBOARD_DRAWER_MODEL')
  },
  setRightDrawerModel ({ commit }, value) {
    commit('SET_RIGHT_DRAWER_MODEL', value)
  },
  toggleRightDrawerModel ({ commit }) {
    commit('TOGGLE_RIGHT_DRAWER_MODEL')
  },
  updateSystemPrefersColorScheme ({ commit }, value) {
    commit('UPDATE_SYSTEM_PREFERS_COLOR_SCHEME', value)
  }
}
