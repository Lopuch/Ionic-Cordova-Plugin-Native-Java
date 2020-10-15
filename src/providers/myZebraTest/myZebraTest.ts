import {Injectable} from '@angular/core';
import {cordova, IonicNativePlugin} from '@ionic-native/core';

@Injectable({
  providedIn: 'root'
})
export class MyZebraTest extends IonicNativePlugin {
  //name in package.json file of plugin
  static pluginName = 'zebratestplugin';
  // plugin id in the plugin.xml of plugin
  static plugin = 'cordova-plugin-zebra-test';
  // clobbers target in the plugin.xml of plugin
  static pluginRef = 'ZebraTestPlugin';
  static repo = 'YOUR_GITHUB_URL';
  static platforms = ['Android'];

  coolMethod(args: {objednavkaC: string, mnozstvi: string, vyrobek: string, name: string, kontrola: string, charge: string, datum: string}): Promise<boolean> {

    //return cordova(this, "coolMethod", {}, arguments/*[{param1: 1, param2: 2}]*/);
    return cordova(this, 'coolMethod', {}, [
      args]);
  };

  // /**
  //  * Switches the flashlight on
  //  */
  // switchOn(): Promise<boolean>{
  //   const x = cordova(this, 'switchOn', {}, arguments);
  //   return x;
  // };
  //
  // /**
  //  * Switches the flashlight off
  //  */
  // switchOff(): Promise<boolean>{
  //   const x = cordova(this, 'switchOff', {}, arguments);
  //   return x;
  // };
}
