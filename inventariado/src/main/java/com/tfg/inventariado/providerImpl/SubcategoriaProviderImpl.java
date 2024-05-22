package com.tfg.inventariado.providerImpl;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.SubcategoriaDto;
import com.tfg.inventariado.entity.SubcategoriaEntity;
import com.tfg.inventariado.entity.id.SubcategoriaEntityID;
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
	
	@Autowired
    private MessageSource messageSource;

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
		return listaSubcategoriaEntity.stream()
				.sorted(Comparator.comparing(SubcategoriaEntity::getCodigoSubcategoria, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addSubcategoria(SubcategoriaDto subcategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		if(subcategoria.getCodigoCategoria()==null || subcategoria.getCodigoCategoria().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaObligatoria", null, locale));
		}
		if(subcategoria.getCodigoSubcategoria()==null || subcategoria.getCodigoSubcategoria().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("subcategoriaObligatoria", null, locale));
		}
		if(subcategoria.getNombre()==null || subcategoria.getNombre().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("nombreObl", null, locale));
		}
		if(subcategoriaExisteByID(subcategoria.getCodigoCategoria(), subcategoria.getCodigoSubcategoria())) {
			return MessageResponseDto.fail(messageSource.getMessage("subcategoriaExiste", null, locale));
		}
		if(this.categoriaProvider.categoriaExisteByCodigo(subcategoria.getCodigoCategoria())) {
			SubcategoriaEntity newSubcategoria = convertToMapEntity(subcategoria);
			newSubcategoria = this.subcategoriaRepository.save(newSubcategoria);
			return MessageResponseDto.success(messageSource.getMessage("subcategoriaAnadida", null, locale));
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<String> editSubcategoria(SubcategoriaDto subcategoria, String codigoCategoria,
			String codigoSubcategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		SubcategoriaEntityID id = new SubcategoriaEntityID(codigoSubcategoria,codigoCategoria);
		Optional<SubcategoriaEntity> optionalSubcategoria = this.subcategoriaRepository.findById(id);
		if(optionalSubcategoria.isPresent()) {
			SubcategoriaEntity subcategoriaToUpdate = optionalSubcategoria.get();
			this.actualizarCampos(subcategoriaToUpdate, subcategoria);
			this.subcategoriaRepository.save(subcategoriaToUpdate);
			return MessageResponseDto.success(messageSource.getMessage("subcategoriaEditada", null, locale));
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("subcategoriaNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<SubcategoriaDto> getSubcategoriaById(String codigoCategoria, String codigoSubcategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		SubcategoriaEntityID id = new SubcategoriaEntityID(codigoSubcategoria,codigoCategoria);
		Optional<SubcategoriaEntity> optionalSubcategoria = this.subcategoriaRepository.findById(id);
		if(optionalSubcategoria.isPresent()) {
			SubcategoriaDto subcategoriaDto = this.convertToMapDto(optionalSubcategoria.get());
			return MessageResponseDto.success(subcategoriaDto);
		}else {
			return MessageResponseDto.fail(messageSource.getMessage("subcategoriaNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<List<SubcategoriaDto>> listSubcategoriasDeCategoria(String codigoCategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.categoriaProvider.categoriaExisteByCodigo(codigoCategoria)) {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaNoExiste", null, locale));
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
