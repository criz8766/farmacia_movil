package cl.cerda.clgrupo3_cerda_ricciardi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText imputUsuario, imputClave;
    private Button botonIniciarSesion;
    private FirebaseAuth mAuth; // Instancia de Firebase Authentication

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Referencias a los elementos de la UI
        imputUsuario = findViewById(R.id.imputUsuario);
        imputClave = findViewById(R.id.imputClave);
        botonIniciarSesion = findViewById(R.id.botonIniciarSesion);

        // Listener para el botón de iniciar sesión
        botonIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciarSesion();
            }
        });
    }

    // Método para iniciar sesión (o registrar si el usuario no existe)
    private void iniciarSesion() {
        String email = imputUsuario.getText().toString().trim();
        String password = imputClave.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            imputUsuario.setError("El correo electrónico es requerido.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            imputClave.setError("La contraseña es requerida.");
            return;
        }

        // Intenta iniciar sesión con el correo y la contraseña
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Inicio de sesión exitoso, navegar a MenuPrincipal
                            Toast.makeText(MainActivity.this, "Inicio de sesión exitoso.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                            startActivity(intent);
                            finish(); // Finaliza MainActivity para que el usuario no pueda volver con el botón de retroceso
                        } else {
                            // Si el inicio de sesión falla, intentar registrar al usuario
                            registrarUsuario(email, password);
                        }
                    }
                });
    }

    // Método para registrar un nuevo usuario
    private void registrarUsuario(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, navegar a MenuPrincipal
                            Toast.makeText(MainActivity.this, "Registro exitoso y sesión iniciada.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Si el registro falla, mostrar un mensaje de error
                            Toast.makeText(MainActivity.this, "Error de autenticación: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}