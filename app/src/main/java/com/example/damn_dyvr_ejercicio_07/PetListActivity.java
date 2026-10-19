package com.example.damn_dyvr_ejercicio_07;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PetListActivity extends AppCompatActivity {

    private TextView tvUserNameHeader, tvEmptyList;
    private ImageButton btnAddPet;
    private EditText etSearchPet;
    private ListView lvPets;
    private Button btnSalir;

    private AppDatabase db;
    private PetAdapter petAdapter;
    private List<Pet> petList = new ArrayList<>();

    private int userId = -1;
    private String userName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pet_list);

        int horizontalPadding = (int) (24 * getResources().getDisplayMetrics().density);
        int verticalPadding = (int) (16 * getResources().getDisplayMetrics().density);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pet_list_root), (v, insets) -> {
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
        tvEmptyList = findViewById(R.id.tvEmptyList);
        btnAddPet = findViewById(R.id.btnAddPet);
        etSearchPet = findViewById(R.id.etSearchPet);
        lvPets = findViewById(R.id.lvPets);
        btnSalir = findViewById(R.id.btnSalir);

        tvUserNameHeader.setText(userName);

        petAdapter = new PetAdapter(this, petList);
        lvPets.setAdapter(petAdapter);

        btnAddPet.setOnClickListener(v -> {
            Intent intent = new Intent(PetListActivity.this, AddPetActivity.class);
            intent.putExtra("EXTRA_USER_ID", userId);
            intent.putExtra("EXTRA_USER_NAME", userName);
            startActivity(intent);
        });

        lvPets.setOnItemClickListener((parent, view, position, id) -> {
            Pet selectedPet = petAdapter.getItem(position);
            if (selectedPet != null) {
                Intent intent = new Intent(PetListActivity.this, UpdatePetActivity.class);
                intent.putExtra("EXTRA_PET_ID", selectedPet.getId());
                intent.putExtra("EXTRA_USER_ID", userId);
                intent.putExtra("EXTRA_USER_NAME", userName);
                startActivity(intent);
            }
        });

        lvPets.setOnItemLongClickListener((parent, view, position, id) -> {
            Pet selectedPet = petAdapter.getItem(position);
            if (selectedPet != null) {
                showDeleteConfirmationDialog(selectedPet);
            }
            return true;
        });

        etSearchPet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPets(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSalir.setOnClickListener(v -> {
            Intent intent = new Intent(PetListActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPets();
    }

    private void loadPets() {
        String currentQuery = etSearchPerText();
        filterPets(currentQuery);
    }

    private String etSearchPerText() {
        return etSearchPet.getText().toString().trim();
    }

    private void filterPets(String query) {
        if (userId == -1) return;

        if (query.isEmpty()) {
            petList = db.petDao().getPetsByUserId(userId);
        } else {
            petList = db.petDao().searchPetsByUserId(userId, query);
        }

        petAdapter.updatePets(petList);

        if (petList == null || petList.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
            lvPets.setVisibility(View.GONE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
            lvPets.setVisibility(View.VISIBLE);
        }
    }

    private void showDeleteConfirmationDialog(Pet pet) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar mascota")
                .setMessage("¿Esta seguro que quiere eliminar a la mascota " + pet.getNombre() + "?")
                .setCancelable(false)
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    db.petDao().delete(pet);
                    Toast.makeText(PetListActivity.this, "Mascota eliminada", Toast.LENGTH_SHORT).show();
                    loadPets();
                })
                .show();
    }
}
