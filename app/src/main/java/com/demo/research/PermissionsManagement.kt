package com.demo.research

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings


/**
 * 权限管理
 * Permissions management
 */
object PermissionsManagement {

    private fun requestPermissions(arrayOf: Array<String>, i: Int) {
        requestPermissions(
            arrayOf<String>(

            ), 1
        )
    }


    fun Context.hasUsageStatsPermission(): Boolean {
        runCatching {
            val appOps = getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val mode = appOps?.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    packageName
                )
                return mode == AppOpsManager.MODE_ALLOWED
            } else {
                val mode = appOps?.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    packageName
                )
                return mode == AppOpsManager.MODE_ALLOWED
            }
        }.onFailure {

        }
        return false
    }


    fun Context.requestUsageStatsPermission() {
        val parse = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, parse)
        startActivity(intent)
    }


}