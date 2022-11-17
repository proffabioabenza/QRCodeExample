package br.senac.lojaretrofit.model

data class Produto(
	val descProduto: String,
	val qtdMinEstoque: Int,
	val idProduto: Int,
	val precProduto: Double,
	val descontoPromocao: Double,
	val idCategoria: Int,
	val nomeProduto: String,
	val ativoProduto: Boolean
)
