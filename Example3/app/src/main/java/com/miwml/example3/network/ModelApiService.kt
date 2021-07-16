package com.miwml.example3.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "http://biesa.uma.pt/"

/**
 * Build the Moshi object with Kotlin adapter factory that Retrofit will be using.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * The Retrofit object with the Moshi converter.
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
    .baseUrl(BASE_URL)
    .build()

/**
 * A public interface that exposes the [getModelOutput] method
 */
interface ModelApiService {
    @GET("workshop")
    suspend fun getModelOutput(@Query("input") inputSequence: String): ModelOutput
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object ModelApi {
    val retrofitService: ModelApiService by lazy { retrofit.create(ModelApiService::class.java) }
}