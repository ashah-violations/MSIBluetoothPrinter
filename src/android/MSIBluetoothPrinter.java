package msi.cordova.bluetooth.zbtprinter;

import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Log;
import android.os.Looper;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterBluetooth;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

public class MSIBluetoothPrinter extends CordovaPlugin {

    private static final String LOG_TAG = "MSIBluetoothPrinter";
    String mac = "";

    public MSIBluetoothPrinter() {
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("sendData")) {
            try {
                String msg = args.getString(0);
                String mac = args.getString(1);
                sendData(callbackContext, msg,mac);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        if (action.equals("findPrinter")) {
            try {
                findPrinter(callbackContext);
            } catch (Exception e) {
                Log.e(LOG_TAG, e.getMessage());
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
    
    public void findPrinter(final CallbackContext callbackContext) {
      new Thread(new Runnable() {
            public void run() {
                DiscoveryHandler btDiscoveryHandler = new DiscoveryHandler() {

                    public void discoveryError(String message) {

                        callbackContext.error(message);
                    }

                    public void discoveryFinished() {

                        callbackContext.error("not found");
                    }

                    public void foundPrinter(final DiscoveredPrinter printer) {
                       DiscoveredPrinterBluetooth p = (DiscoveredPrinterBluetooth) printer;
			if(p.friendlyName=="msizebra"){
				callbackContext.success(p.address);
			}
                        
                    }
                };

                Looper.prepare();
                try {
                    BluetoothDiscoverer.findPrinters(cordova.getActivity(), btDiscoveryHandler);
                } catch (ConnectionException e) {
                    callbackContext.error(e.getMessage());
                } finally {
                    Looper.myLooper().quit();
                }
            }
        }).start();
    }
    private String findMac() {
        try {
            BluetoothDiscoverer.findPrinters(this.cordova.getActivity().getApplicationContext(), new DiscoveryHandler() {
                
                public void foundPrinter(DiscoveredPrinter printer) {
                    DiscoveredPrinterBluetooth p = (DiscoveredPrinterBluetooth) printer;
                    mac = p.address;
                }
                
                public void discoveryFinished() {
                    //Discovery is done
                }
                
                public void discoveryError(String message) {
                }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mac;
    }
    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData(final CallbackContext callbackContext, final String msg, final String macAddress) throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //mac=findMac();
                    // Instantiate insecure connection for given Bluetooth MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(macAddress);

                    // Verify the printer is ready to print
                    if (isPrinterReady(thePrinterConn)) {

                        // Open the connection - physical connection is established here.
                        thePrinterConn.open();

                        // Send the data to printer as a byte array.
						// thePrinterConn.write("^XA^FO0,20^FD^FS^XZ".getBytes());
                        thePrinterConn.write(msg.getBytes());


                        // Make sure the data got to the printer before closing the connection
                        Thread.sleep(500);

                        // Close the insecure connection to release resources.
                        thePrinterConn.close();
                        callbackContext.success("Done");
                    } else {
						callbackContext.error("Printer is not ready");
					}
                } catch (Exception e) {
                    // Handle communications error here.
                    callbackContext.error(e.getMessage());
                }
            }
        }).start();
    }

    private Boolean isPrinterReady(Connection connection) throws ConnectionException, ZebraPrinterLanguageUnknownException {
        Boolean isOK = false;
        connection.open();
        // Creates a ZebraPrinter object to use Zebra specific functionality like getCurrentStatus()
        ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
        PrinterStatus printerStatus = printer.getCurrentStatus();
        //connection.close();
        if (printerStatus.isReadyToPrint) {
            isOK = true;
        } else if (printerStatus.isPaused) {
            throw new ConnectionException("Cannot print because the printer is paused");
        } else if (printerStatus.isHeadOpen) {
            throw new ConnectionException("Cannot print because the printer media door is open");
        } else if (printerStatus.isPaperOut) {
            throw new ConnectionException("Cannot print because the paper is out");
        } else {
            throw new ConnectionException("Cannot print");
        }
        return isOK;
    }
}

