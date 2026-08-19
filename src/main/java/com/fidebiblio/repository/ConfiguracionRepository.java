package com.fidebiblio.repository;

import com.fidebiblio.domain.Configuracion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Integer> {

    public Optional<Configuracion> findByAtributo(String atributo);
}