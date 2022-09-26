package pucp.dp1.redex.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ResponseObject {
	
	private Estado estado;
	@JsonInclude(Include.NON_NULL)
	private Paginacion paginacion;
	@JsonInclude(Include.NON_NULL)
	private Error error;
	@JsonInclude(Include.NON_NULL)
	private Object resultado;

	public Estado getEstado() {
		return estado;
	}
	public void setEstado(Estado estado) {
		this.estado = estado;
	}
	public Paginacion getPaginacion() {
		return paginacion;
	}
	public void setPaginacion(Paginacion paginacion) {
		this.paginacion = paginacion;
	}
	public Error getError() {
		return error;
	}
	public void setError(Error error) {
		this.error = error;
	}
	public void setError(Integer codigo, String mensaje, String mensajeInterno) {
		this.error = new Error(codigo, mensaje, mensajeInterno);
	}
	public Object getResultado() {
		return resultado;
	}
	public void setResultado(Object resultado) {
		this.resultado = resultado;
	}
}
