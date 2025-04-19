package com.example.a13_zpl_print_sample_01;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.SGD;
import android.Manifest;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private static final String btMac = "04:7F:0E:7E:EE:64";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkAndRequestBluetoothPermissions();



        Button btnPrint = findViewById(R.id.btnPrint);
        Button btnSgd = findViewById(R.id.btnSgd);

        findViewById(R.id.btnPrint).setOnClickListener(v -> {
            //Log.v("ZZZ", "btnPrint pressed");
            sendCpclOverBluetooth(btMac);
        });

        findViewById(R.id.btnSgd).setOnClickListener(v -> {
            //Log.v("ZZZ", "btnSgd pressed");
            sendSgdOverBluetooth(btMac);
        });

    }



    private void checkAndRequestBluetoothPermissions() {

        String[] permissions = {
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        // Check permission
        boolean permissionsNeeded = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded = true;
            }
        }

        // Request premission if needed
        if (permissionsNeeded) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_BLUETOOTH_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    // Reject
                    //
                }
            }
        }
    }

    // SGDによるステータス情報の取得
    private void sendSgdOverBluetooth(final String theBtMacAddress) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    thePrinterConn.open();
                    //SGD.SET("print.tone", "15", thePrinterConn);
                    //String printTone = SGD.GET("print.tone", thePrinterConn);

                    // プリンタの状態  ready = 印刷可能、データ受信可能、   busy = 稼働中、エラー
                    String deviceStatus = SGD.GET("device.status", thePrinterConn);
                    // 用紙の状態    ok = 用紙あり、out = 用紙なし
                    String mediaStatus = SGD.GET("media.status", thePrinterConn);
                    String sensorPaper = SGD.GET("sensor.paper_supply", thePrinterConn);
                    // 用紙カバーの状態     ok = ロック、out = リリース中
                    String headOpen = SGD.GET("head.latch", thePrinterConn);

                    /*
                    Log.v("ZZZ","SGD device.status is " + deviceStatus);
                    Log.v("ZZZ","SGD media.status is " + mediaStatus);
                    Log.v("ZZZ","SGD sensor.paper_supply is " + sensorPaper);
                    Log.v("ZZZ","SGD head.latch is " + headOpen);
                     */

                    // definePrinterStatus
                    Log.v("ZZZ","Printer status: " + definePrinterStatus(deviceStatus, mediaStatus, headOpen));


                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(300);

                    // Close the insecure connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // プリンタステータスの判定テーブル
    private String definePrinterStatus(String deviceStat, String mediaStat, String latchStat){
        String status = "null";

        // Get current status
        String[] currentStat = {deviceStat, mediaStat, latchStat};

        // Golden status image
        String[][] evaluationStat = {
                {"ready",   "ok",   "ok"},      //0 Ready
                {"busy",    "?",    "open" },   //1 Cover Open
                {"busy",    "out",  "ok" }      //2 Out of paper
        };

        // Contrast golden image and current status
        int cnt = -1;
        for (String[] eStat: evaluationStat){
            cnt ++;
            if (Arrays.equals(eStat, currentStat)){
                break;
            }
        }

        // Return status msg
        switch(cnt){
            case 0:
                status = "Ready to print.";
            break;
            case 1:
                status = "Error. Media cover is open";
                break;
            case 2:
                status = "Error. Paper out.";
                break;
            default:
                status = "Unexpected error.";
        }

        return status;
    }

    // 印刷処理
    private void sendCpclOverBluetooth(final String theBtMacAddress) {

        new Thread(new Runnable() {
            public void run() {
                try {
                    // Instantiate insecure connection for given Bluetooth&reg; MAC Address.
                    Connection thePrinterConn = new BluetoothConnectionInsecure(theBtMacAddress);

                    // Initialize
                    Looper.prepare();

                    // Open the connection - physical connection is established here.
                    thePrinterConn.open();

                    //check printer status before sending print job
                    // プリンタの状態  ready = 印刷可能、busy = 稼働中、エラー
                    String deviceStatus = SGD.GET("device.status", thePrinterConn);
                    if (!Objects.equals(deviceStatus, "ready")) {
                        sendSgdOverBluetooth(btMac);
                        Log.v("ZZZ","Printer is not ready. Check status." );
                        return;
                    }


                    /*
                    String cpclData = "! 0 200 200 203 1\r\n" +
                            "PW 400\r\n" +
                            "TONE 0\r\n" +
                            "SPEED 2\r\n" +
                            "SETFF 203 5\r\n" +
                            "ON-FEED FEED\r\n" +
                            "NO-PACE\r\n" +
                            "JOURNAL\r\n" +
                            "BT 0 0 3\r\n" +
                            "B CODABAR 1 20 100 30 30 A123456789012A\r\n" +
                            "PRINT\r\n";

                     */

                    String cpclData = "! 0 200 200 200 1\r\n" +
                            "; Set the country\r\n" +
                            "COUNTRY JAPAN-S\r\n" +
                            "; Print CPF fonts\r\n" +
                            "TEXT GT16NF55.CPF 0 0 0 サイズ16\r\n" +
                            "TEXT GT24NF55.CPF 0 0 30 サイズ24\r\n" +
                            "PRINT\r\n";


                    // Send the data to printer as a byte array.
                    //thePrinterConn.write(cpclData.getBytes());

                    // Convert to Shift-JIS in case of using Japanese fonts.
                    thePrinterConn.write(cpclData.getBytes("Shift-JIS"));

                    // Make sure the data got to the printer before closing the connection
                    Thread.sleep(300);

                    // Close the insecure connection to release resources.
                    thePrinterConn.close();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    // Handle communications error here.
                    e.printStackTrace();
                }
            }
        }).start();
    }

}

