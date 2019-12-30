package com.project.app.Preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.project.app.R


class SearchPreferenceFragment : PreferenceFragmentCompat() {


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference_search, rootKey)
    }



}
