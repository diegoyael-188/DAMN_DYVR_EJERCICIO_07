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

public class UpdatePetActivity extends AppCompatActivity {

    private TextView tvUserNameHeader;
    private EditText etNombre, etRaza, etEdad;
    private Button btnActualizar, btnRegresar;

    private AppDatabase db;
    private Pet currentPet;
    private int petId = -1;
    private int userId = -1;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_pet);

        int horizontalPadding = (int) (28 * getResources().getDisplayMetrics().density);
        int verticalPadding = (int) (20 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.update_pet_root), (v, insets) -> {
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

        petId = getIntent().getIntExtra("EXTRA_PET_ID", -1);
        userId = getIntent().getIntExtra("EXTRA_USER_ID", -1);
        userName = getIntent().getStringExtra("EXTRA_USER_NAME");
        if (userName == null) userName = "Usuario";

        tvUserNameHeader = findViewById(R.id.tvUserNameHeader);
        etNombre = findViewById(R.id.etNombre);
        etRaza = findViewById(R.id.etRaza);
        etEdad = findViewById(R.id.etEdad);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnRegresar = findViewById(R.id.btnRegresar);

        tvUserNameHeader.setText(userName);

        if (petId != -1) {
            currentPet = db.petDao().getPetById(petId);
            if (currentPet != null) {
                etNombre.setText(currentPet.getNombre());
                etRaza.setText(currentPet.getRaza());
                etEdad.setText(currentPet.getEdad());
            } else {
                Toast.makeText(this, "Error: mascota no encontrada", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "Error: ID de mascota inválido", Toast.LENGTH_SHORT).show();
            finish();
        }

        btnActualizar.setOnClickListener(v -> updatePet());
        btnRegresar.setOnClickListener(v -> finish());
    }

    private void updatePet() {
        if (currentPet == null) return;

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

        currentPet.setNombre(nombre);
        currentPet.setRaza(raza);
        currentPet.setEdad(edad);

        db.petDao().update(currentPet);
        Toast.makeText(this, "Mascota actualizada correctamente", Toast.LENGTH_SHORT).show();
        finish();
    }
}
