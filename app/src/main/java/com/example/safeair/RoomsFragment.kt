package com.example.safeair

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.safeair.Adapters.RoomsAdapter
import com.example.safeair.Model.Room
import com.example.safeair.utils.Constants
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_rooms.*
import kotlinx.android.synthetic.main.rooms_layout.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RoomsFragment : Fragment() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: RoomsAdapter

    var resultView: TextView? = null

    companion object {
        fun newInstance(): RoomsFragment {
            return RoomsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        loadLocale()
        return inflater.inflate(R.layout.fragment_rooms, container, false);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)


        recyclerRoomList.setHasFixedSize(true)
        layoutManager = LinearLayoutManager(getContext())
        recyclerRoomList.layoutManager = layoutManager

//        val buttonView: Button = requireActivity()!!.findViewById<View>(R.id.button) as Button
        viewLifecycleOwner.lifecycleScope.launch {
            getRooms()
        }

        settings_button.setOnClickListener{
            findNavController().navigate(R.id.action_roomsFragment_to_settingsFragment)
        }
    }

    public suspend fun getRooms() {
        try {
            val result = GlobalScope.async {
                callApi("http://10.0.2.2:5000/api/mobilerooms")
            }.await()

            onResponse(result)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callApi(apiUrl:String ):String?{
        var result: String? = ""
        val url: URL;
        var connection: HttpURLConnection? = null
        try {
            url = URL(apiUrl)
            connection = url.openConnection() as HttpURLConnection

            connection.requestMethod = "GET"
            val `in` = connection.inputStream
            val reader = InputStreamReader(`in`)

            // read the response data
            var data = reader.read()
            while (data != -1) {
                val current = data.toChar()
                result += current
                data = reader.read()
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun onResponse(result: String?) {
        try {
            val builder = GsonBuilder()
            val gson = builder.create()

            val resultJson: Array<Room> = gson.fromJson(result, Array<Room>::class.java)

            adapter = RoomsAdapter(resultJson)
            adapter.notifyDataSetChanged()
            getActivity()?.runOnUiThread { recyclerRoomList.adapter = adapter }

        } catch (e: Exception) {
            e.printStackTrace()
            this.resultView!!.text = "Oops!! something went wrong, please try again"
        }
    }
    private fun setLocale(s: String) {
        val locale: Locale = Locale(s)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        activity?.baseContext?.resources?.updateConfiguration(config, requireActivity().baseContext.resources.displayMetrics)
        val editor = activity?.getSharedPreferences(Constants.APP_PREFERENCES, Context.MODE_PRIVATE)?.edit()
        editor?.putString("my_lang", s)
        editor?.apply()
    }
    private fun loadLocale(): String{
        val prefs = activity?.getSharedPreferences(Constants.APP_PREFERENCES, Activity.MODE_PRIVATE)
        val language = prefs?.getString("my_lang", "")
        language?.let { setLocale(it) }

        return language?: Locale.getDefault().toLanguageTag()
    }
}