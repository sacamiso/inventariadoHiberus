package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmpleadoCambioContrasena {

	private EmpleadoDto empleado;
	private String contraAct;
	private String contraNueva;
}
