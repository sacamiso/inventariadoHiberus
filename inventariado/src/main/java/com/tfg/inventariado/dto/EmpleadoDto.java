package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EmpleadoDto {

	private Integer idEmpleado;
	private String dni;
	private String nombre;
	private String apellidos;
	private String usuario;
	private String contraseña;
	private String codRol;
	private Integer idOficina;
}
