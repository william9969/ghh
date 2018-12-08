package com.example.hp.proyecto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogginActivity extends Activity {
    private EditText usuario;
    private EditText contraseña;
    private Button Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggin);
        usuario=(EditText)findViewById(R.id.etext1);
        contraseña=(EditText)findViewById(R.id.etxt2);
        Login=(Button)findViewById(R.id.btnIngresar);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuar=usuario.getText().toString().trim();
                String contr=contraseña.getText().toString().trim();

                if((usuar.equals("william")) && (contr.equals("1234"))){
                    Intent intent =new Intent(LogginActivity.this, BTActivity.class);
                    startActivity(intent);
                    return;

                }
                if((usuar.equals("carmen")) && (contr.equals("5678"))){
                    Intent intent =new Intent(LogginActivity.this, BTActivity.class);
                    startActivity(intent);
                    return;
                }

                else if((!usuar.equals("carmen")&& !contr.equals("5678"))||(!usuar.equals("william")&& !contr.equals("1234"))){
                    Toast.makeText(LogginActivity.this,"Datos Mal Ingresados", Toast.LENGTH_SHORT).show();
                    usuario.setText("");
                    contraseña.setText("");
                }
            }
        });

    }

}
