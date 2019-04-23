package com.example.vitors.tcc_kotlin.Helpers

import com.example.vitors.tcc_kotlin.Models.Place
import com.example.vitors.tcc_kotlin.Utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class RequestPlaces : Callback {

    private var url: String? = null
    private var places: Array<Place>? = null
    var onRequestCompleteListener: OnRequestCompleteListener? = null

     fun fetchPlaces(callback: OnRequestCompleteListener) {
        this.onRequestCompleteListener = callback
        this.url = "${Constants.BASE_URL}/${Constants.PLACES}"

        val request = Request.Builder().url(url!!).build()

        val client = OkHttpClient()
         client.newCall(request).enqueue(this)

    }


    override fun onFailure(call: Call, e: IOException) {
        onRequestCompleteListener?.onError()
        println("error")
    }

    override fun onResponse(call: Call, response: Response) {

        if (response.isSuccessful) {
            val body = response.body()?.string()

            val gson = GsonBuilder().create()
            val places = gson.fromJson(body, Array<Place>::class.java)

            parse(places)
        }
        onRequestCompleteListener?.onSuccess(places)
    }

    private fun parse(response: Array<Place>) {
        this.places = response
    }

}

interface OnRequestCompleteListener {

    fun onSuccess(place: Array<Place>?)
    fun onError()

}