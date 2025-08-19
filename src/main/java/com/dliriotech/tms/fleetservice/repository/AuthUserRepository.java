package com.dliriotech.tms.fleetservice.repository;

import com.dliriotech.tms.fleetservice.entity.AuthUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AuthUserRepository extends ReactiveCrudRepository<AuthUser, Integer> {
}