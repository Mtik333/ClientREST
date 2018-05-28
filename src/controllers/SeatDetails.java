package controllers;

import cinemaclient.CinemaClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import nothing.*;

import javax.xml.namespace.QName;
import javax.xml.ws.Response;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SeatDetails implements Initializable {
    public static RsiScreening rsiScreening;
    public static RsiAuditorium rsiAuditorium;
    @FXML
    public GridPane gridPane;
    List<RsiSeatReserved> seatReservedsFromScreening = new ArrayList<>();
    List<RsiSeatReserved> seatReservedsFromSeat = new ArrayList<>();
    List<RsiSeat> seats = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaClient cinemaClient = new CinemaClient();
        ResponseList responseList = cinemaClient.getReservedSeats(ResponseList.class);
        ResponseList seatsList = cinemaClient.getSeats(ResponseList.class);
        seatReservedsFromScreening = responseList.getReservedseats().stream().filter(rsiSeatReserved -> rsiSeatReserved.getScreeningId().getId().equals(rsiScreening.getId())).collect(Collectors.toList());
        seats = seatsList.getSeats();
        for (int i = 0; i < 25; i++) {
            Node node = gridPane.getChildren().get(i);
            Pane pane = new Pane();
            Label label = new Label();
            label.setText(String.valueOf((seats.get(i).getSeatNumber() - 1) * 5 + seats.get(i).getSeatRow()));
            label.setTextFill(Color.web("#ffffff"));
            label.setAlignment(Pos.CENTER);
            pane.getChildren().add(label);
            if (test2(seats.get(i))) {
                pane.setStyle("-fx-background-color: red;");
            } else pane.setStyle("-fx-background-color: green;");
            pane.setOnMouseClicked(e -> {
                Node source = (Node) e.getSource();
                if (source.getStyle().contains("red")) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Reserved seat");
                    alert.setContentText("You can't reserve this seat");
                    alert.showAndWait();
                } else reservationMaker(GridPane.getColumnIndex(source) + 1, GridPane.getRowIndex(source) + 1);
            });
            //gridPane.getChildren().add(0,pane);
            gridPane.add(pane, i / 5, i % 5);
        }
    }

    public boolean test2(RsiSeat seat) {
        for (RsiSeatReserved screeningSeatReserver : seatReservedsFromScreening) {
            if (screeningSeatReserver.getSeatId().getId().intValue()==seat.getId().intValue())
                return true;
        }
        return false;
    }

    public RsiSeat findSeatByRowColumn(int column, int row) {
        for (RsiSeat seat : seats) {
            if (seat.getSeatNumber() == column && seat.getSeatRow() == row) {
                return seat;
            }
        }
        return null;
    }


    public void reservationMaker(int column, int row) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/ReservationDetails.fxml"));
        ReservationDetails.seat = findSeatByRowColumn(column, row);
        ReservationDetails.screening = rsiScreening;
        ReservationDetails.auditorium = rsiAuditorium;
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage = new Stage();
        stage.setTitle("Create reservation");
        stage.setScene(new Scene(root1));
        stage.showAndWait();
        Stage stage_root = (Stage) gridPane.getScene().getWindow();
        stage_root.close();
    }

}
