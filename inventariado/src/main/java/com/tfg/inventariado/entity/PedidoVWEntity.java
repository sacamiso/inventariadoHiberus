package com.tfg.inventariado.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido_vw")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PedidoVWEntity {
	
	@Column(name="numero_pedido")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer numeroPedido;
	
	@Column(name = "fecha_pedido", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaPedido;
	
	@Column(name="iva_pedido", nullable = false)
	private double ivaPedido;
	
	@Column(name="coste_total", nullable = false)
	private double costeTotal;
	
	@Column(name="id_empleado", nullable = false)
	private Integer idEmpleado;
	
	@Column(name="plazo_entrega", nullable = false)
	private Integer plazoEntrega;
	
	@Column(name="costes_envio", nullable = false)
	private double costesEnvio;
	
	@Column(name="id_proveedor", nullable = false)
	private Integer idProveedor;
	
	@Column(name="id_oficina", nullable = false)
	private Integer idOficina;
	
	@Column(name = "fecha_recepcion")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaRecepcion;
	
	@Column(name="condicion_pago", nullable = false)
	private String condicionPago;
	
	@Column(name="medio_pago", nullable = false)
	private String medioPago;
	
	@Column(name="devuelto")
	private Boolean devuelto;
	
	@Column(name="coste_unitario")
	private Double costeUnitario;
	
	@Column(name="numero_unidades")
	private Integer numeroUnidades;
	
	

}
