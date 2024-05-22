package com.tfg.inventariado.providerImpl;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tfg.inventariado.dto.AuthRequestDto;
import com.tfg.inventariado.dto.AuthResponseDto;
import com.tfg.inventariado.dto.EmpleadoCambioContrasena;
import com.tfg.inventariado.dto.EmpleadoDto;
import com.tfg.inventariado.dto.EmpleadoFilterDto;
import com.tfg.inventariado.dto.MessageResponseDto;
import com.tfg.inventariado.dto.MessageResponseListDto;
import com.tfg.inventariado.entity.EmpleadoEntity;
import com.tfg.inventariado.provider.EmpleadoProvider;
import com.tfg.inventariado.provider.JwtProvider;
import com.tfg.inventariado.provider.OficinaProvider;
import com.tfg.inventariado.provider.RolProvider;
import com.tfg.inventariado.repository.EmpleadoRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmpleadoProviderImpl implements EmpleadoProvider {

	@Autowired
	private EmpleadoRepository empleadoRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private OficinaProvider oficinaProvider;

	@Autowired
	private RolProvider rolProvider;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
    private MessageSource messageSource;

	@Override
	public EmpleadoDto convertToMapDto(EmpleadoEntity empleado) {
		return modelMapper.map(empleado, EmpleadoDto.class);
	}

	@Override
	public EmpleadoEntity convertToMapEntity(EmpleadoDto empleado) {
		return modelMapper.map(empleado, EmpleadoEntity.class);
	}

	@Override
	public List<EmpleadoDto> listAllEmpleado() {
		List<EmpleadoEntity> listaEntity = empleadoRepository.findAll();
		return listaEntity.stream()
				.sorted(Comparator.comparing(EmpleadoEntity::getNombre, String.CASE_INSENSITIVE_ORDER))
				.map(this::convertToMapDto).collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto<Integer> addEmpleado(EmpleadoDto empleado) {
		Locale locale = LocaleContextHolder.getLocale();
		if (empleado.getApellidos() == null || empleado.getApellidos().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("apellObl", null, locale));
		}
		if (empleado.getUsuario() == null || empleado.getUsuario().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("usuarioObl", null, locale));
		}
		if (empleado.getContraseña() == null || empleado.getContraseña().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("contraObl", null, locale));
		}
		if (empleado.getCodRol() == null || empleado.getCodRol().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("rolObl", null, locale));
		}
		if (!this.rolProvider.rolExisteByCodigo(empleado.getCodRol())) {
			return MessageResponseDto.fail(messageSource.getMessage("rolNoExiste", null, locale));
		}
		if (empleado.getCodRol() != null && !empleado.getCodRol().isEmpty()
				&& !this.oficinaProvider.oficinaExisteByID(empleado.getIdOficina())) {
			return MessageResponseDto.fail(messageSource.getMessage("oficinaNoExiste", null, locale));
		}
		if (this.empleadoExisteByUsuario(empleado.getUsuario())) {
			return MessageResponseDto.fail(messageSource.getMessage("usuarieExiste", null, locale));
		}
		if (this.empleadoRepository.findByDni(empleado.getDni()).isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("dniUso", null, locale));
		}
		if (empleado.getNombre() == null || empleado.getNombre().isEmpty()) {
			return MessageResponseDto.fail(messageSource.getMessage("nombreObl", null, locale));
		}
		EmpleadoEntity newEmpleado = convertToMapEntity(empleado);
		
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		newEmpleado.setContraseña(passwordEncoder.encode(newEmpleado.getContraseña()));
		
		newEmpleado = empleadoRepository.save(newEmpleado);
		return MessageResponseDto.success(newEmpleado.getIdEmpleado());
	}

	@Override
	public MessageResponseDto<String> editEmpleado(EmpleadoDto empleado, Integer id) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<EmpleadoEntity> optionalEmpleado = empleadoRepository.findById(id);
		if (optionalEmpleado.isPresent()) {
			EmpleadoEntity empleadoToUpdate = optionalEmpleado.get();

			MessageResponseDto<String> respuesta = this.actualizarCampos(empleadoToUpdate, empleado);
			
			if(respuesta.isSuccess()) {
				empleadoRepository.save(empleadoToUpdate);
			}

			return respuesta;

		} else {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoNoExiste", null, locale));
		}
	}

	private MessageResponseDto<String> actualizarCampos(EmpleadoEntity empleado, EmpleadoDto empleadoToUpdate) {

		Locale locale = LocaleContextHolder.getLocale();
		
		String patronDNI = "\\d{8}[A-HJ-NP-TV-Z]";
		Pattern pattern = Pattern.compile(patronDNI);
		Matcher matcher = pattern.matcher(empleadoToUpdate.getDni());

		if (!matcher.matches()) {
			return MessageResponseDto.fail(messageSource.getMessage("dniInvalido", null, locale));
		}
		
		if (this.empleadoRepository.findByDni(empleadoToUpdate.getDni()).isPresent() && !empleado.getDni().equals(empleadoToUpdate.getDni())) {
			return MessageResponseDto.fail(messageSource.getMessage("dniUso", null, locale));
		}
		
		if (this.empleadoExisteByUsuario(empleadoToUpdate.getUsuario()) && !empleado.getUsuario().equals(empleadoToUpdate.getUsuario())) {
			return MessageResponseDto.fail(messageSource.getMessage("usuarieExiste", null, locale));
		}
		
		if (matcher.matches() && !this.empleadoRepository.findByDni(empleadoToUpdate.getDni()).isPresent()) {
			empleado.setDni(empleadoToUpdate.getDni());
		}
		if (StringUtils.isNotBlank(empleadoToUpdate.getNombre())) {
			empleado.setNombre(empleadoToUpdate.getNombre());
		}
		if (StringUtils.isNotBlank(empleadoToUpdate.getApellidos())) {
			empleado.setApellidos(empleadoToUpdate.getApellidos());
		}
		if (StringUtils.isNotBlank(empleadoToUpdate.getCorreo())) {
			empleado.setCorreo(empleadoToUpdate.getCorreo());
		}
		if (!this.empleadoExisteByUsuario(empleadoToUpdate.getUsuario())
				&& StringUtils.isNotBlank(empleadoToUpdate.getUsuario())) {
			empleado.setUsuario(empleadoToUpdate.getUsuario());
		}
		if (StringUtils.isNotBlank(empleadoToUpdate.getContraseña())) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			empleado.setContraseña(passwordEncoder.encode(empleadoToUpdate.getContraseña()));
		}
		if (this.rolProvider.rolExisteByCodigo(empleadoToUpdate.getCodRol())) {
			empleado.setCodRol(empleadoToUpdate.getCodRol());
		}
		if (this.oficinaProvider.oficinaExisteByID(empleadoToUpdate.getIdOficina())) {
			empleado.setIdOficina(empleadoToUpdate.getIdOficina());
		}
		
		return MessageResponseDto.success(messageSource.getMessage("empleadoEditado", null, locale));
	}

	@Override
	public MessageResponseDto<EmpleadoDto> getEmpleadoById(Integer id) {
		Locale locale = LocaleContextHolder.getLocale();
		Optional<EmpleadoEntity> optionalEmpleado = this.empleadoRepository.findById(id);
		if (optionalEmpleado.isPresent()) {
			EmpleadoDto empleadoDto = this.convertToMapDto(optionalEmpleado.get());
			return MessageResponseDto.success(empleadoDto);
		} else {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoNoExiste", null, locale));
		}
	}

	@Override
	public MessageResponseDto<List<EmpleadoDto>> listEmpleadosByOficina(Integer idOficina) {
		Locale locale = LocaleContextHolder.getLocale();
		if (!this.oficinaProvider.oficinaExisteByID(idOficina)) {
			return MessageResponseDto.fail(messageSource.getMessage("oficinaNoExiste", null, locale));
		}
		List<EmpleadoEntity> listaEntity = this.empleadoRepository.findByIdOficina(idOficina);
		List<EmpleadoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public MessageResponseDto<List<EmpleadoDto>> listEmpleadosByRol(String codRol) {
		Locale locale = LocaleContextHolder.getLocale();
		if (!this.rolProvider.rolExisteByCodigo(codRol)) {
			return MessageResponseDto.fail(messageSource.getMessage("rolNoExiste", null, locale));
		}
		List<EmpleadoEntity> listaEntity = this.empleadoRepository.findByCodRol(codRol);
		List<EmpleadoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());
		return MessageResponseDto.success(listaDto);
	}

	@Override
	public boolean empleadoExisteByCodigo(Integer id) {
		Optional<EmpleadoEntity> optionalEmpleado = this.empleadoRepository.findById(id);
		return optionalEmpleado.isPresent() ? true : false;
	}

	@Override
	public boolean empleadoExisteByUsuario(String usuario) {
		Optional<EmpleadoEntity> listEmpleado = empleadoRepository.findByUsuario(usuario);
		return listEmpleado.isPresent();
	}

	@Override
	public MessageResponseListDto<List<EmpleadoDto>> listAllEmpleadosSkipLimit(Integer page, Integer size,
			EmpleadoFilterDto filtros) {
		Specification<EmpleadoEntity> spec = Specification.where(null);

		if (filtros != null) {
			if (filtros.getDni() != null) {
				String dni = filtros.getDni();
				spec = spec.and((root, query, cb) -> cb.like(root.get("dni"), "%" + dni + "%"));
			}
			if (filtros.getNombre() != null) {
				String nombre = filtros.getNombre();
				spec = spec.and((root, query, cb) -> cb.like(root.get("nombre"), "%" + nombre + "%"));
			}
			if (filtros.getApellidos() != null) {
				String apell = filtros.getApellidos();
				spec = spec.and((root, query, cb) -> cb.like(root.get("apellidos"), "%" + apell + "%"));
			}
			if (filtros.getUsuario() != null) {
				String usu = filtros.getUsuario();
				spec = spec.and((root, query, cb) -> cb.like(root.get("usuario"), "%" + usu + "%"));
			}
			if (filtros.getCodRol() != null) {
				String rol = filtros.getCodRol();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("codRol"), rol));
			}
			if (filtros.getIdOficina() != null) {
				Integer idOf = filtros.getIdOficina();
				spec = spec.and((root, query, cb) -> cb.equal(root.get("idOficina"), idOf));
			}
		}

		PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "dni"));
		Page<EmpleadoEntity> pageableProveedor = this.empleadoRepository.findAll(spec, pageable);

		List<EmpleadoEntity> listaEntity = pageableProveedor.getContent();
		List<EmpleadoDto> listaDto = listaEntity.stream().map(this::convertToMapDto).collect(Collectors.toList());

		return MessageResponseListDto.success(listaDto, page, size, (int) this.empleadoRepository.count(spec));
	}

	@Override
	public AuthResponseDto login(@Valid AuthRequestDto authRequest) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				authRequest.getUsername(), authRequest.getPass());

		authenticationManager.authenticate(authToken);

		Optional<EmpleadoEntity> usuario = empleadoRepository.findByUsuario(authRequest.getUsername());
		

		if (!usuario.isPresent()) {
			log.info("USER_NOT_FOUND");
		}

		String jwt = jwtProvider.generateToken(usuario.get(), generateExtraClaims(usuario.get()));

		return new AuthResponseDto(jwt);
	}

	private Map<String, Object> generateExtraClaims(EmpleadoEntity usuarioEntity) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("rol", usuarioEntity.getCodRol());
		extraClaims.put("id", usuarioEntity.getIdEmpleado());
		return extraClaims;
	}

	@Override
	public MessageResponseDto<String> editContrasenaEmpleado(EmpleadoCambioContrasena empleado) {
		Locale locale = LocaleContextHolder.getLocale();
		if(empleado.getContraNueva().equals(empleado.getContraAct())) {
			return MessageResponseDto.fail(messageSource.getMessage("contrasenaRepit", null, locale));
		}
		
		Optional<EmpleadoEntity> optionalEmpleado = empleadoRepository.findById(empleado.getEmpleado().getIdEmpleado());
		if (!optionalEmpleado.isPresent()) {
			return MessageResponseDto.fail(messageSource.getMessage("empleadoNoExiste", null, locale));
		}
		
		
		EmpleadoEntity empleadoBD = optionalEmpleado.get();

		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

		if (!passwordEncoder.matches(empleado.getContraAct(), empleadoBD.getContraseña())) {
	        return MessageResponseDto.fail(messageSource.getMessage("contrasenaIncorrecta", null, locale));
	    }

		empleadoBD.setContraseña(passwordEncoder.encode(empleado.getContraNueva()));
		
		
		empleadoRepository.save(empleadoBD);

		return MessageResponseDto.success(messageSource.getMessage("contrasenaAct", null, locale));

	}
	
}
