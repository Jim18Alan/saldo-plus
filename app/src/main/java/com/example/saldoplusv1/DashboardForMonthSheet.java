package com.example.saldoplusv1;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saldoplusv1.databinding.FragmentSheetDashboardBinding;
import com.example.saldoplusv1.models.Transaccion;
import com.example.saldoplusv1.repositories.RepositorioSQLite;
import com.example.saldoplusv1.repositories.RepositorioTransaccion;
import com.example.saldoplusv1.services.ServicioReportes;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardForMonthSheet extends BottomSheetDialogFragment {
    private static final String ARG_YEAR = "year";
    private static final String ARG_MONTH = "month";

    public static DashboardForMonthSheet newInstance(int year, int month) {
        Bundle args = new Bundle();
        args.putInt(ARG_YEAR, year);
        args.putInt(ARG_MONTH, month);
        DashboardForMonthSheet f = new DashboardForMonthSheet();
        f.setArguments(args);
        return f;
    }

    private FragmentSheetDashboardBinding binding;
    private ServicioReportes servicio;
    private int year, month;

    private RepositorioSQLite repo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSheetDashboardBinding.inflate(inflater, container, false);
        RepositorioSQLite repo = new RepositorioSQLite(requireContext());
        servicio = new ServicioReportes(repo.transacciones());
        year  = getArguments().getInt(ARG_YEAR);
        month = getArguments().getInt(ARG_MONTH);
        setupDashboard(repo.transacciones());
        return binding.getRoot();
    }

    private void setupDashboard(RepositorioTransaccion repo) {
        // Calcula rango del mes seleccionado
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        Date desde = servicio.obtenerInicioMes(cal);
        Date hasta = servicio.obtenerFinMes(cal);

        // Totales
        double ingresos = servicio.calcularTotalIngresos(desde, hasta);
        double gastos   = servicio.calcularTotalGastos(desde, hasta);
        double balance  = ingresos - gastos;

        // Muestra valores
        binding.txtTotalIngresos.setText(String.format("$%.2f", ingresos));
        binding.txtTotalGastos .setText(String.format("$%.2f", gastos));
        binding.txtBalanceActual.setText(String.format("$%.2f", balance));

        // Dibuja gráfica **aquí mismo**
        if (gastos > 0) {
            binding.pieChart.setVisibility(View.VISIBLE);
            mostrarGraficaGastosVsIngresosMes(repo, binding.pieChart, desde, hasta);
        } else {
            binding.pieChart.setVisibility(View.GONE);
        }
    }

    private void mostrarGraficaGastosVsIngresosMes(RepositorioTransaccion repo,
                                                   PieChart pieChart,
                                                   Date desde,
                                                   Date hasta) {
        // igual que antes, filtra por fecha
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
            float pct = totalIngresos > 0
                    ? (float)(e.getValue() / totalIngresos * 100)
                    : 0f;
            entries.add(new PieEntry(pct, e.getKey()));
        }

        PieDataSet ds = new PieDataSet(entries, "Gastos");
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

}
