package com.tfg.inventariado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LineaDto {

	private Integer numeroPedido;
	private Integer numeroLinea;
	private Integer codigoArticulo;
	private Integer numeroUnidades;
	private double precioLinea;
	private double descuento;
}
