package com.micah.jikan

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.micah.jikan.databinding.FragmentStopWatchBinding

class StopWatchFragment : Fragment() {

    private var _binding: FragmentStopWatchBinding? = null
    private val binding get() = _binding!!

    private lateinit var prefs: StopwatchPrefs

    private var startTime = 0L
    private var isRunning = false
    private var pauseOffset = 0L
    private var handler = Handler(Looper.getMainLooper())

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                val elapsed = System.currentTimeMillis() - startTime
                updateTimeDisplay(elapsed)
                handler.postDelayed(this, 1000)
            }
        }
    }

    private fun updateTimeDisplay(elapsedMillis: Long) {

        val totalSeconds = (elapsedMillis / 1000).toInt()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        binding.timeTxt.text = String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentStopWatchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = StopwatchPrefs(requireContext())

        startTime = prefs.getStartTime()
        isRunning = prefs.isRunning()
        pauseOffset = prefs.getPauseOffset()

        if (isRunning) {
            startTime = System.currentTimeMillis() - pauseOffset
            handler.post(updateRunnable)
        } else {
            updateTimeDisplay(pauseOffset)
        }

        binding.startBttn.setOnClickListener {
            if (!isRunning) {
                startTime = System.currentTimeMillis() - pauseOffset
                isRunning = true
                handler.post(updateRunnable)
                savePrefs()
            }
        }

        binding.stopBttn.setOnClickListener {
            if (isRunning) {
                pauseOffset = System.currentTimeMillis() - startTime
                isRunning = false
                handler.removeCallbacks(updateRunnable)
                savePrefs()
            }
        }

        binding.resetBttn.setOnClickListener {
            handler.removeCallbacks(updateRunnable)
            startTime = 0L
            pauseOffset = 0L
            isRunning = false
            updateTimeDisplay(0)
            prefs.clear()
        }
    }

    private fun savePrefs() {
        prefs.save(startTime, isRunning, if (isRunning) System.currentTimeMillis() - startTime else pauseOffset)
    }

    override fun onPause() {
        super.onPause()
        savePrefs()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacks(updateRunnable)
    }
}