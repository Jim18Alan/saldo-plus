package com.example.saldoplusv1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.saldoplusv1.adapters.MonthAdapter;
import com.example.saldoplusv1.databinding.FragmentMonthHistoryBinding;
import com.example.saldoplusv1.models.Transaccion;
import com.example.saldoplusv1.repositories.RepositorioSQLite;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MonthHistoryFragment extends Fragment {

    private FragmentMonthHistoryBinding binding;
    private RepositorioSQLite repo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMonthHistoryBinding.inflate(inflater, container, false);
        repo = new RepositorioSQLite(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Obtiene lista de meses desde la primera transacción hasta hoy
        List<MonthAdapter.YearMonth> meses = calcularMesesDesdePrimeraTransaccion();

        // 2. Configura RecyclerView
        MonthAdapter adapter = new MonthAdapter(meses, (year, month) -> {
            showMonthBottomSheet(year, month);
        });
        binding.rvMonths.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvMonths.setAdapter(adapter);
    }

    private List<MonthAdapter.YearMonth> calcularMesesDesdePrimeraTransaccion() {
        Date minDate = repo.transacciones().obtenerTodas().stream()
                .map(Transaccion::getFecha)
                .min(Date::compareTo)
                .orElse(new Date());
        Calendar start = Calendar.getInstance();
        start.setTime(minDate);
        start.set(Calendar.DAY_OF_MONTH, 1);
        Calendar end = Calendar.getInstance(); // mes actual
        List<MonthAdapter.YearMonth> list = new ArrayList<>();
        while (!start.after(end)) {
            list.add(new MonthAdapter.YearMonth(
                    start.get(Calendar.YEAR),
                    start.get(Calendar.MONTH) + 1
            ));
            start.add(Calendar.MONTH, 1);
        }
        return list;
    }

    private void showMonthBottomSheet(int year, int month) {
        // crea un BottomSheetDialog con el fragmento dashboard adaptado:
        DashboardForMonthSheet sheet = DashboardForMonthSheet.newInstance(year, month);
        sheet.show(getParentFragmentManager(), "monthSheet");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
