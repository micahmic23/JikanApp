package com.micah.jikan

import android.os.Bundle
import android.content.Context
import android.os.Build
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.micah.jikan.databinding.FragmentAlarmBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.*

private val Context.dataStore by preferencesDataStore(name = "alarms_prefs")
private val ALARMS_KEY = stringSetPreferencesKey("alarms")

class AlarmFragment : Fragment() {

    private var _binding: FragmentAlarmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pickTime.setIs24HourView(true)

        binding.alarmBttn.setOnClickListener {
            val hour = binding.pickTime.hour
            val minute = binding.pickTime.minute
            val alarmTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
            lifecycleScope.launch {
                requireContext().dataStore.edit { preferences ->
                    val currentAlarms = preferences[ALARMS_KEY]?.toMutableSet() ?: mutableSetOf()
                    currentAlarms.add(alarmTime)
                    preferences[ALARMS_KEY] = currentAlarms
                }
                Toast.makeText(requireContext(), "Alarm set: $alarmTime", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}