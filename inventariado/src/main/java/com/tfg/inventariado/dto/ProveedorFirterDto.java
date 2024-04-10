package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ProveedorFirterDto {
	private String cif;
	private String razonSocial;
	private String direccion;
	private Integer codigoPostal;
	private String localidad;
	private String telefono;
	private String email;
}
