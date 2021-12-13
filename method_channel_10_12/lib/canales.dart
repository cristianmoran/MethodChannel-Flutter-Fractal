



import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Canales{
  static const platform = MethodChannel('com/background_location');
  static const EventChannel _channel = EventChannel('com/background_location');
  StreamSubscription? stream;

  Canales(){
    // WidgetsFlutterBinding.ensureInitialized();
    // platform.setMethodCallHandler(_fromNative);
  }

  Future<String> getBatteryLevel() async {
    String batteryLevel ="vacio";
    // try {
    //   final int result = await platform.invokeMethod('getBatteryLevel');
    //    batteryLevel = 'Battery level at $result % .';
    // } on PlatformException catch (e) {
    //   batteryLevel = "Failed to get battery level: '${e.message}'.";
    // }

    return batteryLevel;
  }

  getLocationUpdates() {
    stream = _channel.receiveBroadcastStream().listen((event){
      print("Las coordenadas son  $event");
    });
  }


  Future<void> obtenerCoordenadas() async {
    try {
      await platform.invokeMethod('startLocation');
    } on PlatformException catch (e) {
    }
  }

  Future<void> _fromNative(MethodCall call) async {
    if (call.method == 'callTestResuls') {

      print("Las coordenads son : ${call.arguments}");

    }
  }


}