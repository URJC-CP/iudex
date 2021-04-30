package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {

}
