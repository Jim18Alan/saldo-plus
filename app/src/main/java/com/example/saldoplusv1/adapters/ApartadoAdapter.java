package com.example.saldoplusv1.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saldoplusv1.R;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.ImpactoFinanciero;

import java.util.List;

/**
 * Adapter para mostrar la lista de Apartados en un RecyclerView.
 */
public class ApartadoAdapter extends RecyclerView.Adapter<ApartadoAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Apartado apartado);
    }

    private final List<Apartado> apartados;
    private OnItemClickListener clickListener;

    public ApartadoAdapter(List<Apartado> apartados) {
        this.apartados = apartados;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public ApartadoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_apartado, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ApartadoAdapter.ViewHolder holder, int position) {
        Apartado ap = apartados.get(position);
        holder.nombre.setText(ap.getNombre());

        // Muestra el nombre del impacto y ajusta color de barra o texto
        ImpactoFinanciero imp = ap.getImpacto();
        holder.impacto.setText(imp.name());
        switch (imp) {
            case INGRESO:
                holder.impacto.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case GASTO:
                holder.impacto.setTextColor(Color.parseColor("#C62828"));
                break;
            case NEUTRO:
                holder.impacto.setTextColor(Color.parseColor("#1565C0"));
                break;
        }

        // Si dispones de icono drawable, podrías cargarlo aquí:
        // holder.icono.setImageResource(...);

        // También podrías pintar el fondo de la tarjeta con ap.getColor()
        try {
            ((CardView) holder.itemView).setCardBackgroundColor(Color.parseColor(ap.getColor()));
        } catch (Exception e) {
            // color inválido: ignora
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(ap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apartados.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icono;
        TextView nombre;
        TextView impacto;

        ViewHolder(View itemView) {
            super(itemView);
            //icono    = itemView.findViewById(R.id.iconoApartado);
            nombre   = itemView.findViewById(R.id.textNombreApartado);
            impacto  = itemView.findViewById(R.id.textImpacto);
        }
    }
}
