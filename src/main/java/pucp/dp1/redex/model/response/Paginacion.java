package pucp.dp1.redex.model.response;

import org.springframework.data.domain.Pageable;

public class Paginacion {

	private Integer pagina;
	private Integer registros;
	private Integer totalPaginas;
	private Long totalRegistros;
	
	public Integer getPagina() {
		return pagina;
	}
	public void setPagina(Integer pagina) {
		this.pagina = pagina;
	}
	public Integer getRegistros() {
		return registros;
	}
	public void setRegistros(Integer registros) {
		this.registros = registros;
	}
	public Integer getTotalPaginas() {
		return totalPaginas;
	}
	public void setTotalPaginas(Integer totalPaginas) {
		this.totalPaginas = totalPaginas;
	}
	public Long getTotalRegistros() {
		return totalRegistros;
	}
	public void setPageable(Pageable page) {
		this.pagina = page.getPageNumber()+1;
		this.registros = page.getPageSize();
	}
	public void setTotalRegistros(Long totalRegistros) {
		this.totalRegistros = totalRegistros;
		if (this.registros > 0) {
			this.totalPaginas = (int)Math.ceil(this.totalRegistros / (float)this.registros);
		}
	}
}