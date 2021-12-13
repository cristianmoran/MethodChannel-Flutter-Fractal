
import 'dart:async';

import 'package:flutter/services.dart';

class IdentyFinger {
  static const MethodChannel _channel = const MethodChannel('identy_finger');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String?> get capture async {
    final String? result = (await _channel.invokeListMethod('capture')) as String?;
    return result;
  }

}
