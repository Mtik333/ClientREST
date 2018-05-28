package nothing;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Marshal {
    @XmlElement(name = "reservation")
    private RsiReservation rsiReservation;
    @XmlElement(name = "seat")
    private RsiSeat rsiSeat;

    public RsiReservation getRsiReservation() {
        return rsiReservation;
    }

    public void setRsiReservation(RsiReservation rsiReservation) {
        this.rsiReservation = rsiReservation;
    }

    public RsiSeat getRsiSeat() {
        return rsiSeat;
    }

    public void setRsiSeat(RsiSeat rsiSeat) {
        this.rsiSeat = rsiSeat;
    }

}