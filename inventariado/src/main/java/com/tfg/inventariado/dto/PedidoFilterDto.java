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
	private Double ivaPedidoMin;
	private Double ivaPedidoMax;
	private Double costeTotalMin;
	private Double costeTotalMax;
	private Integer idEmpleado;
	private Integer plazoEntregaMin;
	private Integer plazoEntregaMax;
	private Double costesEnvioMin;
	private Double costesEnvioMax;
	private Integer idProveedor;
	private Integer idOficina;
	private LocalDate fechaRecepcion;
	private String codigoCondicionPago;
	private String codigoMedioPago;

	private Boolean recibido;
	private Double costeUnitarioMin;
	private Double costeUnitarioMax;
	
	private Boolean devuelto;
}
