

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:method_channel/method_channel/identy_finger.dart';
import 'package:permission_handler/permission_handler.dart';

class HomePage extends StatefulWidget {

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  Future<void> initPlatformState() async {
    await Permission.camera.request();
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await IdentyFinger.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  Future<void> capture() async {
    Permission.camera.request();
    try{
      var response = await IdentyFinger.capture;
    } catch(Error) {

    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('IDENTY Finger Plugin example'),
      ),
      body: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            padding: EdgeInsets.all(20),
            child: OutlinedButton(
              child: const Text(
                'Capturar',
                style: TextStyle(
                  color: Colors.white,
                    fontSize: 18
                ),
              ),
              onPressed: () { this.capture(); },
              style: OutlinedButton.styleFrom(
                padding: EdgeInsets.symmetric(vertical: 10, horizontal: 30),
                backgroundColor: Colors.blue,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
