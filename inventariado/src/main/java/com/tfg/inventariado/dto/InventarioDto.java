package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InventarioDto {

	private Integer codArticulo;
	private Integer idOficina;
	private Integer stock;
	
	private ArticuloDto articulo;
	private OficinaDto oficina;
}
