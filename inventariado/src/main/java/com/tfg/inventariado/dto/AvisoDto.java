package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AvisoDto {

	private OficinaDto oficina;
	private SubcategoriaDto subcategoria;
	private Integer cantidadStockSeguridad;
	private int cantidadInventario;
	private int cantidadNecesaria;
}
