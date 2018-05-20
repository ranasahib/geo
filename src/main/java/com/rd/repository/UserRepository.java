package com.rd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rd.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {


	public List<User> findByRegistryOrganisationIdAndStatus(String id, int i);

	public User findByIdAndStatus(Long serviceId, int i);

	public User findByLoginNameAndStatus(String username, int i);

	public List<User> findByRegistryOrganisationIdAndStatusIn(String organisationRegistryId, List<Integer> status);

	public User findByLoginNameAndStatusIn(String username, List<Integer> statuses);

	public List<User> findByIdInAndStatus(List<Long> userIds, int i);


    

}