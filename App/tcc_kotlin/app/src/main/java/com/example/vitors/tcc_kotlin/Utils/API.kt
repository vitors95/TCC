package com.example.vitors.tcc_kotlin.Utils

import com.example.vitors.tcc_kotlin.Models.Collect
import com.example.vitors.tcc_kotlin.Models.Place
import io.reactivex.Observable
import retrofit2.http.*

interface API {
    @GET("places")
    fun getPlaces(): Observable<Array<Place>>

    @GET("collect")
    fun getCollects(@Query("place_id") placeId: Int): Observable<Array<Collect>>
}