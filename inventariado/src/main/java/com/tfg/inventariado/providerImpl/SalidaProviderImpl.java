package com.tfg.inventariado.providerImpl;

import java.time.LocalDate;
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
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.InventarioDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.OficinaDto;
import com.tfg.inventariado.dto.SalidaDto;
import com.tfg.inventariado.dto.SalidaFilterDto;
import com.tfg.inventariado.entity.SalidaEntity;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.InventarioProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.SalidaProvider;
import com.tfg.inventariado.repository.SalidaRepository;

@Service
public class SalidaProviderImpl implements SalidaProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private SalidaRepository saldiaRepository;
	
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
	@Autowired
	private InventarioProvider inventarioProvider;
	
	@Override
	public SalidaDto convertToMapDto(SalidaEntity salida) {
		return modelMapper.map(salida, SalidaDto.class);
	}

	@Override
	public SalidaEntity convertToMapEntity(SalidaDto salida) {
		return modelMapper.map(salida, SalidaEntity.class);
	}

	@Override
	public List<SalidaDto> listAllSalidas() {
		List<SalidaEntity> listaEntity = saldiaRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	//Cuando se añade una salida se modifica el inventario y eso a su vez modifica el historial
	@Transactional
	@Override
	public MessageResponseDto<Integer> addSalida(SalidaDto salida) {
		if(salida.getNumUnidades()==null) {
			return MessageResponseDto.fail("El número de unidades es obligatorio");
		}
		if(salida.getIdOficina()==null) {
			return MessageResponseDto.fail("El id de oficina es obligatorio");
		}
		if(salida.getCodArticulo()==null) {
			return MessageResponseDto.fail("El artículo es obligatorio");
		}
		if(!this.articuloProvider.articuloExisteByID(salida.getCodArticulo())) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		if(!this.oficinaProvider.oficinaExisteByID(salida.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		MessageResponseDto<InventarioDto> inventario = inventarioProvider.getInventarioById(salida.getIdOficina(), salida.getCodArticulo());
		if(!inventario.isSuccess()) {
			return MessageResponseDto.fail("No se puede dar salida a artículos que no están inventariados");
		}
		if(inventario.getMessage().getStock() < salida.getNumUnidades()) {
			return MessageResponseDto.fail("No se puede dar salida a más artículos de los existentes");
		}
		if(salida.getFechaSalida()== null) {
			salida.setFechaSalida(LocalDate.now());
		}
		if(salida.getFechaSalida().isAfter(LocalDate.now())) {
			return MessageResponseDto.fail("La fecha no puede ser posterior a la actual");
		}
		
		OficinaDto of = oficinaProvider.getOficinaById(salida.getIdOficina()).getMessage();
		ArticuloDto art = articuloProvider.getArticuloById(salida.getCodArticulo()).getMessage();
		
		InventarioDto inventarioDto = new InventarioDto(salida.getCodArticulo(), salida.getIdOficina(), inventario.getMessage().getStock()-salida.getNumUnidades(),art,of );
		MessageResponseDto<String>  msgInventario = inventarioProvider.editInventario(inventarioDto,salida.getIdOficina(),salida.getCodArticulo());
		if(!msgInventario.isSuccess()) {
			return MessageResponseDto.fail(msgInventario.getError());
		}
		
		SalidaEntity newSalida = convertToMapEntity(salida);
		newSalida = saldiaRepository.save(newSalida);
		return MessageResponseDto.success(newSalida.getIdSalida());
	}

	@Transactional
	@Override
	public MessageResponseDto<String> editSalida(SalidaDto salida, Integer id) {
		Optional<SalidaEntity> optionalSalida = saldiaRepository.findById(id);
		if(optionalSalida.isPresent()) {
			
			if(salida.getCodArticulo()!= 0 && !this.articuloProvider.articuloExisteByID(salida.getCodArticulo())) {
				return MessageResponseDto.fail("El artículo no existe");
			}
			if(salida.getIdOficina()!= 0 && !this.oficinaProvider.oficinaExisteByID(salida.getIdOficina())) {
				return MessageResponseDto.fail("La oficina no existe");
			}
			MessageResponseDto<InventarioDto> inventario = inventarioProvider.getInventarioById(salida.getIdOficina(), salida.getCodArticulo());
			if(!inventario.isSuccess()) {
				return MessageResponseDto.fail("No se puede dar salida a artículos que no están inventariados");
			}
			if(inventario.getMessage().getStock() + optionalSalida.get().getNumUnidades() < salida.getNumUnidades()) {
				return MessageResponseDto.fail("No se puede dar salida a más artículos de los existentes");
			}
			if(salida.getFechaSalida()!=null && salida.getFechaSalida().isAfter(LocalDate.now())) {
				return MessageResponseDto.fail("La fecha no puede ser posterior a la actual");
			}
			
			OficinaDto of = oficinaProvider.getOficinaById(salida.getIdOficina()).getMessage();
			ArticuloDto art = articuloProvider.getArticuloById(salida.getCodArticulo()).getMessage();
			
			InventarioDto inventarioDto = new InventarioDto(salida.getCodArticulo(), salida.getIdOficina(), inventario.getMessage().getStock()+ optionalSalida.get().getNumUnidades()-salida.getNumUnidades(),art,of );
			MessageResponseDto<String>  msgInventario = inventarioProvider.editInventario(inventarioDto,salida.getIdOficina(),salida.getCodArticulo());
			if(!msgInventario.isSuccess()) {
				return MessageResponseDto.fail(msgInventario.getError());
			}
			
			SalidaEntity salidaToUpdate = optionalSalida.get();
			
			this.actualizarCampos(salidaToUpdate, salida);
			
			saldiaRepository.save(salidaToUpdate);
			
			return MessageResponseDto.success("Salida editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La salida que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(SalidaEntity salida, SalidaDto salidaToUpdate) {
		
		if(salidaToUpdate.getCodArticulo()!= 0) {
			salida.setCodArticulo(salidaToUpdate.getCodArticulo());
		}
		if(salidaToUpdate.getIdOficina()!= 0) {
			salida.setIdOficina(salidaToUpdate.getIdOficina());
		}

		salida.setNumUnidades(salidaToUpdate.getNumUnidades());
		
		if(salidaToUpdate.getFechaSalida()!=null && !salidaToUpdate.getFechaSalida().isAfter(LocalDate.now())) {
			salida.setFechaSalida(salidaToUpdate.getFechaSalida());
		}
		
		salida.setCosteTotal(salidaToUpdate.getCosteTotal());
		salida.setCosteUnitario(salidaToUpdate.getCosteUnitario());
		
	}

	@Override
	public MessageResponseDto<SalidaDto> getSalidaById(Integer id) {
		Optional<SalidaEntity> optional = saldiaRepository.findById(id);
		if(optional.isPresent()) {
			SalidaDto salidaDto = this.convertToMapDto(optional.get());
			return MessageResponseDto.success(salidaDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna salida con ese id");
		}
	}

	@Override
	public MessageResponseDto<List<SalidaDto>> listSalidaByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<SalidaEntity> listaEntity = this.saldiaRepository.findByIdOficina(idOficina);
		List<SalidaDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<SalidaDto>> listSalidaByArticulo(Integer idArticulo) {
		if(!this.articuloProvider.articuloExisteByID(idArticulo)) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		List<SalidaEntity> listaEntity = this.saldiaRepository.findByCodArticulo(idArticulo);
		List<SalidaDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean salidaExisteByID(Integer id) {
		Optional<SalidaEntity> optional = saldiaRepository.findById(id);
		return optional.isPresent() ? true : false;

	}

	@Override
	public MessageResponseListDto<List<SalidaDto>> listAllSalidasSkipLimit(Integer page, Integer size, SalidaFilterDto filtros) {
		Specification<SalidaEntity> spec = Specification.where(null);
		if (filtros != null) {
			if (filtros.getNumeroUnidades() != null) {
				Integer numU = filtros.getNumeroUnidades();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("numUnidades"), numU));
			}
			if (filtros.getCosteTotalMin() != null) {
				Double cosTotMin = filtros.getCosteTotalMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeTotal"), cosTotMin));
			}
			if (filtros.getCosteTotalMax() != null) {
				Double cosTotMax = filtros.getCosteTotalMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costeTotal"), cosTotMax));
			}
			if (filtros.getCosteUnitarioMin() != null && filtros.getCosteUnitarioMin() != 0) {
				Double costeUnMin = filtros.getCosteUnitarioMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeUnitario"), costeUnMin));
			}
			if (filtros.getCosteUnitarioMax() != null && filtros.getCosteUnitarioMax() != 0) {
				Double costeUnMax = filtros.getCosteUnitarioMax();
				spec = spec.and((root, query, cb) -> cb.or(
				        cb.isNull(root.get("costeUnitario")),
				        cb.lessThanOrEqualTo(root.get("costeUnitario"), costeUnMax)
				    ));
			}
			if (filtros.getFechaSalida() != null) {
				LocalDate fecha = filtros.getFechaSalida();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaSalida"), fecha));
	        }
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getCodArticulo()!= null && filtros.getCodArticulo()!= 0) {
	            Integer codArticulo = filtros.getCodArticulo();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codArticulo"), codArticulo));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaSalida"));
		Page<SalidaEntity> pageablesalida = saldiaRepository.findAll(spec,pageable);
		
		List<SalidaEntity> listaEntity = pageablesalida.getContent();
		List<SalidaDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) saldiaRepository.count(spec));
		
	}

}
