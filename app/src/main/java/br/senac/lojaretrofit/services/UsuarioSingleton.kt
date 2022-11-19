package br.senac.lojaretrofit.services

/**
 * Singletons são usado para manter instância única de elementos na aplicação
 * Todas as variáveis armazenadas em singletons permanecem com valor enquanto
 * a aplicação está em execução e permitem que atividades e fragmentos compartilhem
 * e armazenem informações em formato de sessão (quando a aplicação é fechada, esses
 * dados se perdem).
 *
 * Pra criar um singleton, clique com o botão direito na pasta onde deseja que o singleton
 * seja criado e escolha New > Kotlin Class/File > Object
 * **/
object UsuarioSingleton {
    //Esta variável pode ser acessada ou modificada de qualquer atividade ou fragmento
    var idUsuario: Int? = null
}