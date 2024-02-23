package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.CategoriaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.CategoriaEntity;
import com.tfg.inventariado.provider.CategoriaProvider;
import com.tfg.inventariado.repository.CategoriaRepository;

@Service
public class CategoriaProviderImpl implements CategoriaProvider {

	
	@Autowired
	private CategoriaRepository categoriaRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Override
	public CategoriaDto convertToMapDto(CategoriaEntity categoria) {
		return modelMapper.map(categoria, CategoriaDto.class);
	}

	@Override
	public CategoriaEntity convertToMapEntity(CategoriaDto categoria) {
		return modelMapper.map(categoria, CategoriaEntity.class);
	}

	@Override
	public List<CategoriaDto> listAllCategoria() {
		List<CategoriaEntity> listaCategoriaEntity = categoriaRepository.findAll();
		return listaCategoriaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addCategoria(CategoriaDto categoria) {
		if(categoria.getCodigoCategoria()==null || categoria.getCodigoCategoria().isEmpty()) {
			return MessageResponseDto.fail("El código es obligatorio");
		}
		if(categoria.getNombre()==null || categoria.getNombre().isEmpty()) {
			return MessageResponseDto.fail("el nombre es obligatorio");
		}
		if(categoriaExisteByCodigo(categoria.getCodigoCategoria())) {
			return MessageResponseDto.fail("El rol ya existe");
		}
		CategoriaEntity newCategoria = convertToMapEntity(categoria);
		newCategoria = categoriaRepository.save(newCategoria);
		return MessageResponseDto.success("Categoria añadida con éxito");
	}

	@Override
	public MessageResponseDto<String> editCategoria(CategoriaDto categoria, String codigoCategoria) {
		Optional<CategoriaEntity> optionalCategoria = categoriaRepository.findById(codigoCategoria);
		if(optionalCategoria.isPresent()) {
			CategoriaEntity categoriaToUpdate = optionalCategoria.get();
			
			this.actualizarCampos(categoriaToUpdate, categoria);
			
			categoriaRepository.save(categoriaToUpdate);
			
			return MessageResponseDto.success("Categoria editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La categoría que se desea editar no existe");
		}
	}

	@Override
	public MessageResponseDto<CategoriaDto> getCategoriaById(String codigoCategoria) {
		Optional<CategoriaEntity> optionalCategoria = categoriaRepository.findById(codigoCategoria);
		if(optionalCategoria.isPresent()) {
			CategoriaDto categoriaDto = this.convertToMapDto(optionalCategoria.get());
			return MessageResponseDto.success(categoriaDto);
		}else {
			return MessageResponseDto.fail("No se encuentra la categoría con ese código");
		}
	}

	@Override
	public boolean categoriaExisteByCodigo(String codigo) {
		Optional<CategoriaEntity> categoria = categoriaRepository.findById(codigo);
		return categoria.isPresent() ? true : false;
	}
	
	private void actualizarCampos(CategoriaEntity categoria, CategoriaDto categoriaToUpdate) {
		if(StringUtils.isNotBlank(categoriaToUpdate.getNombre())) {
			categoria.setNombre(categoriaToUpdate.getNombre());
		}
	}
}
