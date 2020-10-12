  package com.example.aplicacion.Repository;


  import com.example.aplicacion.Entities.LanguageAPI;
  import org.springframework.data.jpa.repository.JpaRepository;

  import java.util.List;

  public interface LanguageRepository extends JpaRepository<LanguageAPI, Long> {


      LanguageAPI findLanguageByNombreLenguaje(String id);
      List<LanguageAPI> findAll();
      LanguageAPI findLanguageById(Long id);

  }
