package com.example.saldoplusv1.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.saldoplusv1.R;
import com.example.saldoplusv1.adapters.TransaccionAdapter;
import com.example.saldoplusv1.databinding.FragmentTransaccionesBinding;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.Categoria;
import com.example.saldoplusv1.models.Transaccion;
import com.example.saldoplusv1.repositories.RepositorioSQLite;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TransaccionesFragment extends Fragment {

    private FragmentTransaccionesBinding binding;
    private TransaccionAdapter adapter;
    private RepositorioSQLite repo;
    private List<Transaccion> listaMes;
    private Date inicioMes, finMes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransaccionesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("Transacciones");

        repo = new RepositorioSQLite(requireContext());



        calcularRangoMesActual();
        cargarTransaccionesDelMes();

        configurarBuscadorYChips();
        configurarEliminacion();

        binding.btnCancelarSeleccion.setOnClickListener(v -> {
            adapter.setModoSeleccion(false); // o tu método para salir de modo selección
            adapter.deseleccionarTodo();     // si tienes un método para eso
            binding.botoneraSeleccion.setVisibility(View.GONE);
        });



    }

    private void mostrarBottomSheetEditar(Transaccion transaccion) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_transaccion, null);
        dialog.setContentView(view);

        // Referencias a los campos
        TextInputEditText inputAmount = view.findViewById(R.id.input_amount);
        TextInputEditText inputDesc = view.findViewById(R.id.input_description);
        AutoCompleteTextView inputType = view.findViewById(R.id.input_type);
        AutoCompleteTextView inputCategory = view.findViewById(R.id.input_category);
        TextInputEditText inputDate = view.findViewById(R.id.input_date);
        MaterialButton btnGuardar = view.findViewById(R.id.btnGuardar);

        RepositorioSQLite repo = new RepositorioSQLite(requireContext());

        // Cargar apartados
        List<Apartado> apartados = repo.apartados().obtenerTodos();
        List<String> nombresApartados = apartados.stream()
                .map(Apartado::getNombre)
                .collect(Collectors.toList());
        ArrayAdapter<String> tipoAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, nombresApartados);
        inputType.setAdapter(tipoAdapter);

        // Cargar categorías iniciales
        List<Categoria> categorias = repo.categorias().obtenerPorApartado(transaccion.getApartado().getId());
        List<String> nombresCategorias = categorias.stream()
                .map(Categoria::getNombre)
                .collect(Collectors.toList());
        ArrayAdapter<String> catAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, nombresCategorias);
        inputCategory.setAdapter(catAdapter);

        // Actualizar categorías al cambiar tipo
        inputType.setOnItemClickListener((parent, v, pos, id) -> {
            Apartado apartadoSel = apartados.get(pos);
            List<Categoria> nuevasCats = repo.categorias().obtenerPorApartado(apartadoSel.getId());
            List<String> nuevosNombres = nuevasCats.stream().map(Categoria::getNombre).collect(Collectors.toList());

            categorias.clear();
            categorias.addAll(nuevasCats);
            ArrayAdapter<String> nuevoCatAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_dropdown_item_1line, nuevosNombres);
            inputCategory.setAdapter(nuevoCatAdapter);
            inputCategory.setText(""); // limpia selección anterior
        });

        // DatePicker
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Seleccionar fecha")
                .setSelection(transaccion.getFecha().getTime())
                .build();

        inputDate.setOnClickListener(v -> datePicker.show(getParentFragmentManager(), "DATE_PICKER"));
        datePicker.addOnPositiveButtonClickListener(sel -> {
            inputDate.setText(datePicker.getHeaderText());
        });

        // Prellenar campos
        inputAmount.setText(String.valueOf(transaccion.getMonto()));
        inputDesc.setText(transaccion.getDescripcion());
        inputType.setText(transaccion.getApartado().getNombre(), false);
        inputCategory.setText(transaccion.getCategoria().getNombre(), false);
        inputDate.setText(new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(transaccion.getFecha()));

        // Guardar cambios
        btnGuardar.setText("Guardar cambios");
        btnGuardar.setOnClickListener(v -> {
            try {
                String montoTxt = inputAmount.getText().toString().trim();
                String desc = inputDesc.getText().toString().trim();
                String tipoTxt = inputType.getText().toString().trim();
                String catTxt = inputCategory.getText().toString().trim();
                String fechaTxt = inputDate.getText().toString().trim();

                if (montoTxt.isEmpty() || desc.isEmpty() || tipoTxt.isEmpty() || catTxt.isEmpty() || fechaTxt.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                double monto = Double.parseDouble(montoTxt);
                Date fecha = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).parse(fechaTxt);

                Apartado apartado = apartados.stream()
                        .filter(a -> a.getNombre().equals(tipoTxt))
                        .findFirst().orElseThrow(() -> new Exception("Tipo no válido"));

                Categoria categoria = categorias.stream()
                        .filter(c -> c.getNombre().equals(catTxt))
                        .findFirst().orElseThrow(() -> new Exception("Categoría no válida"));

                // Actualizar objeto
                transaccion.setMonto(monto);
                transaccion.setDescripcion(desc);
                transaccion.setFecha(fecha);
                transaccion.setApartado(apartado);
                transaccion.setCategoria(categoria);

                // Guardar en base de datos
                repo.transacciones().actualizar(transaccion);

                Toast.makeText(requireContext(), "Transacción actualizada", Toast.LENGTH_SHORT).show();
                cargarTransaccionesDelMes(); // Refrescar lista
                dialog.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        dialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();
        cargarTransaccionesDelMes();
    }

    private void calcularRangoMesActual() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        inicioMes = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        finMes = cal.getTime();
    }

    private void cargarTransaccionesDelMes() {
        listaMes = repo.transacciones().filtrarPorRango(inicioMes, finMes);
        toggleSinDatos(listaMes.isEmpty());
        adapter = new TransaccionAdapter(listaMes);
        // Primero asignas el listener
        adapter.setOnItemClickListener((transaccion, pos) -> {
            mostrarBottomSheetEditar(transaccion);
        });
        binding.recyclerTransacciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerTransacciones.setAdapter(adapter);
        adapter.setOnSelectionChangedListener(count -> {
            binding.btnEliminarSeleccionadas.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
            binding.botoneraSeleccion.setVisibility(count > 0 ? View.VISIBLE : View.GONE);

        });
    }

    private void configurarBuscadorYChips() {
        // Texto
        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarTransacciones();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        // Chips
        ChipGroup chipGroup = binding.chipGroupFiltros;
        chipGroup.removeAllViews();
        List<Apartado> apartados = repo.apartados().obtenerTodos();
        if (!apartados.isEmpty()) {
            for (Apartado a : apartados) {
                Chip chip = new Chip(requireContext());
                chip.setText(a.getNombre());
                chip.setCheckable(true);
                chip.setTag(a.getId());
                chip.setOnCheckedChangeListener((btn, isChecked) -> filtrarTransacciones());
                chipGroup.addView(chip);
            }
            chipGroup.setVisibility(View.VISIBLE);
        } else chipGroup.setVisibility(View.GONE);
    }

    private void filtrarTransacciones() {
        String texto = binding.searchEditText.getText().toString().trim().toLowerCase();
        ChipGroup chipGroup = binding.chipGroupFiltros;
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip c = (Chip) chipGroup.getChildAt(i);
            if (c.isChecked()) ids.add((Integer) c.getTag());
        }
        List<Transaccion> filtradas = new ArrayList<>();
        for (Transaccion t : listaMes) {
            boolean matchTexto = t.getDescripcion().toLowerCase().contains(texto);
            boolean matchChip = ids.isEmpty() || ids.contains(t.getIdApartado());
            if (matchTexto && matchChip) filtradas.add(t);
        }
        adapter.actualizarLista(filtradas);
        toggleSinDatos(filtradas.isEmpty());
    }



    private void configurarEliminacion() {
        binding.btnEliminarSeleccionadas.setOnClickListener(v -> {
            List<Transaccion> sel = adapter.getSeleccionadas();
            for (Transaccion t : sel) repo.transacciones().eliminar(t);
            adapter.setModoSeleccion(false);
            cargarTransaccionesDelMes();
        });
    }

    private void toggleSinDatos(boolean sin) {
        binding.recyclerTransacciones.setVisibility(sin ? View.GONE : View.VISIBLE);
        binding.textSinDatos.setVisibility(sin ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}