package com.example.saldoplusv1.ui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.saldoplusv1.R;
import com.example.saldoplusv1.adapters.ApartadoAdapter;
import com.example.saldoplusv1.databinding.FragmentApartadosBinding;
import com.example.saldoplusv1.data.SQLiteHelper;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.ImpactoFinanciero;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ApartadosFragment extends Fragment {

    private FragmentApartadosBinding binding;

    private SQLiteHelper helper;
    private ApartadoAdapter adapter;
    private List<Apartado> listaApartados;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentApartadosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        helper = new SQLiteHelper(requireContext());

        // RecyclerView
        listaApartados = new ArrayList<>();
        adapter = new ApartadoAdapter(listaApartados);
        androidx.recyclerview.widget.RecyclerView rv = view.findViewById(R.id.recyclerApartados);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        adapter.setOnItemClickListener(ap -> {
            // Lanza el BottomSheet para este apartado
            CategoriasBottomSheet.newInstance(ap.getId())
                    .show(getParentFragmentManager(), "cats_sheet");
        });


        // Placeholder
        View textSin = view.findViewById(R.id.textSinApartados);
        toggleSinDatos(listaApartados.isEmpty(), textSin, rv);

        // Cargar datos iniciales
        cargarApartados();
        toggleSinDatos(listaApartados.isEmpty(), textSin, rv);

    }

    /** Carga desde BD y refresca adapter */
    private void cargarApartados() {
        listaApartados.clear();
        listaApartados.addAll(helper.obtenerTodosLosApartados());  // Implementa este método
        adapter.notifyDataSetChanged();
    }

    /** Muestra u oculta placeholder */
    private void toggleSinDatos(boolean sinDatos, View placeholder, View recycler) {
        placeholder.setVisibility(sinDatos ? View.VISIBLE : View.GONE);
        recycler.setVisibility(sinDatos ? View.GONE : View.VISIBLE);
    }

    /** Diálogo para crear un nuevo apartado */
    private void mostrarDialogoCrear() {
        // 1. Crea el AlertDialog con tu layout custom
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_apartado, null);
        builder.setView(dialogView);
        // Evitas el título por defecto (lo pones en el layout con textTitle)
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // 2. Referencias a los elementos del layout
        AutoCompleteTextView inputType = dialogView.findViewById(R.id.input_type);
        TextInputEditText inputNombre = dialogView.findViewById(R.id.inputNombre);
        MaterialButton btnGuardar = dialogView.findViewById(R.id.btnGuardar);




        ArrayAdapter<ImpactoFinanciero> adImpacto = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                ImpactoFinanciero.values()
        );

        inputType.setAdapter(adImpacto);

        // 4. Acciones al pulsar “Guardar”
        btnGuardar.setOnClickListener(v -> {
            String nom = inputNombre.getText().toString().trim();
            String tipoStr = inputType.getText().toString().trim();

            if (nom.isEmpty()) {
                inputNombre.setError("El nombre no puede ir vacío");
                return;
            }
            if (tipoStr.isEmpty()) {
                inputType.setError("Selecciona un tipo");
                return;
            }

            // Convierte el texto a tu enum
            ImpactoFinanciero imp;
            try {
                imp = ImpactoFinanciero.valueOf(tipoStr);
            } catch (IllegalArgumentException e) {
                inputType.setError("Tipo inválido");
                return;
            }

            // Construye y guarda el Apartado
            Apartado ap = new Apartado(nom, /*icono*/ "", (imp == ImpactoFinanciero.INGRESO)?"#edf3ec": (imp == ImpactoFinanciero.GASTO) ? "#fdebec" : "#ffffff", imp);
            long id = helper.insertarApartado(ap);
            ap.setId((int) id);

            // Actualiza la lista y la UI
            listaApartados.add(ap);
            adapter.notifyItemInserted(listaApartados.size() - 1);
            toggleSinDatos(
                    false,
                    requireView().findViewById(R.id.textSinApartados),
                    requireView().findViewById(R.id.recyclerApartados)
            );

            dialog.dismiss();
        });

        // 5. (Opcional) si quieres un cancel dentro del diálogo, podrías añadir un botón más en tu layout
        // o permitir dismiss al tocar fuera:
        dialog.setCanceledOnTouchOutside(true);
    }

}