package com.demo.research.ui.home

import android.net.TrafficStats
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.research.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private val handler: Handler = Handler(Looper.myLooper()!!)
    private var previousRxBytes: Long = 0
    private var previousTxBytes: Long = 0
    private var previousTimeStamp: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previousRxBytes = TrafficStats.getTotalRxBytes()
        previousTxBytes = TrafficStats.getTotalTxBytes()
        previousTimeStamp = System.currentTimeMillis()

        handler.postDelayed(runnable, 1000)
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            val currentRxBytes = TrafficStats.getTotalRxBytes()
            val currentTxBytes = TrafficStats.getTotalTxBytes()
            val currentTimeStamp = System.currentTimeMillis()
            val rxBytesDelta: Long = currentRxBytes - previousRxBytes
            val txBytesDelta: Long = currentTxBytes - previousTxBytes
            val timeDelta: Long = currentTimeStamp - previousTimeStamp
            val rxSpeed = rxBytesDelta * 1000 / timeDelta // Bytes per second
            val txSpeed = txBytesDelta * 1000 / timeDelta // Bytes per second
            Log.i(TAG, "run: Upload Speed: $txSpeed B/s, Download Speed: $rxSpeed B/s")

            binding.uploadSpeedTextView.text = "Upload Speed: $txSpeed B/s"
            binding.downloadSpeedTextView.text = "Download Speed: $rxSpeed B/s"
            previousRxBytes = currentRxBytes
            previousTxBytes = currentTxBytes
            previousTimeStamp = currentTimeStamp
            handler.postDelayed(this, 1000) // Update every second
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(runnable) // Stop the handler when activity is destroyed
        Log.d(TAG, "onDestroyView: ")

    }
}