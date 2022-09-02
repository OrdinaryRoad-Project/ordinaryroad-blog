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
      titleKey: 'dashboardMenuTitles.dashboard.article.boxTitle',
      icon: 'mdi-file-document-multiple',
      children: [
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.box.draft',
          to: '/dashboard/article/box/DRAFT',
          children: []
        },
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.box.publish',
          to: '/dashboard/article/box/PUBLISH',
          children: []
        },
        {
          titleKey: 'dashboardMenuTitles.dashboard.article.box.trash',
          to: '/dashboard/article/box/TRASH',
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
      to: '/dashboard/article/box',
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
      titleKey: 'logout',
      icon: 'mdi-logout'
    }
  ],
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
