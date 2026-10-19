package com.example.damn_dyvr_ejercicio_07;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etNombre;
    private CheckBox cbTerms;
    private Button btnContinuar;
    private TextView tvIngresar;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        int horizontalPadding = (int) (28 * getResources().getDisplayMetrics().density);
        int verticalPadding = (int) (20 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.register), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    systemBars.left + horizontalPadding,
                    systemBars.top + verticalPadding,
                    systemBars.right + horizontalPadding,
                    systemBars.bottom + verticalPadding
            );
            return insets;
        });

        db = AppDatabase.getInstance(this);

        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        etNombre = findViewById(R.id.etRegisterNombre);
        cbTerms = findViewById(R.id.cbTerms);
        btnContinuar = findViewById(R.id.btnContinuar);
        tvIngresar = findViewById(R.id.tvIngresar);

        btnContinuar.setOnClickListener(v -> performRegistration());

        tvIngresar.setOnClickListener(v -> finish());
    }

    private void performRegistration() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String nombre = etNombre.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Ingrese su correo electrónico");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingrese su contraseña");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese su nombre");
            etNombre.requestFocus();
            return;
        }

        if (!cbTerms.isChecked()) {
            Toast.makeText(this, "Debe aceptar los términos de servicio para continuar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate user duplicates
        User existingUser = db.userDao().getUserByEmail(email);
        if (existingUser != null) {
            Toast.makeText(this, "El correo electrónico ya está registrado", Toast.LENGTH_LONG).show();
            return;
        }

        User newUser = new User(email, password, nombre);
        long newUserId = db.userDao().insert(newUser);

        if (newUserId > 0) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, PetListActivity.class);
            intent.putExtra("EXTRA_USER_ID", (int) newUserId);
            intent.putExtra("EXTRA_USER_NAME", nombre);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
        }
    }
}
