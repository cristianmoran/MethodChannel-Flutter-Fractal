import 'package:app_mc/canales.dart';
import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  var canales = Canales();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: FutureBuilder(
          future: canales.getBatteryLevel(),
          builder: (BuildContext context, AsyncSnapshot<String> snapshot) {
            String nivel ="";
            if(snapshot.hasData){
              nivel = snapshot.data ?? "";
            }
            return Text(nivel);
          },
        ),
      ),
      floatingActionButton: FloatingActionButton(onPressed: ()=>canales.getLocationUpdates(),),
    );
  }
}
