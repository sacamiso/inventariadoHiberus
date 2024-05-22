package com.tfg.inventariado.providerImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.LineaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.LineaEntity;
import com.tfg.inventariado.entity.id.LineaEntityID;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.LineaProvider;
import com.tfg.inventariado.repository.LineaRepository;

@Service
public class LineaProviderImpl implements LineaProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private LineaRepository lineaRepository;
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public LineaDto convertToMapDto(LineaEntity linea) {
		return modelMapper.map(linea, LineaDto.class);
	}

	@Override
	public LineaEntity convertToMapEntity(LineaDto linea) {
		return modelMapper.map(linea, LineaEntity.class);
	}

	@Override
	public List<LineaDto> listAllLineas() {
		List<LineaEntity> listaEntity = lineaRepository.findAll();
		return listaEntity.stream()
				.sorted(Comparator.comparing(LineaEntity::getNumeroLinea))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}
	
	private MessageResponseDto<String> validaLinea(LineaDto linea){
		Locale locale = LocaleContextHolder.getLocale();
		if(linea.getNumeroPedido()==null) {
			return MessageResponseDto.fail("");
		}
		if(linea.getNumeroLinea()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("numPedObl", null, locale));
		}
		LineaEntityID id = new LineaEntityID(linea.getNumeroPedido(), linea.getNumeroLinea());
		if(lineaRepository.findById(id).isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("lineaExiste", null, locale));
		}
		if(linea.getCodigoArticulo()==null || !this.articuloProvider.articuloExisteByID(linea.getCodigoArticulo())) {
			return MessageResponseDto.fail(messageSource.getMessage("articuloNoExiste", null, locale));
		}
		if(linea.getNumeroUnidades()==null || linea.getNumeroUnidades()<=0) {
			return MessageResponseDto.fail(messageSource.getMessage("minUnidad", null, locale));
		}
		if(linea.getPrecioLinea()<0) {
			return MessageResponseDto.fail(messageSource.getMessage("precioMin", null, locale));
		}
		if(linea.getDescuento()<0) {
			return MessageResponseDto.fail(messageSource.getMessage("decuentoNegativo", null, locale));
		}
		return MessageResponseDto.success(messageSource.getMessage("validado", null, locale));
	}

	@Transactional
	@Override
	public MessageResponseDto<String> addListLinea(List<LineaDto> lineas) {
		Locale locale = LocaleContextHolder.getLocale();
		MessageResponseDto<String> validacion;
		for (LineaDto l : lineas) {
			validacion = validaLinea(l);
			
			if(!validacion.isSuccess()) {
				return validacion;
			}
			
		}
		List<LineaEntity> list =  lineas.stream().map(this::convertToMapEntity).collect(Collectors.toList());
		lineaRepository.saveAll(list);
		return MessageResponseDto.success(messageSource.getMessage("lineaAnadida", null, locale));
	}
	
	@Override
	public MessageResponseDto<String> addLinea(LineaDto linea) {
		
		Locale locale = LocaleContextHolder.getLocale();
		
		MessageResponseDto<String> validacion = validaLinea(linea);
		
		if(!validacion.isSuccess()) {
			return validacion;
		}
		LineaEntity newLinea = convertToMapEntity(linea);
		newLinea = lineaRepository.save(newLinea);
		return MessageResponseDto.success(messageSource.getMessage("lineaAnadida", null, locale));
	}

	@Override
	public MessageResponseDto<String> editLinea(LineaDto linea, Integer numPedido, Integer numLinea) {
		Locale locale = LocaleContextHolder.getLocale();

		LineaEntityID id = new LineaEntityID(numPedido, numLinea);
		Optional<LineaEntity> optionalLinea = lineaRepository.findById(id);
		if(optionalLinea.isPresent()) {
			
			LineaEntity lineaToUpdate = optionalLinea.get();
			
			this.actualizarCampos(lineaToUpdate, linea);
			
			lineaRepository.save(lineaToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("lineaEditada", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("lineaNoExiste", null, locale));
		}
	}
	
	private void actualizarCampos(LineaEntity linea, LineaDto lineaToUpdate) {
		
		if(this.articuloProvider.articuloExisteByID(lineaToUpdate.getCodigoArticulo())) {
			linea.setCodigoArticulo(lineaToUpdate.getCodigoArticulo());
		}
		if(lineaToUpdate.getNumeroUnidades()>0) {
			linea.setNumeroUnidades(lineaToUpdate.getNumeroUnidades());
		}
		if(lineaToUpdate.getPrecioLinea()>0) {
			linea.setPrecioLinea(lineaToUpdate.getPrecioLinea());
		}
		if(lineaToUpdate.getDescuento()>=0) {
			linea.setDescuento(lineaToUpdate.getDescuento());
		}
			
	}

	@Override
	public MessageResponseDto<LineaDto> getLineaById(Integer numPedido, Integer numLinea) {
		Locale locale = LocaleContextHolder.getLocale();
		
		LineaEntityID id = new LineaEntityID(numPedido, numLinea);
		Optional<LineaEntity> optionalLinea = lineaRepository.findById(id);
		if(optionalLinea.isPresent()) {
			LineaDto lineaDto = this.convertToMapDto(optionalLinea.get());
			return MessageResponseDto.success(lineaDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("lineaNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<List<LineaDto>> listLineasByPedido(Integer numPedido) {

		List<LineaEntity> listaEntity = this.lineaRepository.findByNumeroPedido(numPedido);
		List<LineaDto> listaDto = listaEntity.stream()
				.sorted(Comparator.comparing(LineaEntity::getNumeroLinea))
				.map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean lineaExisteByID(Integer numPedido, Integer numLinea) {
		LineaEntityID id = new LineaEntityID(numPedido, numLinea);
		Optional<LineaEntity> optionalLinea = lineaRepository.findById(id);
		return optionalLinea    .isPresent() ? true : false;
	}

	

}
