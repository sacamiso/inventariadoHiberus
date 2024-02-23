package com.tfg.inventariado.configModelMapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfigura {

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
}
