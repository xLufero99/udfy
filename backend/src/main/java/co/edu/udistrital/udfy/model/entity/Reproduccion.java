package co.edu.udistrital.udfy.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reproduccion")
public class Reproduccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reproduccion", nullable = false)
    private Long idReproduccion;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_cancion", nullable = false)
    private Long idCancion;

    @Column(name = "fecha_reproduccion", insertable = false, updatable = false)
    private OffsetDateTime fechaReproduccion;

    @Column(name = "minutos_reproducidos")
    private BigDecimal minutosReproducidos;

    @Column(name = "completada")
    private Boolean completada;

    public Long getIdReproduccion() {
        return idReproduccion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCancion() {
        return idCancion;
    }

    public void setIdCancion(Long idCancion) {
        this.idCancion = idCancion;
    }

    public OffsetDateTime getFechaReproduccion() {
        return fechaReproduccion;
    }

    public BigDecimal getMinutosReproducidos() {
        return minutosReproducidos;
    }

    public void setMinutosReproducidos(BigDecimal minutosReproducidos) {
        this.minutosReproducidos = minutosReproducidos;
    }

    public Boolean getCompletada() {
        return completada;
    }

    public void setCompletada(Boolean completada) {
        this.completada = completada;
    }
}
