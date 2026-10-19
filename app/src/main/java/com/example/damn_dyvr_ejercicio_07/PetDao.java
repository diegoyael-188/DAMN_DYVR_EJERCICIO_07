package com.example.damn_dyvr_ejercicio_07;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PetDao {
    @Insert
    long insert(Pet pet);

    @Update
    void update(Pet pet);

    @Delete
    void delete(Pet pet);

    @Query("SELECT * FROM pets WHERE userId = :userId ORDER BY id DESC")
    List<Pet> getPetsByUserId(int userId);

    @Query("SELECT * FROM pets WHERE userId = :userId AND (LOWER(nombre) LIKE '%' || LOWER(:query) || '%' OR LOWER(raza) LIKE '%' || LOWER(:query) || '%') ORDER BY id DESC")
    List<Pet> searchPetsByUserId(int userId, String query);

    @Query("SELECT * FROM pets WHERE id = :petId LIMIT 1")
    Pet getPetById(int petId);
}
