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
public class AsignacionFilterDto {

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String dniEmpleado;
    private String nombreEmpleado;
    private String apellidosEmpleado;
    private Integer codOficinaEmpleado;
    private Integer codUnidad;
    private Boolean finalizadas;
}
