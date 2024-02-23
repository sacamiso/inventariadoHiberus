package com.tfg.inventariado.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class HistorialInventarioEntityID implements Serializable{
	
	@Getter @Setter private Integer codArticulo;
	@Getter @Setter private Integer idOficina;
	@Getter @Setter private LocalDateTime fecha;
	
	public HistorialInventarioEntityID(Integer codArti, Integer idOfi, LocalDateTime fe) {
		this.codArticulo = codArti;
		this.idOficina = idOfi;
		this.fecha = fe;
	}
	
	public HistorialInventarioEntityID() {
		
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HistorialInventarioEntityID)) return false;
        HistorialInventarioEntityID that = (HistorialInventarioEntityID) o;
        return codArticulo == that.codArticulo &&
        		idOficina == that.idOficina &&
        				fecha.equals(that.fecha) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codArticulo, idOficina, fecha);
    }

}
