package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ArticuloFilterDto {
	private String descripcion;
	private Double precioUnitarioMin;
	private Double precioUnitarioMax;
	private String referencia;
	private String codigoCategoria;
	private String codigoSubcatogria;
	private Double ivaMin;
	private Double ivaMax;
	private String fabricante;
	private String modelo;
}
