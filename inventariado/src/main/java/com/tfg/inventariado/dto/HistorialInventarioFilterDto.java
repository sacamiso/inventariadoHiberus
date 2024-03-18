package com.tfg.inventariado.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class HistorialInventarioFilterDto {
	private Integer idOficina;
	private Integer codArticulo;
	private Integer stockMin;
	private Integer stockMax;
	private LocalDate fecha;
	
}
