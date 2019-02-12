package com.uniovi.repositories;

import com.uniovi.entities.Mark;
import org.springframework.data.repository.CrudRepository;

public interface MarksRepository extends CrudRepository<Mark, Long> {
}