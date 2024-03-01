package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.LineaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.entity.LineaEntity;
import com.tfg.inventariado.entity.LineaEntityID;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.LineaProvider;
import com.tfg.inventariado.repository.LineaRepository;

@Service
public class LineaProviderImpl implements LineaProvider {

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private LineaRepository lineaRepository;
	
	@Autowired
	private ArticuloProvider articuloProvider;
	
//	@Autowired
//	private PedidoProvider pedidoProvider;
	
	@Override
	public LineaDto convertToMapDto(LineaEntity linea) {
		return modelMapper.map(linea, LineaDto.class);
	}

	@Override
	public LineaEntity convertToMapEntity(LineaDto linea) {
		return modelMapper.map(linea, LineaEntity.class);
	}

	@Override
	public List<LineaDto> listAllLineas() {
		List<LineaEntity> listaEntity = lineaRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}
	
	private MessageResponseDto<String> validaLinea(LineaDto linea){
		if(linea.getNumeroPedido()==null) {
			return MessageResponseDto.fail("El número de pedido es obligatorio");
		}
		if(linea.getNumeroLinea()==null) {
			return MessageResponseDto.fail("El número de línea es obligatorio");
		}
		LineaEntityID id = new LineaEntityID(linea.getNumeroPedido(), linea.getNumeroLinea());
		if(lineaRepository.findById(id).isPresent()) {
			return MessageResponseDto.fail("La línea ya existe para este pedido");
		}
//		if(!this.pedidoProvider.pedidoExisteByID(linea.getNumeroPedido())) {
//			return MessageResponseDto.fail("El pedido no existe no existe");
//		}
		if(linea.getCodigoArticulo()==null || !this.articuloProvider.articuloExisteByID(linea.getCodigoArticulo())) {
			return MessageResponseDto.fail("El artículo no existe");
		}
		if(linea.getNumeroUnidades()==null || linea.getNumeroUnidades()<=0) {
			return MessageResponseDto.fail("Como mínimo debe haber 1 unidad");
		}
		if(linea.getPrecioLinea()<0) {
			return MessageResponseDto.fail("El precio no puede ser menor que 0");
		}
		if(linea.getDescuento()<0) {
			return MessageResponseDto.fail("No se puede aplicar un descuento negativo");
		}
		return MessageResponseDto.success("validado");
	}

	@Transactional
	@Override
	public MessageResponseDto<String> addListLinea(List<LineaDto> lineas) {
		MessageResponseDto<String> validacion;
		for (LineaDto l : lineas) {
			validacion = validaLinea(l);
			
			if(!validacion.isSuccess()) {
				return validacion;
			}
			
		}
		List<LineaEntity> list =  lineas.stream().map(this::convertToMapEntity).collect(Collectors.toList());
		lineaRepository.saveAll(list);
		return MessageResponseDto.success("Líneas añadidas con éxito");
	}
	
	@Override
	public MessageResponseDto<String> addLinea(LineaDto linea) {
		MessageResponseDto<String> validacion = validaLinea(linea);
		
		if(!validacion.isSuccess()) {
			return validacion;
		}
		LineaEntity newLinea = convertToMapEntity(linea);
		newLinea = lineaRepository.save(newLinea);
		return MessageResponseDto.success("Línea añadida con éxito");
	}

	@Override
	public MessageResponseDto<String> editLinea(LineaDto linea, Integer numPedido, Integer numLinea) {
		LineaEntityID id = new LineaEntityID(numPedido, numLinea);
		Optional<LineaEntity> optionalLinea = lineaRepository.findById(id);
		if(optionalLinea.isPresent()) {
			
			LineaEntity lineaToUpdate = optionalLinea.get();
			
			this.actualizarCampos(lineaToUpdate, linea);
			
			lineaRepository.save(lineaToUpdate);
			
			return MessageResponseDto.success("Linea editada con éxito");
			
		}else {
			return MessageResponseDto.fail("La línea que se desea editar no existe");
		}
	}
	
	private void actualizarCampos(LineaEntity linea, LineaDto lineaToUpdate) {
		
		if(this.articuloProvider.articuloExisteByID(lineaToUpdate.getCodigoArticulo())) {
			linea.setCodigoArticulo(lineaToUpdate.getCodigoArticulo());
		}
		if(lineaToUpdate.getNumeroUnidades()>0) {
			linea.setNumeroUnidades(lineaToUpdate.getNumeroUnidades());
		}
		if(lineaToUpdate.getPrecioLinea()>0) {
			linea.setPrecioLinea(lineaToUpdate.getPrecioLinea());
		}
		if(lineaToUpdate.getDescuento()>=0) {
			linea.setDescuento(lineaToUpdate.getDescuento());
		}
			
	}

	@Override
	public MessageResponseDto<LineaDto> getLineaById(Integer numPedido, Integer numLinea) {
		LineaEntityID id = new LineaEntityID(numPedido, numLinea);
		Optional<LineaEntity> optionalLinea = lineaRepository.findById(id);
		if(optionalLinea.isPresent()) {
			LineaDto lineaDto = this.convertToMapDto(optionalLinea.get());
			return MessageResponseDto.success(lineaDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ninguna linea con ese id");
		}
	}

	@Override
	public MessageResponseDto<List<LineaDto>> listLineasByPedido(Integer numPedido) {
//		if(!this.pedidoProvider.pedidoExisteByID(numPedido)) {
//			return MessageResponseDto.fail("El pedido no existe");
//		}
		List<LineaEntity> listaEntity = this.lineaRepository.findByNumeroPedido(numPedido);
		List<LineaDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean lineaExisteByID(Integer numPedido, Integer numLinea) {
		LineaEntityID id = new LineaEntityID(numPedido, numLinea);
		Optional<LineaEntity> optionalLinea = lineaRepository.findById(id);
		return optionalLinea    .isPresent() ? true : false;
	}

	

}
