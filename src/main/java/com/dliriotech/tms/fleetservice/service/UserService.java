package com.dliriotech.tms.fleetservice.service;

import com.dliriotech.tms.fleetservice.dto.UserInfoResponse;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<UserInfoResponse> getUserInfoById(Integer userId);
}