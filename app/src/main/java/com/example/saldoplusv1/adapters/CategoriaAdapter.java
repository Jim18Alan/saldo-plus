package com.example.saldoplusv1.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.saldoplusv1.R;
import com.example.saldoplusv1.models.Categoria;
import com.example.saldoplusv1.models.ImpactoFinanciero;

import java.util.List;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private final List<Categoria> categorias;

    private final ImpactoFinanciero impactoDelApartado;

    public CategoriaAdapter(List<Categoria> categorias, ImpactoFinanciero impacto) {
        this.categorias = categorias;
        this.impactoDelApartado = impacto;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria c = categorias.get(position);
        holder.text.setText(c.getNombre());

        int color;
        switch (impactoDelApartado) {
            case INGRESO:
                color = Color.parseColor("#2E7D32"); // verde
                break;
            case GASTO:
                color = Color.parseColor("#C62828"); // rojo
                break;
            default:
                color = Color.parseColor("#333333"); // neutro (gris oscuro)
                break;
        }

        holder.text.setTextColor(color);
    }


    @Override public int getItemCount() {
        return categorias.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        ViewHolder(View v) {
            super(v);
            text = v.findViewById(android.R.id.text1);
        }
    }
}
