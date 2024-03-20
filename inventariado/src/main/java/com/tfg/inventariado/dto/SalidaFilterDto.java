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
public class SalidaFilterDto {
	private Integer numeroUnidades;
	private Double costeTotalMin;
	private Double costeTotalMax;
	private Double costeUnitarioMin;
	private Double costeUnitarioMax;
	private LocalDate fechaSalida;
	private Integer idOficina;
	private Integer codArticulo;
}
