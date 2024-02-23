package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.OficinaDto;
import com.tfg.inventariado.entity.OficinaEntity;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.repository.OficinaRepository;

@Service
public class OficinaProviderImpl implements OficinaProvider {

	@Autowired
	private OficinaRepository oficinaRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public OficinaDto convertToMapDto(OficinaEntity oficina) {
		return modelMapper.map(oficina, OficinaDto.class);
	}

	@Override
	public OficinaEntity convertToMapEntity(OficinaDto oficina) {
		return modelMapper.map(oficina, OficinaEntity.class);
	}

	@Override
	public List<OficinaDto> listAllOficinas() {
		List<OficinaEntity> listaOficinaEntity = oficinaRepository.findAll();
		return listaOficinaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addOficina(OficinaDto oficina) {
		if(oficinaExisteByID(oficina.getIdOficina())) {
			return MessageResponseDto.fail("La oficina ya existe");
		}
		if(oficina.getDireccion()==null || oficina.getDireccion().isEmpty()) {
			return MessageResponseDto.fail("La direccion es obligatoria");
		}
		if(oficina.getLocalidad()==null || oficina.getLocalidad().isEmpty()) {
			return MessageResponseDto.fail("La localidad es obligatoria");
		}
		if(oficina.getPais()==null || oficina.getPais().isEmpty()) {
			return MessageResponseDto.fail("El país es obligatorio");
		}
		OficinaEntity newOficina = convertToMapEntity(oficina);
		newOficina = oficinaRepository.save(newOficina);
		return MessageResponseDto.success("Oficina añadida con éxito");
	}

	@Override
	public MessageResponseDto<String> editOficina(OficinaDto oficina, Integer id) {
		Optional<OficinaEntity> optionalOficina = oficinaRepository.findById(id);
		if(optionalOficina.isPresent()) {
			OficinaEntity oficinaToUpdate = optionalOficina.get();
			
			this.actualizarCampos(oficinaToUpdate, oficina);
			
			oficinaRepository.save(oficinaToUpdate);
			
			return MessageResponseDto.success("Oficina editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La oficina que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(OficinaEntity oficina, OficinaDto oficinaToUpdate) {
		String cp = String.valueOf(oficinaToUpdate.getCodigoPostal());
		if(cp.length() == 5) {
			oficina.setCodigoPostal(oficinaToUpdate.getCodigoPostal());
		}
		if(StringUtils.isNotBlank(oficinaToUpdate.getDireccion())) {
			oficina.setDireccion(oficinaToUpdate.getDireccion());
		}
		if(StringUtils.isNotBlank(oficinaToUpdate.getLocalidad())) {
			oficina.setLocalidad(oficinaToUpdate.getLocalidad());
		}
		if(StringUtils.isNotBlank(oficinaToUpdate.getProvincia())) {
			oficina.setProvincia(oficinaToUpdate.getProvincia());
		}
		if(StringUtils.isNotBlank(oficinaToUpdate.getLocalidad())) {
			oficina.setLocalidad(oficinaToUpdate.getLocalidad());
		}
		if(StringUtils.isNotBlank(oficinaToUpdate.getPais())) {
			oficina.setPais(oficinaToUpdate.getPais());
		}
	}

	@Override
	public MessageResponseDto<OficinaDto> getOficinaById(Integer id) {
		Optional<OficinaEntity> optionalOficina = oficinaRepository.findById(id);
		if(optionalOficina.isPresent()) {
			OficinaDto oficinaDto = this.convertToMapDto(optionalOficina.get());
			return MessageResponseDto.success(oficinaDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna oficina con ese id");
		}
	}

	@Override
	public boolean oficinaExisteByID(Integer id) {
		Optional<OficinaEntity> optionalOficina = oficinaRepository.findById(id);
		return optionalOficina.isPresent() ? true : false;
	}

}
