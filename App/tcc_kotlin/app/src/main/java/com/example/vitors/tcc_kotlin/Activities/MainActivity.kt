package com.example.vitors.tcc_kotlin.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.vitors.tcc_kotlin.Adapters.EquipmentAdapter
import com.example.vitors.tcc_kotlin.Adapters.PlaceAdapter
import com.example.vitors.tcc_kotlin.Helpers.OnRequestCompleteListener
import com.example.vitors.tcc_kotlin.Helpers.RequestPlaces
import com.example.vitors.tcc_kotlin.Models.Collect
import com.example.vitors.tcc_kotlin.Models.Equipment
import com.example.vitors.tcc_kotlin.Models.Place
import com.example.vitors.tcc_kotlin.R
import com.example.vitors.tcc_kotlin.Utils.API
import com.example.vitors.tcc_kotlin.Utils.Constants
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView_main.layoutManager = LinearLayoutManager(applicationContext)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(API::class.java)

        api.getPlaces()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val places = it
                api.getCollects(places[0].place_id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        val collects = it
                        setPlaces(places, collects)
                    }, {
                        Log.e("ERRO", "Falha no getCollects")
                    })
            }, {
                Log.e("ERRO", "Falha no getPlaces")
            })

    }

    fun setPlaces(places: Array<Place>, collects: Array<Collect>) {
        recyclerView_main.adapter = PlaceAdapter(places, collects)
    }

//    fun fetchPlaces() {
//        val url = "http://ec2-34-215-199-111.us-west-2.compute.amazonaws.com:5000/places"
//        val request = Request.Builder()
//            .url(url)
//            .build()
//
//        val client = OkHttpClient()
//        client.newCall(request).enqueue(object: Callback {
//
//            override fun onResponse(call: Call, response: Response) {
//                val body = response.body()?.string()
//                val gson = GsonBuilder().create()
//                val places = gson.fromJson(body, Array<Place>::class.java)
//
//                runOnUiThread {
//                    recyclerView_main.adapter = PlaceAdapter(places)
//                }
//            }
//
//            override fun onFailure(call: Call, e: IOException) {
//                println("Falha na requisição")
//            }
//        })
//    }

}

class Teste: OnRequestCompleteListener {

    var response: Array<Place>? = null

    override fun onSuccess(place: Array<Place>?) {
        response = place
    }

    override fun onError() {
        response = arrayOf()
    }

}
