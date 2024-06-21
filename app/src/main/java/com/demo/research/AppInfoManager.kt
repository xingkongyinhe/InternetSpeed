package com.demo.research

import android.Manifest
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.demo.research.bean.TrafficInfo


object AppInfoManager {

    private const val TAG = "AppInfoManager"


    fun getPackageNameByUid(context: Context, uid: Int): String {
        runCatching {
            val packageManager: PackageManager = context.packageManager
            val applicationInfoList: List<ApplicationInfo> =
                packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (appInfo in applicationInfoList) {
                if (appInfo.uid == uid) {
                    return appInfo.packageName
                }
            }
        }
        return "unknown"
    }

    /**
     * 非系统应用
     * Non-system
     * @return true:非系统应用；false：系统应用
     */
    private fun isNonSystemApp(appInfo: ApplicationInfo): Boolean {
        return (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)
    }

    fun sortTrafficInfosByTotalData(trafficInfos: List<TrafficInfo>): List<TrafficInfo> {
        return trafficInfos.sortedByDescending { it.mobileTotalData + it.wifiTotalData }
    }

    fun removeDuplicateUids(trafficInfos: List<TrafficInfo>): List<TrafficInfo> {
        // 创建一个map来存储每个uid出现的次数
        val uidCountMap = mutableMapOf<Int, Int>()
        // 统计每个uid的出现次数
        for (trafficInfo in trafficInfos) {
            uidCountMap[trafficInfo.uid] = uidCountMap.getOrDefault(trafficInfo.uid, 0) + 1
        }

        // 只保留出现一次的uid对应的TrafficInfo
        return trafficInfos.filter { uidCountMap[it.uid] == 1 }
    }

    /**
     * 获取拥有internet权限的应用列表
     *
     * @return
     */
    fun getInternetTrafficInfoList(
        context: Context,
        map: Map<Int, TrafficInfo>
    ): List<TrafficInfo> {
        val trafficInfoList: MutableList<TrafficInfo> = ArrayList()
        // 获取手机中安装的并且具有权限的应用
        // PackageManager.MATCH_UNINSTALLED_PACKAGES：这个标志包含已安装的、已卸载的和即时（ephemeral）应用程序。
        // PackageManager.GET_PERMISSIONS：这个标志请求每个包请求的权限。
        val pm: PackageManager = context.packageManager

        val installedPackages: List<PackageInfo> = pm.getInstalledPackages(
            PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.GET_PERMISSIONS
        )

        for (appInfo in installedPackages) {
            //获取权限数组
            val permissions = appInfo.requestedPermissions
            if (permissions != null && permissions.isNotEmpty()) {
                for (permission in permissions) {
                    if (permission == Manifest.permission.INTERNET) {
                        val applicationInfo = appInfo.applicationInfo
                        val uid = applicationInfo.uid
                        val icon = applicationInfo.loadIcon(pm)
                        val appName = applicationInfo.loadLabel(pm).toString()
                        val packageName = applicationInfo.packageName
                        Log.d(
                            TAG,
                            "getInternetTrafficInfoList: uid: $uid, packageName: $packageName, appName: $appName"
                        )

                        map[uid]?.let {
                            it.appName = appName
                            it.icon = icon
                            it.packageName = packageName
                            trafficInfoList.add(it)
                        } ?: run {
//                            Log.d(TAG, "getInternetTrafficInfoList: uid is $uid, traffic is null")
//                            trafficInfoList.add(TrafficInfo(icon, appName, packageName, uid))
                        }

                    }
                }
            }
        }
        return trafficInfoList
    }

    /**
     * 获取所有 的应用列表
     *
     * @return
     */
    fun getInstalledApplications(context: Context): List<TrafficInfo> {
        val pm: PackageManager = context.packageManager
        val trafficInfoList: MutableList<TrafficInfo> = ArrayList()
        val installedPackages: List<ApplicationInfo> = pm.getInstalledApplications(
            0
        )
        for (applicationInfo in installedPackages) {
            val icon = applicationInfo.loadIcon(pm)
            val appName = applicationInfo.loadLabel(pm).toString()
            val packageName = applicationInfo.packageName
            val uid = applicationInfo.uid
            trafficInfoList.add(TrafficInfo(icon, appName, packageName, uid))
        }
        return trafficInfoList
    }


}