package com.rd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rd.domain.Harvest;

public interface HarvestRepository extends JpaRepository<Harvest, Long> {

	List<Harvest> findByOrganisationIdAndStatus(String organisationRegistryId, int i);

	Harvest findByIdAndStatus(Long harvestId, int i);

	List<Harvest> findByOrganisationIdAndObjectTypeAndStatus(String organisationRegistryId, String string, int i);

	List<Harvest> findByHarvestIdAndStatus(String harvestId, int i);

	Harvest findByDataIdAndStatus(String resultId, int i);
    
}