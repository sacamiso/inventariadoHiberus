package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MedioPagoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.MedioPagoEntity;
import com.tfg.inventariado.provider.MedioPagoProvider;
import com.tfg.inventariado.repository.MedioPagoRepository;

@Service
public class MedioPagoProviderImpl implements MedioPagoProvider {

	@Autowired
	private MedioPagoRepository medioPagoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public MedioPagoDto convertToMapDto(MedioPagoEntity medio) {
		return modelMapper.map(medio, MedioPagoDto.class);
	}

	@Override
	public MedioPagoEntity convertToMapEntity(MedioPagoDto medio) {
		return modelMapper.map(medio, MedioPagoEntity.class);
	}

	@Override
	public List<MedioPagoDto> listAllMedioPago() {
		List<MedioPagoEntity> listaMedioEntity = medioPagoRepository.findAll();
		return listaMedioEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addMedioPago(MedioPagoDto medio) {
		if(medio.getCodigoMedio()==null || medio.getCodigoMedio().isEmpty()) {
			return MessageResponseDto.fail("El código es obligatorio");
		}
		if(medio.getDescripcion()==null || medio.getDescripcion().isEmpty()) {
			return MessageResponseDto.fail("La descripción es obligatoria");
		}
		if(medioExisteByCodigo(medio.getCodigoMedio())) {
			return MessageResponseDto.fail("El medio ya existe");
		}
		MedioPagoEntity newMedio = convertToMapEntity(medio);
		newMedio = medioPagoRepository.save(newMedio);
		return MessageResponseDto.success("Medio de pago añadido con éxito");
	}

	@Override
	public MessageResponseDto<String> editMedioPago(MedioPagoDto medio, String codigo) {
		Optional<MedioPagoEntity> optionalMedio = medioPagoRepository.findById(codigo);
		if(optionalMedio.isPresent()) {
			MedioPagoEntity medioToUpdate = optionalMedio.get();
			
			this.actualizarCampos(medioToUpdate, medio);
			
			medioPagoRepository.save(medioToUpdate);
			
			return MessageResponseDto.success("Medio de pago editado con éxito");
			
		}else {
			return MessageResponseDto.fail("El medio de pago que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(MedioPagoEntity medio, MedioPagoDto medioToUpdate) {
		if(StringUtils.isNotBlank(medioToUpdate.getDescripcion())) {
			medio.setDescripcion(medioToUpdate.getDescripcion());
		}
	}

	@Override
	public MessageResponseDto<MedioPagoDto> getMedioPagoById(String codigo) {
		Optional<MedioPagoEntity> optionalMedio = medioPagoRepository.findById(codigo);
		if(optionalMedio.isPresent()) {
			MedioPagoDto medioDto = this.convertToMapDto(optionalMedio.get());
			return MessageResponseDto.success(medioDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ningún medio de pago con ese código");
		}
	}

	@Override
	public boolean medioExisteByCodigo(String codigo) {
		Optional<MedioPagoEntity> medio = medioPagoRepository.findById(codigo);
		return medio.isPresent() ? true : false;
	}

}
