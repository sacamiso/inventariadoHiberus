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
public class UnidadFilterDto {
	private String codEstado;
	private LocalDate fechaPedido;
	private LocalDate fechaSalida;
	private Integer idOficina;
	private Integer codArticulo;
	private Boolean disponible;
}
