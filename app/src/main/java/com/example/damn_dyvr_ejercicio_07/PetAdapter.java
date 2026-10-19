package com.example.damn_dyvr_ejercicio_07;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class PetAdapter extends BaseAdapter {

    private List<Pet> petList;
    private final LayoutInflater inflater;

    public PetAdapter(Context context, List<Pet> petList) {
        this.petList = petList;
        this.inflater = LayoutInflater.from(context);
    }

    public void updatePets(List<Pet> newPets) {
        this.petList = newPets;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return petList != null ? petList.size() : 0;
    }

    @Override
    public Pet getItem(int position) {
        return petList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return petList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_pet, parent, false);
            holder = new ViewHolder();
            holder.tvNombre = convertView.findViewById(R.id.tvPetNombre);
            holder.tvDetails = convertView.findViewById(R.id.tvPetDetails);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Pet pet = getItem(position);
        if (pet != null) {
            holder.tvNombre.setText(pet.getNombre());

            StringBuilder details = new StringBuilder();
            if (pet.getRaza() != null && !pet.getRaza().trim().isEmpty()) {
                details.append("Raza: ").append(pet.getRaza());
            }
            if (pet.getEdad() != null && !pet.getEdad().trim().isEmpty()) {
                if (details.length() > 0) {
                    details.append(" | ");
                }
                details.append("Edad: ").append(pet.getEdad());
            }

            if (details.length() == 0) {
                holder.tvDetails.setVisibility(View.GONE);
            } else {
                holder.tvDetails.setVisibility(View.VISIBLE);
                holder.tvDetails.setText(details.toString());
            }
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView tvNombre;
        TextView tvDetails;
    }
}
