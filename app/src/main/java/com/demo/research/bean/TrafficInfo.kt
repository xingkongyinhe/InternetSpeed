package com.demo.research.bean

import android.graphics.drawable.Drawable

/**
 * 流量信息
 */
class TrafficInfo {
    //应用图标
    var icon: Drawable? = null

    //app名称
    var appName: String? = null

    //包名
    var packageName: String? = null

    //uid
    var uid = 0

    var traffic: String? = null

    var mobileTotalData: Long = 0 //移动总流量数据

    var mobileRxBytes: Long = 0 //移动 下载字节

    var mobileTxBytes: Long = 0 //移动 上传字节


    var wifiTotalData: Long = 0 //wifi总流量数据

    var wifiRxBytes: Long = 0 //wifi 下载字节

    var wifiTxBytes: Long = 0 //wifi 上传字节



    constructor() {
    }

    constructor(icon: Drawable?, appName: String?, packName: String?, uid: Int) {
        this.icon = icon
        this.appName = appName
        this.packageName = packName
        this.uid = uid
    }


}