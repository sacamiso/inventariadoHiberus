package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class StockSeguridadDto {

	private String codSubcategoria;
	private String codCategoria;
	private Integer idOficina;
	private Integer cantidad;
	private Integer plazoEntregaMedio;
}
