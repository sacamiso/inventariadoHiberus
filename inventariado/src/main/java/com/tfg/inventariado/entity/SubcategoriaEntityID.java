package com.tfg.inventariado.entity;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class SubcategoriaEntityID implements Serializable {
	
	@Getter @Setter String codigoSubcategoria;
	@Getter @Setter String codigoCategoria;
	
	public SubcategoriaEntityID(String codigoSu, String codigoCate) {
		this.codigoCategoria = codigoCate;
		this.codigoSubcategoria = codigoSu;
	}
	
	public SubcategoriaEntityID() {
		
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubcategoriaEntityID)) return false;
        SubcategoriaEntityID that = (SubcategoriaEntityID) o;
        return codigoSubcategoria == that.codigoSubcategoria &&
        		codigoCategoria == that.codigoCategoria;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoSubcategoria, codigoCategoria);
    }
}
