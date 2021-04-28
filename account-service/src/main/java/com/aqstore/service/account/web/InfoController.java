package com.aqstore.service.account.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aqstore.service.account.AccountConstants;
import com.aqstore.service.account.service.AccountInfoService;
import com.aqstore.service.openapi.InfoApi;
import com.aqstore.service.openapi.model.ApiAddressDto;
import com.aqstore.service.openapi.model.ApiUserDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(AccountConstants.ACCOUNT_V1_PREFIX)
@RequiredArgsConstructor
public class InfoController implements InfoApi {
	private final AccountInfoService service;

	@Override
	public ResponseEntity<ApiUserDto> whoami() {
		ApiUserDto response = service.getUserInfo();
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<ApiUserDto> addAddress(ApiAddressDto apiAddressDto) {
		ApiUserDto response = service.addAddress(apiAddressDto);
		return ResponseEntity.ok(response);
	}
}
