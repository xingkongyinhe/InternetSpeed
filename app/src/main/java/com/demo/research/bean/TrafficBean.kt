package com.demo.research.bean

import com.demo.research.TrafficFormat.formatByte

/**
 * 移动流量实体类
 */
class TrafficBean {

    var totalData: Long = 0 // 总流量数据

    var rxBytes: Long = 0 //移动 下载字节

    var txBytes: Long = 0 //移动 上传字节

    var uid: String? = null //包名uid


    override fun toString(): String {
        return "TrafficBean{" +
                "TotalData=" + formatByte(totalData) +
                ", RxBytes=" + formatByte(rxBytes) +
                ", TxBytes=" + formatByte(txBytes) +
                '}'
    }
}