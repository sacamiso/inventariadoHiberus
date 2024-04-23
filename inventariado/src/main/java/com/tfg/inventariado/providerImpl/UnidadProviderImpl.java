package com.tfg.inventariado.providerImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.LineaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.dto.SalidaDto;
import com.tfg.inventariado.dto.UnidadDto;
import com.tfg.inventariado.dto.UnidadFilterDto;
import com.tfg.inventariado.entity.ArticuloEntity;
import com.tfg.inventariado.entity.AsignacionEntity;
import com.tfg.inventariado.entity.InventarioEntity;
import com.tfg.inventariado.entity.UnidadEntity;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.EstadoProvider;
import com.tfg.inventariado.provider.LineaProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.PedidoProvider;
import com.tfg.inventariado.provider.SalidaProvider;
import com.tfg.inventariado.provider.UnidadProvider;
import com.tfg.inventariado.repository.AsignacionRepository;
import com.tfg.inventariado.repository.InventarioRepository;
import com.tfg.inventariado.repository.UnidadRepository;

@Service
public class UnidadProviderImpl implements UnidadProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private UnidadRepository unidadRepository;
		
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
		
	@Autowired
	private AsignacionRepository asignacionRepository;
	
	@Autowired
	private InventarioRepository inventarioRepository;
	
	@Autowired
	private LineaProvider lineaProvider;
	
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
	public MessageResponseDto<Integer> addUnidad(UnidadDto unidad) {
		if(unidad.getCodigoInterno()==null) {
			return MessageResponseDto.fail("El código interno es obligatorio");
		}
		if(unidadRepository.existsById(unidad.getCodigoInterno())){
			return MessageResponseDto.fail("Ya existe una unidad con ese código interno");
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
		return MessageResponseDto.success(newUnidad.getCodigoInterno());
	}

	@Override
	public MessageResponseDto<String> editUnidad(UnidadDto unidad, Integer id) {
		try {
			Optional<UnidadEntity> optionalUnidad = unidadRepository.findById(id);
			if(optionalUnidad.isPresent()) {
				
				UnidadEntity unidadToUpdate = optionalUnidad.get();
				
				MessageResponseDto<String> msg = this.actualizarCampos(unidadToUpdate, unidad);
				if(!msg.isSuccess()) {
					return msg;
				}
				
				unidadRepository.save(unidadToUpdate);
				
				return msg;
				
			}else {
				return MessageResponseDto.fail("La unidad que se desea editar no existe");
			}
		} catch (Exception e) {
			return MessageResponseDto.fail("Error: " + e.getMessage());
		}
	}
	
	private MessageResponseDto<String> actualizarCampos(UnidadEntity unidad, UnidadDto unidadToUpdate) {
		
		if(unidadToUpdate.getCodEstado()!=null && !unidadToUpdate.getCodEstado().isEmpty() && this.estadoProvider.estadoExisteByCodigo(unidadToUpdate.getCodEstado())) {
			if(unidadToUpdate.getCodEstado().equals("OP")&&unidad.getCodEstado().equals("MANT")) {
				unidad.setCodEstado(unidadToUpdate.getCodEstado());
			}
			else if(unidadToUpdate.getCodEstado().equals("MANT")&&unidad.getCodEstado().equals("OP")) {
				List<AsignacionEntity> listaEntity = this.asignacionRepository.findByCodUnidadAndFechaFinIsNull(unidad.getCodigoInterno());
				if(listaEntity.size()>0) {
					return MessageResponseDto.fail("No se puede cambiar el estado a MANT ya que la unidad está asignada");
				}
				unidad.setCodEstado(unidadToUpdate.getCodEstado());
			}
			else if(unidadToUpdate.getCodEstado().equals("S")&&unidad.getCodEstado().equals("OP") || unidadToUpdate.getCodEstado().equals("S")&&unidad.getCodEstado().equals("MANT")) {
				List<AsignacionEntity> listaEntity = this.asignacionRepository.findByCodUnidadAndFechaFinIsNull(unidad.getCodigoInterno());
				if(listaEntity.size()>0) {
					return MessageResponseDto.fail("No se puede cambiar el estado a S ya que la unidad está asignada");
				}
				unidad.setCodEstado(unidadToUpdate.getCodEstado());
			}
			else if(unidadToUpdate.getCodEstado().equals("OP")&& unidad.getCodEstado().equals("S") || unidadToUpdate.getCodEstado().equals("MANT")&&unidad.getCodEstado().equals("S")) {
				unidad.setCodEstado(unidadToUpdate.getCodEstado());
				unidad.setIdSalida(null);
			}
			
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
			MessageResponseDto<SalidaDto> salida = this.salidaProvider.getSalidaById(unidadToUpdate.getIdSalida());
			if( salida.isSuccess() && salida.getMessage().getIdOficina() != unidad.getIdOficina()) {
				return MessageResponseDto.fail("La salida no es de la misma oficina que la unidad");
			}
			if( salida.isSuccess() && salida.getMessage().getCodArticulo() != unidad.getCodArticulo()) {
				return MessageResponseDto.fail("La salida no es del mismo tipo de articulos que la unidad");

			}
			if( salida.isSuccess() && salida.getMessage().getNumUnidades() <= unidadRepository.countBySalidaId(unidadToUpdate.getIdSalida())) {
				return MessageResponseDto.fail("La salida no admite más unidades");
			}
			unidad.setIdSalida(unidadToUpdate.getIdSalida());
		}	
		return MessageResponseDto.success("Unidad editada con éxito"); 	
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
			if(asignacionRepository.existsByCodUnidadAndFechaFinIsNull(idUnidad)) {
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
			if (filtros.getFechaPedido() != null) {
				LocalDate fechaPedido = filtros.getFechaPedido();
	            spec = spec.and((root, query, cb) -> cb.equal(root.join("pedido").get("fechaPedido"), fechaPedido));
	        }
			if (filtros.getFechaSalida() != null) {
				LocalDate fechaSalida = filtros.getFechaSalida();
	            spec = spec.and((root, query, cb) -> cb.equal(root.join("salida").get("fechaSalida"), fechaSalida));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
			if (filtros.getDisponible() != null) {
				List<UnidadEntity> unidadesDisponibles = this.unidadRepository.findUnidadesLibres();
			    if (filtros.getDisponible()) {
			        spec = spec.and((root, query, cb) -> cb.isTrue(root.in(unidadesDisponibles)));
			    } else {
			        spec = spec.and((root, query, cb) -> cb.not(root.in(unidadesDisponibles)));
			    }
			}

		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by("idOficina", "codArticulo", "codigoInterno"));
		Page<UnidadEntity> pageableUnidad = unidadRepository.findAll(spec, pageable);
		
		List<UnidadEntity> listaEntity = pageableUnidad.getContent();
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) unidadRepository.count(spec));
	}

	@Override
	public List<ArticuloDto> listaArticulosDisponiblesEnInventarioParaRegistrarUnidadesByOficina(Integer idOficina) {
		// Contar unidades por cada artículo para la oficina dada
		List<UnidadEntity> unidades = unidadRepository.findByIdOficina(idOficina);
		Map<Integer, Long> unidadesPorArticulo = unidades.stream().collect(Collectors.groupingBy(UnidadEntity::getCodArticulo, Collectors.counting()));

        // Obtener las entidades de inventario para la misma oficina
		List<InventarioEntity> inventario = inventarioRepository.findByIdOficina(idOficina);

		List<ArticuloEntity> listArticulos = new ArrayList<ArticuloEntity>();
		
		for (InventarioEntity inventarioEntity : inventario) {
            Integer codArticulo = inventarioEntity.getCodArticulo();
            if (unidadesPorArticulo.containsKey(codArticulo)) {
                Long cantidadUnidades = unidadesPorArticulo.get(codArticulo);
                if (inventarioEntity.getStock() > cantidadUnidades.intValue()) {
                	listArticulos.add(inventarioEntity.getArticulo());
                }
            } else {
                listArticulos.add(inventarioEntity.getArticulo());
            }
        }
		
		List<ArticuloDto> listaDto = listArticulos.stream().map(this.articuloProvider::convertToMapDto).collect(Collectors.toList());

		return listaDto;
	}
	
	@Override
	public MessageResponseDto<List<PedidoDto>> pedidosDisponiblesByOficinaAndArticulo(Integer idOficina, Integer codArticulo){
		// Contar el número de unidades por cada numeroPedido que cumplan con los criterios
        List<UnidadEntity> unidades = unidadRepository.findByIdOficinaAndCodArticulo(idOficina, codArticulo);
        unidades.removeIf(unidad -> unidad.getNumeroPedido() == null);
        Map<Integer, Long> unidadesPorPedido = unidades.stream().collect(Collectors.groupingBy(UnidadEntity::getNumeroPedido, Collectors.counting()));

        MessageResponseDto<List<PedidoDto>> msgPedidosDto = this.pedidoProvider.listPedidoByOficina(idOficina);
        if(!msgPedidosDto.isSuccess()) {
			return msgPedidosDto;
        }
        
        List<PedidoDto> pedidosDto = msgPedidosDto.getMessage();
        pedidosDto.removeIf(pedido -> pedido.getFechaRecepcion() == null);
        
        List<LineaDto> lineasDto = new ArrayList<LineaDto>();
        
        for (PedidoDto pedido : pedidosDto) {
        	lineasDto.addAll( this.lineaProvider.listLineasByPedido(pedido.getNumeroPedido()).getMessage());
        }
        
        Map<Integer, Integer> mapaSumaUnidadesPorPedido = lineasDto.stream().collect(Collectors.groupingBy(LineaDto::getNumeroPedido,Collectors.summingInt(LineaDto::getNumeroUnidades)));
      
        List<PedidoDto> pedidosMostrar = new ArrayList<PedidoDto>();
        
        for(Integer codPed : mapaSumaUnidadesPorPedido.keySet()) {
        	Long valor = unidadesPorPedido.get(codPed);
        	
        	if (unidadesPorPedido.containsKey(codPed)) {
                Long cantidadUnidades = unidadesPorPedido.get(codPed);
                if (valor > cantidadUnidades.intValue()) {
                	pedidosMostrar.add(this.pedidoProvider.getPedidoById(codPed).getMessage());
                }
            } else {
            	pedidosMostrar.add(this.pedidoProvider.getPedidoById(codPed).getMessage());
            }
        }
        
		return MessageResponseDto.success(pedidosMostrar);
 
	}

	@Override
	public MessageResponseDto<List<UnidadDto>> listUnidadDisponiblesSinAsignarByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<UnidadEntity> listaEntity = this.unidadRepository.findUnidadesLibresByEstadoAndOficina("OP",idOficina);
		List<UnidadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<Boolean> estaAsignada(Integer codInterno) {
		List<UnidadEntity> unidadesAsignadas = this.unidadRepository.findUnidadesAsignadas();
		for (UnidadEntity unidadEntity : unidadesAsignadas) {
			if(codInterno.equals(unidadEntity.getCodigoInterno())) {
				return MessageResponseDto.success(true);
			}
		}
		return MessageResponseDto.success(false);
	}
}
