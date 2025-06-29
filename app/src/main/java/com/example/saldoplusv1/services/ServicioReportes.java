package com.example.saldoplusv1.services;

import com.example.saldoplusv1.models.Categoria;
import com.example.saldoplusv1.models.Transaccion;
import com.example.saldoplusv1.repositories.RepositorioTransaccion;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;


public class ServicioReportes {
    private RepositorioTransaccion repo;

    public ServicioReportes(RepositorioTransaccion repo) {
        this.repo = repo;
    }

    public double calcularTotalIngresos(Date desde, Date hasta) {
        return repo.obtenerPorTipo(true).stream()
                .filter(t -> t.getFecha().compareTo(desde) >= 0 && t.getFecha().compareTo(hasta) <= 0)
                .mapToDouble(Transaccion::getMonto)
                .sum();
    }

    public double calcularTotalGastos(Date desde, Date hasta) {
        return repo.obtenerPorTipo(false).stream()
                .filter(t -> t.getFecha().compareTo(desde) >= 0 && t.getFecha().compareTo(hasta) <= 0)
                .mapToDouble(Transaccion::getMonto)
                .sum();
    }

    public double calcularBalanceActual() {
        double ingresos = calcularTotalIngresos(obtenerInicioMes(), obtenerFinMes());
        double gastos = calcularTotalGastos(obtenerInicioMes(), obtenerFinMes());
        return ingresos - gastos;
    }

    public Map<Categoria, Double> calcularGastosPorCategoria(Calendar mes) {
        Date inicio = obtenerInicioMes(mes);
        Date fin = obtenerFinMes(mes);
        return repo.obtenerPorTipo(false).stream()
                .filter(t -> !t.getFecha().before(inicio) && !t.getFecha().after(fin))
                .collect(Collectors.groupingBy(Transaccion::getCategoria,
                        Collectors.summingDouble(Transaccion::getMonto)));
    }


    public Date calcularInicioMesActual() {
        return obtenerInicioMes();
    }

    public Date calcularFinMesActual() {
        return obtenerFinMes();
    }
    private Date obtenerInicioMes() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
    private Date obtenerFinMes() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }
    public Date obtenerInicioMes(Calendar mes) {
        mes.set(Calendar.DAY_OF_MONTH, 1);
        mes.set(Calendar.HOUR_OF_DAY, 0);
        mes.set(Calendar.MINUTE, 0);
        mes.set(Calendar.SECOND, 0);
        return mes.getTime();
    }
    public Date obtenerFinMes(Calendar mes) {
        Calendar c = (Calendar) mes.clone();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }
}