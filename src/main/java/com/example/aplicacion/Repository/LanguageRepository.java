  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.Language;
  import org.springframework.data.jpa.repository.JpaRepository;

  import java.util.List;

  public interface LanguageRepository extends JpaRepository<Language, Long> {


      Language findLanguageByNombreLenguaje(String id);
      List<Language> findAll();
      Language findLanguageById(Long id);

  }
