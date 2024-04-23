package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.EmpleadoDto;
import com.tfg.inventariado.dto.EmpleadoFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.provider.EmpleadoProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.RolProvider;
import com.tfg.inventariado.repository.EmpleadoRepository;

@Service
public class EmpleadoProviderImpl implements EmpleadoProvider {

	@Autowired
	private EmpleadoRepository empleadoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private RolProvider rolProvider;
	
	@Override
	public EmpleadoDto convertToMapDto(EmpleadoEntity empleado) {
		return modelMapper.map(empleado, EmpleadoDto.class);
	}

	@Override
	public EmpleadoEntity convertToMapEntity(EmpleadoDto empleado) {
		return modelMapper.map(empleado, EmpleadoEntity.class);
	}

	@Override
	public List<EmpleadoDto> listAllEmpleado() {
		List<EmpleadoEntity> listaEntity = empleadoRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<Integer> addEmpleado(EmpleadoDto empleado) {
		if(empleado.getApellidos()==null || empleado.getApellidos().isEmpty()) {
			return MessageResponseDto.fail("El apellido es obligatorio");
		}
		if(empleado.getUsuario()==null || empleado.getUsuario().isEmpty()) {
			return MessageResponseDto.fail("El usuario es obligatorio");
		}
		if(empleado.getContraseña()==null || empleado.getContraseña().isEmpty()) {
			return MessageResponseDto.fail("La contraseña es obligatoria");
		}
		if(empleado.getCodRol()==null || empleado.getCodRol().isEmpty()) {
			return MessageResponseDto.fail("El rol es obligatorio");
		}
		if(!this.rolProvider.rolExisteByCodigo(empleado.getCodRol())) {
			return MessageResponseDto.fail("No se puede añadir el empleado, el rol no existe");
		}
		if(empleado.getCodRol()!=null && !empleado.getCodRol().isEmpty() && !this.oficinaProvider.oficinaExisteByID(empleado.getIdOficina())) {
			return MessageResponseDto.fail("No se puede añadir el empleado, la oficina no existe");
		}
		if(this.empleadoExisteByUsuario(empleado.getUsuario())) {
			return MessageResponseDto.fail("No se puede añadir el empleado, el usuario ya existe");
		}
		if(this.empleadoRepository.findByDni(empleado.getDni()).isPresent()) {
			return MessageResponseDto.fail("No se puede añadir el empleado, el dni ya se está usando");
		}
		if(empleado.getNombre()==null || empleado.getNombre().isEmpty()) {
			return MessageResponseDto.fail("El nombre es obligatorio");
		}
		EmpleadoEntity newEmpleado = convertToMapEntity(empleado);
		newEmpleado = empleadoRepository.save(newEmpleado);
		return MessageResponseDto.success(newEmpleado.getIdEmpleado());
	}

	@Override
	public MessageResponseDto<String> editEmpleado(EmpleadoDto empleado, Integer id) {
		Optional<EmpleadoEntity> optionalEmpleado = empleadoRepository.findById(id);
		if(optionalEmpleado.isPresent()) {
			EmpleadoEntity empleadoToUpdate = optionalEmpleado.get();
			
			this.actualizarCampos(empleadoToUpdate, empleado);
			
			empleadoRepository.save(empleadoToUpdate);
			
			return MessageResponseDto.success("Empledo editado con éxito");
			
		}else {
			return MessageResponseDto.fail("El empleado que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(EmpleadoEntity empleado, EmpleadoDto empleadoToUpdate) {
		
		String patronDNI = "\\d{8}[A-HJ-NP-TV-Z]";
		Pattern pattern = Pattern.compile(patronDNI);
		Matcher matcher = pattern.matcher(empleadoToUpdate.getDni());
		
		if(matcher.matches() && !this.empleadoRepository.findByDni(empleadoToUpdate.getDni()).isPresent()) {
			empleado.setDni(empleadoToUpdate.getDni());
		}
		if(StringUtils.isNotBlank(empleadoToUpdate.getNombre())) {
			empleado.setNombre(empleadoToUpdate.getNombre());
		}
		if(StringUtils.isNotBlank(empleadoToUpdate.getApellidos())) {
			empleado.setApellidos(empleadoToUpdate.getApellidos());
		}
		if(!this.empleadoExisteByUsuario(empleadoToUpdate.getUsuario()) && StringUtils.isNotBlank(empleadoToUpdate.getUsuario())) {
			empleado.setUsuario(empleadoToUpdate.getUsuario());
		}
		if(StringUtils.isNotBlank(empleadoToUpdate.getContraseña())) {
			empleado.setContraseña(empleadoToUpdate.getContraseña());
		}
		if(this.rolProvider.rolExisteByCodigo(empleadoToUpdate.getCodRol())) {
			empleado.setCodRol(empleadoToUpdate.getCodRol());
		}
		if(this.oficinaProvider.oficinaExisteByID(empleadoToUpdate.getIdOficina())) {
			empleado.setIdOficina(empleadoToUpdate.getIdOficina());
		}
	}

	@Override
	public MessageResponseDto<EmpleadoDto> getEmpleadoById(Integer id) {
		Optional<EmpleadoEntity> optionalEmpleado = this.empleadoRepository.findById(id);
		if(optionalEmpleado.isPresent()) {
			EmpleadoDto empleadoDto = this.convertToMapDto(optionalEmpleado.get());
			return MessageResponseDto.success(empleadoDto);
		}else {
			return MessageResponseDto.fail("No se encuentra el empleado con ese identificador");
		}
	}

	@Override
	public MessageResponseDto<List<EmpleadoDto>> listEmpleadosByOficina(Integer idOficina) {

		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<EmpleadoEntity> listaEntity = this.empleadoRepository.findByIdOficina(idOficina);
		List<EmpleadoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<EmpleadoDto>> listEmpleadosByRol(String codRol) {
		if(!this.rolProvider.rolExisteByCodigo(codRol)) {
			return MessageResponseDto.fail("El rol no existe");
		}
		List<EmpleadoEntity> listaEntity = this.empleadoRepository.findByCodRol(codRol);
		List<EmpleadoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean empleadoExisteByCodigo(Integer id) {
		Optional<EmpleadoEntity> optionalEmpleado = this.empleadoRepository.findById(id);
		return optionalEmpleado.isPresent() ? true : false;
	}

	@Override
	public boolean empleadoExisteByUsuario(String usuario) {
		List<EmpleadoEntity> listEmpleado = empleadoRepository.findByUsuario(usuario);
		return listEmpleado.size() != 0;
	}

	@Override
	public MessageResponseListDto<List<EmpleadoDto>> listAllEmpleadosSkipLimit(Integer page, Integer size,
			EmpleadoFilterDto filtros) {
		Specification<EmpleadoEntity> spec = Specification.where(null);
		
		if (filtros != null) {
			if (filtros.getDni() != null) {
				String dni = filtros.getDni();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("dni"), "%" + dni + "%"));
	        }
			if (filtros.getNombre() != null) {
				String nombre = filtros.getNombre();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("nombre"), "%" + nombre + "%"));
	        }
			if (filtros.getApellidos() != null) {
				String apell = filtros.getApellidos();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("apellidos"), "%" + apell + "%"));
	        }
			if (filtros.getUsuario() != null) {
				String usu = filtros.getUsuario();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("usuario"), "%" + usu + "%"));
	        }
			if (filtros.getCodRol() != null) {
				String rol = filtros.getCodRol();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("codRol"), rol));
	        }
			if (filtros.getIdOficina() != null) {
				Integer idOf = filtros.getIdOficina();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOf));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dni"));
		Page<EmpleadoEntity> pageableProveedor = this.empleadoRepository.findAll(spec,pageable);
		
		List<EmpleadoEntity> listaEntity = pageableProveedor.getContent();
		List<EmpleadoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		
		return MessageResponseListDto.success(listaDto, page, size,(int) this.empleadoRepository.count(spec));
	}

}
