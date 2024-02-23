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
public class SalidaDto {

	private Integer idSalida;
	private Integer numUnidades;
	private double costeTotal;
	private double costeUnitario;
    private LocalDate fechaSalida;
	private Integer idOficina;
	private Integer codArticulo;
}
