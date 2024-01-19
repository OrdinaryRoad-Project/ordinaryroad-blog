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

// Create a custom axios instance
import errorCode from '@/plugins/axios/errorCode'

export default function (context, inject) {
  // $dialog和$snackbar必须通过context.$xxx方式调用
  const {
    $axios,
    $dayjs,
    route,
    app
  } = context
  const {
    store,
    router
  } = app
  // 请求拦截器
  $axios.onRequest((config) => {
    // 将获取到token加入到请求头中
    const tokenValue = store.getters['user/getTokenValue']
    // 兼容userInfo方法
    if (tokenValue && config.headers.common) {
      config.headers.common['or-blog-token'] = tokenValue
    }
  })

  // 响应拦截器
  $axios.interceptors.response.use((res) => {
    // 未设置状态码则默认成功状态
    const code = res.status
    if (code === 200) {
      return res.data
    } else if (code === 204) {
      return null
    } else {
      console.log('code !== 2xx', res)
      // 获取错误信息
      const msg = errorCode[code] || res.statusText || errorCode.default
      if (code === 2001) {
        store.commit('user/REMOVE_TOKEN_INFO')
        if (process.client) {
          if (route.path !== '/') {
            context.$dialog({
              persistent: true,
              title: this.$t('systemHint'),
              content: '登录状态已过期，您可以继续留在该页面，或者重新登录。',
              confirmText: '重新登录'
            }).then((dialog) => {
              if (dialog.isConfirm) {
                // 跳转登录页面
                router.push({
                  path: '/user/login',
                  query: { redirect: route.fullPath }
                })
              }
            })
          }
        }
      } else {
        process.client && context.$snackbar.error(msg)
      }
      return Promise.reject(msg)
    }
  },
  (error) => {
    console.log('error', error)
    let message
    if (typeof error === 'string') {
      message = error
    } else {
      const data = error.response.data
      if (typeof data === 'string') {
        message = data
      } else {
        console.error(data)
        message = data.message
        if (!message) {
          message = JSON.stringify(data.violations)
        }
        if (message === '此账号已被封禁') {
          message += `，解封时间：${$dayjs().add(data.disableTime, 'second').format()}`
        } else if (message.startsWith('Token已过期')) {
          // Token无效
          console.log('Token无效，删除Token')
          store.commit('user/REMOVE_TOKEN_INFO')
        }
      }
    }
    if (!message) {
      message = '失败'
    }
    if (message === 'Network Error') {
      message = '后端接口连接异常'
    } else if (message.includes('timeout')) {
      message = '系统接口请求超时'
    } else if (message.includes('Request failed with status code')) {
      message = '系统接口' + message.slice(message.length - 3) + '异常'
    }
    process.client && context.$snackbar.error(message)
    return Promise.reject(message)
  })
}
