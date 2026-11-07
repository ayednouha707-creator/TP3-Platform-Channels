package tn.isetbizerte.tp3_channels

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel

// Exercice 2 : allumer / eteindre la lampe torche
class Torche(context: Context) : MethodChannel.MethodCallHandler {

    private val cameraManager = context.getSystemService(CameraManager::class.java)

    // On cherche la camera qui possede un flash
    private val cameraId: String? = try {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    } catch (e: Exception) {
        null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "allumer" -> {
                val etat = call.argument<Boolean>("etat") ?: false
                val id = cameraId
                if (id == null) {
                    result.error("PAS_DE_FLASH", "Cet appareil n'a pas de flash", null)
                    return
                }
                try {
                    cameraManager.setTorchMode(id, etat)
                    result.success(null)
                } catch (e: Exception) {
                    result.error("TORCHE_INDISPONIBLE", e.message, null)
                }
            }
            else -> result.notImplemented()
        }
    }
}