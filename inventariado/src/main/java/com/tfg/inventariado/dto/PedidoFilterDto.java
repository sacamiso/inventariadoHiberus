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
public class PedidoFilterDto {
	private LocalDate fechaPedido;
	private Integer ivaPedidoMin;
	private Integer ivaPedidoMax;
	private Integer costeTotalMin;
	private Integer costeTotalMax;
	private Integer idEmpleado;
	private Integer plazoEntregaMin;
	private Integer plazoEntregaMax;
	private Integer costesEnvioMin;
	private Integer costesEnvioMax;
	private Integer idProveedor;
	private Integer idOficina;
	private LocalDate fechaRecepcion;
	private String codigoCondicionPago;
	private String codigoMedioPago;

	private Boolean recibido;
	private Double costeUnitarioMin;
	private Double costeUnitarioMax;
}
