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

export default ({ app, store }) => {
  const { router, $access } = app
  // 每次路由变更前判断是否有访问权限
  router.beforeEach((to, from, next) => {
    const dashboardMenuItems = store.getters['app/getDashboardMenuItems']
    const userMenuItems = store.getters['app/getUserMenuItems']
    const items = dashboardMenuItems.concat(userMenuItems)

    let found = false
    let item = items.shift()
    while (item) {
      if (!found && ((item.to && item.to === to.path) ||
        (item.name && item.name === to.name))) {
        found = true
        break
      }
      if (item.children && item.children.length > 0) {
        items.unshift(...item.children)
      }
      item = items.shift()
    }
    if (found && item && item.meta && item.meta.roles && item.meta.roles.or) {
      // 判断权限
      if (!$access.hasRolesOr(item.meta.roles.or)) {
        if (from.path === '/user/login') {
          next('/')
        } else {
          next(false)
        }
      } else {
        next()
      }
    } else {
      next()
    }
  })
}
