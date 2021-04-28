package com.aqstore.service.account.service;

import com.aqstore.service.openapi.model.ApiAddressDto;
import com.aqstore.service.openapi.model.ApiUserDto;

public interface AccountInfoService {

	ApiUserDto getUserInfo();

	ApiUserDto addAddress(ApiAddressDto apiAddressDto);


}
