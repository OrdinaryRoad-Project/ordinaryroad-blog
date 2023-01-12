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

package tech.ordinaryroad.blog.quarkus.service

import cn.hutool.dfa.StopChar
import cn.hutool.dfa.WordTree
import com.baomidou.mybatisplus.core.toolkit.Wrappers
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers
import org.apache.ibatis.session.SqlSessionFactory
import tech.ordinaryroad.blog.quarkus.config.RequestDataHelper
import tech.ordinaryroad.blog.quarkus.dal.dao.BlogCommentDAO
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogArticle
import tech.ordinaryroad.blog.quarkus.dal.entity.BlogComment
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException.Companion.throws
import tech.ordinaryroad.blog.quarkus.exception.BlogArticleCannotCommentException
import tech.ordinaryroad.blog.quarkus.exception.BlogCommentNotValidException
import tech.ordinaryroad.blog.quarkus.mapstruct.BlogCommentMapStruct
import tech.ordinaryroad.blog.quarkus.request.BlogCommentPostRequest
import tech.ordinaryroad.blog.quarkus.request.BlogCommentQueryRequest
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogArticleCommentVO
import tech.ordinaryroad.blog.quarkus.resource.vo.BlogSubCommentVO
import tech.ordinaryroad.commons.mybatis.quarkus.service.BaseService
import tech.ordinaryroad.commons.mybatis.quarkus.utils.PageUtils
import java.util.*
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject
import javax.ws.rs.core.Response

@ApplicationScoped
class BlogCommentService : BaseService<BlogCommentDAO, BlogComment>() {

    val commentMapStruct: BlogCommentMapStruct = BlogCommentMapStruct.INSTANCE

    @Inject
    protected lateinit var articleService: BlogArticleService

    @Inject
    protected lateinit var blogService: BlogService

    @Inject
    protected lateinit var pushService: BlogPushService

    @Inject
    protected lateinit var validateService: BlogValidateService

    @Inject
    protected lateinit var sqlSessionFactory: SqlSessionFactory

    override fun getEntityClass(): Class<BlogComment> {
        return BlogComment::class.java
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val wordTree = WordTree()
            wordTree.addWord("土豆")
            val charFilter = StopChar::isNotStopChar
            wordTree.setCharFilter(charFilter)

            println(wordTree.matchAll("我有一颗大土 豆，刚出锅的彩.票发票"))
        }
    }

    //region 业务相关
    fun post(request: BlogCommentPostRequest): Response {
        val content = request.content

        val fromUser = blogService.currentUser()

        // 先转换，后面需要填充
        val comment = commentMapStruct.transfer(request)

        // 传入parentId视为回复评论
        val isReply = !request.parentId.isNullOrBlank()

        val article: BlogArticle
        var parentComment: BlogComment? = null
        if (isReply) {
            // 回复，直接使用parent的articleId
            parentComment = validateService.validateComment(request.parentId, true)!!

            // 校验articleId
            if (request.articleId.isBlank()) {
                request.articleId = parentComment.articleId
                article = validateService.validatePublishByArticleFirstId(request.articleId)
            } else {
                if (request.articleId != parentComment.articleId) {
                    // 父评论文章和传入的不一致
                    throw BlogCommentNotValidException()
                } else {
                    article = validateService.validatePublishByArticleFirstId(request.articleId)
                    // 只关联最初版本的文章Id
                    if (article.uuid != article.firstId) {
                        comment.articleId = article.firstId
                    }
                }
            }

            // 填充originalId
            comment.originalId = if (parentComment.originalId.isNullOrBlank()) {
                parentComment.uuid
            } else {
                parentComment.originalId
            }
        } else {
            article = validateService.validatePublishByArticleFirstId(request.articleId)
            // 只关联最初版本的文章Id
            if (article.uuid != article.firstId) {
                comment.articleId = article.firstId
            }

            comment.originalId = ""
        }

        // 校验是否允许评论
        if (!article.canComment) {
            BlogArticleCannotCommentException().throws()
        }

        val create = super.create(comment)

        pushService.comment(fromUser, content, article, parentComment)

        return Response.ok(
            if (isReply) {
                commentMapStruct.transferSub(create)
            } else {
                commentMapStruct.transferArticle(create)
            }
        ).build()
    }

    fun pageSubComment(request: BlogCommentQueryRequest): Page<BlogSubCommentVO> {
        val originalComment = validateService.validateComment(request.originalId, true)!!

        val wrapper = ChainWrappers.queryChain(dao)
            .eq("article_id", originalComment.articleId)
            .eq("original_id", request.originalId)

        val page = page(request, wrapper)

        val voPage = PageUtils.copyPage(page) {
            return@copyPage commentMapStruct.transferSub(it)
        }

        return voPage
    }

    fun pageArticleComment(request: BlogCommentQueryRequest): Page<BlogArticleCommentVO> {
        val firstArticleById = validateService.validateFirstVersionArticle(request.articleId)

        val wrapper = ChainWrappers.queryChain(dao)
            .eq("article_id", firstArticleById.uuid)
            .eq("original_id", "")

        val page = page(request, wrapper)

        val voPage = PageUtils.copyPage(page) {
            return@copyPage commentMapStruct.transferArticle(it)
        }

        var total = voPage.total
        voPage.records.forEach {
            total += it.replies.total
        }
        voPage.total = total

        return voPage
    }

    fun page(request: BlogCommentQueryRequest): Page<Any> {
        val configuration = sqlSessionFactory.configuration
        if (configuration.interceptors.size == 1) {
            val interceptor = MybatisPlusInterceptor()
            val dynamicTableNameInnerInterceptor = DynamicTableNameInnerInterceptor()
            dynamicTableNameInnerInterceptor.tableNameHandler = TableNameHandler { sql: String?, tableName: String ->
                // 获取参数方法
                val paramMap: Map<String, Any> = RequestDataHelper.getRequestData()
                paramMap.forEach { (k: String, v: Any) ->
                    System.err.println(
                        "$k----$v"
                    )
                }
                var year = "_2018"
                val random = Random().nextInt(10)
                if (random % 2 == 1) {
                    year = "_2019"
                }
                tableName + year
            }
            interceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor)

            configuration.addInterceptor(interceptor)
        }
        RequestDataHelper.setRequestData(HashMap<String, Any>().apply {
            put("id", 123)
            put("hello", "tomcat")
            put("name", "汤姆凯特")
        })

        val commentPageDTO = PageDTO.of<BlogComment>(1, 10)
        val selectAllWithDataScope = super.dao.selectAllWithDataScope(commentPageDTO)

        val wrapper = ChainWrappers.queryChain(dao)

        val page = page(request, wrapper)

        val voPage = PageUtils.copyPage(page) {
            if (it.originalId.isNullOrBlank()) {
                return@copyPage commentMapStruct.transferArticle(it)
            } else {
                return@copyPage commentMapStruct.transferSub(it)
            }
        }

        return voPage
    }

    /**
     * 获取文章评论量
     */
    fun getCommentsCount(articleId: String): Long {
        val firstArticleById = validateService.validateFirstVersionArticle(articleId)

        val wrapper = Wrappers.query<BlogComment>()
            .eq("article_id", firstArticleById.uuid)

        return super.dao.selectCount(wrapper)
    }
    //endregion

    //region SQL相关
    fun countByUserId(userId: String): Long {
        val wrapper = Wrappers.query<BlogComment>()
            .eq(userId.isNotBlank(), "create_by", userId)
        return super.dao.selectCount(wrapper)
    }

    fun deleteByOriginalIdAndParentId(originalId: String, parentId: String? = null) {
        if (originalId.isBlank()) {
            return
        }
        val wrapper = Wrappers.query<BlogComment>()
            .eq("original_id", originalId)
            .eq(!parentId.isNullOrBlank(), "parent_id", parentId)
        super.dao.delete(wrapper)
    }
    //endregion

}
