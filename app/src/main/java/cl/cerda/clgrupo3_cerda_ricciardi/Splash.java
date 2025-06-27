package cl.cerda.clgrupo3_cerda_ricciardi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth; // Importar FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Importar FirebaseUser

public class Splash extends AppCompatActivity {

    private FirebaseAuth mAuth; // Declarar FirebaseAuth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance(); // Inicializar FirebaseAuth

        Handler miHandler = new Handler();

        miHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (hayConexion(Splash.this)) {
                    FirebaseUser currentUser = mAuth.getCurrentUser(); // Obtener el usuario actual de Firebase

                    if (currentUser != null) {
                        // Si ya hay un usuario logueado, ir directamente a MenuPrincipal
                        startActivity(new Intent(Splash.this, MenuPrincipal.class));
                    } else {
                        // Si no hay usuario logueado, ir a MainActivity (login)
                        startActivity(new Intent(Splash.this, MainActivity.class));
                    }
                    finish(); // Finalizar Splash Activity
                } else {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(Splash.this);
                    alertBuilder.setTitle("Estamos en la B");
                    alertBuilder.setMessage("Necesitamos internet para continuar");
                    alertBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog dialog = alertBuilder.show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                }
            }
        }, 2000); // Retraso de 2 segundos
    }

    public boolean hayConexion(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo datos = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if(datos != null && datos.isConnectedOrConnecting() || wifi != null && wifi.isConnectedOrConnecting()){
                //Celular modo wifi o datos
                return true;
            }else{
                return true;
            }
        }else{
            //Celular modo avión
            return false;
        }
    }
}