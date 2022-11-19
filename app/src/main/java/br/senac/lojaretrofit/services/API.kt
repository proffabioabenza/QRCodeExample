package br.senac.lojaretrofit.services

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Classe para centralizar os serviços do Retrofit **/
class API() {
    //Insância principal do Retrofit, que configura a URL base e o conversor
    //dos arquvios de texto que são mandados/recebidos para/do backend
    private val retrofit: Retrofit
        get() {
            return Retrofit
                .Builder()
                .baseUrl("http://oficinacordova.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    //Instância do serviço do produto, concentra as funções
    //mapeadas para chamadas de endpoints de produto no back end
    val produto: ProdutoService
        get() {
            return retrofit.create(ProdutoService::class.java)
        }

}

