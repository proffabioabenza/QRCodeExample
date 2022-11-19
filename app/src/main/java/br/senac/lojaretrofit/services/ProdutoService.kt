package br.senac.lojaretrofit.services

import br.senac.lojaretrofit.model.Produto
import retrofit2.Call
import retrofit2.http.*

/** Mapeamento dos endpoints de produto dessa aplicação **/
interface ProdutoService {

    //Chamado para obter os detalhes de um produto
    @GET("/android/rest/produto/{id}")
    fun get(@Path("id") id: Int): Call<Produto>

}