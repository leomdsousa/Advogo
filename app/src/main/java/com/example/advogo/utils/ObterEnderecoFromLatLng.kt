package com.example.advogo.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import java.util.*

class ObterEnderecoFromLatLng(
        private val context: Context,
        private val lat: Double,
        private val lng: Double
    )
{

    private val geocoder = Geocoder(context, Locale.getDefault())
    private lateinit var mAddressListener: AddressListener

    fun setCustomAddressListener(addressListener: AddressListener){
        this.mAddressListener = addressListener
    }

    suspend fun launchBackgroundProcessForRequest() {
        val adress = GetAdress()

        withContext(Main) {
            if(adress.isEmpty()) {
                mAddressListener.onError()
            } else {
                mAddressListener.onAddressFound(adress)
            }
        }
    }

    private suspend fun GetAdress(): String {
        try {
            val adressList: List<Address>? = geocoder.getFromLocation(lat, lng, 1)

            if(!adressList.isNullOrEmpty()) {
                val address = adressList[0]
                val sb = StringBuilder()

                for(i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i)+" ")
                }

                sb.deleteCharAt(sb.length-1)
                return sb.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    interface AddressListener{
        fun onAddressFound(address:String)
        fun onError()
    }
}
