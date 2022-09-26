package pucp.dp1.redex.model.utils;

public class FlightPlanElement {
	
	private String codOrigin;
	
	private String codDestiny;
	
	private Long count;
	
	private Integer idFlight;
	
	private String cityOrigin;
	
	private String countryOrigin;
	
	private String cityDestiny;
	
	private String countryDestiny;

	public FlightPlanElement(Integer idFlight, String codOrigin, String codDestiny, Long count, String cityOrigin, String countryOrigin, String cityDestiny, String countryDestiny) {
		super();
		this.codOrigin = codOrigin;
		this.codDestiny = codDestiny;
		this.count = count;
		this.idFlight = idFlight;
		this.cityOrigin=cityOrigin;
		this.countryOrigin=countryOrigin;
		this.cityDestiny=cityDestiny;
		this.countryDestiny=countryDestiny;
	}

	public Integer getIdFlight() {
		return idFlight;
	}

	public void setIdFlight(Integer idFlight) {
		this.idFlight = idFlight;
	}

	public String getCodOrigin() {
		return codOrigin;
	}

	public void setCodOrigin(String codOrigin) {
		this.codOrigin = codOrigin;
	}

	public String getCodDestiny() {
		return codDestiny;
	}

	public void setCodDestiny(String codDestiny) {
		this.codDestiny = codDestiny;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String getCityOrigin() {
		return cityOrigin;
	}

	public void setCityOrigin(String cityOrigin) {
		this.cityOrigin = cityOrigin;
	}

	public String getCountryOrigin() {
		return countryOrigin;
	}

	public void setCountryOrigin(String countryOrigin) {
		this.countryOrigin = countryOrigin;
	}

	public String getCityDestiny() {
		return cityDestiny;
	}

	public void setCityDestiny(String cityDestiny) {
		this.cityDestiny = cityDestiny;
	}

	public String getCountryDestiny() {
		return countryDestiny;
	}

	public void setCountryDestiny(String countryDestiny) {
		this.countryDestiny = countryDestiny;
	}
	
	
}
