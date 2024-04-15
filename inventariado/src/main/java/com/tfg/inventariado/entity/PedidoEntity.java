package com.tfg.inventariado.entity;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PedidoEntity {
	
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
	
	@ManyToOne
	@JoinColumn(name="id_empleado", referencedColumnName="id_empleado", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private EmpleadoEntity empleado;
	
	@ManyToOne
	@JoinColumn(name="id_proveedor", referencedColumnName="id_proveedor", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private ProveedorEntity proveedor;
	
	@ManyToOne
	@JoinColumn(name="id_oficina", referencedColumnName="id_oficina", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private OficinaEntity oficina;
	
	@ManyToOne
	@JoinColumn(name="condicion_pago", referencedColumnName="codigo_condicion", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private CondicionPagoEntity condicion;
	
	@ManyToOne
	@JoinColumn(name="medio_pago", referencedColumnName="codigo_medio", insertable = false, updatable = false)
	//Primero el campo de esta tabla y luego a la que referencia
	private MedioPagoEntity medio;
	
	@OneToMany(mappedBy = "numeroPedido")
	private List<LineaEntity> lineas;
}
