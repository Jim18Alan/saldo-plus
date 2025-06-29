package com.example.saldoplusv1.repositories;

import android.content.Context;

import com.example.saldoplusv1.data.SQLiteHelper;

public class RepositorioSQLite {
    private final RepositorioTransaccion repoTransaccion;
    private final RepositorioCategoria repoCategoria;
    private final RepositorioApartado repoApartado;

    public RepositorioSQLite(Context context) {
        SQLiteHelper helper = new SQLiteHelper(context);
        this.repoTransaccion = new RepositorioTransaccion(helper);
        this.repoCategoria = new RepositorioCategoria(helper);
        this.repoApartado = new RepositorioApartado(helper);
    }

    // Métodos de acceso centralizados
    public RepositorioTransaccion transacciones() {
        return repoTransaccion;
    }

    public RepositorioCategoria categorias() {
        return repoCategoria;
    }

    public RepositorioApartado apartados() {
        return repoApartado;
    }
}

