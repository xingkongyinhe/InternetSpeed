package com.demo.research

import android.icu.text.DecimalFormat

object TrafficFormat {

    //定义TB的计算常量
    private const val TB = (1024f * 1024f * 1024f * 1024f).toDouble()

    //定义GB的计算常量
    private const val GB = (1024f * 1024f * 1024f).toDouble()

    //定义MB的计算常量
    private const val MB = (1024f * 1024f).toDouble()

    //定义KB的计算常量
    private const val KB = 1024f


    /**
     * 格式化数据
     *
     * @param data
     * @return
     */
    fun formatByte(data: Long): String {
        val format = DecimalFormat("####.##")
        return if (data < KB) {
            data.toString() + "B"
        } else if (data < MB) {
            format.format(data / KB) + "KB"
        } else if (data < GB) {
            format.format(data / MB) + "MB"
        } else if (data < TB) {
            format.format(data / GB) + "GB"
        } else {
            ">1TB"
        }
    }


}