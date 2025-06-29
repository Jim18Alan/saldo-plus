package com.example.saldoplusv1.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Importación para el manejo del ícono.
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat; // Importación para obtener colores de forma segura.
import androidx.recyclerview.widget.RecyclerView;

import com.example.saldoplusv1.R;
import com.example.saldoplusv1.models.Gasto;
import com.example.saldoplusv1.models.Ingreso;
import com.example.saldoplusv1.models.Movimiento;

import java.util.List;

/**
 * REPRESENTA: Un adaptador para el RecyclerView que muestra la lista de movimientos.
 * PROPOSITO: Tomar una lista de objetos 'Movimiento' y transformar cada uno en un elemento visual
 * (una fila) dentro de la lista que ve el usuario, personalizando la vista según sea Ingreso o Gasto.
 */
public class MovimientosAdapter extends RecyclerView.Adapter<MovimientosAdapter.MovimientoViewHolder> {

    /**
     * ATRIBUTO: Contexto de la aplicación o de la Activity que usa el adaptador.
     */
    private Context context;

    /**
     * ATRIBUTO: La lista de datos que el adaptador va a mostrar.
     * Es de tipo 'Movimiento', pero puede contener 'Ingresos' y 'Gastos'.
     */
    private List<Movimiento> movimientos;

    /**
     * CONSTRUCTOR: Se utiliza para crear una nueva instancia del MovimientosAdapter.
     */
    public MovimientosAdapter(Context context, List<Movimiento> movimientos) {
        this.context = context;
        this.movimientos = movimientos;
    }

    /**
     * MÉTODO SOBRESCRITO: Se llama cuando el RecyclerView necesita crear un nuevo ViewHolder.
     * PROPOSITO: Infla el layout XML 'item_movimiento.xml' y crea una instancia del ViewHolder.
     */
    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // INFLADO DE LA VISTA: Se convierte el XML en un objeto View de Java.
        View view = LayoutInflater.from(context).inflate(R.layout.item_movimiento, parent, false);
        return new MovimientoViewHolder(view);
    }

    /**
     * MÉTODO SOBRESCRITO: Vincula los datos de un 'Movimiento' a un 'ViewHolder'.
     * PROPOSITO: Es donde se personaliza cada fila de la lista según los datos del objeto.
     */
    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        // Se obtiene el objeto 'Movimiento' de la lista en la posición actual.
        Movimiento movimiento = movimientos.get(position);

        // Se asigna el dato común: la descripción.
        holder.tvDescripcion.setText(movimiento.getDescripcion());

        // IF-ELSE: Se verifica el tipo de objeto para personalizar la fila.
        if (movimiento instanceof Gasto) {
            // CASTING: Se convierte el objeto Movimiento a Gasto para acceder a sus métodos específicos.
            Gasto gasto = (Gasto) movimiento;

            // MONTO: Se formatea como egreso (con un "-") y se colorea de rojo.
            holder.tvMonto.setText(String.format("- $%.2f", gasto.getMonto()));
            holder.tvMonto.setTextColor(ContextCompat.getColor(context, R.color.color_rojo_gasto));

            // TEXTO SECUNDARIO: Se muestra la categoría del gasto.
            holder.tvCategoria.setText(gasto.getCategoria());

            // ICONO: Se asigna un ícono genérico para los gastos (debes tener 'ic_expense' en tus drawables).
            // TODO: Más adelante podrías poner un switch(gasto.getCategoria()) para poner un ícono para "Comida", "Transporte", etc.
            holder.ivIcono.setImageResource(R.drawable.ic_arrow_downward);

        } else if (movimiento instanceof Ingreso) {
            // MONTO: Se formatea como ingreso (con un "+") y se colorea de verde.
            holder.tvMonto.setText(String.format("+ $%.2f", movimiento.getMonto()));
            holder.tvMonto.setTextColor(ContextCompat.getColor(context, R.color.color_verde_ingreso));

            // TEXTO SECUNDARIO: Para los ingresos, mostraremos la fecha en este campo.
            holder.tvCategoria.setText(movimiento.getFecha());

            // ICONO: Se asigna un ícono para los ingresos (debes tener 'ic_income' en tus drawables).
            holder.ivIcono.setImageResource(R.drawable.ic_arrow_upward);
        }
    }

    /**
     * MÉTODO SOBRESCRITO: Devuelve el número total de elementos en la lista.
     */
    @Override
    public int getItemCount() {
        return (movimientos != null) ? movimientos.size() : 0;
    }


    /**
     * CLASE ANIDADA (NESTED CLASS): MovimientoViewHolder
     * JUSTIFICACIÓN DE ANIDAMIENTO (RUBRICA): Esta clase está anidada porque su única función
     * es mantener las referencias de las vistas de una sola fila y está fuertemente acoplada
     * al 'MovimientosAdapter'. Anidarla mejora la encapsulación y la legibilidad del código.
     */
    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {

        // ATRIBUTOS: Referencias a los componentes de la UI definidos en 'item_movimiento.xml'.
        ImageView ivIcono;
        TextView tvDescripcion, tvCategoria, tvMonto;

        /**
         * CONSTRUCTOR DEL VIEWHOLDER
         * @param itemView La vista de la fila (el ConstraintLayout dentro del CardView).
         */
        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            // ENLACE DE VISTAS: Se enlaza cada atributo con su respectivo componente en el layout XML usando los IDs correctos.
            ivIcono = itemView.findViewById(R.id.item_icono_categoria);
            tvDescripcion = itemView.findViewById(R.id.item_texto_descripcion);
            tvCategoria = itemView.findViewById(R.id.item_texto_categoria);
            tvMonto = itemView.findViewById(R.id.item_texto_monto);
        }
    }
}