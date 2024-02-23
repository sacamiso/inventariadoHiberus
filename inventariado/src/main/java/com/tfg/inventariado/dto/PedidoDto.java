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
public class PedidoDto {

	private Integer numeroPedido;
    private LocalDate fechaPedido;
	private double ivaPedido;
	private double costeTotal;
	private Integer idEmpleado;
	private Integer plazoEntrega;
	private double costesEnvio;
	private Integer idProveedor;
	private Integer idOficina;
    private LocalDate fechaRecepcion;
	private String condicionPago;
	private String medioPago;
}
