package br.senac.lojaretrofit.views

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import br.senac.lojaretrofit.R
import br.senac.lojaretrofit.databinding.ActivityMainBinding
import br.senac.lojaretrofit.model.Produto
import br.senac.lojaretrofit.services.API
import br.senac.lojaretrofit.services.UsuarioSingleton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.*

/** Atividade principal **/
class MainActivity : AppCompatActivity() {
    /** Variável de bind automático **/
    lateinit var binding: ActivityMainBinding

    /** Função de callback chamada quando a atividade é criada **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Infla o layout usando binding automático **/
        binding = ActivityMainBinding.inflate(layoutInflater)
        /** Coloca o layout carregado na tela **/
        setContentView(binding.root)
    }

    /** Função de callback chamada quando o Android verifica se precisa carregar um menu **/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        /** Carrega o menu na tela **/
        menuInflater.inflate(R.menu.menu, menu)

        /** Necessário para cumprir a assinatura da função de callback **/
        return super.onCreateOptionsMenu(menu)
    }

    /** Callback chamado quando uma opção de menu é selecionada **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Verifica qual opção de menu foi clicada
        when (item.itemId) {
            //Verifica se clicou na item de menu de QRCode
            R.id.qrcode -> {
                //Cria um intent para ir para atividade de QRCode
                val intent = Intent(this, QrCodeActivity::class.java)
                //Inicia a atividade de QRCode esperando uma resposta
                startActivityForResult(intent, 1)
            }
        }

        /** Necessário para cumprir a assinatura da função de callback **/
        return super.onOptionsItemSelected(item)
    }

    /** Função de callback chamada quando a atividade aberta acima responder **/
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        /** Necessário para correto funcionamento da função de callback **/
        super.onActivityResult(requestCode, resultCode, data)

        //Verifica se a atividade chamada que respondeu foi a mesma acima, se ela
        //respondeu com uma mensagem de sucesso e se devolveu os dados de QRCode
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            //Tenta pegar os dados do QRCode que a atividade mandou em formato inteiro
            //(o id do produto). Caso náo consiga, volta null
            val id = data.getStringExtra("qrcode")?.toIntOrNull()
            //Verifica se conseguiu obter o id
            if (id != null) {
                //Chama a função para obter um produto do back end
                //Se estiver
                obterProduto(id)
            }
        }
    }

    /** Função para chamada da API por Retrofit para obtenção do produto por ID **/
    fun obterProduto(id: Int) {
        //Callback de resposta da chamada do Retrofit
        val callback = object : Callback<Produto> {
            //Chamado quando o back end responder
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {
                //Verifica se a resposta teve sucesso
                if (response.isSuccessful) {
                    //Mostra um aviso indicando que o produto foi encontrado
                    Snackbar.make(binding.root, "Produto encontrado",
                        Snackbar.LENGTH_LONG).show()
                    //Obtém a resposta do produto do backend
                    val produto = response.body()
                    //Chama a função para atualizar a tela com os dados do produto
                    atualizarUI(produto)
                }
                else {
                    //Mostra uma mensagem de erro
                    Snackbar.make(binding.root, "Não é possível encontrar o produto",
                        Snackbar.LENGTH_LONG).show()
                    //Registra o log do erro no console
                    Log.e("ERROR", response.errorBody().toString())
                }
            }

            //Chamado em caso de falha de conexão
            override fun onFailure(call: Call<Produto>, t: Throwable) {
                //Mostra uma mensagem de erro
                Snackbar.make(binding.root, "Não foi possível se conectar ao servidor",
                    Snackbar.LENGTH_LONG).show()
                //Registra o log do erro no console
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        //Chama a API de obternção de produto mapeada na classe de serviço
        API().produto.get(id).enqueue(callback)
    }

    //Função chamada para atualizar a tela com os dados do produto
    fun atualizarUI(produto: Produto?) {
        //Verifica se o produto é nulo e cancela a função caso seja
        if (produto == null) {
            return
        }

        //Atualiza a tela com os dados obtidos do back end
        binding.editNome.setText(produto.nomeProduto)
        binding.editPreco.setText(produto.precProduto.toString())

        //Pede para o Picasso baixar a imagem do produto e colocar no image view
        Picasso.get().load(
            "https://oficinacordova.azurewebsites.net/android/rest/produto/image/${produto.idProduto}"
        ).into(binding.imageView)

    }

}