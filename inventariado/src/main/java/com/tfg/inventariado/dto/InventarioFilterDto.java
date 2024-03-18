package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class InventarioFilterDto {
	private Integer idOficina;
	private Integer codArticulo;
	private Integer stockMin;
	private Integer stockMax;
}
