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
 * This class echoes a string called from JavaScript.
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

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            connect();
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
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
            return "192.168.100.1";
        }

        private String getTcpPortNumber(){
            return "456";
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
            sleep(1000);
        }

        private void sleep(int ms){
            try {
                Thread.sleep(ms);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
}
