package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ArticuloDto {
	
	private Integer codigoArticulo;
	private String descripcion;
	private double precioUnitario;
	private String referencia;
	private String codCategoria;
	private String codSubcategoria;
	private double iva;
	private String fabricante;
	private String modelo;
}
