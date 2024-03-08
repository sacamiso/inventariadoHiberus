package com.tfg.inventariado.dto;

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
	private Integer numeroPedido;
	private Integer idSalida;
	private Integer idOficina;
	private Integer codArticulo;
}
