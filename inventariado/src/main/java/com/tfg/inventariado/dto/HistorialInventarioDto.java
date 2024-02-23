package com.tfg.inventariado.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class HistorialInventarioDto {

	private Integer codArticulo;
	private Integer idOficina;
    private LocalDateTime fecha;
	private Integer stock;
}
