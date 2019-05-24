package com.example.vitors.tcc_kotlin.utils.apis

import com.example.vitors.tcc_kotlin.models.Collect
import com.example.vitors.tcc_kotlin.models.Place
import io.reactivex.Observable
import retrofit2.http.*

interface AppClient {
    @GET("places")
    fun getPlaces(): Observable<Array<Place>>

    @GET("collect")
    fun getCollects(@Query("place_id") placeId: Int): Observable<Array<Collect>>
}