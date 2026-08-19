package com.fidebiblio.service;

import com.fidebiblio.domain.Multa;
import com.fidebiblio.domain.Prestamo;
import com.fidebiblio.repository.MultaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MultaService {

    // Monto de multa diaria 
    private static final int MONTO_MULTA_DIARIA = 500;

    private final MultaRepository multaRepository;
    private final ConfiguracionService configuracionService;
    private final NotificacionService notificacionService;

    public MultaService(MultaRepository multaRepository, ConfiguracionService configuracionService,
            NotificacionService notificacionService) {
        this.multaRepository = multaRepository;
        this.configuracionService = configuracionService;
        this.notificacionService = notificacionService;
    }

    @Transactional(readOnly = true)
    public List<Multa> getMultasPorUsuario(Integer idUsuario) {
        return multaRepository.buscarPorUsuario(idUsuario);
    }

    @Transactional(readOnly = true)
    public List<Multa> getMultasPendientes() {
        return multaRepository.findByEstadoOrderByFechaGeneracionAsc("PENDIENTE");
    }

    // Genera una multa si el préstamo se devuelve con atraso
    @Transactional
    public void generarSiCorresponde(Prestamo prestamo) {
        LocalDate fechaLimite = prestamo.getFechaLimite();
        long diasAtraso = ChronoUnit.DAYS.between(fechaLimite, LocalDate.now());

        if (diasAtraso <= 0) {
            return;
        }

        if (multaRepository.findByPrestamo_IdPrestamo(prestamo.getIdPrestamo()).isPresent()) {
            return;
        }

        int montoDiario = configuracionService.getValorEntero("monto_multa_diaria", MONTO_MULTA_DIARIA);

        Multa multa = new Multa();
        multa.setPrestamo(prestamo);
        multa.setDiasAtraso((int) diasAtraso);
        multa.setMonto(BigDecimal.valueOf(montoDiario * diasAtraso));
        multa.setEstado("PENDIENTE");
        multaRepository.save(multa);

        notificacionService.crear(prestamo.getUsuario(),
                "Se generó una multa de ₡" + multa.getMonto() + " por la devolución tardía de \""
                        + prestamo.getLibro().getTitulo() + "\"");
    }

    // Marca una multa como pagada
    @Transactional
    public void marcarComoPagada(Integer idMulta) {
        Multa multa = multaRepository.findById(idMulta)
                .orElseThrow(() -> new NoSuchElementException("La multa no existe"));
        multa.setEstado("PAGADA");
        multa.setFechaPago(LocalDateTime.now());
        multaRepository.save(multa);
    }
}