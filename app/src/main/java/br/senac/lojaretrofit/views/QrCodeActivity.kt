package br.senac.lojaretrofit.views

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.senac.lojaretrofit.R
import br.senac.lojaretrofit.databinding.ActivityQrCodeBinding
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat

/** Atividade para leitura de QRCode (copiar para sua aplicação) **/
class QrCodeActivity : AppCompatActivity() {
    /** Variável de bind automático **/
    lateinit var binding: ActivityQrCodeBinding
    /** Variáve que armazena a instância do CodeScanner **/
    lateinit var leitorQr: CodeScanner
    /**  Variável que armazena se a permissão de acesso a câmera foi ou não concedida **/
    var permissaoConcedida = false

    /** Função de callback chamada quando a atividade é criada **/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /** Infla o layout usando binding automático **/
        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        /** Coloca o layout carregado na tela **/
        setContentView(binding.root)

        /** Chama a função para verificar e pedir permissão da câmera **/
        verificarPermissaoCamera()
    }

    /** Função chamada para verificar se há permissão de acesso a câmera **/
    private fun verificarPermissaoCamera() {
        //Pergunta ao contexto do Android se a permissão de acesso a câmera ainda não foi concedida
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //Caso não tenha a permissão, solicita a permissão de câmera ao usuário
            //O requestCode fornecido aqui será usado em onRequestPermissionsResult
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1)
        } else {
            //Se já tinha a permissão, configura a variável para indicar essa situação
            permissaoConcedida = true
            //Chama a função que solicita a inicialização do leitor de QRCodes usando a câmera
            inicializarLeitorQrCode()
        }
    }

    /** Inicializa o leitor de QRCode **/
    private fun inicializarLeitorQrCode() {
        //Obtém uma instância do leitor de QRCodes fornecendo a view do XML para exibição da câmera
        leitorQr = CodeScanner(this, binding.scannerView)

        //Define qual câmera usar, frontal ou traseira
        leitorQr.camera = CodeScanner.CAMERA_BACK
        //Define quais tipos de códigos de barra serão detectados
        leitorQr.formats = listOf(BarcodeFormat.QR_CODE)
        //Habilita o foco automático
        leitorQr.isAutoFocusEnabled = true
        //Configura o foco automático como seguro (vs agressivo)
        leitorQr.autoFocusMode = AutoFocusMode.SAFE
        //Configura a leitura de um único QRCode (vs contínuo)
        leitorQr.scanMode = ScanMode.SINGLE
        //Desabilita o flash ao ligar o scanner
        leitorQr.isFlashEnabled = false

        //Callback chamado caso o leitor de QRCode consiga detectar um QRCode válidp
        leitorQr.decodeCallback = DecodeCallback {
            //O parâmetro it contém o QRCode lido
            //Cria um intent de resposta para devolver o QRCode a quem chamou
            val respIntent = Intent()
            //Coloca o QRCode lido dentro do intent, num extra com nome qrcode
            respIntent.putExtra("qrcode", it.text)
            //Configura o intent como o intent de resposta
            setResult(RESULT_OK, respIntent)
            //Encerra a atividade de QRCode e retorna para a anterior
            finish()
        }

        //Configura um callback que é disparado caso ocorra algum erro
        leitorQr.errorCallback = ErrorCallback {
            //Mostra uma mensagem de erro caso não consiga abrir a câmera
            Snackbar.make(binding.root, "Não foi possível abrir a câmera", Snackbar.LENGTH_LONG).show()
            //Loga o erro no console
            Log.e("QrCodeActivity", "inicializarLeitorQrCode", it)
            //Configura o resultado de resposta como cancelado
            setResult(RESULT_CANCELED)
            //Encerra a atividade de QRCode e retorna para a anterior
            finish()
        }

        //Inicializa o leitor de QRCode
        leitorQr.startPreview()
    }

    /**
     *  Callback executado quando o usuário responder ao diálogo de solicitação de permissão,
     *  aberto quando solicitamos a permissão caso não tenha na função verificarPermissaoCamera
     **/
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        //Necessário para correta configuração de permissões
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //Verifica se a requisição de permissão feita foi a que fizemos em verificarPermissaoCamera
        if (requestCode == 1) {
            //Verifica se a permissão foi concedida
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Se foi concedida, configuramos a variável que indica isso de acordo...
                permissaoConcedida = true
                //...e inicializamos o leitor de QRCodes
                inicializarLeitorQrCode()
            //Se a permissão não foi concedida e o usuário pediu para não perguntar mais...
            } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                //...chamamos uma função para mostrar um alerta e avisá-lo que ele
                //precisa ir até configurações para ativar a permissão manualmente,
                //ou não será possível ler QRCodes
                mostrarDialogoPermissaoCamera()
            //Se nada mais der certo, cancela tudo
            } else {
                //Configura a permissão como negada na variável indicativa
                permissaoConcedida = false
                //Mostra um aviso sobre a falta de permissão do uso da câmera
                Snackbar.make(binding.root,
                    "Sem permissão de uso da câmera não é possível ler QR Codes. Habilite a permissão nas configurações da aplicação do Android", Snackbar.LENGTH_LONG).show()
                //Configura o resultado da atividade como cancelado
                setResult(RESULT_CANCELED)
                //Encerra a atividade de QRCodes e retorna para a anterior
                finish()
            }
        }
    }

    /** Função utilizada para auxiliar a configuração das permissões pelo usuário caso ele tenha
     *  negado a primeira vez. Chamada em onRequestPermissionsResult **/
    private fun mostrarDialogoPermissaoCamera() {
        //Cria um diálogo informativo e configura sua interface
        //O diálogo questonará se o usuário quer abrir as configurações para habilitar
        //a permissão da câmera
        AlertDialog.Builder(this)
            .setTitle("Permição de câmera")
            .setMessage(
                "Habilite a permissão de uso da câmera do aplicativo em Configurações"
            )
            .setCancelable(false)
            .setPositiveButton(
                "Ir para configurações") { dialogInterface, i ->
                //Cria um intent para abrir as configurações do Android e o inicia
                val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                i.data = uri
                startActivity(i)
                //Encerra a atividade do QRCode e retorna resultado como cancelado
                setResult(RESULT_CANCELED)
                finish()
            }
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->
                //Encerra a atividade do QRCode e retorna resultado como cancelado
                setResult(RESULT_CANCELED)
                finish()
            })
            .create()
            .show()
    }
}

