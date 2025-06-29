package com.example.saldoplusv1;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.saldoplusv1.adapters.TransaccionAdapter;
import com.example.saldoplusv1.databinding.FragmentFotoBinding;
import com.example.saldoplusv1.databinding.SheetTransaccionesTemporalesBinding;
import com.example.saldoplusv1.models.Apartado;
import com.example.saldoplusv1.models.Categoria;
import com.example.saldoplusv1.models.Movimiento;
import com.example.saldoplusv1.models.Transaccion;
import com.example.saldoplusv1.repositories.RepositorioSQLite;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class FotoFragment extends Fragment {

    private FragmentFotoBinding binding;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap capturedBitmap;
    private File photoFile;
    private final String openaiKey = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFotoBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Permisos de cámara
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 100);
        }

        // Botón: tomar foto
        binding.btnTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());


        // Botón: enviar
        binding.btnSend.setOnClickListener(v -> {
            if (capturedBitmap != null) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.btnSend.setEnabled(false);

                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Foto enviada", Toast.LENGTH_LONG).show()
                );
                OpenAIHelper.enviarImagen(capturedBitmap, openaiKey, new OpenAIHelper.ResponseCallback() {
                    @Override
                    public void onSuccess(String jsonResponse) {
                        requireActivity().runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.btnSend.setEnabled(true);
                        });

                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Respuesta de la IA!", Toast.LENGTH_SHORT).show()
                        );
                        Log.d("IA", jsonResponse);


                        try {
                            JSONObject json = new JSONObject(jsonResponse); // respuesta completa de OpenAI
                            String content = json.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            // Limpia el bloque ```json
                            content = content.replace("```json", "").replace("```", "").trim();

                            // Transforma a JSONArray
                            JSONArray array = new JSONArray(content);
                            List<Transaccion> transaccionesTemporales = new ArrayList<>();

                            RepositorioSQLite repo = new RepositorioSQLite(requireContext());
                            Apartado gasto = repo.apartados().obtenerPorNombre("Gasto");
                            Categoria compras = repo.categorias().obtenerPorNombre("Compras");

                            if (gasto == null || compras == null) {
                                Toast.makeText(requireContext(), "No se encontró el apartado o la categoría", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Date fechaHoy = new Date();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject item = array.getJSONObject(i);

                                if (item.has("producto") && item.has("precio")) {
                                    String descripcion = item.getString("producto");
                                    double precio = item.getDouble("precio");

                                    Transaccion tx = new Movimiento(
                                            -1,
                                            precio,
                                            new Date(),
                                            descripcion,
                                            compras, // Debes cargar esta desde la BD
                                            gasto      // Igual
                                    );

                                    transaccionesTemporales.add(tx);
                                }
                            }

                            // Luego muestra el mensaje en el hilo principal
                            requireActivity().runOnUiThread(() -> {
                                if (!transaccionesTemporales.isEmpty()) {
                                    Toast.makeText(requireContext(), "Transacciones temporales agregadas", Toast.LENGTH_SHORT).show();
                                    mostrarBottomSheetConTransacciones(transaccionesTemporales); // ← tú defines este método
                                } else {
                                    Toast.makeText(requireContext(), "No se encontraron productos válidos", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "Error al procesar la respuesta", Toast.LENGTH_LONG).show()
                            );
                        }


                    }

                    @Override
                    public void onError(String errorMessage) {
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show()
                        );
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Primero toma una foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private File createImageFile(Context context) throws IOException {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("photo", ".jpg", storageDir);
    }


    private void mostrarBottomSheetConTransacciones(List<Transaccion> transaccionesTemporales) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        SheetTransaccionesTemporalesBinding sheetBinding = SheetTransaccionesTemporalesBinding.inflate(getLayoutInflater());
        dialog.setContentView(sheetBinding.getRoot());

        // Configura RecyclerView
        TransaccionAdapter adapter = new TransaccionAdapter(transaccionesTemporales);
        sheetBinding.recyclerTemporales.setLayoutManager(new LinearLayoutManager(requireContext()));
        sheetBinding.recyclerTemporales.setAdapter(adapter);

        adapter.setOnItemClickListener((transaccion, position) -> {
            mostrarDialogoEditar(transaccion, () -> adapter.notifyItemChanged(position));
        });

        sheetBinding.btnEliminar.setOnClickListener(v -> adapter.eliminarSeleccionadas());
        sheetBinding.btnCancelar.setOnClickListener(v -> adapter.setModoSeleccion(false));

        // Lógica para guardar a base de datos
        RepositorioSQLite repo = new RepositorioSQLite(requireContext());
        sheetBinding.btnAgregar.setOnClickListener(v -> {
            for (Transaccion t : transaccionesTemporales) {
                repo.transacciones().agregar(t);
            }
            Toast.makeText(requireContext(), "Transacciones agregadas correctamente", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void mostrarDialogoEditar(Transaccion tx, Runnable onEditCallback) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_transaccion, null);
        EditText inputNombre = dialogView.findViewById(R.id.inputNombre);
        EditText inputPrecio = dialogView.findViewById(R.id.inputPrecio);

        inputNombre.setText(tx.getDescripcion());
        inputPrecio.setText(String.valueOf(tx.getMonto()));

        new AlertDialog.Builder(requireContext())
                .setTitle("Editar transacción")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    tx.setDescripcion(inputNombre.getText().toString());
                    tx.setMonto(Double.parseDouble(inputPrecio.getText().toString()));
                    onEditCallback.run();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }



    private void dispatchTakePictureIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) == null) return;

        try {
            photoFile = createImageFile(requireContext());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (photoFile == null) return;

        Uri photoURI = FileProvider.getUriForFile(requireContext(),
                "com.example.saldoplusv1.fileprovider",
                photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            capturedBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            binding.imagePreview.setImageBitmap(capturedBitmap);
        }
    }
}