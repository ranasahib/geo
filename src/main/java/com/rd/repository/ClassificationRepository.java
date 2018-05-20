package com.rd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rd.domain.Classfication;

public interface ClassificationRepository extends JpaRepository<Classfication, Long> {

	List<Classfication> findByTypeAndStatus(String type, int i);

	List<Classfication> findByIdInAndStatus(List<Long> classificationIds,int i);

}