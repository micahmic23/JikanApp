package com.micah.jikan

import android.os.Bundle
import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.micah.jikan.databinding.FragmentSavedAlarmsBinding

private val Context.dataStore by preferencesDataStore(name = "alarms_prefs")
private val ALARMS_KEY = stringSetPreferencesKey("alarms")

class SavedAlarmsFragment : Fragment() {

    private var _binding: FragmentSavedAlarmsBinding? = null
    private val binding get() = _binding!!

    private val alarmsAdapter = AlarmsAdapter { alarmToDelete ->
        deleteAlarm(alarmToDelete)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedAlarmsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.alarmRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alarmsAdapter
        }

        loadAlarms()
    }

    private fun loadAlarms() {
        lifecycleScope.launch {
            requireContext().dataStore.data
                .map { preferences -> preferences[ALARMS_KEY] ?: emptySet() }
                .collect { alarmsSet ->
                    val alarmsList = alarmsSet.sorted()
                    alarmsAdapter.submitList(alarmsList)
                    binding.alarmstext.visibility = if (alarmsList.isEmpty()) View.VISIBLE else View.GONE
                }
        }
    }

    private fun deleteAlarm(alarmTime: String) {
        lifecycleScope.launch {
            requireContext().dataStore.edit { preferences ->
                val currentAlarms = preferences[ALARMS_KEY]?.toMutableSet() ?: mutableSetOf()
                if (currentAlarms.remove(alarmTime)) {
                    preferences[ALARMS_KEY] = currentAlarms
                    launchOnMain {
                        Toast.makeText(requireContext(), "Deleted alarm: $alarmTime", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun launchOnMain(block: suspend () -> Unit) {
        lifecycleScope.launch {
            block()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// The Adapter containing the delete button callback

class AlarmsAdapter(
    private val onDeleteClicked: ((String) -> Unit)? = null
) : RecyclerView.Adapter<AlarmsAdapter.AlarmViewHolder>() {

    private val alarms = mutableListOf<String>()

    fun submitList(newAlarms: List<String>) {
        alarms.clear()
        alarms.addAll(newAlarms)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view, onDeleteClicked)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount(): Int = alarms.size

    class AlarmViewHolder(
        itemView: View,
        private val onDeleteClicked: ((String) -> Unit)?
    ) : RecyclerView.ViewHolder(itemView) {

        private val alarmtextView = itemView.findViewById<android.widget.TextView>(R.id.alarmtextView)
        private val deleteBttn = itemView.findViewById<android.widget.ImageButton>(R.id.deleteBttn)

        fun bind(alarmTime: String) {
            alarmtextView.text = alarmTime
            deleteBttn.setOnClickListener {
                onDeleteClicked?.invoke(alarmTime)
            }
        }
    }
}
