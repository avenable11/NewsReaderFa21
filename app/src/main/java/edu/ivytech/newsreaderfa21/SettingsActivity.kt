package edu.ivytech.newsreaderfa21

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreference
import androidx.work.*
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            this.findPreference<SwitchPreference>("is_interval")?.setOnPreferenceChangeListener {
                preference, newValue ->
                if(newValue == true) {
                    val interval = PreferenceManager.getDefaultSharedPreferences(requireContext())
                        .getString("interval", "1d")
                    if(interval != null)
                        setupWorker(interval)
                } else {
                    WorkManager.getInstance(requireContext()).cancelUniqueWork(DOWNLOAD_WORK)
                }
                true
            }
            this.findPreference<ListPreference>("interval")?.setOnPreferenceChangeListener {
                preference, newValue ->
                setupWorker(newValue as String)
                true
            }
            this.findPreference<ListPreference>("source")?.setOnPreferenceChangeListener {
                preference, newValue ->
                val pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
                pref.edit().putString(preference.key, newValue as String).apply()
                ArticleRepository.get().fetchArticles()
                true
            }
        }

        private fun setupWorker(interval:String) {
            val constraints =
                Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).build()
            lateinit var workRequest: PeriodicWorkRequest
            when (interval) {
                "1hr" -> workRequest =
                    PeriodicWorkRequest.Builder(DownloadWorker::class.java, 1, TimeUnit.HOURS, 50, TimeUnit.MINUTES)
                        .setConstraints(constraints)
                        .build()
                "3hr" -> workRequest =
                    PeriodicWorkRequest.Builder(DownloadWorker::class.java, 3, TimeUnit.HOURS, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build()
                "8hr" -> workRequest =
                    PeriodicWorkRequest.Builder(DownloadWorker::class.java, 8, TimeUnit.HOURS, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build()
                "12hr" -> workRequest =
                    PeriodicWorkRequest.Builder(DownloadWorker::class.java, 12, TimeUnit.HOURS, 1, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .build()
                else -> {
                    workRequest =
                        PeriodicWorkRequest.Builder(
                            DownloadWorker::class.java,
                            1,
                            TimeUnit.DAYS,
                            1, TimeUnit.HOURS
                        )
                            .setConstraints(constraints)
                            .build()
                }
            }
            WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                DOWNLOAD_WORK,
                ExistingPeriodicWorkPolicy.REPLACE, workRequest
            )
        }

    }
}