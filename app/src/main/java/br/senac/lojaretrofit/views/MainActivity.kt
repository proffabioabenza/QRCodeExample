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
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.*

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun obterProduto(id: Int) {
        val callback = object : Callback<Produto> {
            override fun onResponse(call: Call<Produto>, response: Response<Produto>) {

                if (response.isSuccessful) {
                    val produto = response.body()
                    atualizarUI(produto)
                }
                else {
                    //val error = response.errorBody().toString()
                    Snackbar.make(binding.root, "Não é possível encontrar o produto",
                        Snackbar.LENGTH_LONG).show()

                    Log.e("ERROR", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<Produto>, t: Throwable) {
                Snackbar.make(binding.root, "Não foi possível se conectar ao servidor",
                Snackbar.LENGTH_LONG).show()

                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API(this).produto.get(id).enqueue(callback)

    }

    fun atualizarUI(produto: Produto?) {
        if (produto == null) {
            return
        }

        binding.editNome.setText(produto.nomeProduto)
        binding.editPreco.setText(produto.precProduto.toString())

        Picasso.get().load(
            "https://oficinacordova.azurewebsites.net/android/rest/produto/image/${produto.idProduto}"
        ).into(binding.imageView)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.qrcode -> {
                val intent = Intent(this, QrCodeActivity::class.java)
                startActivityForResult(intent, 1)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            val id = data.getStringExtra("qrcode")?.toIntOrNull()

            if (id != null) {
                obterProduto(id)
            }
        }
    }

}