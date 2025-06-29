package com.example.saldoplusv1.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.saldoplusv1.databinding.FragmentConfiguracionBinding;
import com.example.saldoplusv1.models.Usuario;
import com.example.saldoplusv1.repositories.RepositorioUsuario;

public class ConfiguracionFragment extends Fragment {

    private FragmentConfiguracionBinding binding;
    private RepositorioUsuario repo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentConfiguracionBinding.inflate(inflater, container, false);
        repo = new RepositorioUsuario(requireContext());

        // Mostrar nombre actual
        Usuario usuario = repo.obtenerUsuario();
        if (usuario != null) {
            binding.edtNombreUsuario.setText(usuario.getNombre());
        }

        // Guardar cambios
        binding.btnGuardarNombre.setOnClickListener(v -> {
            String nuevoNombre = binding.edtNombreUsuario.getText().toString().trim();
            if (!nuevoNombre.isEmpty()) {
                repo.actualizarUsuario(nuevoNombre);
                Toast.makeText(getContext(), "Nombre actualizado", Toast.LENGTH_SHORT).show();
                requireActivity().recreate(); // Esto recarga toda la actividad (MainActivity)
            } else {
                Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            }
        });

        return binding.getRoot();
    }
}
