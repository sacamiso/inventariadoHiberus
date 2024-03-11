package com.tfg.inventariado.providerImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.AsignacionDto;
import com.tfg.inventariado.dto.MessageResponseDto;
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
	public MessageResponseDto<String> addAsignación(AsignacionDto asignacion) {
		if(asignacion.getIdAsignacion()!=null && asignacionRepository.findById(asignacion.getIdAsignacion()).isPresent()) {
			return MessageResponseDto.fail("La asignación ya existe");
		}
		if(asignacion.getFechaInicio()==null) {
			return MessageResponseDto.fail("La fecha de inicio es obligatoria");
		}
		if(asignacion.getFechaInicio().isAfter(LocalDate.now())) {
			return MessageResponseDto.fail("La fecha de inicio no puede ser posterior a la fecha actual");
		}
		if(asignacion.getFechaFin()!=null && asignacion.getFechaFin().isBefore(asignacion.getFechaInicio())) {
			return MessageResponseDto.fail("La fecha de fin no puede ser anterior a la fecha de inicio");
		}
		if(asignacion.getFechaFin()!=null && asignacion.getFechaFin().isAfter(LocalDate.now())) {
			return MessageResponseDto.fail("La fecha de fin no puede ser posterior a la fecha actual");
		}
		if(asignacion.getIdEmpleado()==null) {
			return MessageResponseDto.fail("El empleado es obligatorio");
		}
		if(!empleadoProvider.empleadoExisteByCodigo(asignacion.getIdEmpleado())) {
			return MessageResponseDto.fail("El empleado no existe");
		}
		if(asignacion.getCodUnidad()==null) {
			return MessageResponseDto.fail("La unidad es obligatoria");
		}
		if(!unidadProvider.unidadExisteByID(asignacion.getCodUnidad())) {
			return MessageResponseDto.fail("La unidad no existe");
		}
		if(asignacionRepository.existsByCodUnidadAndFechaFinIsNull(asignacion.getCodUnidad())) {
			return MessageResponseDto.fail("La unidad se encuentra en una asignación sin finalizar");
		}
		if(empleadoProvider.getEmpleadoById(asignacion.getIdEmpleado()).getMessage().getIdOficina() != unidadProvider.getUnidadById(asignacion.getCodUnidad()).getMessage().getIdOficina()) {
			return MessageResponseDto.fail("El empleado y la unidad no se encuentran en la misma oficina");
		}
		if(unidadProvider.getUnidadById(asignacion.getCodUnidad()).getMessage().getIdSalida()!=null) {
			return MessageResponseDto.fail("La unidad no está disponible");
		}
		AsignacionEntity newAsignacion = convertToMapEntity(asignacion);
		newAsignacion = asignacionRepository.save(newAsignacion);
		return MessageResponseDto.success("Asignación añadida con éxito");
	}

	@Override
	public MessageResponseDto<String> editAsignación(AsignacionDto asignacion, Integer id) {
		Optional<AsignacionEntity> optionalAsignacion= asignacionRepository.findById(id);

		if(optionalAsignacion.isPresent()) {
			
			AsignacionEntity asignacionToUpdate = optionalAsignacion.get();
			
			this.actualizarCampos(asignacionToUpdate, asignacion);
			
			asignacionRepository.save(asignacionToUpdate);
			
			return MessageResponseDto.success("Asignación editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La asignación que se desea editar no existe");
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
	}

	@Override
	public MessageResponseDto<AsignacionDto> getAsignacionById(Integer id) {
		Optional<AsignacionEntity> optional= asignacionRepository.findById(id);
		if(optional.isPresent()) {
			AsignacionDto asignacionDto = this.convertToMapDto(optional.get());
			return MessageResponseDto.success(asignacionDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna asignación con ese id");
		}
	}

	@Override
	public MessageResponseDto<String> finalizarAsignación(Integer id) {
		Optional<AsignacionEntity> optionalAsignacion= asignacionRepository.findById(id);

		if(optionalAsignacion.isPresent()) {
			AsignacionEntity asignacionToUpdate = optionalAsignacion.get();
			if(asignacionToUpdate.getFechaFin()!=null) {
				return MessageResponseDto.fail("La asignación ya está finalizada");
			}
			asignacionToUpdate.setFechaFin(LocalDate.now());
			asignacionRepository.save(asignacionToUpdate);
			return MessageResponseDto.success("Asignación finalizada con éxito");
		}else {
			return MessageResponseDto.fail("La asignación que se desea finalizar no existe");
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
		if(!this.empleadoProvider.empleadoExisteByCodigo(idEmpleado)) {
			return MessageResponseDto.fail("El empleado no existe");
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByFechaFinIsNullAndIdEmpleado(idEmpleado);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByEmpleadoFinalizadas(Integer idEmpleado) {
		if(!this.empleadoProvider.empleadoExisteByCodigo(idEmpleado)) {
			return MessageResponseDto.fail("El empleado no existe");
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByFechaFinIsNotNullAndIdEmpleado(idEmpleado);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidad(Integer codUnidad) {
		if(!this.unidadProvider.unidadExisteByID(codUnidad)) {
			return MessageResponseDto.fail("La unidad no existe");
		}
		List<AsignacionEntity> listaEntity = this.asignacionRepository.findByCodUnidad(codUnidad);
		List<AsignacionDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<AsignacionDto>> listAsignacionByUnidadSinFinalizar(Integer codUnidad) {
		if(!this.unidadProvider.unidadExisteByID(codUnidad)) {
			return MessageResponseDto.fail("La unidad no existe");
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

}
