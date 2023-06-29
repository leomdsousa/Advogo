package com.example.advogo.utils

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.util.Log
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

@SuppressLint("StaticFieldLeak")
class SendNotificationToUserAsyncTask(
    val title: String,
    private val message: String,
    private val token: String
    ) : AsyncTask<Any, Void, String>()
{
    override fun onPreExecute() {
        super.onPreExecute()
        Log.i(TAG, "onPreExecute")
    }

    override fun doInBackground(vararg params: Any): String {
        var result: String

        var connection: HttpURLConnection? = null

        try {
            val url = URL(Constants.FCM_BASE_URL)
            connection = url.openConnection() as HttpURLConnection

            connection.doOutput = true
            connection.doInput = true
            connection.instanceFollowRedirects = false
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("charset", "utf-8")
            connection.setRequestProperty("Accept", "application/json")
            connection.setRequestProperty(
                Constants.FCM_AUTHORIZATION, "${Constants.FCM_KEY}=${Constants.FCM_SERVER_KEY}"
            )
            connection.useCaches = false

            val wr = DataOutputStream(connection.outputStream)

            val jsonRequest = JSONObject()
            val dataObject = JSONObject()

            dataObject.put(Constants.FCM_KEY_TITLE, title)
            dataObject.put(Constants.FCM_KEY_MESSAGE, message)

            jsonRequest.put(Constants.FCM_KEY_DATA, dataObject)
            jsonRequest.put(Constants.FCM_KEY_TO, token)

            wr.writeBytes(jsonRequest.toString())
            wr.flush()
            wr.close()

            val httpResult: Int =
                connection.responseCode

            if (httpResult == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream

                val reader = BufferedReader(InputStreamReader(inputStream))
                val sb = StringBuilder()
                var line: String?

                try {
                    while (reader.readLine().also { line = it } != null) {
                        sb.append(line + "\n")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    try {
                        inputStream.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

                result = sb.toString()
            } else {
                result = connection.responseMessage
            }

        } catch (e: SocketTimeoutException) {
            result = "Connection Timeout"
        } catch (e: Exception) {
            result = "Error : " + e.message
        } finally {
            connection?.disconnect()
        }

        return result
    }

    override fun onPostExecute(result: String) {
        super.onPostExecute(result)
        Log.e("JSON Response Result", result)
    }
}