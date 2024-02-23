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
public class AsignacionDto {

	private Integer idAsignacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
	private Integer idEmpleado;
	private Integer codUnidad;
}
