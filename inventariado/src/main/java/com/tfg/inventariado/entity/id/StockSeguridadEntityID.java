package com.tfg.inventariado.entity.id;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class StockSeguridadEntityID implements Serializable {

	@Getter @Setter private String codSubcategoria;
	@Getter @Setter private String codCategoria;
	@Getter @Setter private Integer idOficina;
	
	public StockSeguridadEntityID(String codSub, String codCa, Integer idOf) {
		this.codSubcategoria = codSub;
		this.codCategoria = codCa;
		this.idOficina = idOf;
	}
	
	public StockSeguridadEntityID() {
		
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockSeguridadEntityID)) return false;
        StockSeguridadEntityID that = (StockSeguridadEntityID) o;
        return codSubcategoria == that.codSubcategoria &&
        		idOficina == that.idOficina &&
        		codCategoria == that.codCategoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codSubcategoria, codCategoria, idOficina);
    }
}
