package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.CondicionPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.CondicionPagoEntity;
import com.tfg.inventariado.provider.CondicionPagoProvider;
import com.tfg.inventariado.repository.CondicionPagoRepository;

@Service
public class CondicionPagoProviderImpl implements CondicionPagoProvider {

	@Autowired
	private CondicionPagoRepository condicionPagoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public CondicionPagoDto convertToMapDto(CondicionPagoEntity condicion) {
		return modelMapper.map(condicion, CondicionPagoDto.class);
	}

	@Override
	public CondicionPagoEntity convertToMapEntity(CondicionPagoDto condicion) {
		return modelMapper.map(condicion, CondicionPagoEntity.class);
	}

	@Override
	public List<CondicionPagoDto> listAllCondicionPago() {
		List<CondicionPagoEntity> listaCondicionEntity = condicionPagoRepository.findAll();
		return listaCondicionEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addCondicionPago(CondicionPagoDto condicion) {
		if(condicion.getCodigoCondicion()==null || condicion.getCodigoCondicion().isEmpty()) {
			return MessageResponseDto.fail("El código es obligatorio");
		}
		if(condicion.getDescripcion()==null || condicion.getDescripcion().isEmpty()) {
			return MessageResponseDto.fail("La descripción es obligatoria");
		}
		if(condicionExisteByCodigo(condicion.getCodigoCondicion())) {
			return MessageResponseDto.fail("La condicion ya existe");
		}
		CondicionPagoEntity newCondicion = convertToMapEntity(condicion);
		newCondicion = condicionPagoRepository.save(newCondicion);
		return MessageResponseDto.success("Condición de pago añadida con éxito");
	}

	@Override
	public MessageResponseDto<String> editCondicionPago(CondicionPagoDto condicion, String codigoCondicion) {
		Optional<CondicionPagoEntity> optionalCondicion = condicionPagoRepository.findById(codigoCondicion);
		if(optionalCondicion.isPresent()) {
			CondicionPagoEntity condicionToUpdate = optionalCondicion.get();
			
			this.actualizarCampos(condicionToUpdate, condicion);
			
			condicionPagoRepository.save(condicionToUpdate);
			
			return MessageResponseDto.success("Condición de pago editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La condición de pago que se desea editar no existe");
		}
	}

	private void actualizarCampos(CondicionPagoEntity condicion, CondicionPagoDto condicionToUpdate) {
		if(StringUtils.isNotBlank(condicionToUpdate.getDescripcion())) {
			condicion.setDescripcion(condicionToUpdate.getDescripcion());
		}
	}
	
	@Override
	public MessageResponseDto<CondicionPagoDto> getCondicionPagoById(String codigoCondicion) {
		Optional<CondicionPagoEntity> optionalCondicion = condicionPagoRepository.findById(codigoCondicion);
		if(optionalCondicion.isPresent()) {
			CondicionPagoDto condicionDto = this.convertToMapDto(optionalCondicion.get());
			return MessageResponseDto.success(condicionDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna condición de pago con ese código");
		}
	}

	@Override
	public boolean condicionExisteByCodigo(String codigo) {
		Optional<CondicionPagoEntity> condicion = condicionPagoRepository.findById(codigo);
		return condicion.isPresent() ? true : false;
	}

}
