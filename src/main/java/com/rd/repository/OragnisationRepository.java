package com.rd.repository;

import com.rd.domain.Organisation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OragnisationRepository extends JpaRepository<Organisation, String> {

	List<Organisation> findByStatus(int i);

	List<Organisation> findByStatusOrderByIdDesc(int i);

	Organisation findByIdAndStatus(Long id, int i);

    

}