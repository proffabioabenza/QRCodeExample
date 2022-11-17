package br.senac.lojaretrofit.services

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class API(context: Context) {

    private val retrofit: Retrofit
        get() {
                     return Retrofit
                .Builder()
                .baseUrl("https://oficinacordova.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    val produto: ProdutoService
        get() {
            return retrofit.create(ProdutoService::class.java)
        }

}

