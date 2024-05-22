package com.tfg.inventariado.providerImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.AsignacionDto;
import com.tfg.inventariado.dto.AsignacionFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.AsignacionEntity;
import com.tfg.inventariado.provider.AsignacionProvider;
import com.tfg.inventariado.provider.EmpleadoProvider;
import com.tfg.inventariado.provider.UnidadProvider;
import com.tfg.inventariado.repository.AsignacionRepository;

@Service
public class AsignacionProviderImpl implements AsignacionProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AsignacionRepository asignacionRepository;
	
	@Autowired
	private EmpleadoProvider empleadoProvider;
	
	@Autowired
	private UnidadProvider unidadProvider;
	
	@Autowired
    private MessageSource messageSource;
	
	@Override
	public AsignacionDto convertToMapDto(AsignacionEntity asignacion) {
		return modelMapper.map(asignacion, AsignacionDto.class);
	}

	@Override
	public AsignacionEntity convertToMapEntity(AsignacionDto asignacion) {
		return modelMapper.map(asignacion, AsignacionEntity.class);
	}

	@Override
	public List<AsignacionDto> listAllAsignacion() {
		List<AsignacionEntity> listaEntity = asignacionRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<Integer> addAsignacion(AsignacionDto asignacion) {
		Locale locale = LocaleContextHolder.getLocale();

		if(asignacion.getIdAsignacion()!=null && asignacionRepository.findById(asignacion.getIdAsignacion()).isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("asignacionExiste", null, locale));
		}
		if(asignacion.getFechaInicio()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("fechaInObligatoria", null, locale));
		}
		if(asignacion.getFechaInicio().isAfter(LocalDate.now())) {
			return MessageResponseDto.fail(messageSource.getMessage("fechaInPost", null, locale));
		}
		if(asignacion.getFechaFin()!=null && asignacion.getFechaFin().isBefore(asignacion.getFechaInicio())) {
			return MessageResponseDto.fail(messageSource.getMessage("fechafinAnt", null, locale));
		}
		if(asignacion.getFechaFin()!=null && asignacion.getFechaFin().isAfter(LocalDate.now())) {
			return MessageResponseDto.fail(messageSource.getMessage("fechaFinPost", null, locale));
		}
		if(asignacion.getIdEmpleado()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoObligatorio", null, locale));
		}
		if(!empleadoProvider.empleadoExisteByCodigo(asignacion.getIdEmpleado())) {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoNoExiste", null, locale));
		}
		if(asignacion.getCodUnidad()==null) {
			return MessageResponseDto.fail(messageSource.getMessage("unidadObligatoria", null, locale));
		}
		if(!unidadProvider.unidadExisteByID(asignacion.getCodUnidad())) {
			return MessageResponseDto.fail(messageSource.getMessage("unidadNoExiste", null, locale));
		}
		if(asignacionRepository.existsByCodUnidadAndFechaFinIsNull(asignacion.getCodUnidad())) {
			return MessageResponseDto.fail(messageSource.getMessage("unidadAsigSinFin", null, locale));
		}
		if(empleadoProvider.getEmpleadoById(asignacion.getIdEmpleado()).getMessage().getIdOficina() != unidadProvider.getUnidadById(asignacion.getCodUnidad()).getMessage().getIdOficina()) {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoOficinaDiferente", null, locale));
		}
		if(unidadProvider.getUnidadById(asignacion.getCodUnidad()).getMessage().getIdSalida()!=null) {
			return MessageResponseDto.fail(messageSource.getMessage("unidadNoDisponible", null, locale));
		}
		AsignacionEntity newAsignacion = convertToMapEntity(asignacion);
		newAsignacion = asignacionRepository.save(newAsignacion);
		return MessageResponseDto.success(newAsignacion.getIdAsignacion());
	}

	@Override
	public MessageResponseDto<String> editAsignacion(AsignacionDto asignacion, Integer id) {
		Locale locale = LocaleContextHolder.getLocale();

		Optional<AsignacionEntity> optionalAsignacion= asignacionRepository.findById(id);

		if(optionalAsignacion.isPresent()) {
			
			AsignacionEntity asignacionToUpdate = optionalAsignacion.get();
			
			this.actualizarCampos(asignacionToUpdate, asignacion);
			
			asignacionRepository.save(asignacionToUpdate);
			
			return MessageResponseDto.success(messageSource.getMessage("asignacionEditExito", null, locale));
			
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("asignacionNoExiste", null, locale));
		}
	}
	
	private void actualizarCampos(AsignacionEntity asignacion, AsignacionDto asignacionToUpdate) {
		//No le veo sentido a que se pueda editar el empleado y la unidad.
		//Mejor crear una asignación nueva
		if(asignacionToUpdate.getFechaInicio()!=null && !asignacionToUpdate.getFechaInicio().isAfter(LocalDate.now())) {
			asignacion.setFechaInicio(asignacionToUpdate.getFechaInicio());
		}
		if(asignacionToUpdate.getFechaFin()!=null && !asignacionToUpdate.getFechaFin().isBefore(asignacionToUpdate.getFechaInicio()) && !asignacionToUpdate.getFechaFin().isAfter(LocalDate.now())) {
			asignacion.setFechaFin(asignacionToUpdate.getFechaFin());
		}
		if(asignacionToUpdate.getFechaFin()==null ) {
			asignacion.setFechaFin(null);
		}
	}

	@Override
	public MessageResponseDto<AsignacionDto> getAsignacionById(Integer id) {
		Locale locale = LocaleContextHolder.getLocale();

		Optional<AsignacionEntity> optional= asignacionRepository.findById(id);
		if(optional.isPresent()) {
			AsignacionDto asignacionDto = this.convertToMapDto(optional.get());
			return MessageResponseDto.success(asignacionDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("asignacionNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<String> finalizarAsignación(Integer id) {
		Optional<AsignacionEntity> optionalAsignacion= asignacionRepository.findById(id);

		Locale locale = LocaleContextHolder.getLocale();

		if(optionalAsignacion.isPresent()) {
			AsignacionEntity asignacionToUpdate = optionalAsignacion.get();
			if(asignacionToUpdate.getFechaFin()!=null) {
				return MessageResponseDto.fail(messageSource.getMessage("asignacionFinalizada", null, locale));
			}
			asignacionToUpdate.setFechaFin(LocalDate.now());
			asignacionRepository.save(asignacionToUpdate);
			return MessageResponseDto.success(messageSource.getMessage("asignacionFinExito", null, locale));
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("asignacionNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleado(Integer idEmpleado) {
		if(!this.empleadoProvider.empleadoExisteByCodigo(idEmpleado)) {
			return MessageResponseDto.fail("El empleado no existe");
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByIdEmpleado(idEmpleado);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoSinFinalizar(Integer idEmpleado) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.empleadoProvider.empleadoExisteByCodigo(idEmpleado)) {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoNoExiste", null, locale));
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByFechaFinIsNullAndIdEmpleado(idEmpleado);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoFinalizadas(Integer idEmpleado) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.empleadoProvider.empleadoExisteByCodigo(idEmpleado)) {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoNoExiste", null, locale));
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByFechaFinIsNotNullAndIdEmpleado(idEmpleado);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidad(Integer codUnidad) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.unidadProvider.unidadExisteByID(codUnidad)) {
			return MessageResponseDto.fail(messageSource.getMessage("unidadNoExiste", null, locale));
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByCodUnidad(codUnidad);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidadSinFinalizar(Integer codUnidad) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.unidadProvider.unidadExisteByID(codUnidad)) {
			return MessageResponseDto.fail(messageSource.getMessage("unidadNoExiste", null, locale));
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByCodUnidadAndFechaFinIsNull(codUnidad);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}
	
	@Override
	public boolean asignacionExisteByID(Integer id) {
		Optional<AsignacionEntity> optionalAsignacion= asignacionRepository.findById(id);
		return optionalAsignacion.isPresent() ? true : false;
	}

	@Override
	public MessageResponseListDto<List<AsignacionDto>> listAllAsignacionesSkipLimit(Integer page, Integer size,
			AsignacionFilterDto filtros) {
		Specification<AsignacionEntity> spec = Specification.where(null);
		if (filtros != null) {
			if (filtros.getFechaInicio() != null) {
				LocalDate fechaI = filtros.getFechaInicio();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaInicio"), fechaI));
	        }
			if (filtros.getFechaFin() != null) {
				LocalDate fechaF = filtros.getFechaFin();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaFin"), fechaF));
	        }
			if (filtros.getDniEmpleado() != null) {
				String dni = filtros.getDniEmpleado();
			    spec = spec.and((root, query, cb) -> cb.like(root.join("empleado").get("dni"), "%" + dni + "%"));
	        }
			if (filtros.getNombreEmpleado() != null) {
				String nombre = filtros.getNombreEmpleado();
			    spec = spec.and((root, query, cb) -> cb.like(root.join("empleado").get("nombre"), "%" + nombre + "%"));
	        }
			if (filtros.getApellidosEmpleado() != null) {
				String apell = filtros.getApellidosEmpleado();
			    spec = spec.and((root, query, cb) -> cb.like(root.join("empleado").get("apellidos"), "%" + apell + "%"));
	        }
			if (filtros.getCodOficinaEmpleado()!= null && filtros.getCodOficinaEmpleado()!= 0) {
	            Integer idOficina = filtros.getCodOficinaEmpleado();
	            spec = spec.and((root, query, cb) -> cb.equal(root.join("empleado").get("idOficina"), idOficina));
	        }
			if (filtros.getCodUnidad()!= null) {
	            Integer codUnidad = filtros.getCodUnidad();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codUnidad"), codUnidad));
	        }
			if (filtros.getFinalizadas() != null) {
	            if (filtros.getFinalizadas()) {
	                spec = spec.and((root, query, cb) -> cb.isNotNull(root.get("fechaFin")));
	            } else {
	                spec = spec.and((root, query, cb) -> cb.isNull(root.get("fechaFin")));
	            }
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaInicio"));
		Page<AsignacionEntity> pageablesalida = asignacionRepository.findAll(spec,pageable);
		
		List<AsignacionEntity> listaEntity = pageablesalida.getContent();
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) asignacionRepository.count(spec));
	}

}
