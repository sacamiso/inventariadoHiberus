package com.tfg.inventariado.providerImpl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.HistorialInventarioDto;
import com.tfg.inventariado.dto.HistorialInventarioFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.HistorialInventarioEntity;
import com.tfg.inventariado.entity.id.HistorialInventarioEntityID;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.HistorialInventarioProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.repository.HistorialInventarioRepository;

@Service
public class HistorialInventarioProviderImpl implements HistorialInventarioProvider{

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private HistorialInventarioRepository historialRepository;
	
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public HistorialInventarioDto convertToMapDto(HistorialInventarioEntity historial) {
		return modelMapper.map(historial, HistorialInventarioDto.class);
	}

	@Override
	public HistorialInventarioEntity convertToMapEntity(HistorialInventarioDto historial) {
		return modelMapper.map(historial, HistorialInventarioEntity.class);
	}

	@Override
	public List<HistorialInventarioDto> listAllHistoriales() {
		List<HistorialInventarioEntity> listaEntity = historialRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Transactional
	@Override
	public MessageResponseDto<String> addHistorial(HistorialInventarioDto historial) {
		Locale locale = LocaleContextHolder.getLocale();
		
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(historial.getCodArticulo(), historial.getIdOficina(), historial.getFecha());
		if(historialRepository.findById(id).isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("historialExiste", null, locale));
		}
		if(historial.getCodArticulo()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("codigoObl", null, locale));
		}
		if(historial.getIdOficina()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("oficinaObl", null, locale));
		}
		if(historial.getStock()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("stockObl", null, locale));
		}
		if(!this.articuloProvider.articuloExisteByID(historial.getCodArticulo())) {
			return MessageResponseDto.fail(messageSource.getMessage("articuloNoExiste", null, locale));
		}
		if(!this.oficinaProvider.oficinaExisteByID(historial.getIdOficina())) {
			return MessageResponseDto.fail(messageSource.getMessage("oficinaNoExiste", null, locale));
		}
		if( historial.getFecha() == null) {
			return MessageResponseDto.fail(messageSource.getMessage("fechaObl", null, locale));
		}
		if( historial.getFecha().isAfter(LocalDateTime.now())) {
			return MessageResponseDto.fail(messageSource.getMessage("fechaPost", null, locale));
		}
		HistorialInventarioEntity newHistorial = convertToMapEntity(historial);
		newHistorial = historialRepository.save(newHistorial);
		return MessageResponseDto.success(messageSource.getMessage("historialAnadido", null, locale));
	}

	@Transactional
	@Override
	public MessageResponseDto<String> editHistorial(HistorialInventarioDto historial, Integer idOf, Integer idArt,
			LocalDateTime fecha) {
		Locale locale = LocaleContextHolder.getLocale();
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(idArt, idOf, fecha);
		Optional<HistorialInventarioEntity> optionalHistorial = historialRepository.findById(id);
		if(optionalHistorial.isPresent()) {
			HistorialInventarioEntity historialToUpdate = optionalHistorial.get();
			
			this.actualizarCampos(historialToUpdate, historial);
			
			historialRepository.save(historialToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("historialEditado", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("historialNoExiste", null, locale));
		}
	}

	private void actualizarCampos(HistorialInventarioEntity historial, HistorialInventarioDto historialToUpdate) {
		
		historial.setStock(historialToUpdate.getStock());
		
	}
	@Override
	public MessageResponseDto<HistorialInventarioDto> getHistorialById(Integer idOf, Integer idArt, LocalDateTime fecha) {
		Locale locale = LocaleContextHolder.getLocale();
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(idArt, idOf, fecha);
		Optional<HistorialInventarioEntity> optionalHistorial = historialRepository.findById(id);
		if(optionalHistorial.isPresent()) {
			HistorialInventarioDto historialDto = this.convertToMapDto(optionalHistorial.get());
			return MessageResponseDto.success(historialDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("historialNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<List<HistorialInventarioDto>> listHistorialByOficina(Integer idOficina) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail(messageSource.getMessage("oficinaNoExiste", null, locale));
		}
		List<HistorialInventarioEntity> listaEntity = this.historialRepository.findByIdOficina(idOficina);
		List<HistorialInventarioDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<HistorialInventarioDto>> listHistorialByArticulo(Integer idArticulo) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.articuloProvider.articuloExisteByID(idArticulo)) {
			return MessageResponseDto.fail(messageSource.getMessage("articuloNoExiste", null, locale));
		}
		List<HistorialInventarioEntity> listaEntity = this.historialRepository.findByCodArticulo(idArticulo);
		List<HistorialInventarioDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean inventarioExisteByID(Integer idOf, Integer idArt, LocalDateTime fecha) {
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(idArt, idOf, fecha);
		Optional<HistorialInventarioEntity> optionalHistorial = historialRepository.findById(id);
		return optionalHistorial.isPresent() ? true : false;	
	}

	@Override
	public MessageResponseListDto<List<HistorialInventarioDto>> listAllHistorialSkipLimit(Integer page, Integer size, HistorialInventarioFilterDto filtros) {
		Specification<HistorialInventarioEntity> spec = Specification.where(null);
		
		if (filtros != null) {
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
			if (filtros.getStockMin() != null) {
	            Integer stockMin = filtros.getStockMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("stock"), stockMin));
	        }
	        if (filtros.getStockMax() != null) {
	            Integer stockMax = filtros.getStockMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("stock"), stockMax));
	        }
	        if (filtros.getFecha() != null) {
	        	LocalDate fecha = filtros.getFecha();
	            LocalDateTime fechaInicio = fecha.atStartOfDay();
	            LocalDateTime fechaFin = fecha.atTime(LocalTime.MAX);
	            spec = spec.and((root, query, cb) -> cb.between(root.get("fecha"), fechaInicio, fechaFin)); 
	        }
	        if (filtros.getFechaInicioIntervalo() != null) {
	            LocalDateTime fechaInicioIntervalo = filtros.getFechaInicioIntervalo().atStartOfDay();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), fechaInicioIntervalo));
	        }
	        if (filtros.getFechaFinIntervalo() != null) {
	            LocalDateTime fechaFinIntervalo = filtros.getFechaFinIntervalo().atTime(LocalTime.MAX);
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), fechaFinIntervalo));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fecha"));
		Page<HistorialInventarioEntity> pageableHistorial = historialRepository.findAll(spec,pageable);
		
		List<HistorialInventarioEntity> listaEntity = pageableHistorial.getContent();
		List<HistorialInventarioDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) historialRepository.count(spec));
	}

	@Override
	public byte[] descargarExcelHistorialInventario(HistorialInventarioFilterDto filtros) throws IOException {
		Specification<HistorialInventarioEntity> spec = Specification.where(null);
		
		if (filtros != null) {
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
			if (filtros.getStockMin() != null) {
	            Integer stockMin = filtros.getStockMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("stock"), stockMin));
	        }
	        if (filtros.getStockMax() != null) {
	            Integer stockMax = filtros.getStockMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("stock"), stockMax));
	        }
	        if (filtros.getFecha() != null) {
	        	LocalDate fecha = filtros.getFecha();
	            LocalDateTime fechaInicio = fecha.atStartOfDay();
	            LocalDateTime fechaFin = fecha.atTime(LocalTime.MAX);
	            spec = spec.and((root, query, cb) -> cb.between(root.get("fecha"), fechaInicio, fechaFin)); 
	        }
	        if (filtros.getFechaInicioIntervalo() != null) {
	            LocalDateTime fechaInicioIntervalo = filtros.getFechaInicioIntervalo().atStartOfDay();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fecha"), fechaInicioIntervalo));
	        }
	        if (filtros.getFechaFinIntervalo() != null) {
	            LocalDateTime fechaFinIntervalo = filtros.getFechaFinIntervalo().atTime(LocalTime.MAX);
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fecha"), fechaFinIntervalo));
	        }
		}
		
		Sort sort = Sort.by(Sort.Direction.DESC, "fecha");
		List<HistorialInventarioEntity> listaHistorialInventarioEntity = this.historialRepository.findAll(spec,sort);
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet hoja = workbook.createSheet("Historial");
		
		XSSFCellStyle headerStyle = headerStyle(workbook);
		
		String[] encabezados = { "Fecha", "Referencia artículo", "Stock","Identificador oficina", "Dirección oficina", "Descripción artículo", "Categoría artículo", "Subcategoría Artículo", "Precio artículo (€)", "IVA artículo (%)", "Fabricante artículo", "Modelo artículo"};

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
	    dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy hh:mm:ss"));

		
		indiceFila++;
        for (HistorialInventarioEntity histo : listaHistorialInventarioEntity) {
            fila = hoja.createRow(indiceFila);
            fila.createCell(0).setCellValue(histo.getFecha());
            fila.getCell(0).setCellStyle(dateCellStyle);
            fila.createCell(1).setCellValue(histo.getArticulo().getReferencia());
            fila.createCell(2).setCellValue(histo.getStock());
            String dirOficina = histo.getOficina().getDireccion() +", " + histo.getOficina().getCodigoPostal() +", " + histo.getOficina().getLocalidad() +", " + histo.getOficina().getPais();
            fila.createCell(3).setCellValue(histo.getIdOficina());
            fila.createCell(4).setCellValue(dirOficina);
            fila.createCell(5).setCellValue(histo.getArticulo().getDescripcion());
            fila.createCell(6).setCellValue(histo.getArticulo().getCodCategoria());
            fila.createCell(7).setCellValue(histo.getArticulo().getCodSubcategoria());
            fila.createCell(8).setCellValue(histo.getArticulo().getPrecioUnitario());
            fila.createCell(9).setCellValue(histo.getArticulo().getIva());
            fila.createCell(10).setCellValue(histo.getArticulo().getFabricante());
            fila.createCell(11).setCellValue(histo.getArticulo().getModelo());
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

}
