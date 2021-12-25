package com.example.safeair

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.safeair.utils.Constants.Companion.APP_PREFERENCES
import kotlinx.android.synthetic.main.fragment_settings.*
import java.util.*

class SettingsFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        loadLocale()
        var rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        return rootView
    }
    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)
        val localeTag = loadLocale()
        Log.d("LOCALE", localeTag)

        if (localeTag == ukranian.hint.toString()){
            (ukranian as RadioButton).isChecked = true
        } else {
            (english as RadioButton).isChecked = true
        }

        switch_language.setOnCheckedChangeListener { group, checkedId -> // checkedId is the RadioButton selected
            val rb = activity?.findViewById<RadioButton>(checkedId)
            setLocale(rb?.hint.toString())
            activity?.let { ActivityCompat.recreate(it) }
        }

        change_loc_button.setOnClickListener{
            findNavController().navigate(R.id.action_settingsFragment_to_roomsFragment)
        }

        exit_btn.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_authorizeFragment)
        }
    }

    private fun setLocale(s: String) {
        val locale: Locale = Locale(s)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity?.baseContext?.resources?.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)
        val editor = activity?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)?.edit()
        editor?.putString("my_lang", s)
        editor?.apply()
    }

    private fun loadLocale(): String{
        val prefs = activity?.getSharedPreferences(APP_PREFERENCES, Activity.MODE_PRIVATE)
        val language = prefs?.getString("my_lang", "")
        language?.let { setLocale(it) }

        return language?: Locale.getDefault().toLanguageTag()
    }
}