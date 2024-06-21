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


    /**
     * 获取拥有internet权限的应用列表
     *
     * @return
     */
    fun getInternetTrafficInfoList(context: Context, map: Map<Int, TrafficInfo>): List<TrafficInfo> {
        val trafficInfoList: MutableList<TrafficInfo> = ArrayList()
        // 获取手机中安装的并且具有权限的应用
        // PackageManager.MATCH_UNINSTALLED_PACKAGES：这个标志包含已安装的、已卸载的和即时（ephemeral）应用程序。
        // PackageManager.GET_PERMISSIONS：这个标志请求每个包请求的权限。
        val pm: PackageManager = context.packageManager

        val installedPackages: List<PackageInfo> = pm.getInstalledPackages(
            PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.GET_PERMISSIONS
        )
        for (info in installedPackages) {
            //获取权限数组
            val permissions = info.requestedPermissions
            if (permissions != null && permissions.isNotEmpty()) {
                for (permission in permissions) {
                    if (permission == Manifest.permission.INTERNET) {
                        val applicationInfo = info.applicationInfo
                        val uid = applicationInfo.uid
                        val icon = applicationInfo.loadIcon(pm)
                        val appName = applicationInfo.loadLabel(pm).toString()
                        val packageName = applicationInfo.packageName
                        map[uid]?.let {
                            it.appName = appName
                            it.icon = icon
                            it.packageName = packageName
                            trafficInfoList.add(it)
                        } ?: run {
                            Log.d(TAG, "getInternetTrafficInfoList: uid is $uid, traffic is null")
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