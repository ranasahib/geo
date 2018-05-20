package com.rd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rd.domain.LayerProperty;

@Repository
public interface LayerPropertyRepository extends JpaRepository<LayerProperty, Long> {


}