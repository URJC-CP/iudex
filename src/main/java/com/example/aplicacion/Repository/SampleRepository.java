package com.example.aplicacion.Repository;

import com.example.aplicacion.Entities.Sample;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SampleRepository extends JpaRepository<Sample, Long> {
    boolean existsSampleById(Long id);

    Optional<Sample> findSampleById(Long aLong);
}
