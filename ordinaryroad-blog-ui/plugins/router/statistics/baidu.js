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

export function baiduCountAllCode (code) {
  (function () {
    // 会叠加 需要 每次执行前，先移除上次插入的代码
    document.getElementById('g_baidu') && document.getElementById('g_baidu').remove()
    const hm = document.createElement('script')
    hm.src = 'https://hm.baidu.com/hm.js?' + code
    hm.id = 'g_baidu'
    const s = document.getElementsByTagName('script')[0]
    s.parentNode.insertBefore(hm, s)
  })()
}

export function baiduCountCode (code) {
  (function () {
    // 会叠加 需要 每次执行前，先移除上次插入的代码
    document.getElementById('w_baidu') && document.getElementById('w_baidu').remove()
    const hm = document.createElement('script')
    hm.src = 'https://hm.baidu.com/hm.js?' + code
    hm.id = 'w_baidu'
    const s = document.getElementsByTagName('script')[0]
    s.parentNode.insertBefore(hm, s)
  })()
}
