package com.tfg.inventariado.providerImpl;

import java.util.ArrayList;
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

import com.tfg.inventariado.dto.AvisoDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.StockSeguridadDto;
import com.tfg.inventariado.dto.StockSeguridadFilterDto;
import com.tfg.inventariado.entity.InventarioEntity;
import com.tfg.inventariado.entity.StockSeguridadEntity;
import com.tfg.inventariado.entity.StockSeguridadEntityID;
import com.tfg.inventariado.provider.CategoriaProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.StockSeguridadProvider;
import com.tfg.inventariado.provider.SubcategoriaProvider;
import com.tfg.inventariado.repository.InventarioRepository;
import com.tfg.inventariado.repository.StockSeguridadRepository;

@Service
public class StockSeguridadProviderImpl implements StockSeguridadProvider {

	private static Boolean hayAvisos = false;
	
	public static Boolean getHayAvisos() {
		return hayAvisos;
	}

	public static void setHayAvisos(Boolean hayAvisos) {
		StockSeguridadProviderImpl.hayAvisos = hayAvisos;
	}


	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private StockSeguridadRepository stockSeguridadRepository;
	
	@Autowired
	private InventarioRepository inventarioRepository;
		
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private SubcategoriaProvider subcategoriaProvider;
	
	@Autowired
	private CategoriaProvider categoriaProvider;
	
	@Override
	public StockSeguridadDto convertToMapDto(StockSeguridadEntity seguridad) {
		return modelMapper.map(seguridad, StockSeguridadDto.class);
	}

	@Override
	public StockSeguridadEntity convertToMapEntity(StockSeguridadDto seguridad) {
		return modelMapper.map(seguridad, StockSeguridadEntity.class);
	}

	@Override
	public List<StockSeguridadDto> listAllStockSeguridad() {
		List<StockSeguridadEntity> listaEntity = stockSeguridadRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}
	
	private MessageResponseDto<String> validaSS(StockSeguridadDto seguridad) {
		if(seguridad.getCodCategoria()==null || !this.categoriaProvider.categoriaExisteByCodigo(seguridad.getCodCategoria())) {
			return MessageResponseDto.fail("La categoría no existe");
		}
		if(seguridad.getCodSubcategoria()==null || !this.subcategoriaProvider.subcategoriaExisteByID(seguridad.getCodCategoria(),seguridad.getCodSubcategoria())) {
			return MessageResponseDto.fail("La subcategoría no existe");
		}
		if(seguridad.getIdOficina()==null || !this.oficinaProvider.oficinaExisteByID(seguridad.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		if(seguridad.getCantidad()==null || seguridad.getCantidad()<=0) {
			return MessageResponseDto.fail("Como mínimo la cantidad debe ser uno");
		}
		if(seguridad.getPlazoEntregaMedio()==null || seguridad.getPlazoEntregaMedio()<=0) {
			return MessageResponseDto.fail("Como mínimo el plazo de entrega medio debe ser uno");
		}
		return MessageResponseDto.success("correcto");
	}

	@Override
	public MessageResponseDto<String> addStockSteguridad(StockSeguridadDto seguridad) {
		MessageResponseDto<String> validacion1 = this.validaSS(seguridad);
		if(!validacion1.isSuccess()) {
			return validacion1;
		}
		
		StockSeguridadEntityID id = new StockSeguridadEntityID(seguridad.getCodSubcategoria(), seguridad.getCodCategoria(), seguridad.getIdOficina());
		
		if(stockSeguridadRepository.findById(id).isPresent()) {
			return MessageResponseDto.fail("El stock de seguridad ya existe, debe editarlo");
		}
		
		StockSeguridadEntity newStock = convertToMapEntity(seguridad);
		newStock = stockSeguridadRepository.save(newStock);
		
		StockSeguridadProviderImpl.setHayAvisos(this.compruebaAvisos());
		
		return MessageResponseDto.success("Stock de seguridad añadido con éxito");
	}

	@Override
	public MessageResponseDto<String> editStockSeguridad(StockSeguridadDto seguridad, String cat, String subCat,
			Integer idOficina) {
		
		StockSeguridadEntityID id = new StockSeguridadEntityID(subCat, cat, idOficina);
		Optional<StockSeguridadEntity> optionalStockSeguridad= stockSeguridadRepository.findById(id);

		if(optionalStockSeguridad.isPresent()) {
			
			StockSeguridadEntity seguridadToUpdate = optionalStockSeguridad.get();
			
			this.actualizarCampos(seguridadToUpdate, seguridad);
			
			stockSeguridadRepository.save(seguridadToUpdate);
			
			StockSeguridadProviderImpl.setHayAvisos(this.compruebaAvisos());
			
			return MessageResponseDto.success("Stock de seguridad editado con éxito");
			
		}else {
			return MessageResponseDto.fail("El stock de seguridad que se desea editar no existe");
		}
	}

	private void actualizarCampos(StockSeguridadEntity seguridad, StockSeguridadDto seguridadToUpdate) {
		
		if(seguridadToUpdate.getCantidad()!=null && seguridadToUpdate.getCantidad()>0) {
			seguridad.setCantidad(seguridadToUpdate.getCantidad());
		}
		if(seguridadToUpdate.getPlazoEntregaMedio()!=null && seguridadToUpdate.getPlazoEntregaMedio()>0) {
			seguridad.setPlazoEntregaMedio(seguridadToUpdate.getPlazoEntregaMedio());
		}
			
	}
	
	@Override
	public MessageResponseDto<StockSeguridadDto> getStockSeguridadById(String cat, String subCat, Integer idOficina) {
		StockSeguridadEntityID id = new StockSeguridadEntityID(subCat, cat, idOficina);
		Optional<StockSeguridadEntity> optional= stockSeguridadRepository.findById(id);
		if(optional.isPresent()) {
			StockSeguridadDto sueguridadDto = this.convertToMapDto(optional.get());
			return MessageResponseDto.success(sueguridadDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ningún stock de seguridad con ese id");
		}
	}

	@Override
	public MessageResponseDto<List<StockSeguridadDto>> listStockSeguridadByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<StockSeguridadEntity> listaEntity = this.stockSeguridadRepository.findByIdOficina(idOficina);
		List<StockSeguridadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<StockSeguridadDto>> listStockSeguridadBySubcategoria(String cat, String subCat) {
		if(!this.subcategoriaProvider.subcategoriaExisteByID(cat,subCat)) {
			return MessageResponseDto.fail("La subcategoria no existe");
		}
		List<StockSeguridadEntity> listaEntity = this.stockSeguridadRepository.findByCodCategoriaAndCodSubcategoria(cat,subCat);
		List<StockSeguridadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean stockSeguridadExisteByID(String cat, String subCat, Integer idOficina) {
		StockSeguridadEntityID id = new StockSeguridadEntityID(subCat, cat, idOficina);
		Optional<StockSeguridadEntity> optional= stockSeguridadRepository.findById(id);
		return optional.isPresent() ? true : false;
	}

	@Override
	public MessageResponseListDto<List<StockSeguridadDto>> listAllStockSeguridadSkipLimit(Integer page, Integer size, StockSeguridadFilterDto filtros) {
		Specification<StockSeguridadEntity> spec = Specification.where(null);
		if (filtros != null) {
			if (filtros.getCodCategoria() != null) {
				String cat = filtros.getCodCategoria();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codCategoria"), cat));
			}
			if (filtros.getCodSubcategoria() != null) {
				String scat = filtros.getCodSubcategoria();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("codSubcategoria"), scat));
			}
			if (filtros.getIdOficina()!= null && filtros.getIdOficina()!= 0) {
	            Integer idOficina = filtros.getIdOficina();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
	        }
			if (filtros.getCantidad() != null) {
				Integer cant = filtros.getCantidad();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("cantidad"), cant));
			}
			if (filtros.getPlazoMin() != null) {
				Integer ptMin = filtros.getPlazoMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("plazoEntregaMedio"), ptMin));
			}
			if (filtros.getPlazoMax() != null) {
				Integer pMax = filtros.getPlazoMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("plazoEntregaMedio"), pMax));
			}
		}
		PageRequest pageable = PageRequest.of(page, size, Sort.by("idOficina"));
		Page<StockSeguridadEntity> pageableSS = stockSeguridadRepository.findAll(spec, pageable);
		
		List<StockSeguridadEntity> listaEntity = pageableSS.getContent();
		List<StockSeguridadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) stockSeguridadRepository.count(spec));
	}
	
	@Override
	public MessageResponseDto<List<AvisoDto>> validarStockSeguridadAvisos() {
        
        try {
			List<AvisoDto> avisosList = new ArrayList<AvisoDto>();

			List<StockSeguridadEntity> allStockSeguridad = stockSeguridadRepository.findAllOrdered();

			for (StockSeguridadEntity stockSeguridad : allStockSeguridad) {
			    Specification<InventarioEntity> spec = (root, query, cb) -> {
			        return cb.and(
			                cb.equal(root.get("oficina").get("idOficina"), stockSeguridad.getIdOficina()),
			                cb.equal(root.get("articulo").get("codCategoria"), stockSeguridad.getCodCategoria()),
			                cb.equal(root.get("articulo").get("codSubcategoria"), stockSeguridad.getCodSubcategoria())
			        );
			    };

			    List<InventarioEntity> inventarioList = inventarioRepository.findAll(spec);
			    int stockSum = inventarioList.stream().mapToInt(InventarioEntity::getStock).sum();

			    AvisoDto avisoAux;
			    if (stockSum < stockSeguridad.getCantidad()) {
			    	avisoAux = new AvisoDto(this.oficinaProvider.convertToMapDto(stockSeguridad.getOficina()), this.subcategoriaProvider.getSubcategoriaById(stockSeguridad.getCodCategoria(), stockSeguridad.getCodSubcategoria()).getMessage() ,
			    			stockSeguridad.getCantidad(), stockSum, (stockSeguridad.getCantidad()-stockSum));
			    	avisosList.add(avisoAux);
			    }
			}

			return MessageResponseDto.success(avisosList);
		} catch (Exception e) {
			return MessageResponseDto.fail("Se ha producido un error interno al recuperar los datos" + e.toString());
		}
    }
	
	
	@Override
	public MessageResponseDto<Boolean> hayAvisosPrimeraVez() {
        
        try {

			List<StockSeguridadEntity> allStockSeguridad = stockSeguridadRepository.findAllOrdered();

			for (StockSeguridadEntity stockSeguridad : allStockSeguridad) {
			    Specification<InventarioEntity> spec = (root, query, cb) -> {
			        return cb.and(
			                cb.equal(root.get("oficina").get("idOficina"), stockSeguridad.getIdOficina()),
			                cb.equal(root.get("articulo").get("codCategoria"), stockSeguridad.getCodCategoria()),
			                cb.equal(root.get("articulo").get("codSubcategoria"), stockSeguridad.getCodSubcategoria())
			        );
			    };

			    List<InventarioEntity> inventarioList = inventarioRepository.findAll(spec);
			    int stockSum = inventarioList.stream().mapToInt(InventarioEntity::getStock).sum();

			    if (stockSum < stockSeguridad.getCantidad()) {
			    	StockSeguridadProviderImpl.setHayAvisos(true);
			    	return MessageResponseDto.success(true);
			    }
			}
			StockSeguridadProviderImpl.setHayAvisos(false);
			return MessageResponseDto.success(false);
		} catch (Exception e) {
			return MessageResponseDto.fail("Se ha producido un error interno al recuperar los datos" + e.toString());
		}
    }
	
	@Override
	public MessageResponseDto<Boolean> hayAvisosCron() {
		return MessageResponseDto.success(StockSeguridadProviderImpl.getHayAvisos());
    }

	@Override
	@Transactional
	public MessageResponseDto<String> guardarStockSeguridadOf(List<StockSeguridadDto> seguridad) {
		MessageResponseDto<String> validacion1;
		int idOF = 0;
		for(StockSeguridadDto s : seguridad) {
			validacion1 = this.validaSS(s);
			idOF = s.getIdOficina();
			if(!validacion1.isSuccess()) {
				return validacion1;
			}
		}
		
		validacion1 = this.guardar(seguridad, idOF);
		return validacion1;
	}
	
	
	private MessageResponseDto<String> guardar(List<StockSeguridadDto> seguridad, int idOficina) {
		List<StockSeguridadEntity> listaEntity = seguridad.stream().map(this::convertToMapEntity).collect(Collectors.toList());
		this.stockSeguridadRepository.deleteByIdOficina(idOficina);
		this.stockSeguridadRepository.saveAll(listaEntity);
		
		StockSeguridadProviderImpl.setHayAvisos(this.compruebaAvisos());
		
		return MessageResponseDto.success("Stock de seguridad guardado con éxito");
	}
	
	
	private boolean compruebaAvisos() {
        
		List<StockSeguridadEntity> allStockSeguridad = stockSeguridadRepository.findAllOrdered();

		for (StockSeguridadEntity stockSeguridad : allStockSeguridad) {
		    Specification<InventarioEntity> spec = (root, query, cb) -> {
		        return cb.and(
		                cb.equal(root.get("oficina").get("idOficina"), stockSeguridad.getIdOficina()),
		                cb.equal(root.get("articulo").get("codCategoria"), stockSeguridad.getCodCategoria()),
		                cb.equal(root.get("articulo").get("codSubcategoria"), stockSeguridad.getCodSubcategoria())
		        );
		    };

		    List<InventarioEntity> inventarioList = inventarioRepository.findAll(spec);
		    int stockSum = inventarioList.stream().mapToInt(InventarioEntity::getStock).sum();

		    if (stockSum < stockSeguridad.getCantidad()) {
		    	return true;
		    }
		}

		return false;
		
    }
}
