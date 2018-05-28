package controllers;

import cinemaclient.CinemaClient;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import nothing.*;

import javax.ws.rs.core.Response;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ChangeReservation implements Initializable {
    static RsiReservation reservation;
    @FXML
    public ChoiceBox choiceBox;
    @FXML
    public Button changeButton;
    List<RsiSeatReserved> seatReservedsFromScreening = new ArrayList<>();
    List<RsiSeat> seats = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaClient cinemaClient = new CinemaClient();
        ResponseList reservedSeatsList = cinemaClient.getReservedSeats(ResponseList.class);
        ResponseList seatsList = cinemaClient.getSeats(ResponseList.class);
        seatReservedsFromScreening = reservedSeatsList.getReservedseats().stream().filter(rsiSeatReserved -> rsiSeatReserved.getScreeningId().getId().equals(reservation.getScreeningId().getId())).collect(Collectors.toList());
        seats = seatsList.getSeats();
        for (RsiSeatReserved seat : seatReservedsFromScreening) {
            seats = seats.stream().filter(myseat -> myseat.getId().intValue() != seat.getSeatId().getId().intValue()).collect(Collectors.toList());
        }
        List<Integer> numbers = seats.stream().map(seat -> seat.getId()).collect(Collectors.toList());
        choiceBox.getItems().addAll(numbers);
        choiceBox.getSelectionModel().select(0);
        changeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                RsiSeat newSeat = seats.stream().filter(rsiSeat -> ((rsiSeat.getSeatNumber()-1)*5+rsiSeat.getSeatRow()) == (int) choiceBox.getSelectionModel().getSelectedItem()).findFirst().get();
                Marshal marshal = new Marshal();
                marshal.setRsiSeat(newSeat);
                marshal.setRsiReservation(reservation);
                Response response = cinemaClient.changeReservation(marshal, reservation.getId().toString());
                int i=0;
                //hello.changeReservation(reservation, newSeat);
            }
        });
    }
    @FXML
    public void dismiss() {
        Stage stage = (Stage) choiceBox.getScene().getWindow();
        stage.close();
    }

}
