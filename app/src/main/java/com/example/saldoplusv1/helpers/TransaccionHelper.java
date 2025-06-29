package com.example.saldoplusv1.helpers;

import android.content.Context;

import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.Movimiento;
import com.example.saldoplusv1.repositories.RepositorioTransaccion;

import java.util.Date;

public class TransaccionHelper {
    public static void crearTransaccion(Context context,
                                        double monto,
                                        String descripcion,
                                        Categoria categoria,
                                        Apartado apartado,
                                        long fechaMillis) {
        if (categoria == null || apartado == null) {
            throw new IllegalArgumentException("Categoría y Apartado no pueden ser nulos");
        }

        RepositorioTransaccion repo = new RepositorioTransaccion(context);
        long id = System.currentTimeMillis();
        Date fecha = new Date(fechaMillis);

        // Usamos Movimiento en lugar de Transaccion
        Movimiento tx = new Movimiento(id, monto, fecha, descripcion, categoria, apartado);
        repo.agregar(tx);
    }
}

