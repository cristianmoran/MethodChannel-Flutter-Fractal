package com.example.method_channel

import android.os.Build
import android.util.Base64
import com.identy.*
import com.identy.enums.Finger
import com.identy.enums.FingerDetectionMode
import com.identy.enums.Hand
import com.identy.enums.Template
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.*

class MainActivity: FlutterActivity(), MethodChannel.MethodCallHandler {

    private lateinit var channel: MethodChannel
    private var detectionModes = emptyList<FingerDetectionMode>()

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        channel = MethodChannel(flutterEngine.dartExecutor.binaryMessenger,"identy_finger")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {

        if (call.method == "getPlatformVersion") {
            result.success("Android " + Build.VERSION.RELEASE)
        } else {
            if (call.method == "capture") {
                detectionModes = listOf(FingerDetectionMode.L4F)
                try {
                    val templatesConfig =
                        HashMap<Template, HashMap<Finger, ArrayList<TemplateSize>>>()
                    val sizes = HashMap<Finger, ArrayList<TemplateSize>>()
                    val sises = ArrayList<TemplateSize>()
                    sises.add(TemplateSize.DEFAULT_MINUS_15)
                    sises.add(TemplateSize.DEFAULT_PLUS_15)
                    sises.add(TemplateSize.DEFAULT)
                    sizes[Finger.INDEX] = sises
                    sizes[Finger.MIDDLE] = sises
                    sizes[Finger.RING] = sises
                    sizes[Finger.LITTLE] = sises
                    templatesConfig[Template.WSQ] = sizes
                    IdentySdk.newInstance(
                        activity,
                        "966_io.identy.fingerdemoarthur2021-03-31 00_00_00.lic",
                        { d ->
                            try {
                                d.base64EncodingFlag = Base64.DEFAULT
                                d.setDisplayImages(true)
                                d.setAS(true)
                                d.setRequiredTemplates(templatesConfig)
                                d.setDisplayBoxes(true)
                                d.wsqCompression = WSQCompression.WSQ_10_1
                                d.setDetectionMode(detectionModes.toTypedArray())
                                d.setDebug(true)
                                d.setQC { true }
                                d.capture()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        },
                        object : IdentyResponseListener {
                            override fun onAttempt(hand: Hand, i: Int, map: Map<Finger, Attempt>) {}
                            override fun onResponse(
                                identyResponse: IdentyResponse,
                                hashSet: HashSet<String>
                            ) {
                                val respuesta = identyResponse.toJson(activity).toString()
                                result.success("Respuesta: $respuesta")
                            }

                            override fun onErrorResponse(
                                identyError: IdentyError,
                                hashSet: HashSet<String>
                            ) {
                                val error = identyError.message
                                result.error("403", error, error)
                            }
                        },
                        "AIzaSyDXGkfIpxx3svm7ZAM5H9OFOvCNZxDMegw",
                        true
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                result.notImplemented()
            }
        }
    }

}
