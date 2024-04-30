package com.tfg.inventariado.providerImpl;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.inventariado.dto.ArticuloDto;
import com.tfg.inventariado.dto.EmpleadoDto;
import com.tfg.inventariado.dto.InventarioDto;
import com.tfg.inventariado.dto.LineaDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.dto.OficinaDto;
import com.tfg.inventariado.dto.PedidoDto;
import com.tfg.inventariado.dto.PedidoFilterDto;
import com.tfg.inventariado.dto.ProveedorDto;
import com.tfg.inventariado.entity.PedidoEntity;
import com.tfg.inventariado.entity.PedidoVWEntity;
import com.tfg.inventariado.provider.ArticuloProvider;
import com.tfg.inventariado.provider.CondicionPagoProvider;
import com.tfg.inventariado.provider.EmpleadoProvider;
import com.tfg.inventariado.provider.InventarioProvider;
import com.tfg.inventariado.provider.LineaProvider;
import com.tfg.inventariado.provider.MedioPagoProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.PedidoProvider;
import com.tfg.inventariado.provider.ProveedorProvider;
import com.tfg.inventariado.repository.PedidoRepository;
import com.tfg.inventariado.repository.PedidoVWRepository;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class PedidoProviderImpl implements PedidoProvider {

	@Autowired
	private PedidoRepository pedidoRepository;

	@Autowired
	private PedidoVWRepository pedidoVistaRepository;

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

	@Autowired
	private LineaProvider lineaProvider;

	@Autowired
	private InventarioProvider inventarioProvider;

	@Autowired
	private ArticuloProvider articuloProvider;

	@Override
	public PedidoDto convertToMapDto(PedidoEntity pedido) {
		return modelMapper.map(pedido, PedidoDto.class);
	}

	@Override
	public PedidoEntity convertToMapEntity(PedidoDto pedido) {
		return modelMapper.map(pedido, PedidoEntity.class);
	}

	@Override
	public PedidoDto convertToMapDtoVista(PedidoVWEntity pedido) {
		return modelMapper.map(pedido, PedidoDto.class);
	}

	@Override
	public PedidoVWEntity convertToMapEntityVista(PedidoDto pedido) {
		return modelMapper.map(pedido, PedidoVWEntity.class);
	}

	@Override
	public List<PedidoDto> listAllPedidos() {
		List<PedidoEntity> listaEntity = pedidoRepository.findAll();
		List<PedidoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		MessageResponseDto<List<LineaDto>> lineas;
		for (PedidoDto pedidoDto : listaDto) {
			lineas = lineaProvider.listLineasByPedido(pedidoDto.getNumeroPedido());
			if (lineas.isSuccess()) {
				int numeroUnidades = 0;
				double costeTotal = 0;
				for (LineaDto l : lineas.getMessage()) {
					numeroUnidades = numeroUnidades + l.getNumeroUnidades();
					costeTotal = costeTotal + l.getPrecioLinea();
				}

				pedidoDto.setNumeroUnidades(numeroUnidades);
				if (numeroUnidades != 0) {
					pedidoDto.setCosteUnitario(costeTotal / numeroUnidades);
				} else {
					pedidoDto.setCosteUnitario(0.0);
				}
			}
		}

		return listaDto;
	}

	@Transactional
	@Override
	public MessageResponseDto<?> addPedido(PedidoDto pedido) {

		if (pedido.getNumeroPedido() != null && pedidoRepository.findById(pedido.getNumeroPedido()).isPresent()) {
			return MessageResponseDto.fail("El pedido ya existe");
		}

		pedido.setFechaPedido(LocalDate.now());

		if (pedido.getIvaPedido() == 0) {
			return MessageResponseDto.fail("El iva es obligatorio");
		}

		if (pedido.getIdEmpleado() == null || !this.empleadoProvider.empleadoExisteByCodigo(pedido.getIdEmpleado())) {
			return MessageResponseDto.fail("El empleado no existe");
		}
		if (pedido.getPlazoEntrega() == null || pedido.getPlazoEntrega() <= 0) {
			return MessageResponseDto.fail("El plazo de entrega debe ser un día como mínimo");

		}
		if (pedido.getIdProveedor() == null || !this.proveedorProvider.proveedorExisteByID(pedido.getIdProveedor())) {
			return MessageResponseDto.fail("El proveedor no existe");
		}
		if (pedido.getIdOficina() == null || !this.oficinaProvider.oficinaExisteByID(pedido.getIdOficina())) {
			return MessageResponseDto.fail("La oficina no existe no existe");
		}
		if (pedido.getFechaRecepcion() != null && pedido.getFechaRecepcion().isBefore(pedido.getFechaPedido())) {
			return MessageResponseDto.fail("La fecha de recepción no puede ser anterior a la de envío");
		}
		if (pedido.getCondicionPago() == null
				|| !this.condiconProvider.condicionExisteByCodigo(pedido.getCondicionPago())) {
			return MessageResponseDto.fail("La condición de pago no existe");
		}
		if (pedido.getMedioPago() == null || !this.medioProvider.medioExisteByCodigo(pedido.getMedioPago())) {
			return MessageResponseDto.fail("El medio de pago no existe");
		}

		List<LineaDto> listLinea = pedido.getLineas();

		double costeTotal = 0;
		int unidadesTotal = 0;

		for (LineaDto l : listLinea) {
			if (articuloProvider.articuloExisteByID(l.getCodigoArticulo())) {
				l.setArticulo(articuloProvider.getArticuloById(l.getCodigoArticulo()).getMessage());
				l.setPrecioLinea(
						l.getArticulo().getPrecioUnitario() * l.getNumeroUnidades() * (100 - l.getDescuento()) / 100);

				costeTotal = costeTotal + l.getPrecioLinea();
				unidadesTotal = unidadesTotal + l.getNumeroUnidades();

			}
		}

		pedido.setCosteTotal(costeTotal + pedido.getCostesEnvio());
		pedido.setCosteUnitario(costeTotal / unidadesTotal);
		pedido.setNumeroUnidades(unidadesTotal);

		PedidoEntity newPedido = convertToMapEntity(pedido);
		newPedido = pedidoRepository.save(newPedido);

		for (LineaDto l : listLinea) {
			l.setNumeroPedido(newPedido.getNumeroPedido());
		}

		MessageResponseDto<String> msgLineas = this.lineaProvider.addListLinea(listLinea);

		if (!msgLineas.isSuccess()) {
			return msgLineas;
		}

		return MessageResponseDto.success(newPedido.getNumeroPedido());
	}

	@Override
	public MessageResponseDto<String> editPedido(PedidoDto pedido, Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		if (optionalPedido.isPresent()) {
			PedidoEntity pedidoToUpdate = optionalPedido.get();

			this.actualizarCampos(pedidoToUpdate, pedido);

			pedidoRepository.save(pedidoToUpdate);

			return MessageResponseDto.success("Pedido editado con éxito");

		} else {
			return MessageResponseDto.fail("El pedido que se desea editar no existe");
		}
	}

	private void actualizarCampos(PedidoEntity pedido, PedidoDto pedidoToUpdate) {

		if (pedidoToUpdate.getFechaPedido() != null) {
			pedido.setFechaPedido(pedidoToUpdate.getFechaPedido());
		}
		if (pedidoToUpdate.getIvaPedido() != 0) {
			pedido.setIvaPedido(pedidoToUpdate.getIvaPedido());
		}
		if (pedidoToUpdate.getCosteTotal() != 0) {
			pedido.setCosteTotal(pedidoToUpdate.getCosteTotal());
		}
		if (this.empleadoProvider.empleadoExisteByCodigo(pedidoToUpdate.getIdEmpleado())) {
			pedido.setIdEmpleado(pedidoToUpdate.getIdEmpleado());
		}
		if (pedidoToUpdate.getPlazoEntrega() > 0) {
			pedido.setPlazoEntrega(pedidoToUpdate.getPlazoEntrega());
		}

		pedido.setCostesEnvio(pedidoToUpdate.getCostesEnvio());

		if (this.proveedorProvider.proveedorExisteByID(pedidoToUpdate.getIdProveedor())) {
			pedido.setIdProveedor(pedidoToUpdate.getIdProveedor());
		}
		if (this.oficinaProvider.oficinaExisteByID(pedidoToUpdate.getIdOficina())) {
			pedido.setIdOficina(pedidoToUpdate.getIdOficina());
		}
		if (pedidoToUpdate.getFechaRecepcion() != null
				&& pedidoToUpdate.getFechaRecepcion().isAfter(pedido.getFechaPedido())) {
			pedido.setFechaRecepcion(pedidoToUpdate.getFechaRecepcion());
		}
		if (this.condiconProvider.condicionExisteByCodigo(pedidoToUpdate.getCondicionPago())) {
			pedido.setCondicionPago(pedidoToUpdate.getCondicionPago());
		}
		if (this.medioProvider.medioExisteByCodigo(pedidoToUpdate.getMedioPago())) {
			pedido.setMedioPago(pedidoToUpdate.getMedioPago());
		}

	}

	@Override
	public MessageResponseDto<PedidoDto> getPedidoById(Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		if (optionalPedido.isPresent()) {
			PedidoDto pedidoDto = this.convertToMapDto(optionalPedido.get());

			MessageResponseDto<List<LineaDto>> lineas = lineaProvider.listLineasByPedido(id);
			if (lineas.isSuccess()) {
				int numeroUnidades = 0;
				double costeTotal = 0;
				for (LineaDto l : lineas.getMessage()) {
					numeroUnidades = numeroUnidades + l.getNumeroUnidades();
					costeTotal = costeTotal + l.getPrecioLinea();
				}

				pedidoDto.setNumeroUnidades(numeroUnidades);
				if (numeroUnidades != 0) {
					pedidoDto.setCosteUnitario(costeTotal / numeroUnidades);
				} else {
					pedidoDto.setCosteUnitario(0.0);
				}

			}

			return MessageResponseDto.success(pedidoDto);
		} else {
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

		if (!this.proveedorProvider.proveedorExisteByID(idProveedor)) {
			return MessageResponseDto.fail("El proveedor no existe");
		}
		List<PedidoEntity> listaPedioEntity = this.pedidoRepository.findByIdProveedor(idProveedor);
		List<PedidoDto> listapedidoDto = listaPedioEntity.stream().map(this::convertToMapDto)
				.collect(Collectors.toList());
		return MessageResponseDto.success(listapedidoDto);

	}

	@Override
	public MessageResponseDto<List<PedidoDto>> listPedidoByOficina(Integer idOficina) {
		if (!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail("La oficina no existe");
		}
		List<PedidoEntity> listaPedioEntity = this.pedidoRepository.findByIdOficina(idOficina);
		List<PedidoDto> listapedidoDto = listaPedioEntity.stream().map(this::convertToMapDto)
				.collect(Collectors.toList());
		return MessageResponseDto.success(listapedidoDto);
	}

	@Override
	public MessageResponseListDto<List<PedidoDto>> listAllPedidosSkipLimit(Integer page, Integer size,
			PedidoFilterDto filtros) {
		Specification<PedidoVWEntity> spec = Specification.where(null);

		if (filtros != null) {
			if (filtros.getFechaPedido() != null) {
				LocalDate fechaPedido = filtros.getFechaPedido();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaPedido"), fechaPedido));
			}
			if (filtros.getIvaPedidoMin() != null) {
				Double ivaMin = filtros.getIvaPedidoMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ivaPedido"), ivaMin));
			}
			if (filtros.getIvaPedidoMax() != null) {
				Double ivaMax = filtros.getIvaPedidoMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("ivaPedido"), ivaMax));
			}
			if (filtros.getCosteTotalMin() != null) {
				Double cosTotMin = filtros.getCosteTotalMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeTotal"), cosTotMin));
			}
			if (filtros.getCosteTotalMax() != null) {
				Double cosTotMax = filtros.getCosteTotalMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costeTotal"), cosTotMax));
			}
			if (filtros.getIdEmpleado() != null && filtros.getIdEmpleado() != 0) {
				Integer idEmpleado = filtros.getIdEmpleado();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idEmpleado"), idEmpleado));
			}
			if (filtros.getPlazoEntregaMin() != null) {
				Integer plazoMin = filtros.getPlazoEntregaMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("plazoEntrega"), plazoMin));
			}
			if (filtros.getPlazoEntregaMax() != null) {
				Integer plazoMax = filtros.getPlazoEntregaMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("plazoEntrega"), plazoMax));
			}
			if (filtros.getCostesEnvioMin() != null) {
				Double costesMin = filtros.getCostesEnvioMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costesEnvio"), costesMin));
			}
			if (filtros.getCostesEnvioMax() != null) {
				Double costesMax = filtros.getCostesEnvioMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costesEnvio"), costesMax));
			}
			if (filtros.getIdProveedor() != null && filtros.getIdProveedor() != 0) {
				Integer idProveedor = filtros.getIdProveedor();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idProveedor"), idProveedor));
			}
			if (filtros.getIdOficina() != null && filtros.getIdOficina() != 0) {
				Integer idOficina = filtros.getIdOficina();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
			}
			if (filtros.getFechaRecepcion() != null) {
				LocalDate fechaRecepcion = filtros.getFechaRecepcion();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaRecepcion"), fechaRecepcion));
			}
			if (filtros.getCodigoCondicionPago() != null && !filtros.getCodigoCondicionPago().isEmpty()) {
				String codigoCondicion = filtros.getCodigoCondicionPago();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("condicionPago"), codigoCondicion));
			}
			if (filtros.getCodigoMedioPago() != null && !filtros.getCodigoMedioPago().isEmpty()) {
				String codigoMedio = filtros.getCodigoMedioPago();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("medioPago"), codigoMedio));
			}
			if (filtros.getRecibido() != null) {
				if (filtros.getRecibido()) {
					spec = spec.and((root, query, cb) -> cb.isNotNull(root.get("fechaRecepcion")));
				} else {
					spec = spec.and((root, query, cb) -> cb.isNull(root.get("fechaRecepcion")));
				}
			}
			if (filtros.getDevuelto() != null) {
				if (filtros.getDevuelto()) {
					spec = spec.and((root, query, cb) -> cb.isTrue(root.get("devuelto")));
				} else {
					spec = spec.and((root, query, cb) -> cb.isFalse(root.get("devuelto")));
				}
			}
			if (filtros.getCosteUnitarioMin() != null && filtros.getCosteUnitarioMin() != 0) {
				Double costeUnMin = filtros.getCosteUnitarioMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeUnitario"), costeUnMin));
			}
			if (filtros.getCosteUnitarioMax() != null && filtros.getCosteUnitarioMax() != 0) {
				Double costeUnMax = filtros.getCosteUnitarioMax();
				spec = spec.and((root, query, cb) -> cb.or(cb.isNull(root.get("costeUnitario")),
						cb.lessThanOrEqualTo(root.get("costeUnitario"), costeUnMax)));
			}
			if (filtros.getFechaInicioIntervalo() != null) {
				LocalDate fechaInicioIntervalo = filtros.getFechaInicioIntervalo();
				spec = spec.and(
						(root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaPedido"), fechaInicioIntervalo));
			}
			if (filtros.getFechaFinIntervalo() != null) {
				LocalDate fechaFinIntervalo = filtros.getFechaFinIntervalo();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaPedido"), fechaFinIntervalo));
			}
		}

		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fechaPedido"));
		Page<PedidoVWEntity> pageablePedido = pedidoVistaRepository.findAll(spec, pageable);

		List<PedidoVWEntity> listaEntity = pageablePedido.getContent();
		List<PedidoDto> listaDto = listaEntity.stream().map(this::convertToMapDtoVista).collect(Collectors.toList());

		return MessageResponseListDto.success(listaDto, page, size, (int) pedidoVistaRepository.count(spec));
	}

	@Override
	@Transactional
	public MessageResponseDto<String> marcarRecibido(Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		if (optionalPedido.isPresent()) {
			PedidoEntity pedidoToUpdate = optionalPedido.get();

			pedidoToUpdate.setFechaRecepcion(LocalDate.now());

			pedidoRepository.save(pedidoToUpdate);

			OficinaDto of = oficinaProvider.getOficinaById(pedidoToUpdate.getIdOficina()).getMessage();
			MessageResponseDto<List<LineaDto>> lineas = lineaProvider
					.listLineasByPedido(pedidoToUpdate.getNumeroPedido());
			ArticuloDto art;
			MessageResponseDto<InventarioDto> inventario;
			InventarioDto inventarioDto;
			MessageResponseDto<String> msgInventario;

			if (lineas.isSuccess()) {
				for (LineaDto linea : lineas.getMessage()) {
					art = linea.getArticulo();
					inventario = inventarioProvider.getInventarioById(pedidoToUpdate.getIdOficina(),
							art.getCodigoArticulo());

					if (inventario.isSuccess()) {
						inventarioDto = new InventarioDto(art.getCodigoArticulo(), pedidoToUpdate.getIdOficina(),
								inventario.getMessage().getStock() + linea.getNumeroUnidades(), art, of);
						msgInventario = inventarioProvider.editInventario(inventarioDto, pedidoToUpdate.getIdOficina(),
								art.getCodigoArticulo());
					} else {
						inventarioDto = new InventarioDto(art.getCodigoArticulo(), pedidoToUpdate.getIdOficina(),
								linea.getNumeroUnidades(), art, of);
						msgInventario = inventarioProvider.addInventario(inventarioDto);
					}
					if (!msgInventario.isSuccess()) {
						return MessageResponseDto.fail(msgInventario.getError());
					}
				}
			}

			return MessageResponseDto.success("Pedido recibido");

		} else {
			return MessageResponseDto.fail("El pedido que se desea marcar como recibido no existe");
		}
	}

	@Override
	@Transactional
	public MessageResponseDto<String> devolverPedido(Integer id) {
		Optional<PedidoEntity> optionalPedido = pedidoRepository.findById(id);
		if (optionalPedido.isPresent()) {
			PedidoEntity pedidoToUpdate = optionalPedido.get();

			if (pedidoToUpdate.getFechaRecepcion() == null) {
				return MessageResponseDto.fail("No se puede devolver un pedio que no se ha recibido");
			}

			OficinaDto of = oficinaProvider.getOficinaById(pedidoToUpdate.getIdOficina()).getMessage();
			MessageResponseDto<List<LineaDto>> lineas = lineaProvider
					.listLineasByPedido(pedidoToUpdate.getNumeroPedido());
			ArticuloDto art;
			MessageResponseDto<InventarioDto> inventario;
			InventarioDto inventarioDto;
			MessageResponseDto<String> msgInventario;

			if (lineas.isSuccess()) {
				for (LineaDto linea : lineas.getMessage()) {
					art = linea.getArticulo();
					inventario = inventarioProvider.getInventarioById(pedidoToUpdate.getIdOficina(),
							art.getCodigoArticulo());

					if (inventario.getMessage().getStock() - linea.getNumeroUnidades() < 0) {
						return MessageResponseDto.fail(
								"No se puede devolver el pedido, ya se le ha dado salida a alguno de los productos");
					}
					if (inventario.isSuccess()) {
						inventarioDto = new InventarioDto(art.getCodigoArticulo(), pedidoToUpdate.getIdOficina(),
								inventario.getMessage().getStock() - linea.getNumeroUnidades(), art, of);
						msgInventario = inventarioProvider.editInventario(inventarioDto, pedidoToUpdate.getIdOficina(),
								art.getCodigoArticulo());
					} else {
						return MessageResponseDto.fail(
								"No se puede devolver el pedido, ya se le ha dado salida a alguno de los productos");
					}
					if (!msgInventario.isSuccess()) {
						return MessageResponseDto.fail(msgInventario.getError());
					}
				}
			}

			pedidoToUpdate.setDevuelto(true);

			pedidoRepository.save(pedidoToUpdate);

			return MessageResponseDto.success("Pedido devuelto");

		} else {
			return MessageResponseDto.fail("El pedido que se desea devolver no existe");
		}
	}

	@Override
	public byte[] descargarExcelPedido(PedidoFilterDto filtros) throws IOException {
		Specification<PedidoVWEntity> spec = Specification.where(null);

		if (filtros != null) {
			if (filtros.getFechaPedido() != null) {
				LocalDate fechaPedido = filtros.getFechaPedido();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaPedido"), fechaPedido));
			}
			if (filtros.getIvaPedidoMin() != null) {
				Double ivaMin = filtros.getIvaPedidoMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("ivaPedido"), ivaMin));
			}
			if (filtros.getIvaPedidoMax() != null) {
				Double ivaMax = filtros.getIvaPedidoMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("ivaPedido"), ivaMax));
			}
			if (filtros.getCosteTotalMin() != null) {
				Double cosTotMin = filtros.getCosteTotalMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeTotal"), cosTotMin));
			}
			if (filtros.getCosteTotalMax() != null) {
				Double cosTotMax = filtros.getCosteTotalMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costeTotal"), cosTotMax));
			}
			if (filtros.getIdEmpleado() != null && filtros.getIdEmpleado() != 0) {
				Integer idEmpleado = filtros.getIdEmpleado();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idEmpleado"), idEmpleado));
			}
			if (filtros.getPlazoEntregaMin() != null) {
				Integer plazoMin = filtros.getPlazoEntregaMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("plazoEntrega"), plazoMin));
			}
			if (filtros.getPlazoEntregaMax() != null) {
				Integer plazoMax = filtros.getPlazoEntregaMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("plazoEntrega"), plazoMax));
			}
			if (filtros.getCostesEnvioMin() != null) {
				Double costesMin = filtros.getCostesEnvioMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costesEnvio"), costesMin));
			}
			if (filtros.getCostesEnvioMax() != null) {
				Double costesMax = filtros.getCostesEnvioMax();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("costesEnvio"), costesMax));
			}
			if (filtros.getIdProveedor() != null && filtros.getIdProveedor() != 0) {
				Integer idProveedor = filtros.getIdProveedor();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idProveedor"), idProveedor));
			}
			if (filtros.getIdOficina() != null && filtros.getIdOficina() != 0) {
				Integer idOficina = filtros.getIdOficina();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOficina));
			}
			if (filtros.getFechaRecepcion() != null) {
				LocalDate fechaRecepcion = filtros.getFechaRecepcion();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("fechaRecepcion"), fechaRecepcion));
			}
			if (filtros.getCodigoCondicionPago() != null && !filtros.getCodigoCondicionPago().isEmpty()) {
				String codigoCondicion = filtros.getCodigoCondicionPago();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("condicionPago"), codigoCondicion));
			}
			if (filtros.getCodigoMedioPago() != null && !filtros.getCodigoMedioPago().isEmpty()) {
				String codigoMedio = filtros.getCodigoMedioPago();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("medioPago"), codigoMedio));
			}
			if (filtros.getRecibido() != null) {
				if (filtros.getRecibido()) {
					spec = spec.and((root, query, cb) -> cb.isNotNull(root.get("fechaRecepcion")));
				} else {
					spec = spec.and((root, query, cb) -> cb.isNull(root.get("fechaRecepcion")));
				}
			}
			if (filtros.getDevuelto() != null) {
				if (filtros.getDevuelto()) {
					spec = spec.and((root, query, cb) -> cb.isTrue(root.get("devuelto")));
				} else {
					spec = spec.and((root, query, cb) -> cb.isFalse(root.get("devuelto")));
				}
			}
			if (filtros.getCosteUnitarioMin() != null && filtros.getCosteUnitarioMin() != 0) {
				Double costeUnMin = filtros.getCosteUnitarioMin();
				spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("costeUnitario"), costeUnMin));
			}
			if (filtros.getCosteUnitarioMax() != null && filtros.getCosteUnitarioMax() != 0) {
				Double costeUnMax = filtros.getCosteUnitarioMax();
				spec = spec.and((root, query, cb) -> cb.or(cb.isNull(root.get("costeUnitario")),
						cb.lessThanOrEqualTo(root.get("costeUnitario"), costeUnMax)));
			}
			if (filtros.getFechaInicioIntervalo() != null) {
				LocalDate fechaInicioIntervalo = filtros.getFechaInicioIntervalo();
				spec = spec.and(
						(root, query, cb) -> cb.greaterThanOrEqualTo(root.get("fechaPedido"), fechaInicioIntervalo));
			}
			if (filtros.getFechaFinIntervalo() != null) {
				LocalDate fechaFinIntervalo = filtros.getFechaFinIntervalo();
				spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("fechaPedido"), fechaFinIntervalo));
			}
		}

		Sort sort = Sort.by(Sort.Direction.DESC, "fechaPedido");
		List<PedidoVWEntity> listaPedidoEntity = this.pedidoVistaRepository.findAll(spec, sort);

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet hoja = workbook.createSheet("Pedidos");

		XSSFCellStyle headerStyle = headerStyle(workbook);

		String[] encabezados = { "Fecha de pedido", "Fecha de recepción", "IVA (%)", "Coste total (€)",
				"Número de unidades", "Coste unitario (€)", "Plazo de entrega", "Costes de envío (€)",
				"Condición de pago", "Medio de pago", "Devuelto", "Realizado por", "Oficina de recepción",
				"Proveedor" };

		int indiceFila = 0;
		XSSFRow fila = hoja.createRow(indiceFila);

		for (int i = 0; i < encabezados.length; i++) {
			String encabezado = encabezados[i];
			XSSFCell celda = fila.createCell(i);
			celda.setCellValue(encabezado);
			celda.setCellStyle(headerStyle);
		}

		HashMap<String, XSSFCellStyle> styles = new HashMap<>();
		styles.put("HEADER", headerStyle);

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// Formato de fecha
		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));

		EmpleadoDto empleado;
		OficinaDto oficina;
		ProveedorDto proveedor;

		indiceFila++;
		for (PedidoVWEntity pedido : listaPedidoEntity) {

			empleado = this.empleadoProvider.getEmpleadoById(pedido.getIdEmpleado()).getMessage();
			oficina = this.oficinaProvider.getOficinaById(pedido.getIdOficina()).getMessage();
			proveedor = this.proveedorProvider.getProveedorById(pedido.getIdProveedor()).getMessage();

			fila = hoja.createRow(indiceFila);

			fila.createCell(0).setCellValue(pedido.getFechaPedido());
			fila.getCell(0).setCellStyle(dateCellStyle);

			if (pedido.getFechaRecepcion() != null) {
				fila.createCell(1).setCellValue(pedido.getFechaRecepcion());
				fila.getCell(1).setCellStyle(dateCellStyle);
			} else {
				fila.createCell(1).setCellValue("-");
			}
			fila.createCell(2).setCellValue(pedido.getIvaPedido());
			fila.createCell(3).setCellValue(pedido.getCosteTotal());

			if (pedido.getNumeroUnidades() != null) {
				fila.createCell(4).setCellValue(pedido.getNumeroUnidades());
			} else {
				fila.createCell(4).setCellValue("-");
			}

			if (pedido.getCosteUnitario() != null) {
				fila.createCell(5).setCellValue(String.format("%.2f", pedido.getCosteUnitario()));
			} else {
				fila.createCell(5).setCellValue("-");
			}

			fila.createCell(6).setCellValue(pedido.getPlazoEntrega());
			fila.createCell(7).setCellValue(pedido.getCostesEnvio());
			fila.createCell(8).setCellValue(pedido.getCondicionPago());
			fila.createCell(9).setCellValue(pedido.getMedioPago());

			if (pedido.getDevuelto() != null && pedido.getDevuelto() == true) {
				fila.createCell(10).setCellValue("SI");
			} else {
				fila.createCell(10).setCellValue("NO");
			}

			String nomEm = empleado.getDni() + ": " + empleado.getNombre() + " " + empleado.getApellidos();
			fila.createCell(11).setCellValue(nomEm);

			String dirOficina = oficina.getDireccion() + ", " + oficina.getCodigoPostal() + ", "
					+ oficina.getLocalidad() + ", " + oficina.getPais();
			fila.createCell(12).setCellValue(dirOficina);

			String prov = proveedor.getRazonSocial() + ": " + proveedor.getCif();
			fila.createCell(13).setCellValue(prov);
			indiceFila++;
		}

		for (int i = 0; i < encabezados.length; i++) {
			hoja.autoSizeColumn(i);
			hoja.setDefaultColumnStyle(i, cellStyle);
		}

		// Convertir el workbook a bytes
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		byte[] bytes = outputStream.toByteArray();
		outputStream.close();
		workbook.close();

		return bytes;
	}

	XSSFCellStyle headerStyle(XSSFWorkbook workbook) {
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		XSSFFont headerFont = workbook.createFont();
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}

	@Override
	public byte[] descargarExcelPedidoById(Integer id) throws IOException {

		MessageResponseDto<PedidoDto> pedidoMSG = this.getPedidoById(id);
		if (!pedidoMSG.isSuccess()) {
			throw new IOException("No se ha encontrado el pedido");
		}
		PedidoDto pedido = pedidoMSG.getMessage();

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet hoja = workbook.createSheet("Pedido " + id);

		XSSFCellStyle headerStyle = headerStyle(workbook);

		String[] encabezados = { "Fecha de pedido", "Fecha de recepción", "IVA (%)", "Coste total (€)",
				"Número de unidades", "Coste unitario (€)", "Plazo de entrega", "Costes de envío (€)",
				"Condición de pago", "Medio de pago", "Devuelto", "Realizado por", "Oficina de recepción",
				"Proveedor" };

		int indiceFila = 0;
		XSSFRow fila = hoja.createRow(indiceFila);

		for (int i = 0; i < encabezados.length; i++) {
			String encabezado = encabezados[i];
			XSSFCell celda = fila.createCell(i);
			celda.setCellValue(encabezado);
			celda.setCellStyle(headerStyle);
		}

		HashMap<String, XSSFCellStyle> styles = new HashMap<>();
		styles.put("HEADER", headerStyle);

		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		// Formato de fecha
		CreationHelper creationHelper = workbook.getCreationHelper();
		CellStyle dateCellStyle = workbook.createCellStyle();
		dateCellStyle.setDataFormat(creationHelper.createDataFormat().getFormat("dd/mm/yyyy"));

		indiceFila++;

		fila = hoja.createRow(indiceFila);

		fila.createCell(0).setCellValue(pedido.getFechaPedido());
		fila.getCell(0).setCellStyle(dateCellStyle);

		if (pedido.getFechaRecepcion() != null) {
			fila.createCell(1).setCellValue(pedido.getFechaRecepcion());
			fila.getCell(1).setCellStyle(dateCellStyle);
		} else {
			fila.createCell(1).setCellValue("-");
		}
		fila.createCell(2).setCellValue(pedido.getIvaPedido());
		fila.createCell(3).setCellValue(pedido.getCosteTotal());

		if (pedido.getNumeroUnidades() != null) {
			fila.createCell(4).setCellValue(pedido.getNumeroUnidades());
		} else {
			fila.createCell(4).setCellValue("-");
		}

		if (pedido.getCosteUnitario() != null) {
			fila.createCell(5).setCellValue(String.format("%.2f", pedido.getCosteUnitario()));
		} else {
			fila.createCell(5).setCellValue("-");
		}

		fila.createCell(6).setCellValue(pedido.getPlazoEntrega());
		fila.createCell(7).setCellValue(pedido.getCostesEnvio());
		fila.createCell(8).setCellValue(pedido.getCondicionPago());
		fila.createCell(9).setCellValue(pedido.getMedioPago());

		if (pedido.getDevuelto() != null && pedido.getDevuelto() == true) {
			fila.createCell(10).setCellValue("SI");
		} else {
			fila.createCell(10).setCellValue("NO");
		}

		String nomEm = pedido.getEmpleado().getDni() + ": " + pedido.getEmpleado().getNombre() + " "
				+ pedido.getEmpleado().getApellidos();
		fila.createCell(11).setCellValue(nomEm);
		String dirOficina;
		if (pedido.getOficina().getCodigoPostal() != null) {
			dirOficina = pedido.getOficina().getDireccion() + ", " + pedido.getOficina().getCodigoPostal() + ", "
					+ pedido.getOficina().getLocalidad() + ", " + pedido.getOficina().getPais();

		} else {
			dirOficina = pedido.getOficina().getDireccion() + ", " + pedido.getOficina().getLocalidad() + ", "
					+ pedido.getOficina().getPais();

		}
		fila.createCell(12).setCellValue(dirOficina);

		String prov = pedido.getProveedor().getRazonSocial() + ": " + pedido.getProveedor().getCif();
		fila.createCell(13).setCellValue(prov);

		indiceFila++;
		indiceFila++;

		String[] encabezadosLinea = { "Número de línea", "Número de unidades", "Precio de línea (€)", "Descuento (%)",
				"Referencia artículo", "Descripción artículo", "Precio artículo (€)", "IVA artículo (%)", "Categoría",
				"Subcategoría", "Fabricante", "Modelo" };

		fila = hoja.createRow(indiceFila);
		XSSFCellStyle headerStyleLinea = headerStyleLinea(workbook);

		for (int i = 0; i < encabezadosLinea.length; i++) {
			String encabezadoL = encabezadosLinea[i];
			XSSFCell celda = fila.createCell(i);
			celda.setCellValue(encabezadoL);
			celda.setCellStyle(headerStyleLinea);
		}

		indiceFila++;

		for (LineaDto linea : pedido.getLineas()) {

			fila = hoja.createRow(indiceFila);

			fila.createCell(0).setCellValue(linea.getNumeroLinea());
			fila.createCell(1).setCellValue(linea.getNumeroUnidades());
			fila.createCell(2).setCellValue(linea.getPrecioLinea());
			fila.createCell(3).setCellValue(linea.getDescuento());
			fila.createCell(4).setCellValue(linea.getArticulo().getReferencia());
			fila.createCell(5).setCellValue(linea.getArticulo().getDescripcion());
			fila.createCell(6).setCellValue(linea.getArticulo().getPrecioUnitario());
			fila.createCell(7).setCellValue(linea.getArticulo().getIva());
			fila.createCell(8).setCellValue(linea.getArticulo().getCodCategoria());
			fila.createCell(9).setCellValue(linea.getArticulo().getCodSubcategoria());
			fila.createCell(10).setCellValue(linea.getArticulo().getFabricante());
			fila.createCell(11).setCellValue(linea.getArticulo().getModelo());

			indiceFila++;
		}

		for (int i = 0; i < encabezados.length; i++) {
			hoja.autoSizeColumn(i);
			hoja.setDefaultColumnStyle(i, cellStyle);
		}

		// Convertir el workbook a bytes
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		byte[] bytes = outputStream.toByteArray();
		outputStream.close();
		workbook.close();

		return bytes;
	}

	XSSFCellStyle headerStyleLinea(XSSFWorkbook workbook) {
		XSSFCellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		XSSFFont headerFont = workbook.createFont();
		headerFont.setColor(IndexedColors.BLACK.getIndex());
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		return headerStyle;
	}

	@Override
	public void generarPDFById(Integer id) {
		try {
			// Cargo el archivo compilado JasperReport
			JasperReport jasperReport = (JasperReport) JRLoader
					.loadObjectFromFile("src/main/resources/jasper/ReportePedido.jasper");

			// Cargo datos
			MessageResponseDto<PedidoDto> pedidoMSG = this.getPedidoById(id);
			if (!pedidoMSG.isSuccess()) {
				throw new IOException("No se ha encontrado el pedido");
			}
			PedidoDto pedido = pedidoMSG.getMessage();
			String nomEm = pedido.getEmpleado().getDni() + ": " + pedido.getEmpleado().getNombre() + " "
					+ pedido.getEmpleado().getApellidos();

			String dirOficina;
			if (pedido.getOficina().getCodigoPostal() != null) {
				dirOficina = pedido.getOficina().getDireccion() + ", " + pedido.getOficina().getCodigoPostal() + ", "
						+ pedido.getOficina().getLocalidad() + ", " + pedido.getOficina().getPais();
			} else {
				dirOficina = pedido.getOficina().getDireccion() + ", " + pedido.getOficina().getLocalidad() + ", "
						+ pedido.getOficina().getPais();
			}
			String prov = pedido.getProveedor().getRazonSocial() + ": " + pedido.getProveedor().getCif();
		
			
			Map<String, Object> parametros = new HashMap<>();
			parametros.put("logoEmpresa", new FileInputStream("src/main/resources/images/hiberus.jpg"));
			parametros.put("imagenAlternativa", new FileInputStream("src/main/resources/images/hiberus.jpg"));
			parametros.put("empleado", nomEm);
			parametros.put("oficina", dirOficina);
			parametros.put("proveedor", prov);
			parametros.put("fechaPedido", pedido.getFechaPedido());
			
			if(pedido.getFechaRecepcion()!=null) {
				parametros.put("fechaRecepcion", pedido.getFechaRecepcion());
			}else {
				parametros.put("fechaRecepcion", null);
			}
			
			if(pedido.getDevuelto()!=null && pedido.getDevuelto() == true) {
				parametros.put("devuelto", "DEVUELTO");
			}else {
				parametros.put("devuelto", "");
			}
			
			
			parametros.put("costeUnitario", pedido.getCosteUnitario());
			parametros.put("costesEnvio", pedido.getCostesEnvio());

			List<Hashtable<String, Object>> listadoLienas= new ArrayList<>();
			for (LineaDto linea : pedido.getLineas()) {

				Hashtable<String, Object> hash = new Hashtable<>();

				hash.put("linea", linea.getNumeroLinea());
				hash.put("unidades", linea.getNumeroUnidades());
				hash.put("articulo", linea.getArticulo().getReferencia()+": "+ linea.getArticulo().getDescripcion());
				hash.put("precioUnitario", linea.getArticulo().getPrecioUnitario());
				hash.put("descuento", linea.getDescuento());
				hash.put("precioTotal", linea.getPrecioLinea());
				
				listadoLienas.add(hash);
			}
			parametros.put("listaLineas", listadoLienas);

			// Llenar el informe con datos y parámetros
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());

			// Exportar el informe a un archivo PDF
			JasperExportManager.exportReportToPdfFile(jasperPrint, "reportPedido.pdf");
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
