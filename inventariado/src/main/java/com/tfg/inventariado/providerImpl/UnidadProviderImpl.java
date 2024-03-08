package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.SalidaDto;
import com.tfg.inventariado.dto.UnidadDto;
import com.tfg.inventariado.dto.UnidadFilterDto;
import com.tfg.inventariado.entity.UnidadEntity;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.EstadoProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.PedidoProvider;
import com.tfg.inventariado.provider.SalidaProvider;
import com.tfg.inventariado.provider.UnidadProvider;
import com.tfg.inventariado.repository.AsignacionRepository;
import com.tfg.inventariado.repository.UnidadRepository;

@Service
public class UnidadProviderImpl implements UnidadProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UnidadRepository unidadRepository;
	
	@Autowired
	private AsignacionRepository asignaciónRepository;
	
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
	@Autowired
	private EstadoProvider estadoProvider;
	
	@Autowired
	private PedidoProvider pedidoProvider;
	
	@Autowired
	private SalidaProvider salidaProvider;
	
	@Override
	public UnidadDto convertToMapDto(UnidadEntity unidad) {
		return modelMapper.map(unidad, UnidadDto.class);
	}

	@Override
	public UnidadEntity convertToMapEntity(UnidadDto unidad) {
		return modelMapper.map(unidad, UnidadEntity.class);
	}

	@Override
	public List<UnidadDto> listAllUnidades() {
		List<UnidadEntity> listaEntity = unidadRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addUnidad(UnidadDto unidad) {
		if(unidad.getCodigoInterno()==null) {
			return MessageResponseDto.fail("El código interno es obligatorio");
		}
		if(unidadRepository.existsById(unidad.getCodigoInterno())){
			return MessageResponseDto.fail("La unidad ya existe");
		}
		if(unidad.getCodEstado()==null || unidad.getCodEstado().isEmpty()) {
			return MessageResponseDto.fail("El estado es obligatorio");
		}
		if(!this.estadoProvider.estadoExisteByCodigo(unidad.getCodEstado())) {
			return MessageResponseDto.fail("El estado no existe");
		}
		if(unidad.getNumeroPedido()!= null && !this.pedidoProvider.pedidoExisteByID(unidad.getNumeroPedido())) {
			return MessageResponseDto.fail("El pedido no existe");
		}
		if(unidad.getCodArticulo()!= null && !this.articuloProvider.articuloExisteByID(unidad.getCodArticulo())) {
			return MessageResponseDto.fail("El articulo no existe");
		}
		if(unidad.getIdOficina()!= null && !this.oficinaProvider.oficinaExisteByID(unidad.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		if(unidad.getIdSalida()!= null && !this.salidaProvider.salidaExisteByID(unidad.getIdSalida())) {
			return MessageResponseDto.fail("La salida no existe");
		}else {
			if(unidad.getIdSalida()!= null) {
				MessageResponseDto<SalidaDto> salida = this.salidaProvider.getSalidaById(unidad.getIdSalida());
				if(salida.getMessage().getCodArticulo() != unidad.getCodArticulo()) {
					return MessageResponseDto.fail("La salida no es de ese tipo de artículo");
				}
				if(salida.getMessage().getIdOficina() != unidad.getIdOficina()) {
					return MessageResponseDto.fail("La salida no es de es de esa oficina");
				}
			}
			
			//Se podría comprobar si se ha dado salida ya a tantas unidades como pone en la salida y por ello no se puede dar salida a esta
			
		}
		
		UnidadEntity newUnidad = convertToMapEntity(unidad);
		newUnidad = unidadRepository.save(newUnidad);
		return MessageResponseDto.success("Unidad añadida con éxito");
	}

	@Override
	public MessageResponseDto<String> editUnidad(UnidadDto unidad, Integer id) {
		Optional<UnidadEntity> optionalUnidad = unidadRepository.findById(id);
		if(optionalUnidad.isPresent()) {
			
			UnidadEntity unidadToUpdate = optionalUnidad.get();
			
			this.actualizarCampos(unidadToUpdate, unidad);
			
			unidadRepository.save(unidadToUpdate);
			
			return MessageResponseDto.success("Unidad editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La unidad que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(UnidadEntity unidad, UnidadDto unidadToUpdate) {
		
		if(unidadToUpdate.getCodEstado()!=null && !unidadToUpdate.getCodEstado().isEmpty() && this.estadoProvider.estadoExisteByCodigo(unidadToUpdate.getCodEstado())) {
			unidad.setCodEstado(unidadToUpdate.getCodEstado());
		}
		if(unidadToUpdate.getNumeroPedido()!= null && this.pedidoProvider.pedidoExisteByID(unidadToUpdate.getNumeroPedido())) {
			unidad.setNumeroPedido(unidadToUpdate.getNumeroPedido());
		}
		if(unidadToUpdate.getCodArticulo()!= null && this.articuloProvider.articuloExisteByID(unidadToUpdate.getCodArticulo())) {
			unidad.setCodArticulo(unidadToUpdate.getCodArticulo());
		}
		if(unidadToUpdate.getIdOficina()!= null && this.oficinaProvider.oficinaExisteByID(unidadToUpdate.getIdOficina())) {
			unidad.setIdOficina(unidadToUpdate.getIdOficina());
		}
		if(unidadToUpdate.getIdSalida()!= null && this.salidaProvider.salidaExisteByID(unidadToUpdate.getIdSalida())) {
			MessageResponseDto<SalidaDto> salida = this.salidaProvider.getSalidaById(unidad.getIdSalida());
			if(salida.getMessage().getCodArticulo() == unidadToUpdate.getCodArticulo() && salida.getMessage().getIdOficina() == unidadToUpdate.getIdOficina()) {
				unidad.setIdSalida(unidadToUpdate.getIdSalida());
			}
		}	
			//Se podría comprobar si se ha dado salida ya a tantas unidades como pone en la salida y por ello no se puede dar salida a esta
			
	}

	@Override
	public MessageResponseDto<UnidadDto> getUnidadById(Integer id) {
		Optional<UnidadEntity> optional = unidadRepository.findById(id);
		if(optional.isPresent()) {
			UnidadDto unidadDto = this.convertToMapDto(optional.get());
			return MessageResponseDto.success(unidadDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna unidad con ese id");
		}
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadByEstado(String codEstado) {
		if(!this.estadoProvider.estadoExisteByCodigo(codEstado)) {
			return MessageResponseDto.fail("El estado no existe");
		}
		List<UnidadEntity> listaEntity = this.unidadRepository.findByCodEstado(codEstado);
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadDisponibles() {
		List<UnidadEntity> listaEntity = this.unidadRepository.findByIdSalidaIsNullAndCodEstadoNot("MANT");
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadDisponiblesByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<UnidadEntity> listaEntity = this.unidadRepository.findByIdSalidaIsNullAndCodEstadoNotAndIdOficina("MANT",idOficina);
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadNODisponibles() {
		List<UnidadEntity> listaEntity = this.unidadRepository.findByIdSalidaIsNotNullOrCodEstado("S");
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadNODisponiblesByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<UnidadEntity> listaEntity = this.unidadRepository.findByIdSalidaIsNotNullAndIdOficina(idOficina);
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadesByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<UnidadEntity> listaEntity = this.unidadRepository.findByIdOficina(idOficina);
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadByArticulo(Integer idArticulo) {
		if(!this.articuloProvider.articuloExisteByID(idArticulo)) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		List<UnidadEntity> listaEntity = this.unidadRepository.findByCodArticulo(idArticulo);
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean unidadExisteByID(Integer id) {
		Optional<UnidadEntity> optional = unidadRepository.findById(id);
		return optional.isPresent() ? true : false;
	}

	@Override
	public MessageResponseDto<String> darSalidaUnidad(Integer idSalida, Integer idUnidad) {
		Optional<UnidadEntity> optionalUnidad = unidadRepository.findById(idUnidad);
		MessageResponseDto<SalidaDto> salida = salidaProvider.getSalidaById(idSalida);
		if(!salida.isSuccess()) {
			return MessageResponseDto.fail("La salida no existe");
		}
		
		if(optionalUnidad.isPresent()) {
			
			UnidadEntity unidad = optionalUnidad.get();
			if(salida.getMessage().getCodArticulo() != unidad.getCodArticulo() && salida.getMessage().getIdOficina() != unidad.getIdOficina()) {
				return MessageResponseDto.fail("la salida elegida no vale para esta unidad");
			}
			if(asignaciónRepository.existsByCodUnidadAndFechaFinIsNull(idUnidad)) {
				return MessageResponseDto.fail("La unidad se encuentra en una asignación");
			}

			unidad.setIdSalida(idSalida);
			unidadRepository.save(unidad);
			
			return MessageResponseDto.success("Unidad editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La unidad que se desea editar no existe");
		}
	}

	@Override
	public MessageResponseListDto<List<UnidadDto>> listAllUnidadesSkipLimit(Integer page, Integer size, UnidadFilterDto filtros) {

		Specification<UnidadEntity> spec = Specification.where(null);
		if (filtros != null) {
			if (filtros.getCodEstado()!=null && !filtros.getCodEstado().isEmpty()) {
	            String codEstado = filtros.getCodEstado();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codEstado"), codEstado));
	        }
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getNumeroPedido()!= null && filtros.getNumeroPedido()!= 0) {
	            Integer numeroPedido = filtros.getNumeroPedido();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("numeroPedido"), numeroPedido));
	        }
			if (filtros.getIdSalida()!= null && filtros.getIdSalida()!= 0) {
	            Integer idSalida = filtros.getIdSalida();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idSalida"), idSalida));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by("idOficina", "codArticulo", "codigoInterno"));
		Page<UnidadEntity> pageableUnidad = unidadRepository.findAll(spec, pageable);
		
		List<UnidadEntity> listaEntity = pageableUnidad.getContent();
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) unidadRepository.count(spec));
	}

}
