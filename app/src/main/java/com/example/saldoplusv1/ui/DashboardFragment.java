package com.example.saldoplusv1.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saldoplusv1.R;
import com.example.saldoplusv1.databinding.FragmentDashboardBinding;

import com.example.saldoplusv1.models.ImpactoFinanciero;
import com.example.saldoplusv1.models.Transaccion;
import com.example.saldoplusv1.repositories.RepositorioSQLite;
import com.example.saldoplusv1.repositories.RepositorioTransaccion;
import com.example.saldoplusv1.services.ServicioReportes;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ServicioReportes servicio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar repositorio y servicio
        RepositorioSQLite repo = new RepositorioSQLite(requireContext());
        servicio = new ServicioReportes(repo.transacciones());


        List<Transaccion> g = repo.transacciones().obtenerPorTipo(false);

        Date inicioMes = servicio.calcularInicioMesActual();
        Date finMes = servicio.calcularFinMesActual();


        // En onViewCreated, reemplaza la llamada actual por esta:
        if (servicio.calcularTotalGastos(inicioMes, finMes) > 0) {
            binding.pieChart.setVisibility(View.VISIBLE);
            mostrarGraficaGastosVsIngresosMes(repo.transacciones(), binding.pieChart, inicioMes, finMes);
        } else {
            binding.pieChart.setVisibility(View.GONE);
        }

        double ingresos = servicio.calcularTotalIngresos(inicioMes, finMes);
        double gastos = servicio.calcularTotalGastos(inicioMes, finMes);
        double balance = servicio.calcularBalanceActual();

        // Mostrar resultados
        binding.txtTotalIngresos.setText(String.format("$%.2f", ingresos));
        binding.txtTotalGastos.setText(String.format("$%.2f", gastos));
        binding.txtBalanceActual.setText(String.format("$%.2f", balance));

        // Botón agregar movimiento
        binding.btnAgregar.setOnClickListener(v -> movAgregar());

    }

    private void mostrarGraficaGastosVsIngresosMes(RepositorioTransaccion repo,
                                                   PieChart pieChart,
                                                   Date desde,
                                                   Date hasta) {
        // Filtrar ingresos y gastos solo de este mes
        double totalIngresos = repo.obtenerPorTipo(true).stream()
                .filter(t -> !t.getFecha().before(desde) && !t.getFecha().after(hasta))
                .mapToDouble(Transaccion::getMonto)
                .sum();

        Map<String, Double> gastosPorCategoria = repo.obtenerPorTipo(false).stream()
                .filter(t -> !t.getFecha().before(desde) && !t.getFecha().after(hasta))
                .collect(Collectors.groupingBy(
                        t -> t.getCategoria().getNombre(),
                        Collectors.summingDouble(Transaccion::getMonto)
                ));

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> e : gastosPorCategoria.entrySet()) {
            float pct = (float)(e.getValue() / totalIngresos * 100);
            entries.add(new PieEntry(pct, e.getKey()));
        }

        PieDataSet ds = new PieDataSet(entries, "Gastos (mes actual)");
        ds.setColors(ColorTemplate.MATERIAL_COLORS);
        ds.setValueTextSize(12f);
        ds.setValueTextColor(Color.BLACK);

        pieChart.setData(new PieData(ds));
        pieChart.setUsePercentValues(true);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterText("Gastos %");
        pieChart.setCenterTextSize(16f);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setWordWrapEnabled(true);
        pieChart.invalidate();
    }

    public double calcularTotalNeutros(List<Transaccion> todas) {
        double total = 0;
        for (Transaccion t : todas) {
            if (t.getImpacto() == ImpactoFinanciero.NEUTRO) {
                total += t.getMonto();
            }
        }
        return total;
    }

    private void movAgregar() {
        NavController navController = NavHostFragment.findNavController(this);
        navController.navigate(R.id.action_home_to_add);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}