package com.avispa.ecm.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.UUID;

/**
 * @author Rafał Hiszpański
 */
@NoRepositoryBean
public interface EcmEntityRepository<T extends EcmEntity> extends JpaRepository<T, UUID> {
}
