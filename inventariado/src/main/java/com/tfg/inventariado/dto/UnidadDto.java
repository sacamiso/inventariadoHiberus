package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UnidadDto {

	private Integer codigoInterno;
	private String codEstado;
	private Integer numeroPedido;
	private Integer idSalida;
	private Integer idOficina;
	private Integer codArticulo;
}
