package com.tfg.inventariado.providerImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.entity.PedidoEntity;
import com.tfg.inventariado.provider.CondicionPagoProvider;
import com.tfg.inventariado.provider.EmpleadoProvider;
import com.tfg.inventariado.provider.MedioPagoProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.PedidoProvider;
import com.tfg.inventariado.provider.ProveedorProvider;
import com.tfg.inventariado.repository.PedidoRepository;

@Service
public class PedidoProviderImpl implements PedidoProvider {

	@Autowired
	private PedidoRepository pedidoRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private EmpleadoProvider empleadoProvider;
	
	@Autowired
	private ProveedorProvider proveedorProvider;
	
	@Autowired
	private OficinaProvider oficinaProvider;
	
	@Autowired
	private CondicionPagoProvider condiconProvider;
	
	@Autowired
	private MedioPagoProvider medioProvider;
	
	@Override
	public PedidoDto convertToMapDto(PedidoEntity pedido) {
		return modelMapper.map(pedido, PedidoDto.class);
	}

	@Override
	public PedidoEntity convertToMapEntity(PedidoDto pedido) {
		return modelMapper.map(pedido, PedidoEntity.class);
	}

	@Override
	public List<PedidoDto> listAllPedidos() {
		List<PedidoEntity> listaEntity = pedidoRepository.findAll();
		return listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<String> addPedido(PedidoDto pedido) {
		
		if(pedido.getNumeroPedido()!=null && pedidoRepository.findById(pedido.getNumeroPedido()).isPresent()) {
			return MessageResponseDto.fail("El pedido ya existe");
		}
		if(pedido.getFechaPedido() == null) {
			return MessageResponseDto.fail("La fecha de pedido es obligatoria");
		}
		if(pedido.getIvaPedido() == 0) {
			return MessageResponseDto.fail("El iva es obligatorio");
		}
		if(pedido.getCosteTotal() == 0) {
			return MessageResponseDto.fail("El coste total es obligatorio");
		}
		if(pedido.getIdEmpleado() == null || !this.empleadoProvider.empleadoExisteByCodigo(pedido.getIdEmpleado())) {
			return MessageResponseDto.fail("El empleado no existe");
		}
		if(pedido.getPlazoEntrega() == null || pedido.getPlazoEntrega()<=0) {
			return MessageResponseDto.fail("El plazo de entrega debe ser un día como mínimo");

		}
		if(pedido.getIdProveedor() == null || !this.proveedorProvider.proveedorExisteByID(pedido.getIdProveedor())) {
			return MessageResponseDto.fail("El proveedor no existe");
		}
		if(pedido.getIdOficina() == null || !this.oficinaProvider.oficinaExisteByID(pedido.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe no existe");
		}
		if(pedido.getFechaRecepcion()!=null && pedido.getFechaRecepcion().isBefore(pedido.getFechaPedido())) {
			return MessageResponseDto.fail("La fecha de recepción no puede ser anterior a la de envío");
		}
		if(pedido.getCondicionPago() == null || !this.condiconProvider.condicionExisteByCodigo(pedido.getCondicionPago())) {
			return MessageResponseDto.fail("La condición de pago no existe");
		}
		if(pedido.getMedioPago() == null || !this.medioProvider.medioExisteByCodigo(pedido.getMedioPago())) {
			return MessageResponseDto.fail("El medio de pago no existe");
		}
		PedidoEntity newPedido = convertToMapEntity(pedido);
		newPedido = pedidoRepository.save(newPedido);
		return MessageResponseDto.success("Pedido añadido con éxito");
	}

	@Override
	public MessageResponseDto<String> editPedido(PedidoDto pedido, Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		if(optionalPedido.isPresent()) {
			PedidoEntity pedidoToUpdate = optionalPedido.get();
			
			this.actualizarCampos(pedidoToUpdate, pedido);
			
			pedidoRepository.save(pedidoToUpdate);
			
			return MessageResponseDto.success("Pedido editado con éxito");
			
		}else {
			return MessageResponseDto.fail("El pedido que se desea editar no existe");
		}
	}
	
private void actualizarCampos(PedidoEntity pedido, PedidoDto pedidoToUpdate) {
				
		if(pedidoToUpdate.getFechaPedido() != null) {
			pedido.setFechaPedido(pedidoToUpdate.getFechaPedido());
		}
		if(pedidoToUpdate.getIvaPedido() != 0) {
			pedido.setIvaPedido(pedidoToUpdate.getIvaPedido());
		}
		if(pedidoToUpdate.getCosteTotal() != 0) {
			pedido.setCosteTotal(pedidoToUpdate.getCosteTotal());
		}
		if(this.empleadoProvider.empleadoExisteByCodigo(pedidoToUpdate.getIdEmpleado())) {
			pedido.setIdEmpleado(pedidoToUpdate.getIdEmpleado());
		}
		if(pedidoToUpdate.getPlazoEntrega()>0) {
			pedido.setPlazoEntrega(pedidoToUpdate.getPlazoEntrega());
		}
		
		pedido.setCostesEnvio(pedidoToUpdate.getCostesEnvio());

		if(this.proveedorProvider.proveedorExisteByID(pedidoToUpdate.getIdProveedor())) {
			pedido.setIdProveedor(pedidoToUpdate.getIdProveedor());
		}
		if(this.oficinaProvider.oficinaExisteByID(pedidoToUpdate.getIdOficina())) {
			pedido.setIdOficina(pedidoToUpdate.getIdOficina());
		}
		if(pedidoToUpdate.getFechaRecepcion()!=null && pedidoToUpdate.getFechaRecepcion().isAfter(pedido.getFechaPedido())) {
			pedido.setFechaRecepcion(pedidoToUpdate.getFechaRecepcion());
		}
		if(this.condiconProvider.condicionExisteByCodigo(pedidoToUpdate.getCondicionPago())) {
			pedido.setCondicionPago(pedidoToUpdate.getCondicionPago());
		}
		if(this.medioProvider.medioExisteByCodigo(pedidoToUpdate.getMedioPago())) {
			pedido.setMedioPago(pedidoToUpdate.getMedioPago());
		}
		
		
	}

	@Override
	public MessageResponseDto<PedidoDto> getPedidoById(Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		if(optionalPedido.isPresent()) {
			PedidoDto pedidoDto = this.convertToMapDto(optionalPedido.get());
			return MessageResponseDto.success(pedidoDto);
		}else {
			return MessageResponseDto.fail("No se encuentra ningún pedido con ese id");
		}
	}

	@Override
	public boolean pedidoExisteByID(Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		return optionalPedido.isPresent() ? true : false;	
	}

	@Override
	public MessageResponseDto<List<PedidoDto>> listPedidoByProveedor(Integer idProveedor) {

		if(!this.proveedorProvider.proveedorExisteByID(idProveedor)) {
			return MessageResponseDto.fail("El proveedor no existe");
		}
		List<PedidoEntity> listaPedioEntity = this.pedidoRepository.findByIdProveedor(idProveedor);
		List<PedidoDto> listapedidoDto = listaPedioEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listapedidoDto);
		
	}

	@Override
	public MessageResponseDto<List<PedidoDto>> listPedidoByOficina(Integer idOficina) {
		if(!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<PedidoEntity> listaPedioEntity = this.pedidoRepository.findByIdOficina(idOficina);
		List<PedidoDto> listapedidoDto = listaPedioEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listapedidoDto);
	}

}
