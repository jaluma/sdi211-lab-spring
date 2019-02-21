package com.uniovi.repositories;

import com.uniovi.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UsersRepository extends CrudRepository<User, Long> {

	User findByDni(String dni);

	@Query("SELECT s FROM User s ORDER BY s.id ASC")
	Page<User> findAll(Pageable pageable);

	@Query("SELECT s FROM User s WHERE LOWER(s.dni) LIKE LOWER(?1) OR LOWER(s.name) LIKE LOWER(?1) OR LOWER(s.lastName) LIKE LOWER(?1) ORDER BY s.id ASC")
	Page<User> findAllByDniAndFullName(Pageable pageable, String searchText);
}