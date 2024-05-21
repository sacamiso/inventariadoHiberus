package com.tfg.inventariado.providerImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.ProveedorDto;
import com.tfg.inventariado.dto.ProveedorFirterDto;
import com.tfg.inventariado.entity.ProveedorEntity;
import com.tfg.inventariado.provider.ProveedorProvider;
import com.tfg.inventariado.repository.ProveedorRepository;

@Service
public class ProveedorProviderImpl implements ProveedorProvider {

	@Autowired
	private ProveedorRepository proveedorRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public ProveedorDto convertToMapDto(ProveedorEntity proveedor) {
		return modelMapper.map(proveedor, ProveedorDto.class);
	}

	@Override
	public ProveedorEntity convertToMapEntity(ProveedorDto proveedor) {
		return modelMapper.map(proveedor, ProveedorEntity.class);
	}

	@Override
	public List<ProveedorDto> listAllProveedores() {
		List<ProveedorEntity> listaEntity = proveedorRepository.findAll();
		return listaEntity.stream()
				.sorted(Comparator.comparing(ProveedorEntity::getRazonSocial, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<Integer> addProveedor(ProveedorDto proveedor) {
		
		if(proveedor.getCif()==null || !StringUtils.isNotBlank(proveedor.getCif())) {
			return MessageResponseDto.fail("El CIF es obligatorio");
		}
		if(proveedorRepository.findByCif(proveedor.getCif()).isPresent()) {
			return MessageResponseDto.fail("Ya existe un proveedor con ese CIF");
		}
		if(proveedor.getRazonSocial()==null || !StringUtils.isNotBlank(proveedor.getRazonSocial())) {
			return MessageResponseDto.fail("La razon social es obligatoria");
		}
		if(proveedor.getDireccion()==null || !StringUtils.isNotBlank(proveedor.getDireccion())) {
			return MessageResponseDto.fail("La dirección es obligatoria");
		}
		if(proveedor.getCodigoPostal()!= null && proveedor.getCodigoPostal()<=0) {
			return MessageResponseDto.fail("El código postal es incorrecto");
		}
		if(proveedor.getLocalidad()==null || !StringUtils.isNotBlank(proveedor.getLocalidad())) {
			return MessageResponseDto.fail("La localidad es obligatoria");
		}
		if(proveedor.getTelefono()==null || !StringUtils.isNotBlank(proveedor.getTelefono())) {
			return MessageResponseDto.fail("El teléfono es obligatorio");
		}
		if(proveedor.getEmail()==null || !StringUtils.isNotBlank(proveedor.getEmail())) {
			return MessageResponseDto.fail("El email es obligatorio");
		}
		ProveedorEntity newProveedor = convertToMapEntity(proveedor);
		newProveedor = proveedorRepository.save(newProveedor);
		return MessageResponseDto.success(newProveedor.getIdProveedor());
	}

	@Override
	public MessageResponseDto<String> editProveedor(ProveedorDto proveedor, Integer id) {
		Optional<ProveedorEntity> optionalProveedor = proveedorRepository.findById(id);
		if(optionalProveedor.isPresent()) {
			ProveedorEntity proveedorToUpdate = optionalProveedor.get();
			
			MessageResponseDto<String> respuesta = this.actualizarCampos(proveedorToUpdate, proveedor);
			
			if(respuesta.isSuccess()) {
				proveedorRepository.save(proveedorToUpdate);
			}
			return respuesta;
			
		}else {
			return MessageResponseDto.fail("El proveedor que se desea editar no existe");
		}
	}

	private MessageResponseDto<String> actualizarCampos(ProveedorEntity proveedor, ProveedorDto proveedorToUpdate) {
		
		if( proveedorRepository.findByCif(proveedorToUpdate.getCif()).isPresent() && !proveedor.getCif().equals(proveedorToUpdate.getCif())) {
			return MessageResponseDto.fail("CIF en uso");
		}
		String cp = String.valueOf(proveedorToUpdate.getCodigoPostal());
		if(cp.length() != 5) {
			return MessageResponseDto.fail("Código postal inválido");
		}
		if(StringUtils.isNotBlank(proveedorToUpdate.getCif()) && !proveedorRepository.findByCif(proveedorToUpdate.getCif()).isPresent()) {
			proveedor.setCif(proveedorToUpdate.getCif());
		}
		
		if(StringUtils.isNotBlank(proveedorToUpdate.getRazonSocial())) {
			proveedor.setRazonSocial(proveedorToUpdate.getRazonSocial());
		}
		if(StringUtils.isNotBlank(proveedorToUpdate.getDireccion())) {
			proveedor.setDireccion(proveedorToUpdate.getDireccion());
		}
		
		if(cp.length() == 5) {
			proveedor.setCodigoPostal(proveedorToUpdate.getCodigoPostal());
		}
		if(StringUtils.isNotBlank(proveedorToUpdate.getLocalidad())) {
			proveedor.setLocalidad(proveedorToUpdate.getLocalidad());
		}
		if(StringUtils.isNotBlank(proveedorToUpdate.getTelefono())) {
			proveedor.setTelefono(proveedorToUpdate.getTelefono());
		}
		if(StringUtils.isNotBlank(proveedorToUpdate.getEmail())) {
			proveedor.setEmail(proveedorToUpdate.getEmail());
		}
		
		return MessageResponseDto.success("Proveedor editado con éxito");

	}
	
	@Override
	public MessageResponseDto<ProveedorDto> getProveedorById(Integer id) {
		Optional<ProveedorEntity> optionalProveedor = proveedorRepository.findById(id);
		if(optionalProveedor.isPresent()) {
			ProveedorDto proveedorDto = this.convertToMapDto(optionalProveedor.get());
			return MessageResponseDto.success(proveedorDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ningún proveedor con ese id");
		}
	}

	@Override
	public boolean proveedorExisteByID(Integer id) {
		Optional<ProveedorEntity> optionalProveedor = proveedorRepository.findById(id);
		return optionalProveedor.isPresent() ? true : false;	
	}

	@Override
	public MessageResponseListDto<List<ProveedorDto>> listAllProveedoresSkipLimit(Integer page, Integer size,
			ProveedorFirterDto filtros) {
		Specification<ProveedorEntity> spec = Specification.where(null);
		
		if (filtros != null) {
			if (filtros.getCif() != null) {
				String cif = filtros.getCif();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("cif"), "%" + cif + "%"));
	        }
			if (filtros.getRazonSocial() != null) {
				String raz = filtros.getRazonSocial();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("razonSocial"), "%" + raz + "%"));
	        }
			if (filtros.getDireccion() != null) {
				String direc = filtros.getDireccion();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("direccion"), "%" + direc + "%"));
	        }
			if (filtros.getCodigoPostal() != null) {
				Integer cp = filtros.getCodigoPostal();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("codigoPostal"), cp));
	        }
			if (filtros.getLocalidad() != null) {
				String loc = filtros.getLocalidad();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("localidad"), "%" + loc + "%"));
	        }
			if (filtros.getTelefono() != null) {
				String tele = filtros.getTelefono();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("telefono"), "%" + tele + "%"));
	        }
			if (filtros.getEmail() != null) {
				String email = filtros.getEmail();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("email"), "%" + email + "%"));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "cif"));
		Page<ProveedorEntity> pageableProveedor = this.proveedorRepository.findAll(spec,pageable);
		
		List<ProveedorEntity> listaEntity = pageableProveedor.getContent();
		List<ProveedorDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		
		return MessageResponseListDto.success(listaDto, page, size,(int) this.proveedorRepository.count(spec));
	}

}
