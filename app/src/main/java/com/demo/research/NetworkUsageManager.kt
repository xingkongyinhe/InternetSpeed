package com.demo.research

import android.annotation.SuppressLint
import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.RemoteException
import android.telephony.TelephonyManager
import android.util.Log
import com.demo.research.DateUtil.getTimesMonthmorning
import com.demo.research.DateUtil.getTimesmorning
import com.demo.research.bean.TrafficBean
import com.demo.research.bean.TrafficInfo


/**
 *
 */
class NetworkUsageManager {

    companion object {
        private const val TAG = "NetworkUsage"
    }


    /**
     * 获取 所有移动使用流量信息
     *
     * @param context       上下文
     * @param isDayAndMonth 是否是当天还是当月
     * @return 返回 当天 还是当月的流量信息
     */
    fun getAllDayMonthMobileInfo(context: Context, isDayAndMonth: Boolean): TrafficBean {
        val networkStatsManager =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as? NetworkStatsManager
        val trafficBean = TrafficBean()
        val bucket = try {
            networkStatsManager?.querySummaryForDevice(
                ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(context),
                if (isDayAndMonth) getTimesmorning() else getTimesMonthmorning(),
                System.currentTimeMillis()
            )
        } catch (e: RemoteException) {
            return trafficBean
        }
        trafficBean.rxBytes = (bucket?.rxBytes ?: 0)
        trafficBean.txBytes = (bucket?.txBytes ?: 0)
        trafficBean.totalData = ((bucket?.txBytes ?: 0) + (bucket?.rxBytes ?: 0))
        return trafficBean
    }

    private var packageUid = 0

    fun setPackageUid(packageUid: Int) {
        this.packageUid = packageUid
    }

    /**
     * 获取所有应用一天使用的移动流量信息
     *
     * @param context   上下文
     * @param startTime 开始时间
     * @return 流量信息
     */
    fun getOneDayMobileInfo(context: Context, startTime: Long): TrafficBean {
        val trafficBean = TrafficBean()
        try {
            val networkStatsManager =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as? NetworkStatsManager
            val bucket = networkStatsManager?.querySummaryForDevice(
                ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                startTime,
                startTime + 86400000
            )
            trafficBean.rxBytes = (bucket?.rxBytes ?: 0)
            trafficBean.txBytes = (bucket?.txBytes ?: 0)
            trafficBean.totalData = ((bucket?.txBytes ?: 0) + (bucket?.rxBytes ?: 0))
        } catch (e: RemoteException) {
            return trafficBean
        }
        return trafficBean
    }

    /**
     * 获取今日 或者今月的实时流量使用情况
     *
     * @param context       上下文
     * @param isDayAndMonth 是否是今天还是今月
     * @return 获取今日 或者今月的流量使用情况
     */
    fun getSummaryTrafficMobile(context: Context, isDayAndMonth: Boolean): TrafficBean {
        val trafficBean = TrafficBean()
        trafficBean.uid = (packageUid.toString())
        var networkStats: NetworkStats? = null
        try {
            val networkStatsManager =
                context.getSystemService(Context.NETWORK_STATS_SERVICE) as? NetworkStatsManager
            networkStats = networkStatsManager?.querySummary(
                ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(context, ConnectivityManager.TYPE_MOBILE),
                if (isDayAndMonth) getTimesmorning() else getTimesMonthmorning(),
                System.currentTimeMillis()
            )
            val mobileTraffic: Long
            var mobileRx: Long = 0
            var mobileTx: Long = 0
            val bucket = NetworkStats.Bucket()
            do {
                networkStats?.getNextBucket(bucket)
                val summaryUid = bucket.uid
                if (packageUid == summaryUid) {
                    mobileTx += bucket.txBytes
                    mobileRx += bucket.rxBytes
                }
            } while (networkStats?.hasNextBucket() == true)
            mobileTraffic = mobileRx + mobileTx
            trafficBean.txBytes = mobileTx
            trafficBean.rxBytes = mobileRx
            trafficBean.totalData = mobileTraffic
        } catch (e: RemoteException) {
            e.printStackTrace()
        } finally {
            networkStats?.close()
        }
        return trafficBean
    }


    /**
     * 获取用户id android 10 以后获取不了 传null 即可
     * 需要权限
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @param context     上下文
     * @param networkType 网络类型
     * @return null
     */
    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getSubscriberId(
        context: Context,
        networkType: Int = ConnectivityManager.TYPE_MOBILE
    ): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return null
        }
        if (ConnectivityManager.TYPE_MOBILE == networkType) {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.subscriberId
        }
        return null
    }

    fun getNetworkUsageStats(context: Context): Map<Int, TrafficInfo> {
        val networkStatsManager =
            context.getSystemService(Context.NETWORK_STATS_SERVICE) as? NetworkStatsManager

        val localList = mutableListOf<TrafficInfo>()

        val localMap = HashMap<Int, TrafficInfo>()

        try {
            // 获取Wi-Fi数据使用情况
            val wifiStats = networkStatsManager?.querySummary(
                ConnectivityManager.TYPE_WIFI,
                "",
                0,
                System.currentTimeMillis()
            )

            val wifiBucket = NetworkStats.Bucket()
            wifiStats?.apply {
                while (hasNextBucket()) {
                    getNextBucket(wifiBucket)
                    val localUid = wifiBucket.uid
                    val rxBytes = wifiBucket.rxBytes
                    val txBytes = wifiBucket.txBytes
                    val trafficInfo = TrafficInfo().apply {
                        uid = localUid
                        wifiTotalData = rxBytes + txBytes
                        wifiRxBytes = rxBytes
                        wifiTxBytes = txBytes
                    }
                    localList.add(trafficInfo)
                    localMap[localUid] = trafficInfo

                    Log.d(
                        "NetworkUsage",
                        "UID: $localUid, Wi-Fi Data Received: $rxBytes bytes, Wi-Fi Data Sent: $txBytes bytes"
                    )
                }
            }

            // 获取移动数据使用情况
            val mobileStats = networkStatsManager?.querySummary(
                ConnectivityManager.TYPE_MOBILE,
                getSubscriberId(context),
                0,
                System.currentTimeMillis()
            )


            mobileStats?.let { it ->
                val mobileBucket = NetworkStats.Bucket()
                Log.d(TAG, "getNetworkUsageStats: mobileStats")
                while (it.hasNextBucket()) {
                    it.getNextBucket(mobileBucket)
                    val localUid = mobileBucket.uid
                    val rxBytes = mobileBucket.rxBytes
                    val txBytes = mobileBucket.txBytes
                    localMap[localUid]?.let {
                        Log.d("NetworkUsage", "UID: $localUid, map[localUid] ")
                        it.mobileTotalData = rxBytes + txBytes
                        it.mobileRxBytes = rxBytes
                        it.mobileTxBytes = txBytes
                    } ?: run {
                        Log.d("NetworkUsage", "UID: $localUid, map[localUid] is null ")
                        val trafficInfo = TrafficInfo().apply {
                            uid = localUid
                            wifiTotalData = rxBytes + txBytes
                            wifiRxBytes = rxBytes
                            wifiTxBytes = txBytes
                        }
                        localList.add(trafficInfo)
                        localMap[localUid] = trafficInfo
                    }

                    Log.d(
                        "NetworkUsage",
                        "UID: $localUid, Mobile Data Received: $rxBytes bytes, Mobile Data Sent: $txBytes bytes"
                    )
                }
            } ?: {
                Log.d(TAG, "getNetworkUsageStats: mobileStats is null")
            }
            return localMap
        } catch (e: RemoteException) {
            e.printStackTrace()
            Log.e(TAG, "getNetworkUsageStats: ", e)
        }

        return localMap
    }

}