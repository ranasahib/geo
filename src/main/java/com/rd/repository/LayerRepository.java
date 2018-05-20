package com.rd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.rd.domain.Layer;

@Repository
public interface LayerRepository extends JpaRepository<Layer, Long> {

	
	List<Layer> findByDatasetIdInAndStatus(List<String> dataSetIds, int i);
	
	Layer findByDatasetIdAndStatus(String dataSetIds, int i);
	

}