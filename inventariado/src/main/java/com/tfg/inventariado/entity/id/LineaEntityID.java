package com.tfg.inventariado.entity.id;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

public class LineaEntityID implements Serializable{

	@Getter @Setter private Integer numeroPedido;
	@Getter @Setter private Integer numeroLinea;
	
	public LineaEntityID(Integer numeroPe, Integer numeroLi) {
		this.numeroLinea = numeroLi;
		this.numeroPedido = numeroPe;
	}
	
	public LineaEntityID() {
		
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineaEntityID)) return false;
        LineaEntityID that = (LineaEntityID) o;
        return numeroPedido == that.numeroPedido &&
        		numeroLinea == that.numeroLinea;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroPedido, numeroLinea);
    }
}
