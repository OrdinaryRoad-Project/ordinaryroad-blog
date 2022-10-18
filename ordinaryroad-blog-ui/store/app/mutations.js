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

import { setSelectedThemeOption } from 'static/js/utils/cookie/vuex/app'
import colors from 'vuetify/es5/util/colors'

export default {
  SET_SEARCH_INPUT_HOT_KEY_ENABLED: (state, value) => (state.searchInputHotKeyEnabled = value),
  SET_IMAGE: (state, value) => (state.image = value),
  SET_TITLE_KEY: (state, value) => (state.titleKey = value),
  SET_SELECTED_THEME_OPTION (state, {
    value,
    $vuetify
  }) {
    state.selectedThemeOption = value
    setSelectedThemeOption(value)
  },
  UPDATE_THEME (state, { value, $vuetify }) {
    switch (value) {
      case 0:
        $vuetify.theme.themes.light = {
          primary: '#1976D2',
          secondary: '#424242',
          accent: '#82B1FF',
          error: '#FF5252',
          info: '#2196F3',
          success: '#4CAF50',
          warning: '#FFC107'
        }
        $vuetify.theme.dark = false
        break
      case 1:
        $vuetify.theme.themes.dark = {
          primary: colors.blue.darken2,
          accent: colors.grey.darken3,
          secondary: colors.amber.darken3,
          info: colors.teal.lighten1,
          warning: colors.amber.base,
          error: colors.deepOrange.accent4,
          success: colors.green.accent3
        }
        $vuetify.theme.dark = true
        break
      case 2:
        $vuetify.theme.themes.light = {
          primary: colors.pink.base,
          secondary: colors.lime.base,
          accent: colors.cyan.base,
          error: colors.red.base,
          warning: colors.yellow.base,
          info: colors.blue.base,
          success: colors.green.base
        }
        $vuetify.theme.dark = false
        break
      case 3:
        $vuetify.theme.themes.dark = {
          primary: colors.pink.base,
          secondary: colors.lime.base,
          accent: colors.cyan.base,
          error: colors.red.base,
          warning: colors.yellow.base,
          info: colors.blue.base,
          success: colors.green.base
        }
        $vuetify.theme.dark = true
        break
      default:
    }
  },
  SET_DASHBOARD_DRAWER_MODEL: (state, value) => {
    state.dashboardDrawerModel = value
  },
  TOGGLE_DASHBOARD_DRAWER_MODEL: (state) => {
    state.dashboardDrawerModel = !state.dashboardDrawerModel
  },
  SET_RIGHT_DRAWER_MODEL: (state, value) => {
    state.rightDrawerModel = value
  },
  TOGGLE_RIGHT_DRAWER_MODEL: (state) => {
    state.rightDrawerModel = !state.rightDrawerModel
  },
  UPDATE_ACCESSIBLE_USER_MENU_ITEMS: (state, $access) => {
    const menuItems = state.userMenuItems
    const accessibleUserMenuItems = []
    menuItems.forEach((item) => {
      if (!item.meta || !item.meta.roles || !item.meta.roles.or || $access.hasRolesOr(item.meta.roles.or)) {
        accessibleUserMenuItems.push(item)
      }
    })
    state.accessibleUserMenuItems = accessibleUserMenuItems
  },
  UPDATE_ACCESSIBLE_DASHBOARD_MENU_ITEMS: (state, $access) => {
    const dashboardMenuItems = state.dashboardMenuItems
    const accessibleDashboardMenuItems = []
    dashboardMenuItems.forEach((item) => {
      if (item.children && item.children.length > 0) {
        const dashboardMenuItems1 = []
        item.children.forEach((item1) => {
          if (item1.children && item1.children.length > 0) {
            const dashboardMenuItems2 = []
            item1.children.forEach((item2) => {
              if (!item2.meta || !item2.meta.roles || !item2.meta.roles.or || $access.hasRolesOr(item2.meta.roles.or)) {
                dashboardMenuItems2.push(item2)
              }
            })
            if (dashboardMenuItems2.length > 0) {
              item1.children = dashboardMenuItems2
              dashboardMenuItems1.push(item1)
            }
          } else if (!item1.meta || !item1.meta.roles || !item1.meta.roles.or || $access.hasRolesOr(item1.meta.roles.or)) {
            dashboardMenuItems1.push(item1)
          }
        })
        if (dashboardMenuItems1.length > 0) {
          item.children = dashboardMenuItems1
          accessibleDashboardMenuItems.push(item)
        }
      } else if (!item.meta || !item.meta.roles || !item.meta.roles.or || $access.hasRolesOr(item.meta.roles.or)) {
        accessibleDashboardMenuItems.push(item)
      }
    })
    state.accessibleDashboardMenuItems = accessibleDashboardMenuItems
  }
}
