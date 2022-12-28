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

export default ({
  app,
  store,
  route
}, inject) => {
  const isLogged = () => {
    const userInfo = store.getters['user/getUserInfo']
    return userInfo != null
  }
  const checkLogin = () => {
    const logged = isLogged()
    if (!logged) {
      app.$dialog({
        persistent: false,
        content: app.i18n.$t('pleaseLogin'),
        confirmText: app.i18n.$t('login')
      }).then(({ isConfirm }) => {
        if (isConfirm) {
          app.router.push({
            path: '/user/login',
            query: { redirect: route.fullPath }
          })
        }
      })
    }
    return logged
  }
  const hasRolesOr = (roles) => {
    const logged = isLogged()
    if (!logged) {
      return false
    }
    if (roles.length === 0) {
      return true
    }
    let hasRole = false
    if (logged) {
      const userRoleCodes = store.getters['user/getRoleCodes']
      for (let i = 0; i < roles.length; i++) {
        const role = roles[i]
        if (userRoleCodes.includes(role)) {
          hasRole = true
          break
        }
      }
    }
    return hasRole
  }
  const hasAuditorRole = () => {
    const logged = isLogged()
    if (!logged) {
      return false
    }
    let hasRole = false
    if (logged) {
      const userRoleCodes = store.getters['user/getRoleCodes']
      const role = 'AUDITOR'
      if (userRoleCodes.includes(role)) {
        hasRole = true
      }
    }
    return hasRole
  }
  inject('access', {
    has: (permissionCode) => {
      // if (permissionCode === 'blog:type:create') {
      // return hasRolesOr(['DEVELOPER', 'ADMIN'])
      // }
      // const userInfo = store.getters['user/getUserInfo']
      // console.log('hasAccess', permissionCode, userInfo)
      return true
      // let permissions = []
      // if (userInfo && userInfo.permissions) {
      //   permissions = userInfo.permissions
      // }
      // return permissions.includes(permissionCode)
    },
    isLogged,
    checkLogin,
    /**
     * 判断当前用户是否拥有角色中的一个
     *
     * @param roles {String[]} 角色列表
     */
    hasRolesOr,
    /**
     * 判断是否拥有审核权限
     */
    hasAuditorRole
  })
}
