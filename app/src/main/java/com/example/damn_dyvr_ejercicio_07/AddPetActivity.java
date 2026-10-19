package com.example.damn_dyvr_ejercicio_07;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddPetActivity extends AppCompatActivity {

    private TextView tvUserNameHeader;
    private EditText etNombre, etRaza, etEdad;
    private Button btnGuardar, btnRegresar;

    private AppDatabase db;
    private int userId = -1;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_pet);

        int horizontalPadding = (int) (28 * getResources().getDisplayMetrics().density);
        int verticalPadding = (int) (20 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_pet_root), (v, insets) -> {
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

        userId = getIntent().getIntExtra("EXTRA_USER_ID", -1);
        userName = getIntent().getStringExtra("EXTRA_USER_NAME");
        if (userName == null) userName = "Usuario";

        tvUserNameHeader = findViewById(R.id.tvUserNameHeader);
        etNombre = findViewById(R.id.etNombre);
        etRaza = findViewById(R.id.etRaza);
        etEdad = findViewById(R.id.etEdad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnRegresar = findViewById(R.id.btnRegresar);

        tvUserNameHeader.setText(userName);

        btnGuardar.setOnClickListener(v -> savePet());
        btnRegresar.setOnClickListener(v -> finish());
    }

    private void savePet() {
        String nombre = etNombre.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String edad = etEdad.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingrese el nombre de la mascota");
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(raza)) {
            etRaza.setError("Ingrese la raza");
            etRaza.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(edad)) {
            etEdad.setError("Ingrese la edad");
            etEdad.requestFocus();
            return;
        }

        if (userId == -1) {
            Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        Pet newPet = new Pet(userId, nombre, raza, edad);
        long result = db.petDao().insert(newPet);

        if (result > 0) {
            Toast.makeText(this, "Mascota guardada correctamente", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al guardar la mascota", Toast.LENGTH_SHORT).show();
        }
    }
}
