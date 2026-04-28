package com.hari.finTrack.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.hari.finTrack.model.TipoTransaccion;
import com.hari.finTrack.model.Transaction;

public class TransactionResponse {
    //transaction id
    private Long id;
    private String descripcion;
    private BigDecimal monto;
    private TipoTransaccion tipo;
    private LocalDate fecha;
    private String categoria;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, String descripcion, BigDecimal monto, TipoTransaccion tipo, LocalDate fecha, String categoria) {
        this.id = id;
        this.descripcion = descripcion;
        this.monto = monto;
        this.tipo = tipo;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public static TransactionResponse fromTransaction(Transaction transaction){
        return new TransactionResponse(
            transaction.getId(),
            transaction.getDescripcion(),
            transaction.getMonto(),
            transaction.getTipo(),
            transaction.getFecha(),
            transaction.getCategoria()
        );
    }
}