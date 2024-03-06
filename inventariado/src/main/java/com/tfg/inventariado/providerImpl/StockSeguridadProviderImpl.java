package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.StockSeguridadDto;
import com.tfg.inventariado.entity.StockSeguridadEntity;
import com.tfg.inventariado.entity.StockSeguridadEntityID;
import com.tfg.inventariado.provider.CategoriaProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.StockSeguridadProvider;
import com.tfg.inventariado.provider.SubcategoriaProvider;
import com.tfg.inventariado.repository.StockSeguridadRepository;

@Service
public class StockSeguridadProviderImpl implements StockSeguridadProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private StockSeguridadRepository stockSeguridadRepository;
	
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

	@Override
	public MessageResponseDto<String> addStockSteguridad(StockSeguridadDto seguridad) {
		if(seguridad.getCodCategoria()==null || !this.categoriaProvider.categoriaExisteByCodigo(seguridad.getCodCategoria())) {
			return MessageResponseDto.fail("La categoría no existe");
		}
		if(seguridad.getCodSubcategoria()==null || !this.subcategoriaProvider.subcategoriaExisteByID(seguridad.getCodCategoria(),seguridad.getCodSubcategoria())) {
			return MessageResponseDto.fail("La subcategoría no existe");
		}
		if(seguridad.getIdOficina()==null || !this.oficinaProvider.oficinaExisteByID(seguridad.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		StockSeguridadEntityID id = new StockSeguridadEntityID(seguridad.getCodSubcategoria(), seguridad.getCodCategoria(), seguridad.getIdOficina());
		
		if(stockSeguridadRepository.findById(id).isPresent()) {
			return MessageResponseDto.fail("El stock de seguridad ya existe, debe editarlo");
		}
		if(seguridad.getCantidad()==null || seguridad.getCantidad()<=0) {
			return MessageResponseDto.fail("Como mínimo la cantidad debe ser uno");
		}
		if(seguridad.getPlazoEntregaMedio()==null || seguridad.getPlazoEntregaMedio()<=0) {
			return MessageResponseDto.fail("Como mínimo el plazo de entrega medio debe ser uno");
		}
		StockSeguridadEntity newStock = convertToMapEntity(seguridad);
		newStock = stockSeguridadRepository.save(newStock);
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
	public MessageResponseListDto<List<StockSeguridadDto>> listAllStockSeguridadSkipLimit(Integer page, Integer size) {
		PageRequest pageable = PageRequest.of(page, size, Sort.by("idOficina"));
		Page<StockSeguridadEntity> pageableSS = stockSeguridadRepository.findAll(pageable);
		
		List<StockSeguridadEntity> listaEntity = pageableSS.getContent();
		List<StockSeguridadDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		return MessageResponseListDto.success(listaDto, page, size,(int) stockSeguridadRepository.count());
	}

}
