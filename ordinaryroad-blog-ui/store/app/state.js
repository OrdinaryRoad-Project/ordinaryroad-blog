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

export default () => ({
  /**
   * 正在输入时禁用搜索快捷键
   */
  searchInputHotKeyEnabled: true,
  dashboardDrawerModel: true,
  rightDrawerModel: false,
  selectedThemeOption: 0,
  dashboardMenuItems: [
    {
      to: '/dashboard',
      titleKey: 'dashboardMenuTitles.dashboard.title',
      icon: 'mdi-view-dashboard',
      children: []
    },
    {
      to: '/dashboard/user/profile',
      titleKey: 'dashboardMenuTitles.dashboard.userProfileTitle',
      icon: 'mdi-account',
      children: []
    },
    {
      to: '/dashboard/article',
      titleKey: 'dashboardMenuTitles.dashboard.article.title',
      icon: 'mdi-file-document-multiple',
      children: [
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.status.draft',
          to: '/dashboard/article/status/DRAFT',
          children: []
        },
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.status.publish',
          to: '/dashboard/article/status/PUBLISH',
          children: []
        },
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.status.trash',
          to: '/dashboard/article/status/TRASH',
          children: []
        },
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.liked',
          to: '/dashboard/article/liked',
          children: []
        },
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.browsed',
          to: '/dashboard/article/browsed',
          children: []
        }
      ]
    },
    {
      to: '/dashboard/type',
      titleKey: 'dashboardMenuTitles.dashboard.type.title',
      icon: 'mdi-view-list',
      children: []
    },
    {
      to: '/dashboard/tag',
      titleKey: 'dashboardMenuTitles.dashboard.tag.title',
      icon: 'mdi-tag-multiple',
      children: []
    },
    {
      to: '/dashboard/system/log',
      titleKey: 'dashboardMenuTitles.dashboard.systemTitle',
      icon: 'mdi-cog',
      children: [
        {
          to: '/dashboard/system/log',
          titleKey: 'dashboardMenuTitles.dashboard.system.log',
          icon: 'mdi-text-box-multiple',
          children: [],
          meta: {
            roles: {
              or: ['ADMIN', 'DEVELOPER']
            }
          }
        }
      ],
      meta: {
        roles: {
          or: ['ADMIN', 'DEVELOPER']
        }
      }
    }
  ],
  userMenuItems: [
    {
      titleKey: 'userMenuTitles.space',
      icon: 'mdi-earth'
    },
    {
      titleKey: 'userMenuTitles.dashboard',
      to: '/dashboard',
      icon: 'mdi-view-dashboard'
    },
    {
      titleKey: 'userMenuTitles.profile',
      to: '/dashboard/user/profile',
      icon: 'mdi-account'
    },
    {
      titleKey: 'userMenuTitles.article',
      to: '/dashboard/article/status',
      icon: 'mdi-file-document-multiple'
    },
    {
      titleKey: 'userMenuTitles.type',
      to: '/dashboard/type',
      icon: 'mdi-view-list'
    },
    {
      titleKey: 'userMenuTitles.tag',
      to: '/dashboard/tag',
      icon: 'mdi-tag-multiple'
    },
    {
      titleKey: 'userMenuTitles.log',
      to: '/dashboard/system/log',
      icon: 'mdi-text-box-multiple',
      meta: {
        roles: {
          or: ['ADMIN', 'DEVELOPER']
        }
      }
    },
    {
      titleKey: 'logout',
      icon: 'mdi-logout'
    }
  ],
  accessibleDashboardMenuItems: [],
  accessibleUserMenuItems: [],
  titleKey: null,
  image: 'http://rekryt.ru/files/sidebar-2.32103624.jpg',
  themeOptions: [
    {
      titleKey: 'themeTitles.light',
      icon: 'mdi-white-balance-sunny'
    },
    {
      titleKey: 'themeTitles.dark',
      icon: 'mdi-weather-night'
    },
    {
      titleKey: 'themeTitles.hunk',
      icon: 'mdi-arm-flex'
    },
    {
      titleKey: 'themeTitles.system',
      // icon: 'mdi-desktop-tower-monitor'
      icon: 'mdi-arm-flex-outline'
    }
  ]
})
