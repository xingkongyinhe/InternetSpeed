package com.demo.research

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


object DateUtil {

    /**
     * 格式到天
     *
     * @param time
     * @return
     */
    fun getDay(time: Long): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(time)
    }

    /**
     * 获得当前时间的``
     *
     *
     */
    fun now(): Date {
        return Date()
    }

    /**
     * 获取当天的零点时间
     */
    fun getTimesmorning(): Long {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.getTimeInMillis()
    }

    /**
     * 获得本月第一天0点时间
     */
    fun getTimesMonthmorning(): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = 1 // M月置1
        cal[Calendar.HOUR_OF_DAY] = 0 // H置零
        cal[Calendar.MINUTE] = 0 // m置零
        cal[Calendar.SECOND] = 0 // s置零
        cal[Calendar.MILLISECOND] = 0 // S置零
        return cal.getTimeInMillis()
    }


    /**
     * 获得当前月的第一天
     *
     *
     * HH:mm:ss SS为零
     *
     * @return
     */
    fun firstDayOfMonthData(): Date {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = 1 // M月置1
        cal[Calendar.HOUR_OF_DAY] = 0 // H置零
        cal[Calendar.MINUTE] = 0 // m置零
        cal[Calendar.SECOND] = 0 // s置零
        cal[Calendar.MILLISECOND] = 0 // S置零
        return cal.time
    }

    /**
     * 获得当前月的第一天
     *
     *
     * HH:mm:ss SS为零
     *
     *
     */
    fun firstDayOfMonth(dayOfMonth: Int): Long {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = dayOfMonth // M月置1
        cal[Calendar.HOUR_OF_DAY] = 0 // H置零
        cal[Calendar.MINUTE] = 0 // m置零
        cal[Calendar.SECOND] = 0 // s置零
        cal[Calendar.MILLISECOND] = 0 // S置零
        return cal.getTimeInMillis()
    }


    /**
     * 获得当前月的第一天到当前时间的时间戳集合
     *
     *
     * HH:mm:ss SS为零
     *
     *
     */
    fun getListDayOfMonth(): List<Long> {
        val dayOfMonth: MutableList<Long> = ArrayList()
        val days: Long = getDayDiff(firstDayOfMonthData(), now())
        for (i in 1..days) {
            val day = firstDayOfMonth(i.toInt())
            dayOfMonth.add(day)
        }
        dayOfMonth.sortWith { o1, o2 -> ((o2 ?: 0) - (o1 ?: 0)).toInt() }
        return dayOfMonth
    }


    /**
     * 获得天数差
     *
     * @param begin 开始
     * @param end   结束
     * @return 天数
     */
    fun getDayDiff(begin: Date, end: Date): Long {
        var day: Long = 1
        if (end.time < begin.time) {
            day = -1
        } else if (end.time == begin.time) {
            day = 1
        } else {
            day += (end.time - begin.time) / (24 * 60 * 60 * 1000)
        }
        return day
    }


}