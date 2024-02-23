package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.SubcategoriaDto;
import com.tfg.inventariado.entity.SubcategoriaEntity;
import com.tfg.inventariado.entity.SubcategoriaEntityID;
import com.tfg.inventariado.provider.CategoriaProvider;
import com.tfg.inventariado.provider.SubcategoriaProvider;
import com.tfg.inventariado.repository.SubcategoriaRepository;

@Service
public class SubcategoriaProviderImpl implements SubcategoriaProvider {
	
	@Autowired
	private SubcategoriaRepository subcategoriaRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private CategoriaProvider categoriaProvider;

	@Override
	public SubcategoriaDto convertToMapDto(SubcategoriaEntity subcategoria) {
		return modelMapper.map(subcategoria, SubcategoriaDto.class);
	}

	@Override
	public SubcategoriaEntity convertToMapEntity(SubcategoriaDto subcategoria) {
		return modelMapper.map(subcategoria, SubcategoriaEntity.class);
	}

	@Override
	public List<SubcategoriaDto> listAllSubcategoria() {
		List<SubcategoriaEntity> listaSubcategoriaEntity = this.subcategoriaRepository.findAll();
		return listaSubcategoriaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addSubcategoria(SubcategoriaDto subcategoria) {
		if(subcategoria.getCodigoCategoria()==null || subcategoria.getCodigoCategoria().isEmpty()) {
			return MessageResponseDto.fail("La categoría es obligatoria");
		}
		if(subcategoria.getCodigoSubcategoria()==null || subcategoria.getCodigoSubcategoria().isEmpty()) {
			return MessageResponseDto.fail("La subcategoría es obligatoria");
		}
		if(subcategoria.getNombre()==null || subcategoria.getNombre().isEmpty()) {
			return MessageResponseDto.fail("El nombre es obligatorio");
		}
		if(subcategoriaExisteByID(subcategoria.getCodigoCategoria(), subcategoria.getCodigoSubcategoria())) {
			return MessageResponseDto.fail("La subcategoría ya existe");
		}
		if(this.categoriaProvider.categoriaExisteByCodigo(subcategoria.getCodigoCategoria())) {
			SubcategoriaEntity newSubcategoria = convertToMapEntity(subcategoria);
			newSubcategoria = this.subcategoriaRepository.save(newSubcategoria);
			return MessageResponseDto.success("Subcategoria añadida con éxito");
		}else {
			return MessageResponseDto.fail("No se puede añadir la subcategoria a una categoria inexistente");
		}
	}

	@Override
	public MessageResponseDto<String> editSubcategoria(SubcategoriaDto subcategoria, String codigoCategoria,
			String codigoSubcategoria) {
		SubcategoriaEntityID id = new SubcategoriaEntityID(codigoSubcategoria,codigoCategoria);
		Optional<SubcategoriaEntity> optionalSubcategoria = this.subcategoriaRepository.findById(id);
		if(optionalSubcategoria.isPresent()) {
			SubcategoriaEntity subcategoriaToUpdate = optionalSubcategoria.get();
			this.actualizarCampos(subcategoriaToUpdate, subcategoria);
			this.subcategoriaRepository.save(subcategoriaToUpdate);
			return MessageResponseDto.success("Subcategoria editada con éxito");
		}else {
			return MessageResponseDto.fail("La subcategoría que se desea editar no existe");
		}
	}

	@Override
	public MessageResponseDto<SubcategoriaDto> getSubcategoriaById(String codigoCategoria, String codigoSubcategoria) {
		SubcategoriaEntityID id = new SubcategoriaEntityID(codigoSubcategoria,codigoCategoria);
		Optional<SubcategoriaEntity> optionalSubcategoria = this.subcategoriaRepository.findById(id);
		if(optionalSubcategoria.isPresent()) {
			SubcategoriaDto subcategoriaDto = this.convertToMapDto(optionalSubcategoria.get());
			return MessageResponseDto.success(subcategoriaDto);
		}else {
			return MessageResponseDto.fail("No se encuentra la subcategoría con ese identificador");
		}
	}

	@Override
	public MessageResponseDto<List<SubcategoriaDto>> listSubcategoriasDeCategoria(String codigoCategoria) {
		
		if(!this.categoriaProvider.categoriaExisteByCodigo(codigoCategoria)) {
			return MessageResponseDto.fail("La categoria no existe");
		}
		List<SubcategoriaEntity> listaSubcategoriaEntity = this.subcategoriaRepository.findByCodigoCategoria(codigoCategoria);
		List<SubcategoriaDto> listaArticuloDto = listaSubcategoriaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaArticuloDto);
	}

	private void actualizarCampos(SubcategoriaEntity subcategoria, SubcategoriaDto subcategoriaToUpdate) {
		if(StringUtils.isNotBlank(subcategoriaToUpdate.getNombre())) {
			subcategoria.setNombre(subcategoriaToUpdate.getNombre());
		}
	}

	@Override
	public boolean subcategoriaExisteByID(String codigoCategoria, String codigoSubcategoria) {
		SubcategoriaEntityID id = new SubcategoriaEntityID(codigoSubcategoria,codigoCategoria);
		Optional<SubcategoriaEntity> optionalSubcategoria = this.subcategoriaRepository.findById(id);
		return optionalSubcategoria.isPresent() ? true : false;
	}
}
