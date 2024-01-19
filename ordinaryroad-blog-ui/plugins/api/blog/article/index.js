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

/*
 http://t.zoukankan.com/taohuaya-p-10181655.html

 我们想通过axios 提交FromData,需要用到qs库（这个库不用npm下载，直接引用即可），
 qs是一个url参数转化（parse和stringify）的js库。
 qs.stringify() 和JSON.stringify() 作用相似，都是序列化，但他们完全不是一个东西，
 假设我要提交的数据是：  let a = {name:'hehe',age:10};
 qs.stringify(a)序列化结果为  name=hehe&age=10
 而JSON.stringify序列化结果为： {"name":"hehe","age":10}  转成字符串

 使用 application/x-www-form-urlencoded format 使用这种请求头 发送法数据就是 FromData格式
 默认情况下，axios将JavaScript对象序列化为JSON。 要以application / x-www-form-urlencoded格式发送数据，您可以使用以下选项之一。
 这里我们是采用的ES6 语法 引入 的qs模块
 */
import { urlEncode } from 'ordinaryroad-vuetify/src/utils'

let $axios = null

export default {
  initAxios (axios) {
    $axios = $axios || axios
  },
  apis: {
    saveDraft: (article) => {
      return $axios({
        url: '/blog/article/draft',
        method: 'post',
        data: article
      })
    },
    publish: (article) => {
      return $axios({
        url: '/blog/article/publish',
        method: 'post',
        data: article
      })
    },
    findOffendById: (token, id = '') => {
      return $axios({
        url: `/blog/article/offend/${id}`,
        method: 'get',
        headers: { 'or-blog-token': token }
      })
    },
    findUnderReviewById: (token, id = '') => {
      return $axios({
        url: `/blog/article/under_review/${id}`,
        method: 'get',
        headers: { 'or-blog-token': token }
      })
    },
    findPublishById: (token, id) => {
      return $axios({
        url: `/blog/article/publish/${id}`,
        method: 'get',
        headers: { 'or-blog-token': token }
      })
    },
    findOwnById: (id) => {
      return $axios({
        url: `/blog/article/own/${id}`,
        method: 'get'
      })
    },
    findOwnWritingById: (id = '') => {
      return $axios({
        url: `/blog/article/own/writing?id=${id}`,
        method: 'get'
      })
    },
    pagePublish: (page, {
      createBy,
      tagName = '',
      title = '',
      summary = '',
      content = '',
      typeId = '',
      sortBy = ['createdTime'],
      sortDesc = [true]
    }) => {
      const data = { createBy, tagName, title, summary, content, typeId, sortBy, sortDesc }
      return $axios({
        url: `/blog/article/page/publish/${page}/20?${urlEncode(data)}`,
        method: 'get'
      })
    },
    searchPublish: (page, {
      createBy,
      tagName = '',
      title = '',
      summary = '',
      content = '',
      typeId = '',
      sortBy = ['createdTime'],
      sortDesc = [true]
    }) => {
      const data = { createBy, tagName, title, summary, content, typeId, sortBy, sortDesc }
      return $axios({
        url: `/blog/article/search/publish/${page}/20?${urlEncode(data)}`,
        method: 'get'
      })
    },
    page: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/article/page/${page}/${size}?${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    pageOwnLiked: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/article/page/own/liked/${page}/${size}?${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    pageOwnBrowsed: (page, size, sortBy, sortDesc, searchParams) => {
      return $axios({
        url: `/blog/article/page/own/browsed/${page}/${size}?${urlEncode(searchParams)}${urlEncode(sortBy, 'sortBy')}${urlEncode(sortDesc, 'sortDesc')}`,
        method: 'get'
      })
    },
    moveToTrash: (id) => {
      return $axios({
        url: `/blog/article/move_to_trash/${id}`,
        method: 'post'
      })
    },
    recoverFromTrash: (id) => {
      return $axios({
        url: `/blog/article/recover_from_trash/${id}`,
        method: 'post'
      })
    },
    startAuditing: (id) => {
      return $axios({
        url: `/blog/article/start_auditing/${id}`,
        method: 'post'
      })
    },
    auditApproved: (id) => {
      return $axios({
        url: `/blog/article/audit_approved/${id}`,
        method: 'post'
      })
    },
    auditFailed: (id, reason) => {
      return $axios({
        url: `/blog/article/audit_failed/${id}`,
        method: 'post',
        data: {
          reason
        }
      })
    },
    articleViolation: (id, reason) => {
      return $axios({
        url: `/blog/article/article_violation/${id}`,
        method: 'post',
        data: {
          reason
        }
      })
    },
    articleAppeal: (id, reason) => {
      return $axios({
        url: `/blog/article/article_appeal/${id}`,
        method: 'post',
        data: {
          reason
        }
      })
    },
    count: (userId = '') => {
      return $axios({
        url: `/blog/article/count?userId=${userId}`,
        method: 'get'
      })
    },
    countBrowsed: (userId = '') => {
      return $axios({
        url: `/blog/article/count/browsed?userId=${userId}`,
        method: 'get'
      })
    },
    countLiked: (userId = '') => {
      return $axios({
        url: `/blog/article/count/liked?userId=${userId}`,
        method: 'get'
      })
    },
    getPreAndNextArticle (id) {
      return $axios({
        url: `/blog/article/pre_and_next/${id}`,
        method: 'get'
      })
    },
    getTopNComments ({ n = 10, userId }) {
      const params = { n, userId }
      return $axios({
        url: `/blog/article/top/comment?${urlEncode(params)}`,
        method: 'get'
      })
    },
    getTopNLiked ({ n = 10, userId }) {
      const params = { n, userId }
      return $axios({
        url: `/blog/article/top/liked?${urlEncode(params)}`,
        method: 'get'
      })
    },
    getTopNBrowsed ({ n = 10, userId }) {
      const params = { n, userId }
      return $axios({
        url: `/blog/article/top/browsed?${urlEncode(params)}`,
        method: 'get'
      })
    },
    getPinnedArticles ({ userId }) {
      const params = { userId }
      return $axios({
        url: `/blog/article/pinned?${urlEncode(params)}`,
        method: 'get'
      })
    },
    getArticlePublishedDays ({ userId }) {
      const params = { userId }
      return $axios({
        url: `/blog/article/days/published?${urlEncode(params)}`,
        method: 'get'
      })
    },
    getArticlePublishedMonths ({ userId }) {
      const params = { userId }
      return $axios({
        url: `/blog/article/months/published?${urlEncode(params)}`,
        method: 'get'
      })
    },
    getArticlePublishedYears ({ userId }) {
      const params = { userId }
      return $axios({
        url: `/blog/article/years/published?${urlEncode(params)}`,
        method: 'get'
      })
    },
    countDailyPosts ({ startDateTime = '', endDateTime = '', userId }) {
      const params = { startDateTime, endDateTime, userId }
      return $axios({
        url: `/blog/article/count/daily/posts?${urlEncode(params)}`,
        method: 'get'
      })
    },
    countMonthlyPosts ({ startDateTime = '', endDateTime = '', userId }) {
      const params = { startDateTime, endDateTime, userId }
      return $axios({
        url: `/blog/article/count/monthly/posts?${urlEncode(params)}`,
        method: 'get'
      })
    },
    countYearlyPosts ({ startDateTime = '', endDateTime = '', userId }) {
      const params = { startDateTime, endDateTime, userId }
      return $axios({
        url: `/blog/article/count/yearly/posts?${urlEncode(params)}`,
        method: 'get'
      })
    },
    likesArticle: (id) => {
      return $axios({
        url: `/blog/article/likes/${id}`,
        method: 'post'
      })
    },
    unlikesArticle: (id) => {
      return $axios({
        url: `/blog/article/unlikes/${id}`,
        method: 'post'
      })
    },
    getLiked: (id) => {
      return $axios({
        url: `/blog/article/liked/${id}`,
        method: 'get'
      })
    },
    unBrowsesArticle: (id) => {
      return $axios({
        url: `/blog/article/un_browses/${id}`,
        method: 'post'
      })
    }
  }
}
