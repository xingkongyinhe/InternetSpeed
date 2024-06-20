package com.demo.research.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.research.AppInfoManager
import com.demo.research.PermissionsManagement
import com.demo.research.PermissionsManagement.hasUsageStatsPermission
import com.demo.research.PermissionsManagement.requestUsageStatsPermission
import com.demo.research.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    companion object {
        private const val TAG = "DashboardFragment"
    }

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[DashboardViewModel::class.java]

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            if (!it.hasUsageStatsPermission()) {
                it.requestUsageStatsPermission()
            } else {
                loadingInstallAppList()
//                NetworkUsageManager().getNetworkUsageStats(it)
            }
        }
    }

    /**
     * 获取设备安装的程序
     */
    private fun loadingInstallAppList() {

        context?.let { it ->
            AppInfoManager.getInstalledApplications(it).apply {
                Log.i(TAG, "loadingInstallAppList: total app num is $size")
                onEach {
                    Log.i(TAG, "loadingInstallAppList: ${it.appName}")
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}