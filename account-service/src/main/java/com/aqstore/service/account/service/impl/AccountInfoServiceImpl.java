package com.aqstore.service.account.service.impl;

import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import com.aqstore.service.account.AccountExceptionType;
import com.aqstore.service.account.mapper.AddressMapper;
import com.aqstore.service.account.persistence.AddressRepository;
import com.aqstore.service.account.persistence.entity.Address;
import com.aqstore.service.account.service.AccountInfoService;
import com.aqstore.service.exception.AQStoreExceptionHandler;
import com.aqstore.service.openapi.model.ApiAddressDto;
import com.aqstore.service.openapi.model.ApiUserDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountInfoServiceImpl implements AccountInfoService {
	private final AddressRepository repository;
	private final AddressMapper mapper;
	
	
	
	@Override
	public ApiUserDto getUserInfo() {
		log.info("Retrieve userinfo");
		ApiUserDto dto = getAuthenticationInfo();
		Optional<Address> address=repository.findById(dto.getUsername());
		ApiAddressDto addressDto = mapper.toDTO(address.orElse(null));
		dto.setAddress(addressDto);
		return dto;
	}
	@Override
	public ApiUserDto addAddress(ApiAddressDto apiAddressDto) {
		log.info("Update user address");
		ApiUserDto userDto = getAuthenticationInfo();
		Address entity = mapper.toEntity(apiAddressDto, userDto.getUsername(),userDto.getFullName());
		Address dbresponse=  repository.save(entity);
		ApiAddressDto addressResponseDto = mapper.toDTO(dbresponse);
		userDto.setAddress(addressResponseDto);
		return userDto;
	}


	
	
	
	private ApiUserDto getAuthenticationInfo() {
		JwtAuthenticationToken authentication=(JwtAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(!authentication.isAuthenticated()) {
			throw AQStoreExceptionHandler.handleException(AccountExceptionType.UNAUTHORIZED_ROLE);
		}
		String userName = authentication.getName();
		String firstName = authentication.getToken().getClaimAsString("given_name");
		String lastName = authentication.getToken().getClaimAsString("family_name");
		String email = authentication.getToken().getClaimAsString("email");
		ApiUserDto dto = new ApiUserDto();
		dto.setEmail(email);
		dto.setFullName(firstName.concat(" ").concat(lastName));
		dto.setUsername(userName);
		dto.setName(firstName);
		dto.setLastName(lastName);
		return dto;
		
	}
	
	

}
