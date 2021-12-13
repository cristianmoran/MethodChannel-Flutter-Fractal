import UIKit
import Flutter
import Identy

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
      let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
      let CHANNEL = FlutterMethodChannel(name: "identy_finger", binaryMessenger: controller as! FlutterBinaryMessenger)
      
      CHANNEL.setMethodCallHandler {[unowned self] (methodCall, result) in
          
          if methodCall.method == "capture"{

            var stringValue = ""

            let bundlePath = Bundle.main.path(forResource: "1033_io.identy.fingerdemoarthur2021-02-11 00_00_00", ofType: "lic")!

            let instance = IdentyFramework.init(with: bundlePath, localizablePath: Bundle.main.path(forResource: "en", ofType: "lproj"), table: "Main")
              
              instance.capture(viewcontrol: controller) { responseModel, transactionID, noOfAttempts in
                  stringValue = responseModel?.responseDictionary?.description ?? ""
                  result(stringValue);
              } onFailure: { error, responseModel, transactionID, noOfAttempts in
                  stringValue = error?.errorDescription ?? ""
                  result(stringValue);
              } onAttempts: { responseAttempts in
                  
              }

            
              
          }
          
      }
      
    GeneratedPluginRegistrant.register(with: controller)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
}
