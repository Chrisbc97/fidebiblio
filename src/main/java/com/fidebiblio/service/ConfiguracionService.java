package com.fidebiblio.service;

import com.fidebiblio.domain.Configuracion;
import com.fidebiblio.repository.ConfiguracionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionService {

    private final ConfiguracionRepository configuracionRepository;

    public ConfiguracionService(ConfiguracionRepository configuracionRepository) {
        this.configuracionRepository = configuracionRepository;
    }

    @Transactional(readOnly = true)
    public List<Configuracion> getConfiguraciones() {
        return configuracionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Configuracion getConfiguracion(Integer idConfiguracion) {
        return configuracionRepository.findById(idConfiguracion)
                .orElseThrow(() -> new NoSuchElementException("Configuración no encontrada"));
    }

    // Valor entero de un parámetro, con un valor de respaldo por si no existe
    @Transactional(readOnly = true)
    public int getValorEntero(String atributo, int valorPorDefecto) {
        return configuracionRepository.findByAtributo(atributo)
                .map(c -> Integer.parseInt(c.getValor()))
                .orElse(valorPorDefecto);
    }

    // Se ctualiza un parámetro existente
    @Transactional
    public void actualizar(Integer idConfiguracion, String valor) {
        Configuracion configuracion = configuracionRepository.findById(idConfiguracion)
                .orElseThrow(() -> new NoSuchElementException("Configuración no encontrada"));
        configuracion.setValor(valor);
        configuracion.setFechaModificacion(LocalDateTime.now());
        configuracionRepository.save(configuracion);
    }
}