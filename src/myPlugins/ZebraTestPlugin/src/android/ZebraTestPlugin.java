package cordova.plugin.zebra.test;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Random;

/**
 ionic cordova plugin add "./src/myPlugins/ZebraTestPlugin" --save
 */
public class ZebraTestPlugin extends CordovaPlugin {

private Connection connection;


    private static final String bluetoothAddressKey = "ZEBRA_DEMO_BLUETOOTH_ADDRESS";
    private static final String tcpAddressKey = "ZEBRA_DEMO_TCP_ADDRESS";
    private static final String tcpPortKey = "ZEBRA_DEMO_TCP_PORT";
    private static final String PREFS_NAME = "OurSavedAddress";

    private Button testButton;
    private ZebraPrinter printer;
    private TextView statusField;

    private String objednavkaC;
    private String mnozstvi;
    private String vyrobek;
    private String name;
    private String kontrola;
    private String charge;
    private String datum;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
          try{

            JSONObject obj = args.getJSONObject(0);

            objednavkaC = obj.get("objednavkaC").toString();
            mnozstvi = obj.get("mnozstvi").toString();
            vyrobek = obj.get("vyrobek").toString();
            name = obj.get("name").toString();
            kontrola = obj.get("kontrola").toString();
            charge = obj.get("charge").toString();
            datum = obj.get("datum").toString();
            this.coolMethod(callbackContext);
            return true;
          }
          catch(Exception exc){
            callbackContext.error("Chyba při parsování argumentů: " + exc.getMessage());
          }


        }
        return false;
    }

    private void coolMethod(CallbackContext callbackContext) {

        try{
            printer = connect();
            if(printer != null){
              String hlaska = sendTestLabel();
              callbackContext.success(hlaska);
            }else{
              callbackContext.error("Nepřipojeno k tiskárně");
            }
        }
        catch(Exception exc){
          callbackContext.error("EXC: " + exc.toString());
        }
    }

    private String sendTestLabel() {
        try {
            byte[] configLabel = getConfigLabel();
            connection.write(configLabel);
            return "Vytisknuto";
        } catch (ConnectionException e) {
            return "Tahle chyba: " + e.toString();
        } finally {
            disconnect();
        }
    }

    private byte[] getConfigLabel() {
        byte[] configLabel = null;
        try {
            PrinterLanguage printerLanguage = printer.getPrinterControlLanguage();
            SGD.SET("device.languages", "zpl", connection);

            if (printerLanguage == PrinterLanguage.ZPL) {
                //configLabel = "^XA^FO17,16^GB379,371,8^FS^FT65,255^A0N,135,134^FDTEST^FS^XZ".getBytes();
                String label = "^XA\n" +
                        "\n" +
                        "^FT15,108^A0N,28,28^FH\\^CI28^FDObjednávka č. (Bst.Nr.):^FS^CI27\n" +
                        "^FT15,143^A0N,28,28^FH\\^CI28^FDMnožství (Menge):^FS^CI27\n" +
                        "^FT15,178^A0N,28,28^FH\\^CI28^FDArt.Nr.:^FS^CI27\n" +
                        "^FT15,213^A0N,28,28^FH\\^CI28^FD"+name+"^FS^CI27\n" +
                        "^FT15,248^A0N,28,28^FH\\^CI28^FDKontrola:^FS^CI27\n" +
                        "^FT15,283^A0N,28,28^FH\\^CI28^FDCharge Nr.:^FS^CI27\n" +
                        "^FT15,318^A0N,28,28^FH\\^CI28^FDDatum:^FS^CI27\n" +
                        "^FO466,80^GFA,977,1936,16,:Z64:eJylVL1q3EAQnlUQHFeYBG574yrI4AdwpSuu34PV+whXQsU9g1AasQcipDpUuc5TGFdCHPsMmZ9dSXaSwngkbm/1MTPf/AJ8TrbOubqqSjpRyrRgATktJPz5HHEAwXPNhwGYHD3PWz7Hd3gOkLFeEw4A/Q4Xwy+ClxAcBxwJJmt9vCtxrIUeykQyOvrt6C54pBcItFlwHwhAdB8I1NG9ELAQ3QuBbnYvBHKY3XMGfncUfcf2gRXZPdsnApmQEFyjoprdE4GuY/M180cCueChRIlrhV7WBAJCwsQaTohh9K0TfbCQ78l8HhxkwbzED/CVk2DnHviOpjHGEH+MYTYPSU0GWlfPX9TCjgPoqfrRPUCeknp0j3i2RfUQPjNQ+0Ud+TVjVbt6dq9Su1vjWP3ztu2WL0vyGH/y3r+u2Osl+SyPwzAs7CS5S3hwg+q+O813y/jMIGnc2Z27c7Sf7g+IH+cE9KeT95iBl6i+2xW2OESGSdNcyL/7GcgpRdXRkYG/uTmdeu9cL3djyEChY4Ewb40MYYg9VSolPBCYelL32TQ98/1ojJEBEALDr7Ydhja2P1idono6p8D3D6TuYv30IagHAvftD3dulv5PjzrOj2Rv7P31O28BztA3TJ8xy3xlGHx7WebPIJ6u5tN5P2YyYERAWVvkq/lOsDefVvOfFjurV/thO51w+F8yXgMl1f6wf7t/2qf1/pHdsewvbnzcX04IqLC//rf/3u7HAjYVSwmlCHxYNvjAZsMvhm8saAO8gXCQsXs67H9kiYEA5kcdDeNFxF9HeBhh7MFXcCUcd5MB7F4LjOPUPV7g8giDgwv1fg5W7FvGfQnXEbyn9xpwu+BfUB/T4u4hS8R/TojR0f4rK48PJRkqiZ8S8mIiacT5gENIvxifQnLCn/DOJ11G8U38flhKeip86P/f6U/gFu7u8EC5vfuX/qbalBVUVAA8Pyt/ACFU7Rs=:B934\n" +
                        "^FT346,108^A0N,28,28^FH\\^CI28^FD"+objednavkaC+"^FS^CI27\n" +
                        "^FT346,143^A0N,28,28^FH\\^CI28^FD"+mnozstvi+"^FS^CI27\n" +
                        "^FT346,178^A0N,28,28^FH\\^CI28^FD"+vyrobek+"^FS^CI27\n" +
                        "^FT346,248^A0N,28,28^FH\\^CI28^FD"+kontrola+"^FS^CI27\n" +
                        "^FT346,283^A0N,28,28^FH\\^CI28^FD"+charge+"^FS^CI27\n" +
                        "^FT346,318^A0N,28,28^FH\\^CI28^FD"+datum+"^FS^CI27\n" +
                        "^FO1,67^GB598,272,8^FS\n" +
                        "\n" +
                        "^XZ";

                configLabel = label.getBytes();
            } else if (printerLanguage == PrinterLanguage.CPCL) {
                String cpclConfigLabel = "! 0 200 200 406 1\r\n" + "ON-FEED IGNORE\r\n" + "BOX 20 20 380 380 8\r\n" + "T 0 6 137 177 TEST\r\n" + "PRINT\r\n";
                configLabel = cpclConfigLabel.getBytes();
            }
        } catch (ConnectionException e) {

        }
        return configLabel;
    }

    private ZebraPrinter connect(){
        setStatus("Connecting...", Color.YELLOW);
        connection = null;
        if (isBluetoothSelected()) {
            connection = new BluetoothConnection(getMacAddressFieldText());
        } else {
            try {
                int port = Integer.parseInt(getTcpPortNumber());
                connection = new TcpConnection(getTcpAddress(), port);
            } catch (NumberFormatException e) {
                setStatus("Port Number Is Invalid", Color.RED);
                return null;
            }
        }

        try {
            connection.open();
            setStatus("Connected", Color.GREEN);
        } catch (ConnectionException e) {
            setStatus("Comm Error! Disconnecting", Color.RED);
            sleep(1000);
            disconnect();
        }

        ZebraPrinter printer = null;

        if (connection.isConnected()) {
            try {

                printer = ZebraPrinterFactory.getInstance(connection);
                setStatus("Determining Printer Language", Color.YELLOW);
                String pl = SGD.GET("device.languages", connection);
                setStatus("Printer Language " + pl, Color.BLUE);
            } catch (ConnectionException e) {
                setStatus("Unknown Printer Language", Color.RED);
                printer = null;
                sleep(1000);
                disconnect();
            } catch (ZebraPrinterLanguageUnknownException e) {
                setStatus("Unknown Printer Language", Color.RED);
                printer = null;
                sleep(1000);
                disconnect();
            }
        }

        return printer;
    }

    public void disconnect() {
        try {
            setStatus("Disconnecting", Color.RED);
            if (connection != null) {
                connection.close();
            }
            setStatus("Not Connected", Color.RED);
        } catch (ConnectionException e) {
            setStatus("COMM Error! Disconnected", Color.RED);
        } finally {
            enableTestButton(true);
        }
    }

    private void enableTestButton(boolean val){

    }

    private String getTcpAddress(){
        return "192.168.100.139";
    }

    private String getTcpPortNumber(){
        return "9100";
    }

    private boolean isBluetoothSelected(){
        return false;
    }

    private String getMacAddressFieldText(){
        return "01:02:03";
    }

    private void setStatus(final String statusMessage, final int color) {
//        runOnUiThread(new Runnable() {
//            public void run() {
//                outputTextView.setBackgroundColor(color);
//                outputTextView.setText(statusMessage);
//            }
//        });
        //sleep(1000);
    }

    private void sleep(int ms){
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
