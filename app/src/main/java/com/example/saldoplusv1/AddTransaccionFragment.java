package com.example.saldoplusv1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.saldoplusv1.databinding.FragmentAddTransaccionBinding;
import com.example.saldoplusv1.db.SQLiteHelper;
import com.example.saldoplusv1.helpers.TransaccionHelper;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.Categoria;
import com.example.saldoplusv1.models.ImpactoFinanciero;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class AddTransaccionFragment extends Fragment {

    private FragmentAddTransaccionBinding binding;

    private SQLiteHelper helper;
    private List<Apartado> apartados;
    private List<Categoria> categorias;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAddTransaccionBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        helper = new SQLiteHelper(requireContext());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("Agregar transacción");

        // 1) Carga dinámica de Apartados
        apartados = new ArrayList<>();
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor c = db.rawQuery("SELECT id, nombre, icono, color, impacto FROM apartados", null)) {
            while (c.moveToNext()) {
                apartados.add(new Apartado(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        ImpactoFinanciero.valueOf(c.getString(4))
                ));
            }
        }
        List<String> tiposNombres = apartados.stream()
                .map(Apartado::getNombre)
                .collect(Collectors.toList());
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                tiposNombres
        );
        binding.inputType.setAdapter(adapterTipo);

        // 2) Cuando el usuario selecciona un tipo, recarga categorías
        binding.inputType.setOnItemClickListener((parent, v, pos, id) -> {
            Apartado elegido = apartados.get(pos);
            cargarCategorias(elegido.getId());
        });

        // 3) DatePicker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar fecha")
                .build();
        binding.inputDate.setOnClickListener(v -> datePicker.show(
                getParentFragmentManager(), "MATERIAL_DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(sel ->
                binding.inputDate.setText(datePicker.getHeaderText()));

        // 4) Guardar
        binding.btnGuardar.setOnClickListener(v -> guardarTransaccion());
    }

    /** Carga las categorías asociadas al apartado dado */
    private void cargarCategorias(int apartadoId) {
        categorias = new ArrayList<>();
        try (SQLiteDatabase db = helper.getReadableDatabase();
             Cursor c = db.rawQuery(
                     "SELECT id, nombre, icono, color, apartado_id FROM categorias WHERE apartado_id = ?",
                     new String[]{ String.valueOf(apartadoId) })) {
            while (c.moveToNext()) {
                categorias.add(new Categoria(
                        c.getInt(0),
                        c.getString(1),
                        c.getString(2),
                        c.getString(3),
                        c.getInt(4)
                ));
            }
        }
        List<String> catNombres = categorias.stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
        ArrayAdapter<String> adapterCat = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                catNombres
        );
        binding.inputCategory.setAdapter(adapterCat);
    }

    /** Valida inputs y crea la transacción usando Apartado y Categoria */
    private void guardarTransaccion() {
        try {
            String montoTxt   = binding.inputAmount.getText().toString().trim();
            String desc       = binding.inputDescription.getText().toString().trim();
            String tipoTxt    = binding.inputType.getText().toString().trim();
            String catTxt     = binding.inputCategory.getText().toString().trim();
            String fechaTxt   = binding.inputDate.getText().toString().trim();
            if (montoTxt.isEmpty() || desc.isEmpty() || tipoTxt.isEmpty()
                    || catTxt.isEmpty() || fechaTxt.isEmpty()) {
                Toast.makeText(requireContext(),
                        "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double monto = Double.parseDouble(montoTxt);
            // Buscar objeto Apartado y Categoria seleccionados
            Apartado apartado = apartados.stream()
                    .filter(a -> a.getNombre().equals(tipoTxt))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Apartado inválido"));
            Categoria categoria = categorias.stream()
                    .filter(c -> c.getNombre().equals(catTxt))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Categoría inválida"));

            Date fecha = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .parse(fechaTxt);
            long fechaMillis = fecha.getTime();

            // Llamada al helper con el nuevo método
            TransaccionHelper.crearTransaccion(
                    requireContext(),
                    monto,
                    desc,
                    categoria,
                    apartado,
                    fechaMillis
            );

            Toast.makeText(requireContext(),
                    "Transacción guardada exitosamente",
                    Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(requireContext(),
                    "Error al guardar: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void limpiarCampos() {
        binding.inputAmount.setText("");
        binding.inputDescription.setText("");
        binding.inputType.setText("");
        binding.inputCategory.setText("");
        binding.inputDate.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}