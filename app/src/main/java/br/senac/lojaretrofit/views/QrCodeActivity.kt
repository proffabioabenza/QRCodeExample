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
            val respIntent = Intent()
            respIntent.putExtra("qrcode", it.text)
            setResult(RESULT_OK, respIntent)
            finish()
        }

        leitorQr.errorCallback = ErrorCallback {
            Toast.makeText(this, "Não foi possível abrir a câmera", Toast.LENGTH_LONG).show()
            Log.e("QrCodeActivity", "inicializarLeitorQrCode", it)
            setResult(RESULT_CANCELED)
            finish()
        }

        leitorQr.startPreview()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissaoConcedida = true
                inicializarLeitorQrCode()
            } else if (!shouldShowRequestPermissionRationale(permissions[0])) {
                mostrarDialogoPermissaoCamera()
            } else {
                permissaoConcedida = false
                Toast.makeText(this,
                    "Sem permissão de uso da câmera não é possível ler QR Codes", Toast.LENGTH_LONG).show()
                setResult(RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun mostrarDialogoPermissaoCamera() {
        AlertDialog.Builder(this)
            .setTitle("Permição de câmera")
            .setMessage(
                "Habilite a permissão de uso da câmera do aplicativo em Configurações"
            )
            .setCancelable(false)
            .setPositiveButton(
                "Configurações") { dialogInterface, i ->
                val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                i.data = uri
                startActivity(i)
                setResult(RESULT_CANCELED)
                finish()
            }
            .setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialogInterface, i ->
                setResult(RESULT_CANCELED)
                finish()
            })
            .create()
            .show()
    }
}

