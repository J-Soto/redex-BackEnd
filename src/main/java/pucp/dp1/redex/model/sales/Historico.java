package pucp.dp1.redex.model.sales;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

import java.sql.Time;
import java.util.Date;
import javax.persistence.*;

@Table(name = "historico")
@Entity
public class Historico implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name =  "historico_id")
    private int idHistorico;

    @Column(name = "codigo_pais_salida")
    private String codigoPaisSalida;

    //el numero de vuelo es identico al id
    //@Column(name = "nro_vuelo")
    //private Long nroVuelo;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    @JsonFormat(pattern="dd/MM/yyyy")
    private Date fecha;

    @Column(name = "hora")
    private Time hora;

    @Column(name = "codigo_pais_llegada")
    private String codigoPaisLlegada;

    @Column(name = "nro_paquetes")
    private Integer nroPaquetes;

    public int getIdHistorico() {
        return this.idHistorico;
    }

    public void setIdHistorico(final int idHistorico) {
        this.idHistorico = idHistorico;
    }

    public String getCodigoPaisSalida() {
        return this.codigoPaisSalida;
    }

    public void setCodigoPaisSalida(final String codigoPaisSalida) {
        this.codigoPaisSalida = codigoPaisSalida;
    }

    public Date getFecha() {
        return this.fecha;
    }

    public void setFecha(final Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return this.hora;
    }

    public void setHora(final Time hora) {
        this.hora = hora;
    }

    public String getCodigoPaisLlegada() {
        return this.codigoPaisLlegada;
    }

    public void setCodigoPaisLlegada(final String codigoPaisLlegada) {
        this.codigoPaisLlegada = codigoPaisLlegada;
    }

    public Integer getNroPaquetes() {
        return this.nroPaquetes;
    }

    public void setNroPaquetes(final Integer nroPaquetes) {
        this.nroPaquetes = nroPaquetes;
    }
}
