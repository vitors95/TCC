package com.example.vitors.tcc_kotlin.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.example.vitors.tcc_kotlin.utils.adapters.PlaceAdapter
import com.example.vitors.tcc_kotlin.models.Collect
import com.example.vitors.tcc_kotlin.models.Place
import com.example.vitors.tcc_kotlin.R
import com.example.vitors.tcc_kotlin.utils.apis.AppClient
import com.example.vitors.tcc_kotlin.utils.constants.Constants
import com.google.firebase.messaging.FirebaseMessaging
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    var places: Array<Place> = arrayOf()
    var collects: Array<Collect> = arrayOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseMessaging.getInstance().subscribeToTopic("all")

        recyclerView_main.layoutManager = LinearLayoutManager(applicationContext)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(AppClient::class.java)

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

