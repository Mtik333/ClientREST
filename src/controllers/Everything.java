package controllers;

import nothing.RsiClient;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

class Everything {

    static RsiClient rsiClient;

    public static void setClient(List<RsiClient> clients, String username) {
        rsiClient = clients.stream().filter(client -> client.getUsername().contentEquals(username)).findFirst().get();
    }

    public static XMLGregorianCalendar getGregorianCalendar(long value) throws DatatypeConfigurationException {
        Timestamp ts = new Timestamp(new Date().getTime());
        ts.setTime(value);
        LocalDateTime ldt = ts.toLocalDateTime();
        XMLGregorianCalendar cal = DatatypeFactory.newInstance().newXMLGregorianCalendar();
        cal.setYear(ldt.getYear());
        cal.setMonth(ldt.getMonthValue());
        cal.setDay(ldt.getDayOfMonth());
        cal.setHour(ldt.getHour());
        cal.setMinute(ldt.getMinute());
        cal.setSecond(ldt.getSecond());
        cal.setFractionalSecond(new BigDecimal("0." + ldt.getNano()));
        System.out.println(ldt);
        System.out.println(cal);
        return cal;
    }
}
