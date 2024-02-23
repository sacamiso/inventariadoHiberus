package com.tfg.inventariado.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historial_inventario")
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(HistorialInventarioEntityID.class)
public class HistorialInventarioEntity {

	@Column(name="cod_articulo")
	@Id
	private Integer codArticulo;
	
	@Column(name="id_oficina")
	@Id
	private Integer idOficina;
	
	@Column(name = "fecha")
	@Id
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fecha;
	
	@Column(name="stock", nullable = false)
	private Integer stock;
}
