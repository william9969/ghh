package com.example.hp.proyecto;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class BTActivity extends Activity {

    private static final String TAG="BTActivity";
    ListView idLista;
    public static String EXTRA_DEVICE_ADDRESS="device_address";

    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);
    }


    @Override
    public void onResume(){
        super.onResume();
        VerificarEstadoBT();
        mPairedDevicesArrayAdapter=new ArrayAdapter<String>(this,R.layout.nombre_dispositivos);
        idLista=(ListView) findViewById(R.id.idLista);
        idLista.setAdapter(mPairedDevicesArrayAdapter);
        idLista.setOnItemClickListener(mDeviceClickListener);
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices= mBtAdapter.getBondedDevices();

        if(pairedDevices.size()>0){
            for (BluetoothDevice device : pairedDevices){
                mPairedDevicesArrayAdapter.add(device.getName()+"\n"+device.getAddress());
            }
        }

    }



    private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);

            // Make an intent to start next activity while taking an extra which is the MAC address.
            Intent i = new Intent(BTActivity.this, ControlarActivity.class);
            i.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(i);
        }
    };

    private void VerificarEstadoBT(){
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){
            Toast.makeText(getBaseContext(),"El Dispositivo no Soporta Bluetooth",Toast.LENGTH_SHORT).show();

        }
        else{
            if(mBtAdapter.isEnabled()){
                Log.d(TAG,".... Bluetooth Activado...");
            }
            else {
                Intent enableBtIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1);
            }
        }
    }


}
