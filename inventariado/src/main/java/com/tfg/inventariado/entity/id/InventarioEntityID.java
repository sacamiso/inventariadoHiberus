package com.tfg.inventariado.entity.id;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class InventarioEntityID implements Serializable{

	@Getter @Setter private Integer codArticulo;
	@Getter @Setter private Integer idOficina;
	
	public InventarioEntityID(Integer codArt, Integer idOf) {
		this.codArticulo = codArt;
		this.idOficina = idOf;
	}
	
	public InventarioEntityID() {
		
	}
	
	public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InventarioEntityID)) return false;
        InventarioEntityID that = (InventarioEntityID) o;
        return codArticulo == that.codArticulo &&
        		idOficina == that.idOficina;
    }
	
	@Override
    public int hashCode() {
        return Objects.hash(codArticulo, idOficina);
    }
}
