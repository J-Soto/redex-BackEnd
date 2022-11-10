package pucp.dp1.redex.model.utils;



import pucp.dp1.redex.model.sales.Airport;

import java.sql.Time;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FlightElement {
	
	private Airport arrivalAirport;

    private Airport takeOffAirport;
	
	private Integer idFlight;


    private LocalTime takeOffTime;    

    private LocalTime arrivalTime;

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public Airport getTakeOffAirport() {
        return takeOffAirport;
    }

    public void setTakeOffAirport(Airport takeOffAirport) {
        this.takeOffAirport = takeOffAirport;
    }

    public LocalTime getTakeOffTime() {
        return takeOffTime;
    }

    public void setTakeOffTime(LocalTime takeOffTime) {
        this.takeOffTime = takeOffTime;
    }

	public LocalTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public FlightElement(Integer idFlight, Airport arrivalAirport, Airport takeOffAirport,LocalTime arrivalTime,LocalTime takeOffTime) {
		super();
		this.arrivalAirport = arrivalAirport;
		this.takeOffAirport = takeOffAirport;
		this.idFlight = idFlight;
        this.takeOffTime=takeOffTime;
        this.arrivalTime=arrivalTime;

	}

    public FlightElement(Integer idFlight, Airport arrivalAirport, Airport takeOffAirport) {
		super();
		this.arrivalAirport = arrivalAirport;
		this.takeOffAirport = takeOffAirport;
		this.idFlight = idFlight;

	}

    public FlightElement(Integer idFlight,LocalTime arrivalTime,LocalTime takeOffTime) {
		super();
		this.idFlight = idFlight;
        this.takeOffTime=takeOffTime;
        this.arrivalTime=arrivalTime;

	}
    public FlightElement(Integer idFlight) {
		super();
        this.idFlight = idFlight;
	}

    public FlightElement() {
		super();
	}

	public Integer getIdFlight() {
		return idFlight;
	}

	public void setIdFlight(Integer idFlight) {
		this.idFlight = idFlight;
	}




	
	
}
