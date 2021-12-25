package com.example.safeair

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.safeair.Model.AuthorizeModel
import com.example.safeair.utils.Constants
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.fragment_authorize.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

class AuthorizeFragment: Fragment() {
    var Res: Int = 0
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        loadLocale()
        var rootView = inflater.inflate(R.layout.fragment_authorize, container, false)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?){
        super.onActivityCreated(savedInstanceState)

        text_password.addTextChangedListener {
            GlobalScope.async {
                sendData()
            }
        }

        enter_button.setOnClickListener{
            GlobalScope.async {
                sendData()
            }
            if(Res==1) {
                findNavController().navigate(R.id.action_authorizeFragment_to_roomsFragment)
/*                            Handler().postDelayed({
                findNavController().navigate(R.id.action_authorizeFragment_to_roomsFragment)
            }, 500)*/
            }


        }
    }
    private suspend fun sendData() {
        try {
            val result = GlobalScope.async {
                callApi("http://10.0.2.2:5000/api/mobileauthorize")
            }.await()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun callApi(apiUrl:String){
        var result: String? = ""
        val url: URL;
        var connection: HttpURLConnection? = null
        try {
            url = URL(apiUrl)
            connection = url.openConnection() as HttpURLConnection


            var message: AuthorizeModel = AuthorizeModel(text_email.text.toString(),text_password.text.toString())

            val builder = GsonBuilder()
            val gson = builder.create()

            val resultJson=gson.toJson(message)
            val postData: ByteArray = resultJson.toByteArray(StandardCharsets.UTF_8)

            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Content-length", postData.size.toString())
            connection.setRequestProperty("Content-Type", "application/json")
            connection.requestMethod = "POST"
            val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
            outputStream.write(postData)
            outputStream.flush()

            // read the response data
            val inputStream: DataInputStream = DataInputStream(connection.inputStream)
            val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
            val output: String = reader.readLine()

            val result: Int = gson.fromJson(output, Int::class.java)
            Res = result
            if (connection.responseCode != HttpURLConnection.HTTP_OK && connection.responseCode != HttpURLConnection.HTTP_CREATED) {
                try {
                    val inputStream: DataInputStream = DataInputStream(connection.inputStream)
                    val reader: BufferedReader = BufferedReader(InputStreamReader(inputStream))
                    val output: String = reader.readLine()
                    val builder = GsonBuilder()
                    val gson = builder.create()


                    println("There was error while connecting the chat $output")
                    System.exit(0)

                } catch (exception: Exception) {
                    throw Exception("Exception while push the notification  $exception.message")
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
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