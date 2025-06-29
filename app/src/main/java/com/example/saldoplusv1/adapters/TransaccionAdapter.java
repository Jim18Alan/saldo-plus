package com.example.saldoplusv1.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saldoplusv1.R;
import com.example.saldoplusv1.models.Transaccion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Adaptador para la lista de transacciones con soporte para filtrado, selección y visualización.
 */
public class TransaccionAdapter extends RecyclerView.Adapter<TransaccionAdapter.ViewHolder> {

    public void deseleccionarTodo() {
        seleccionadas.clear();
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    public interface OnItemClickListener {
        void onItemClick(Transaccion transaccion, int position);
    }

    private OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }


    private final List<Transaccion> origen;
    private final List<Transaccion> filtradas;
    private final Set<Transaccion> seleccionadas = new HashSet<>();
    private boolean modoSeleccion = false;
    private OnSelectionChangedListener selectionListener;

    public TransaccionAdapter(List<Transaccion> transacciones) {
        this.origen = new ArrayList<>(transacciones);
        this.filtradas = new ArrayList<>(transacciones);
    }

    /** Registra un listener para cambios en la selección */
    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionListener = listener;
    }

    private void notifySelectionChanged() {
        if (selectionListener != null) {
            selectionListener.onSelectionChanged(seleccionadas.size());
        }
    }

    /** Activa o desactiva el modo selección múltiple */
    public void setModoSeleccion(boolean activo) {
        modoSeleccion = activo;
        if (!activo) {
            seleccionadas.clear();
        }
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    public boolean isModoSeleccion() {
        return modoSeleccion;
    }

    public List<Transaccion> getSeleccionadas() {
        return new ArrayList<>(seleccionadas);
    }

    /** Reemplaza toda la lista actual */
    public void actualizarLista(List<Transaccion> nuevas) {
        origen.clear();
        origen.addAll(nuevas);
        filtrar("");
    }

    /** Filtra por descripción */
    public void filtrar(String texto) {
        filtradas.clear();
        if (texto == null || texto.trim().isEmpty()) {
            filtradas.addAll(origen);
        } else {
            String lower = texto.toLowerCase();
            for (Transaccion t : origen) {
                if (t.getDescripcion().toLowerCase().contains(lower)) {
                    filtradas.add(t);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TransaccionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaccion, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TransaccionAdapter.ViewHolder holder, int position) {
        Transaccion tx = filtradas.get(position);

        holder.descripcion.setText(tx.getDescripcion());
        holder.categoria.setText(tx.getCategoria().getNombre());

        // Formatea la fecha
        holder.fecha.setText(
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        .format(tx.getFecha())
        );

        // Determina apariencia según el apartado (tipo)
        String tipo = tx.getApartado().getNombre().toLowerCase(Locale.ROOT);
        int color;
        String prefijo;
        int iconoRes;

        switch (tipo) {
            case "ingreso":
                color = Color.parseColor("#2E7D32");
                prefijo = "+$";
                iconoRes = R.drawable.ic_arrow_upward;
                break;
            case "gasto":
                color = Color.parseColor("#C62828");
                prefijo = "-$";
                iconoRes = R.drawable.ic_arrow_downward;
                break;
            default: // Ahorro, neutro u otros
                color = Color.parseColor("#1565C0");
                prefijo = "$";
                iconoRes = R.drawable.ic_balance
                ;
                break;
        }

        holder.monto.setTextColor(color);
        holder.monto.setText(String.format("%s%.2f", prefijo, tx.getMonto()));
        holder.icono.setImageResource(iconoRes);

        // Checkbox de selección múltiple
        holder.checkbox.setVisibility(modoSeleccion ? View.VISIBLE : View.GONE);
        holder.checkbox.setOnCheckedChangeListener(null);
        holder.checkbox.setChecked(seleccionadas.contains(tx));
        holder.checkbox.setOnCheckedChangeListener((cb, checked) -> {
            if (checked) seleccionadas.add(tx);
            else seleccionadas.remove(tx);
            notifySelectionChanged();
        });

        // Long click inicia modo selección
        holder.itemView.setOnLongClickListener(v -> {
            if (!modoSeleccion) {
                setModoSeleccion(true);
            }
            return true;
        });




        holder.itemView.setOnClickListener(v -> {
            if (!modoSeleccion && clickListener != null) {
                clickListener.onItemClick(tx, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return filtradas.size();
    }

    /** ViewHolder que mapea el layout item_transaccion */
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icono;
        TextView descripcion, categoria, monto, fecha;
        CheckBox checkbox;

        ViewHolder(View itemView) {
            super(itemView);
            icono        = itemView.findViewById(R.id.icon_tipo);
            descripcion  = itemView.findViewById(R.id.text_descripcion);
            categoria    = itemView.findViewById(R.id.text_categoria);
            monto        = itemView.findViewById(R.id.text_monto);
            fecha        = itemView.findViewById(R.id.text_fecha);
            checkbox     = itemView.findViewById(R.id.checkboxSeleccion);
        }
    }

    public void eliminarSeleccionadas() {
        origen.removeAll(seleccionadas);
        filtradas.removeAll(seleccionadas);
        seleccionadas.clear();
        notifySelectionChanged();
        notifyDataSetChanged();
    }

}
