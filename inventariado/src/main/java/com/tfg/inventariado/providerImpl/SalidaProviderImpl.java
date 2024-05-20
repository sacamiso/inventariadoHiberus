package com.tfg.inventariado.providerImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.InventarioDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.OficinaDto;
import com.tfg.inventariado.dto.SalidaDto;
import com.tfg.inventariado.dto.SalidaFilterDto;
import com.tfg.inventariado.entity.SalidaEntity;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.InventarioProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.SalidaProvider;
import com.tfg.inventariado.repository.SalidaRepository;

@Service
public class SalidaProviderImpl implements SalidaProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private SalidaRepository saldiaRepository;
	
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
	@Autowired
	private InventarioProvider inventarioProvider;
	
	@Override
	public SalidaDto convertToMapDto(SalidaEntity salida) {
		return modelMapper.map(salida, SalidaDto.class);
	}

	@Override
	public SalidaEntity convertToMapEntity(SalidaDto salida) {
		return modelMapper.map(salida, SalidaEntity.class);
	}

	@Override
	public List<SalidaDto> listAllSalidas() {
		List<SalidaEntity> listaEntity = saldiaRepository.findAll();
		return listaEntity.stream()
				.sorted(Comparator.comparing(SalidaEntity::getIdSalida))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	//Cuando se añade una salida se modifica el inventario y eso a su vez modifica el historial
	@Transactional
	@Override
	public MessageResponseDto<Integer> addSalida(SalidaDto salida) {
		if(salida.getNumUnidades()==null) {
			return MessageResponseDto.fail("El número de unidades es obligatorio");
		}
		if(salida.getIdOficina()==null) {
			return MessageResponseDto.fail("El id de oficina es obligatorio");
		}
		if(salida.getCodArticulo()==null) {
			return MessageResponseDto.fail("El artículo es obligatorio");
		}
		if(!this.articuloProvider.articuloExisteByID(salida.getCodArticulo())) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		if(!this.oficinaProvider.oficinaExisteByID(salida.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		MessageResponseDto<InventarioDto> inventario = inventarioProvider.getInventarioById(salida.getIdOficina(), salida.getCodArticulo());
		if(!inventario.isSuccess()) {
			return MessageResponseDto.fail("No se puede dar salida a artículos que no están inventariados");
		}
		if(inventario.getMessage().getStock() < salida.getNumUnidades()) {
			return MessageResponseDto.fail("No se puede dar salida a más artículos de los existentes");
		}
		if(salida.getFechaSalida()== null) {
			salida.setFechaSalida(LocalDate.now());
		}
		if(salida.getFechaSalida().isAfter(LocalDate.now())) {
			return MessageResponseDto.fail("La fecha no puede ser posterior a la actual");
		}
		
		OficinaDto of = oficinaProvider.getOficinaById(salida.getIdOficina()).getMessage();
		ArticuloDto art = articuloProvider.getArticuloById(salida.getCodArticulo()).getMessage();
		
		InventarioDto inventarioDto = new InventarioDto(salida.getCodArticulo(), salida.getIdOficina(), inventario.getMessage().getStock()-salida.getNumUnidades(),art,of );
		MessageResponseDto<String>  msgInventario = inventarioProvider.editInventario(inventarioDto,salida.getIdOficina(),salida.getCodArticulo());
		if(!msgInventario.isSuccess()) {
			return MessageResponseDto.fail(msgInventario.getError());
		}
		
		SalidaEntity newSalida = convertToMapEntity(salida);
		newSalida = saldiaRepository.save(newSalida);
		return MessageResponseDto.success(newSalida.getIdSalida());
	}

	@Transactional
	@Override
	public MessageResponseDto<String> editSalida(SalidaDto salida, Integer id) {
		Optional<SalidaEntity> optionalSalida = saldiaRepository.findById(id);
		if(optionalSalida.isPresent()) {
			
			if(salida.getCodArticulo()!= 0 && !this.articuloProvider.articuloExisteByID(salida.getCodArticulo())) {
				return MessageResponseDto.fail("El artículo no existe");
			}
			if(salida.getIdOficina()!= 0 && !this.oficinaProvider.oficinaExisteByID(salida.getIdOficina())) {
				return MessageResponseDto.fail("La oficina no existe");
			}
			MessageResponseDto<InventarioDto> inventario = inventarioProvider.getInventarioById(salida.getIdOficina(), salida.getCodArticulo());
			if(!inventario.isSuccess()) {
				return MessageResponseDto.fail("No se puede dar salida a artículos que no están inventariados");
			}
			if(inventario.getMessage().getStock() + optionalSalida.get().getNumUnidades() < salida.getNumUnidades()) {
				return MessageResponseDto.fail("No se puede dar salida a más artículos de los existentes");
			}
			if(salida.getFechaSalida()!=null && salida.getFechaSalida().isAfter(LocalDate.now())) {
				return MessageResponseDto.fail("La fecha no puede ser posterior a la actual");
			}
			
			OficinaDto of = oficinaProvider.getOficinaById(salida.getIdOficina()).getMessage();
			ArticuloDto art = articuloProvider.getArticuloById(salida.getCodArticulo()).getMessage();
			
			InventarioDto inventarioDto = new InventarioDto(salida.getCodArticulo(), salida.getIdOficina(), inventario.getMessage().getStock()+ optionalSalida.get().getNumUnidades()-salida.getNumUnidades(),art,of );
			MessageResponseDto<String>  msgInventario = inventarioProvider.editInventario(inventarioDto,salida.getIdOficina(),salida.getCodArticulo());
			if(!msgInventario.isSuccess()) {
				return MessageResponseDto.fail(msgInventario.getError());
			}
			
			SalidaEntity salidaToUpdate = optionalSalida.get();
			
			this.actualizarCampos(salidaToUpdate, salida);
			
			saldiaRepository.save(salidaToUpdate);
			
			return MessageResponseDto.success("Salida editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La salida que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(SalidaEntity salida, SalidaDto salidaToUpdate) {
		
		if(salidaToUpdate.getCodArticulo()!= 0) {
			salida.setCodArticulo(salidaToUpdate.getCodArticulo());
		}
		if(salidaToUpdate.getIdOficina()!= 0) {
			salida.setIdOficina(salidaToUpdate.getIdOficina());
		}

		salida.setNumUnidades(salidaToUpdate.getNumUnidades());
		
		if(salidaToUpdate.getFechaSalida()!=null && !salidaToUpdate.getFechaSalida().isAfter(LocalDate.now())) {
			salida.setFechaSalida(salidaToUpdate.getFechaSalida());
		}
		
		salida.setCosteTotal(salidaToUpdate.getCosteTotal());
		salida.setCosteUnitario(salidaToUpdate.getCosteUnitario());
		
	}

	@Override
	public MessageResponseDto<SalidaDto> getSalidaById(Integer id) {
		Optional<SalidaEntity> optional = saldiaRepository.findById(id);
		if(optional.isPresent()) {
			SalidaDto salidaDto = this.convertToMapDto(optional.get());
			return MessageResponseDto.success(salidaDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna salida con ese id");
		}
	}

	@Override
	public MessageResponseDto<List<SalidaDto>> listSalidaByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<SalidaEntity> listaEntity = this.saldiaRepository.findByIdOficina(idOficina);
		List<SalidaDto> listaDto = listaEntity.stream()
				.sorted(Comparator.comparing(SalidaEntity::getIdSalida))
				.map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<SalidaDto>> listSalidaByArticulo(Integer idArticulo) {
		if(!this.articuloProvider.articuloExisteByID(idArticulo)) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		List<SalidaEntity> listaEntity = this.saldiaRepository.findByCodArticulo(idArticulo);
		List<SalidaDto> listaDto = listaEntity.stream()
				.sorted(Comparator.comparing(SalidaEntity::getIdSalida))
				.map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean salidaExisteByID(Integer id) {
		Optional<SalidaEntity> optional = saldiaRepository.findById(id);
		return optional.isPresent() ? true : false;

	}

	@Override
	public MessageResponseListDto<List<SalidaDto>> listAllSalidasSkipLimit(Integer page, Integer size, SalidaFilterDto filtros) {
		Specification<SalidaEntity> spec = Specification.where(null);
		if (filtros != null) {
			if (filtros.getNumeroUnidades() != null) {
				Integer numU = filtros.getNumeroUnidades();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("numUnidades"), numU));
			}
			if (filtros.getCosteTotalMin() != null) {
				Double cosTotMin = filtros.getCosteTotalMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeTotal"), cosTotMin));
			}
			if (filtros.getCosteTotalMax() != null) {
				Double cosTotMax = filtros.getCosteTotalMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costeTotal"), cosTotMax));
			}
			if (filtros.getCosteUnitarioMin() != null && filtros.getCosteUnitarioMin() != 0) {
				Double costeUnMin = filtros.getCosteUnitarioMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeUnitario"), costeUnMin));
			}
			if (filtros.getCosteUnitarioMax() != null && filtros.getCosteUnitarioMax() != 0) {
				Double costeUnMax = filtros.getCosteUnitarioMax();
				spec = spec.and((root, query, cb) -> cb.or(
				        cb.isNull(root.get("costeUnitario")),
				        cb.lessThanOrEqualTo(root.get("costeUnitario"), costeUnMax)
				    ));
			}
			if (filtros.getFechaSalida() != null) {
				LocalDate fecha = filtros.getFechaSalida();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaSalida"), fecha));
	        }
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
			if (filtros.getFechaInicioIntervalo() != null) {
	            LocalDate fechaInicioIntervalo = filtros.getFechaInicioIntervalo();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaSalida"), fechaInicioIntervalo));
	        }
	        if (filtros.getFechaFinIntervalo() != null) {
	            LocalDate fechaFinIntervalo = filtros.getFechaFinIntervalo();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaSalida"), fechaFinIntervalo));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaSalida"));
		Page<SalidaEntity> pageablesalida = saldiaRepository.findAll(spec,pageable);
		
		List<SalidaEntity> listaEntity = pageablesalida.getContent();
		List<SalidaDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) saldiaRepository.count(spec));
		
	}

	@Override
	public byte[] descargarExcelSalida(SalidaFilterDto filtros) throws IOException {
		Specification<SalidaEntity> spec = Specification.where(null);
		if (filtros != null) {
			if (filtros.getNumeroUnidades() != null) {
				Integer numU = filtros.getNumeroUnidades();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("numUnidades"), numU));
			}
			if (filtros.getCosteTotalMin() != null) {
				Double cosTotMin = filtros.getCosteTotalMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeTotal"), cosTotMin));
			}
			if (filtros.getCosteTotalMax() != null) {
				Double cosTotMax = filtros.getCosteTotalMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costeTotal"), cosTotMax));
			}
			if (filtros.getCosteUnitarioMin() != null && filtros.getCosteUnitarioMin() != 0) {
				Double costeUnMin = filtros.getCosteUnitarioMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeUnitario"), costeUnMin));
			}
			if (filtros.getCosteUnitarioMax() != null && filtros.getCosteUnitarioMax() != 0) {
				Double costeUnMax = filtros.getCosteUnitarioMax();
				spec = spec.and((root, query, cb) -> cb.or(
				        cb.isNull(root.get("costeUnitario")),
				        cb.lessThanOrEqualTo(root.get("costeUnitario"), costeUnMax)
				    ));
			}
			if (filtros.getFechaSalida() != null) {
				LocalDate fecha = filtros.getFechaSalida();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaSalida"), fecha));
	        }
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
			if (filtros.getFechaInicioIntervalo() != null) {
	            LocalDate fechaInicioIntervalo = filtros.getFechaInicioIntervalo();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaSalida"), fechaInicioIntervalo));
	        }
	        if (filtros.getFechaFinIntervalo() != null) {
	            LocalDate fechaFinIntervalo = filtros.getFechaFinIntervalo();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaSalida"), fechaFinIntervalo));
	        }
		}
		
		Sort sort = Sort.by(Sort.Direction.DESC, "fechaSalida");
		List<SalidaEntity> listaSalidaEntity = this.saldiaRepository.findAll(spec,sort);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet hoja = workbook.createSheet("Salidas");
		
		XSSFCellStyle headerStyle = headerStyle(workbook);
		
		String[] encabezados = { "Fecha de salida", "Número de unidades", "Coste total (€)", "Precio medio ponderado (PMP €)", "Dirección de la oficina de salida",
				"Referencia artículo", "Descripción artículo", "Categoría artículo", "Subcategoría Artículo", "Precio artículo (€)", "IVA artículo (%)",
				"Fabricante artículo", "Modelo artículo"};

		int indiceFila = 0;
		XSSFRow fila = hoja.createRow(indiceFila); 
		
		for (int i = 0; i < encabezados.length; i++) {
            String encabezado = encabezados[i];
            XSSFCell celda = fila.createCell(i);
            celda.setCellValue(encabezado);
            celda.setCellStyle(headerStyle);
        }
		
		HashMap<String, XSSFCellStyle> styles = new HashMap<>();
		styles.put("HEADER", headerStyle);
		
		XSSFCellStyle cellStyle = workbook.createCellStyle();
	    cellStyle.setAlignment(HorizontalAlignment.CENTER);
	    cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    
	    //Formato de fecha
	    CreationHelper creationHelper = workbook.getCreationHelper();
	    CellStyle dateCellStyle = workbook.createCellStyle();
	    dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));
		
		indiceFila++;
        for (SalidaEntity salida : listaSalidaEntity) {
            fila = hoja.createRow(indiceFila);
            fila.createCell(0).setCellValue(salida.getFechaSalida());
            fila.getCell(0).setCellStyle(dateCellStyle);
            fila.createCell(1).setCellValue(salida.getNumUnidades());
            fila.createCell(2).setCellValue(salida.getCosteTotal());
            fila.createCell(3).setCellValue(salida.getCosteUnitario());
            
            String dirOficina = salida.getOficina().getDireccion() +", " + salida.getOficina().getLocalidad() +", " + salida.getOficina().getPais();
            fila.createCell(4).setCellValue(dirOficina);
            
            fila.createCell(5).setCellValue(salida.getArticulo().getReferencia());
            fila.createCell(6).setCellValue(salida.getArticulo().getDescripcion());
            fila.createCell(7).setCellValue(salida.getArticulo().getCodCategoria());
            fila.createCell(8).setCellValue(salida.getArticulo().getCodSubcategoria());
            fila.createCell(9).setCellValue(salida.getArticulo().getPrecioUnitario());
            fila.createCell(10).setCellValue(salida.getArticulo().getIva());
            fila.createCell(11).setCellValue(salida.getArticulo().getFabricante());
            fila.createCell(12).setCellValue(salida.getArticulo().getModelo());
            indiceFila++;
        }
        
        for (int i = 0; i < encabezados.length; i++) {
            hoja.autoSizeColumn(i);
            hoja.setDefaultColumnStyle(i, cellStyle);
        }
        
        // Convertir el workbook a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] bytes = outputStream.toByteArray();
        outputStream.close();
        workbook.close();
        
        return bytes;
	}
	
	XSSFCellStyle headerStyle(XSSFWorkbook workbook) {
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		XSSFFont headerFont = workbook.createFont();
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}

	@Override
	public byte[] descargarExcelSalidaById(Integer id) throws IOException {
		
		MessageResponseDto<SalidaDto> salidaMSG = this.getSalidaById(id);
		if(!salidaMSG.isSuccess()) {
			throw new IOException("No se ha encontrado la salida");
		}
		SalidaDto salida = salidaMSG.getMessage();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet hoja = workbook.createSheet("Salida " + id);
		
		XSSFCellStyle headerStyle = headerStyle(workbook);
		
		String[] encabezados = { "Número de salida", "Fecha de salida", "Número de unidades", "Coste unitario (PMP-€)",
				"Coste total"};
		
		int indiceFila = 1;
		XSSFRow fila = hoja.createRow(indiceFila);
		
		for (int i = 0; i < encabezados.length; i++) {
			String encabezado = encabezados[i];
			XSSFCell celda = fila.createCell(i);
			celda.setCellValue(encabezado);
			celda.setCellStyle(headerStyle);
		}
		
		HashMap<String, XSSFCellStyle> styles = new HashMap<>();
		styles.put("HEADER", headerStyle);

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		// Formato de fecha
		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));
		
		indiceFila++;
		
		fila = hoja.createRow(indiceFila);
		
		fila.createCell(0).setCellValue(salida.getIdSalida());
		
		fila.createCell(1).setCellValue(salida.getFechaSalida());
		fila.getCell(1).setCellStyle(dateCellStyle);
		
		fila.createCell(2).setCellValue(salida.getNumUnidades());
		fila.createCell(3).setCellValue(salida.getCosteUnitario());
		fila.createCell(4).setCellValue(salida.getCosteTotal());
		
		indiceFila++;
		indiceFila++;
		
		String[] encabezadosArtículo = { "Referencia", "Descripción", "Precio (€)", "IVA (%)", "Categoría",
				"Subcategoría", "Fabricante", "Modelo" };
		
		fila = hoja.createRow(indiceFila);
		XSSFCell celda = fila.createCell(0);
		celda.setCellValue("ARTÍCULO");
		celda.setCellStyle(headerStyle);
		indiceFila++;
		indiceFila++;
		
		XSSFCellStyle headerStyleLinea = headerStyleLinea(workbook);
		fila = hoja.createRow(indiceFila);
		for (int i = 0; i < encabezadosArtículo.length; i++) {
			String encabezadoL = encabezadosArtículo[i];
			XSSFCell celda1 = fila.createCell(i);
			celda1.setCellValue(encabezadoL);
			celda1.setCellStyle(headerStyleLinea);
		}
		indiceFila++;
		fila = hoja.createRow(indiceFila);
		fila.createCell(0).setCellValue(salida.getArticulo().getReferencia());
        fila.createCell(1).setCellValue(salida.getArticulo().getDescripcion());
        fila.createCell(2).setCellValue(salida.getArticulo().getPrecioUnitario());
        fila.createCell(3).setCellValue(salida.getArticulo().getIva());
        fila.createCell(4).setCellValue(salida.getArticulo().getCodCategoria());
        fila.createCell(5).setCellValue(salida.getArticulo().getCodSubcategoria());
        fila.createCell(6).setCellValue(salida.getArticulo().getFabricante());
        fila.createCell(7).setCellValue(salida.getArticulo().getModelo());
        
        indiceFila++;
		indiceFila++;
		
		String[] encabezadosOficina = { "Dirección", "Localidad", "Provincia", "País", "Código postal"};
		
		fila = hoja.createRow(indiceFila);
		XSSFCell celda3 = fila.createCell(0);
		celda3.setCellValue("OFICINA DE SALIDA");
		celda3.setCellStyle(headerStyle);
		indiceFila++;
		indiceFila++;
		
		fila = hoja.createRow(indiceFila);
		for (int i = 0; i < encabezadosOficina.length; i++) {
			String encabezadoL = encabezadosOficina[i];
			XSSFCell celda1 = fila.createCell(i);
			celda1.setCellValue(encabezadoL);
			celda1.setCellStyle(headerStyleLinea);
		}
		indiceFila++;
		fila = hoja.createRow(indiceFila);
		fila.createCell(0).setCellValue(salida.getOficina().getDireccion());
		fila.createCell(1).setCellValue(salida.getOficina().getLocalidad());
		
		if(salida.getOficina().getProvincia()!=null) {
			fila.createCell(2).setCellValue(salida.getOficina().getProvincia());
		}else {
			fila.createCell(2).setCellValue("-");
		}
		
		fila.createCell(3).setCellValue(salida.getOficina().getPais());
		
		if(salida.getOficina().getProvincia()!=null) {
			fila.createCell(4).setCellValue(salida.getOficina().getCodigoPostal());
		}else {
			fila.createCell(4).setCellValue("-");
		}
		
		
		for (int i = 0; i < 8; i++) {
			hoja.autoSizeColumn(i);
			hoja.setDefaultColumnStyle(i, cellStyle);
		}
		
		// Convertir el workbook a bytes
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		byte[] bytes = outputStream.toByteArray();
		outputStream.close();
		workbook.close();

		return bytes;
	}

	XSSFCellStyle headerStyleLinea(XSSFWorkbook workbook) {
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		XSSFFont headerFont = workbook.createFont();
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}
}
