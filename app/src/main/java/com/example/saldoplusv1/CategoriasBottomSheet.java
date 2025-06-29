package com.example.saldoplusv1;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.saldoplusv1.adapters.CategoriaAdapter;
import com.example.saldoplusv1.db.SQLiteHelper;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.Categoria;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.ArrayList;
import java.util.List;

public class CategoriasBottomSheet extends BottomSheetDialogFragment {
    private static final String ARG_APARTADO_ID = "apartado_id";

    private int apartadoId;
    private SQLiteHelper helper;
    private List<Categoria> listaCats;
    private CategoriaAdapter adapter;

    public static CategoriasBottomSheet newInstance(int apartadoId) {
        CategoriasBottomSheet bs = new CategoriasBottomSheet();
        Bundle args = new Bundle();
        args.putInt(ARG_APARTADO_ID, apartadoId);
        bs.setArguments(args);
        return bs;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_categorias, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        helper = new SQLiteHelper(requireContext());
        apartadoId = requireArguments().getInt(ARG_APARTADO_ID);

        // Título
        TextView titulo = view.findViewById(R.id.textTituloCategorias);
        titulo.setText("Categorías");

        // Recycler
        listaCats = new ArrayList<>();
        Apartado ap = helper.obtenerApartadoPorId(apartadoId); // Ya lo hablamos antes
        adapter = new CategoriaAdapter(listaCats, ap.getImpacto());
        androidx.recyclerview.widget.RecyclerView rv = view.findViewById(R.id.recyclerCategorias);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        View placeholder = view.findViewById(R.id.textSinCategorias);
        cargarCategorias();
        placeholder.setVisibility(listaCats.isEmpty() ? View.VISIBLE : View.GONE);

        // Botón agregar
        view.findViewById(R.id.btnAgregarCategoria).setOnClickListener(v ->
                mostrarDialogoCrearCategoria(placeholder, rv)
        );
    }

    private void cargarCategorias() {
        listaCats.clear();
        listaCats.addAll(helper.obtenerCategoriasPorApartado(apartadoId));
        adapter.notifyDataSetChanged();
    }

    private void mostrarDialogoCrearCategoria(View placeholder, View recycler) {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_categoria, null);
        EditText inputNom = dialogView.findViewById(R.id.inputNombreCat);
        // Ya no necesitas inputColor

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(true)
                .create(); // Usamos create() en lugar de show() directamente

        // Mostrar el diálogo
        dialog.show();

        // Fondo transparente para permitir ver los bordes redondeados de MaterialCardView
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Botón guardar (manual, ya que usamos .create())
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardarCategoria);
        btnGuardar.setOnClickListener(v -> {
            String nom = inputNom.getText().toString().trim();
            if (!nom.isEmpty()) {
                Categoria c = new Categoria(nom, "", "#1565C0", apartadoId);
                helper.insertarCategoria(c);
                listaCats.add(c);
                adapter.notifyItemInserted(listaCats.size() - 1);
                placeholder.setVisibility(View.GONE);
                recycler.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
    }

}
