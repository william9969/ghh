package com.example.hp.proyecto;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class ControlarActivity extends Activity {

    Button encenderLed,apagarLed,abrirPuerta,cerrarPuerta,salir;
    Handler bluetoothIn;
    final int handlerState=0;
    private BluetoothAdapter btAdapter=null;
    private BluetoothSocket btSocket=null;
    private StringBuilder DataStringIn= new StringBuilder();
    private ConnectedThread mConnectedThread;

    private static final UUID BTMODULEUUID= UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static String address=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlar);
        encenderLed=(Button) findViewById(R.id.btnEncender);
        apagarLed=(Button)findViewById(R.id.btnApagar);
        abrirPuerta=(Button)findViewById(R.id.btnAbri);
        cerrarPuerta=(Button)findViewById(R.id.btnCerrar);
        salir=(Button)findViewById(R.id.btnSalir);
        bluetoothIn=new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what==handlerState){
                    String readMessage=(String) msg.obj;
                    DataStringIn.append(readMessage);
                    int endOfLineIndex=DataStringIn.indexOf("#");
                    if(endOfLineIndex>0){
                        String dataInPrint =DataStringIn.substring(0,endOfLineIndex);
                        DataStringIn.delete(0,DataStringIn.length());
                    }
                }
            }

        };
        btAdapter=BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();
        encenderLed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("1");
            }
        });
        apagarLed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("2");
            }
        });
        abrirPuerta.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("3");
            }
        });
        cerrarPuerta.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mConnectedThread.write("4");
            }
        });
        salir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(ControlarActivity.this, LogginActivity.class);
                startActivity(intent);
            }
        });
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device)throws IOException{
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent=getIntent();
        address=intent.getStringExtra(BTActivity.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device=btAdapter.getRemoteDevice(address);
        try
        {
            btSocket=createBluetoothSocket(device);
        }catch(IOException e){
            Toast.makeText(getBaseContext(),"Creacion del Socket Fallo", Toast.LENGTH_LONG).show();
        }
        try{
            btSocket.connect();
        }catch (IOException e){
            try{
                btSocket.close();
            }catch(IOException e2){
                           }
        }
        mConnectedThread=new ConnectedThread(btSocket);
        mConnectedThread.start();
    }
    @Override
    public void onPause(){
        super.onPause();
        try{
            btSocket.close();
        }catch (IOException e){}

    }

    private void VerificarEstadoBT(){
        if(btAdapter==null){
            Toast.makeText(getBaseContext(),"El Dispositivo no soporta Bluetooth",Toast.LENGTH_LONG).show();
        }else{
            if(btAdapter.isEnabled()){}
            else{
                Intent enableBTIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent,1);
            }
        }
    }

    public class ConnectedThread extends Thread{
        private final InputStream mmInSream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket){
            InputStream tmpin=null;
            OutputStream tmpout=null;
            try{
                tmpin=socket.getInputStream();
                tmpout=socket.getOutputStream();
            }catch(IOException e){}
            mmInSream = tmpin;
            mmOutStream=tmpout;
        }
        public void run(){
            byte[]buffer= new byte[256];
            int bytes;
            while (true){
                try{
                    bytes=mmInSream.read(buffer);
                    String readMessage=new String(buffer,0,bytes);
                    bluetoothIn.obtainMessage(handlerState,bytes,-1,readMessage).sendToTarget();
                }catch(IOException e){
                    break;
                }

            }
        }

        public void write(String input){
            try {
                mmOutStream.write(input.getBytes());
            }catch (IOException e){
                Toast.makeText(getBaseContext(),"La Conexion Fallo",Toast.LENGTH_LONG).show();
                finish();
            }
        }


    }

}
