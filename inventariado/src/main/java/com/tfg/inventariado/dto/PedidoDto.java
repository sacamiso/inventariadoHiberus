package com.tfg.inventariado.dto;

import java.time.LocalDate;
import java.util.List;

import com.tfg.inventariado.entity.CondicionPagoEntity;
import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.entity.MedioPagoEntity;
import com.tfg.inventariado.entity.OficinaEntity;
import com.tfg.inventariado.entity.ProveedorEntity;

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
	
	private double costeUnitario;
	private Integer numeroUnidades;
	
	private EmpleadoEntity empleado;
	private ProveedorEntity proveedor;
	private OficinaEntity oficina;
	private CondicionPagoEntity condicion;
	private MedioPagoEntity medio;
	
	private List<LineaDto> lineas;
	
}
