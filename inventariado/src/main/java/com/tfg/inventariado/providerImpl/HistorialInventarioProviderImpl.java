package com.tfg.inventariado.providerImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.HistorialInventarioDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.HistorialInventarioEntity;
import com.tfg.inventariado.entity.HistorialInventarioEntityID;
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
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(historial.getCodArticulo(), historial.getIdOficina(), historial.getFecha());
		if(historialRepository.findById(id).isPresent()) {
			return MessageResponseDto.fail("El historial ya existe");
		}
		if(historial.getCodArticulo()==null) {
			return MessageResponseDto.fail("El codigo de artículo es obligatorio");
		}
		if(historial.getIdOficina()==null) {
			return MessageResponseDto.fail("La oficina es obligatoria");
		}
		if(historial.getStock()==null) {
			return MessageResponseDto.fail("El stock es obligatorio");
		}
		if(!this.articuloProvider.articuloExisteByID(historial.getCodArticulo())) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		if(!this.oficinaProvider.oficinaExisteByID(historial.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		if( historial.getFecha() == null) {
			return MessageResponseDto.fail("La fecha es obligatoria");
		}
		if( historial.getFecha().isAfter(LocalDateTime.now())) {
			return MessageResponseDto.fail("La fecha no puede ser posterior a la actual");
		}
		HistorialInventarioEntity newHistorial = convertToMapEntity(historial);
		newHistorial = historialRepository.save(newHistorial);
		return MessageResponseDto.success("Historial añadido con éxito");
	}

	@Transactional
	@Override
	public MessageResponseDto<String> editHistorial(HistorialInventarioDto historial, Integer idOf, Integer idArt,
			LocalDateTime fecha) {
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(idArt, idOf, fecha);
		Optional<HistorialInventarioEntity> optionalHistorial = historialRepository.findById(id);
		if(optionalHistorial.isPresent()) {
			HistorialInventarioEntity historialToUpdate = optionalHistorial.get();
			
			this.actualizarCampos(historialToUpdate, historial);
			
			historialRepository.save(historialToUpdate);
			
			return MessageResponseDto.success("Historial editado con éxito");
			
		}else {
			return MessageResponseDto.fail("El historial que se desea editar no existe");
		}
	}

	private void actualizarCampos(HistorialInventarioEntity historial, HistorialInventarioDto historialToUpdate) {
		
		historial.setStock(historialToUpdate.getStock());
		
	}
	@Override
	public MessageResponseDto<HistorialInventarioDto> getHistorialById(Integer idOf, Integer idArt, LocalDateTime fecha) {
		HistorialInventarioEntityID id = new HistorialInventarioEntityID(idArt, idOf, fecha);
		Optional<HistorialInventarioEntity> optionalHistorial = historialRepository.findById(id);
		if(optionalHistorial.isPresent()) {
			HistorialInventarioDto historialDto = this.convertToMapDto(optionalHistorial.get());
			return MessageResponseDto.success(historialDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ningún historial con ese id");
		}
	}

	@Override
	public MessageResponseDto<List<HistorialInventarioDto>> listHistorialByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<HistorialInventarioEntity> listaEntity = this.historialRepository.findByIdOficina(idOficina);
		List<HistorialInventarioDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<HistorialInventarioDto>> listHistorialByArticulo(Integer idArticulo) {
		if(!this.articuloProvider.articuloExisteByID(idArticulo)) {
			return MessageResponseDto.fail("El artículo no existe");
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

}
