package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StockSeguridadFilterDto {
	private String codCategoria;
	private String codSubcategoria;
	private Integer idOficina;
	private Integer cantidad;
	private Integer plazoMin;
	private Integer plazoMax;
}
