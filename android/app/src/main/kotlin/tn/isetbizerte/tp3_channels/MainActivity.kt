package tn.isetbizerte.tp3_channels

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

class MainActivity : FlutterActivity() {

    private val scope = MainScope() // portee des coroutines de l'activite
    private var resultatPermission: MethodChannel.Result? = null // reponse en attente

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        val messager = flutterEngine.dartExecutor.binaryMessenger

        // Etape 1 : infos de l'appareil
        MethodChannel(messager, "appareil/infos").setMethodCallHandler { call, result ->
            when (call.method) {
                "lire" -> result.success(
                    mapOf(
                        "marque" to Build.MANUFACTURER,
                        "modele" to Build.MODEL,
                        "version" to Build.VERSION.RELEASE,
                        "sdk" to Build.VERSION.SDK_INT
                    )
                )
                else -> result.notImplemented()
            }
        }

        // Etape 2 : vibration
        MethodChannel(messager, "appareil/vibrer").setMethodCallHandler(Vibreur(this))

        // Etape 3 : capteur de lumiere
        EventChannel(messager, "capteur/lumiere").setStreamHandler(CapteurLumiere(this))

        // Etape 4a : permission du micro
        MethodChannel(messager, "audio/permission").setMethodCallHandler { call, result ->
            when (call.method) {
                "demander" -> {
                    if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED
                    ) {
                        result.success(true) // deja accordee
                    } else {
                        resultatPermission = result // "je vous rappelle"
                        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQ_MICRO)
                    }
                }
                else -> result.notImplemented()
            }
        }

        // Etape 4b : niveau sonore
        EventChannel(messager, "audio/niveau").setStreamHandler(NiveauSonore(scope))

        // Exercice 1 : capteur de proximite
        EventChannel(messager, "capteur/proximite").setStreamHandler(CapteurProximite(this))

        // Exercice 2 : lampe torche
        MethodChannel(messager, "appareil/torche").setMethodCallHandler(Torche(this))
    }

    // L'utilisateur a repondu a la boite de dialogue
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_MICRO) {
            resultatPermission?.success(
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
            )
            resultatPermission = null
        }
    }

    override fun onDestroy() {
        scope.cancel() // annule les coroutines encore actives
        super.onDestroy()
    }

    companion object {
        private const val REQ_MICRO = 42
    }
}