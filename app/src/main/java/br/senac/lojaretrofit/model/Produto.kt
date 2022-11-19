package br.senac.lojaretrofit.model

/** Classe de modelo que representa um produto **/
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
