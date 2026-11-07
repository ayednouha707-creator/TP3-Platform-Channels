package tn.isetbizerte.tp3_channels

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import io.flutter.plugin.common.EventChannel

class CapteurLumiere(context: Context) : EventChannel.StreamHandler, SensorEventListener {

    private val sensorManager = context.getSystemService(SensorManager::class.java)
    private val capteur: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    private var sink: EventChannel.EventSink? = null // le "micro" vers Flutter

    // Flutter commence a ecouter
    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        Log.d("TP3", "onListen")
        if (capteur == null) {
            events.error("PAS_DE_CAPTEUR", "Ce téléphone n'a pas de capteur de lumière", null)
            return
        }
        sink = events
        sensorManager.registerListener(this, capteur, SensorManager.SENSOR_DELAY_NORMAL)
    }

    // Flutter arrete d'ecouter : on libere le capteur (batterie !)
    override fun onCancel(arguments: Any?) {
        Log.d("TP3", "onCancel")
        sensorManager.unregisterListener(this)
        sink = null
    }

    // Nouvelle mesure : on la diffuse vers Flutter
    override fun onSensorChanged(event: SensorEvent) {
        sink?.success(event.values[0].toDouble()) // luminosite en lux
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}