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

package tech.ordinaryroad.blog.quarkus.util

import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.core.io.resource.ResourceUtil
import cn.hutool.core.net.NetUtil
import io.quarkus.vertx.http.runtime.CurrentVertxRequest
import io.vertx.core.json.Json
import org.lionsoul.ip2region.xdb.Searcher
import tech.ordinaryroad.blog.quarkus.exception.BaseBlogException
import java.time.LocalDateTime
import javax.enterprise.inject.spi.CDI

/**
 *
 *
 * @author mjz
 * @date 2022/9/1
 */
object BlogUtils {

    val IP_REGION_SEARCHER = Searcher.newWithBuffer(ResourceUtil.readBytes("ip2region/data_ip2region.xdb"))

    /**
     * 解析两个List的差异
     *
     * @return list[0]: 需要删除的
     * list[1]: 需要添加的
     */
    fun <E> List<E>.differ(newList: List<E>): List<List<E>> {
        val oldList = this

        val intersectSet = newList.intersect(oldList.toSet())

        val listToAdd = arrayListOf<E>()
        val listToDelete = arrayListOf<E>()
        oldList.forEach {
            if (!intersectSet.contains(it)) {
                listToDelete.add(it)
            }
        }
        newList.forEach {
            if (!intersectSet.contains(it)) {
                listToAdd.add(it)
            }
        }

        return arrayListOf(listToDelete, listToAdd)
    }

    /**
     * 获取当前Request
     */
    fun currentVertxRequest(): CurrentVertxRequest {
        return CDI.current().select(CurrentVertxRequest::class.java).get()!!
    }

    /**
     * 获取当前请求的IP
     */
    fun getClientIp(request: CurrentVertxRequest = this.currentVertxRequest()): String {
        val currentRequest = request.current.request()
        var ip = NetUtil.getMultistageReverseProxyIp(currentRequest.remoteAddress().hostAddress())
        val headerNames = arrayOf(
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        )
        for (header in headerNames) {
            val tempIp = currentRequest.getHeader(header) ?: ""
            if (!NetUtil.isUnknown(tempIp)) {
                ip = NetUtil.getMultistageReverseProxyIp(tempIp)
                break
            }
        }
        return ip
    }

    /**
     * 获取IP归属地
     */
    fun getIpRegion(ip: String?): IpRegion? {
        var ipRegion: IpRegion? = null
        if (!ip.isNullOrBlank()) {
            ipRegion = IpRegion()
            try {
                IP_REGION_SEARCHER.search(ip).let {
                    if (!it.isNullOrBlank()) {
                        val split = it.split("|")
                        ipRegion.country = split.getOrNull(0)
                        ipRegion.area = split.getOrNull(1)
                        ipRegion.province = split.getOrNull(2)
                        ipRegion.city = split.getOrNull(3)
                        ipRegion.isp = split.getOrNull(4)
                    }
                }
            } catch (_: Exception) {
                // ignore
            }
        }
        return ipRegion
    }

    /**
     * 解析时间范围
     */
    fun parseDateRange(startDateTime: LocalDateTime?, endDateTime: LocalDateTime?): Pair<String, String> {
        val normDatetimeFormatter = DatePattern.NORM_DATETIME_FORMATTER
        val startDateTimeString: String
        val endDateTimeString: String
        if (startDateTime == null || endDateTime == null) {
            val now = LocalDateTime.now()
            val currentYear = now.year
            startDateTimeString = if (startDateTime == null) {
                "${currentYear}-01-01 00:00:00"
            } else {
                LocalDateTimeUtil.format(startDateTime, normDatetimeFormatter)
            }
            endDateTimeString = if (endDateTime == null) {
                "${currentYear}-12-31 23:59:59"
            } else {
                LocalDateTimeUtil.format(endDateTime, normDatetimeFormatter)
            }
        } else {
            if (endDateTime.isBefore(startDateTime)) {
                throw BaseBlogException("结束时间不能小于开始时间")
            } else if (startDateTime.isAfter(endDateTime)) {
                throw BaseBlogException("开始时间不能大于开始时间")
            } else {
                startDateTimeString = LocalDateTimeUtil.format(startDateTime, normDatetimeFormatter)
                endDateTimeString = LocalDateTimeUtil.format(endDateTime, normDatetimeFormatter)
            }
        }
        return Pair(startDateTimeString, endDateTimeString)
    }

    /**
     * 转义SQL模糊查询特殊字符
     */
    fun String?.escapeSqlLike(): String {
        if (this.isNullOrBlank()) {
            return ""
        }

        return this
            .replace("\\", "\\\\")
            .replace("_", "\\_")
            .replace("%", "\\%")
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println(Json.encode(getIpRegion("127.0.0.1")))
        println(Json.encode(getIpRegion(null)))
        println(Json.encode(getIpRegion("")))
        println(Json.encode(getIpRegion("1.2.3.4")))
    }

}