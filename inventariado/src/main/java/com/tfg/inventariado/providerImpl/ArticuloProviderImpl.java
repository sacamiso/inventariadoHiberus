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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.ArticuloFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.ArticuloEntity;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.CategoriaProvider;
import com.tfg.inventariado.provider.SubcategoriaProvider;
import com.tfg.inventariado.repository.ArticuloRepository;

@Service
public class ArticuloProviderImpl implements ArticuloProvider {
	
	@Autowired
	private ArticuloRepository articuloRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private SubcategoriaProvider subcategoriaProvider;
	
	@Autowired
    private MessageSource messageSource;

	@Autowired
	private CategoriaProvider categoriaProvider;
	
	@Override
	public ArticuloDto convertToMapDto(ArticuloEntity articulo) {
		return modelMapper.map(articulo, ArticuloDto.class);
	}

	@Override
	public ArticuloEntity convertToMapEntity(ArticuloDto articulo) {
		return modelMapper.map(articulo, ArticuloEntity.class);
	}

	@Override
	public List<ArticuloDto> listAllArticulo() {
		List<ArticuloEntity> listaArticuloEntity = this.articuloRepository.findAll();
		return listaArticuloEntity.stream()
		        .sorted(Comparator.comparing(ArticuloEntity::getReferencia))
		        .map(this::convertToMapDto)
		        .collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<Integer> addArticulo(ArticuloDto articulo) {
		Locale locale = LocaleContextHolder.getLocale();
		
		if(articulo.getDescripcion() == null || articulo.getDescripcion().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("descripcionObligatoria", null, locale));
		}
		if(articulo.getPrecioUnitario() <= 0) {
			return MessageResponseDto.fail(messageSource.getMessage("precioMayor0", null, locale));
		}
		if(articulo.getIva() <= 0) {
			return MessageResponseDto.fail(messageSource.getMessage("ivaMayor0", null, locale));
		}
		if(articulo.getReferencia() == null || articulo.getReferencia().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("referenciaObligatoria", null, locale));
		}
		if(articulo.getCodCategoria() == null || articulo.getCodCategoria().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaObligatoria", null, locale));
		}
		if(articulo.getCodSubcategoria() == null || articulo.getCodSubcategoria().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("subcategoriaObligatoria", null, locale));
		}
		if(articulo.getFabricante() == null || articulo.getFabricante().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("fabricanteObligatorio", null, locale));
		}
		if(!this.categoriaProvider.categoriaExisteByCodigo(articulo.getCodCategoria())) {
			return MessageResponseDto.fail(messageSource.getMessage("anadirNoExisteCat", null, locale));
		}
		if(!this.subcategoriaProvider.subcategoriaExisteByID(articulo.getCodCategoria(), articulo.getCodSubcategoria())) {
			return MessageResponseDto.fail(messageSource.getMessage("anadirNoExisteSub", null, locale));
		}
		ArticuloEntity newArticulo = this.convertToMapEntity(articulo);
		newArticulo = this.articuloRepository.save(newArticulo);
		return MessageResponseDto.success(newArticulo.getCodigoArticulo());
	}

	@Override
	public MessageResponseDto<String> editArticulo(ArticuloDto articulo, Integer articuloId) {
		Locale locale = LocaleContextHolder.getLocale();
		
		Optional<ArticuloEntity> optionalArticulo = this.articuloRepository.findById(articuloId);
		if(!optionalArticulo.isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("articuloNoExiste", null, locale));
		}
		ArticuloEntity articuloToUpdate = optionalArticulo.get();
		this.actualizarCampos(articuloToUpdate, articulo);
		this.articuloRepository.save(articuloToUpdate);
		return MessageResponseDto.success(messageSource.getMessage("articuloEditExito", null, locale));
	}
	
	private void actualizarCampos(ArticuloEntity articulo, ArticuloDto articuloToUpdate) {
		if(StringUtils.isNotBlank(articuloToUpdate.getDescripcion())) {
			articulo.setDescripcion(articuloToUpdate.getDescripcion());
		}
		if (Double.compare(articuloToUpdate.getPrecioUnitario(), 0.0) != 0) {
			articulo.setPrecioUnitario(articuloToUpdate.getPrecioUnitario());
		}
		if(StringUtils.isNotBlank(articuloToUpdate.getReferencia())) {
			articulo.setReferencia(articuloToUpdate.getReferencia());
		}
		if(StringUtils.isNotBlank(articuloToUpdate.getCodCategoria())) {
			articulo.setCodCategoria(articuloToUpdate.getCodCategoria());
		}
		if(StringUtils.isNotBlank(articuloToUpdate.getCodSubcategoria())) {
			articulo.setCodSubcategoria(articuloToUpdate.getCodSubcategoria());
		}
		if (Double.compare(articuloToUpdate.getIva(), 0.0) != 0) {
			articulo.setIva(articuloToUpdate.getIva());
		}
		if(StringUtils.isNotBlank(articuloToUpdate.getFabricante())) {
			articulo.setFabricante(articuloToUpdate.getFabricante());
		}
		if(StringUtils.isNotBlank(articuloToUpdate.getModelo())) {
			articulo.setModelo(articuloToUpdate.getModelo());
		}
		
	}

	@Override
	public MessageResponseDto<ArticuloDto> getArticuloById(Integer articuloId) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<ArticuloEntity> optionalArticulo = this.articuloRepository.findById(articuloId);
		if(!optionalArticulo.isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("articuloNoExiste", null, locale));
		}
		ArticuloDto articuloDto = this.convertToMapDto(optionalArticulo.get());
		return MessageResponseDto.success(articuloDto);
	}

	@Override
	public MessageResponseDto<List<ArticuloDto>> listArticulosByCategoria(String codigoCategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.categoriaProvider.categoriaExisteByCodigo(codigoCategoria)) {
			return MessageResponseDto.fail(messageSource.getMessage("categoriaNoExiste", null, locale));
		}
		List<ArticuloEntity> listaArticuloEntity = this.articuloRepository.findByCodCategoria(codigoCategoria);
		List<ArticuloDto> listaArticuloDto = listaArticuloEntity.stream().sorted(Comparator.comparing(ArticuloEntity::getReferencia)).map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaArticuloDto);
	}

	@Override
	public MessageResponseDto<List<ArticuloDto>> listArticulosBySubcategoria(String codigoCategoria, String codigoSubcategoria) {
		Locale locale = LocaleContextHolder.getLocale();
		if(!this.subcategoriaProvider.subcategoriaExisteByID(codigoCategoria, codigoSubcategoria)) {
			return MessageResponseDto.fail(messageSource.getMessage("subcategoriaNoExiste", null, locale));
		}
		List<ArticuloEntity> listaArticuloEntity = this.articuloRepository.findByCodCategoriaAndCodSubcategoria(codigoCategoria,codigoSubcategoria);
		List<ArticuloDto> listaArticuloDto = listaArticuloEntity.stream().sorted(Comparator.comparing(ArticuloEntity::getReferencia)).map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaArticuloDto);
		
	}

	@Override
	public boolean articuloExisteByID(Integer articuloId) {
		Optional<ArticuloEntity> optionalArticulo = this.articuloRepository.findById(articuloId);
		return optionalArticulo.isPresent() ? true : false;
	}

	@Override
	public MessageResponseListDto<List<ArticuloDto>> listAllArticulosSkipLimit(Integer page, Integer size,
			ArticuloFilterDto filtros) {
		Specification<ArticuloEntity> spec = Specification.where(null);
		
		if (filtros != null) {
			if (filtros.getDescripcion() != null) {
				String descripcion = filtros.getDescripcion();
			    spec = spec.and((root, query, cb) -> cb.like(root.get("descripcion"), "%" + descripcion + "%"));
	        }
			if (filtros.getPrecioUnitarioMin() != null) {
				Double precioMin = filtros.getPrecioUnitarioMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("precioUnitario"), precioMin));
			}
			if (filtros.getPrecioUnitarioMax() != null) {
				Double precioMax = filtros.getPrecioUnitarioMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("precioUnitario"), precioMax));
			}
			if (filtros.getReferencia()!= null) {
	            String referencia = filtros.getReferencia();
	            spec = spec.and((root, query, cb) -> cb.equal(root.get("referencia"), referencia));
	        }
			if (filtros.getCodigoCategoria() != null) {
				String cat = filtros.getCodigoCategoria();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("codCategoria"), cat));
	        }
			if (filtros.getCodigoSubcatogria() != null) {
				String sub = filtros.getCodigoSubcatogria();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("codSubcategoria"), sub));
	        }
			if (filtros.getIvaMin() != null) {
				Double ivaMin = filtros.getIvaMin();
	            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("iva"), ivaMin));
			}
			if (filtros.getIvaMax() != null) {
				Double ivaMax = filtros.getIvaMax();
	            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("iva"), ivaMax));
			}
			if (filtros.getFabricante() != null) {
				String fab = filtros.getFabricante();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("fabricante"), fab));
	        }
			if (filtros.getModelo() != null) {
				String mod = filtros.getModelo();
			    spec = spec.and((root, query, cb) -> cb.equal(root.get("modelo"), mod));
	        }
		}
		
		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "referencia"));
		Page<ArticuloEntity> pageableArticulo = this.articuloRepository.findAll(spec,pageable);
		
		List<ArticuloEntity> listaEntity = pageableArticulo.getContent();
		List<ArticuloDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		
		
		return MessageResponseListDto.success(listaDto, page, size,(int) this.articuloRepository.count(spec));
	}

}
