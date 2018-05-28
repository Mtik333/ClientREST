package controllers;

import cinemaclient.CinemaClient;
import com.qoppa.pdf.PDFException;
import com.qoppa.pdfViewerFX.PDFViewer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Callback;
import nothing.ResponseList;
import nothing.RsiReservation;

import javax.ws.rs.core.Response;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MyReservationsController implements Initializable {

    static List<RsiReservation> reservationList;
    @FXML
    public ListView listView;
    static ObservableList<String> list;
    public static RsiReservation findReservation(String id) {
        int reservationId = Integer.parseInt(id.replace("-", ""));
        for (RsiReservation rsiReservation : reservationList) {
            if (rsiReservation.getId().intValue() == reservationId) {
                return rsiReservation;
            }
        }
        return null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CinemaClient cinemaClient = new CinemaClient();
        ResponseList responseList = cinemaClient.getReservations(ResponseList.class);

        reservationList = responseList.getReservations().stream().filter(reservation -> reservation.getClientReserverId().getId().intValue() == Everything.rsiClient.getId().intValue()).collect(Collectors.toList());
        List<String> reservations = new ArrayList<>();
        for (RsiReservation rsiReservation : reservationList) {
            StringBuilder sb = new StringBuilder();
            sb.append(rsiReservation.getId() + "\t");
            sb.append(rsiReservation.getScreeningId().getMovieId().getTitle() + "\t");
            Date date = null;
            try {
                date = rsiReservation.getScreeningId().getGregorianCalendar().toGregorianCalendar().getTime();
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
            DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            String formattedDate  = formatter.format(date);
            sb.append(formattedDate);
            reservations.add(sb.toString());
        }
        list = FXCollections.observableArrayList(reservations);
        listView.setItems(list);
        listView.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> param) {
                return new XCell();
            }
        });
    }

    @FXML
    private void goToMyReservations() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/FXMLDocument.fxml"));
        Parent root1 = null;
        try {
            root1 = (Parent) fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Stage stage_root = (Stage) listView.getScene().getWindow();
        stage_root.setTitle("RSI Cinema - Movies");
        stage_root.setScene(new Scene(root1));
        stage_root.showAndWait();
    }

    @FXML
    public void dismiss() {
        Stage stage = (Stage) listView.getScene().getWindow();
        stage.close();
    }

    static class XCell extends ListCell<String> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Label label2 = new Label("aaa");
        Label label3 = new Label();
        Pane pane = new Pane();
        Button editButton = new Button("Edit");
        Button cancelButton = new Button("Cancel");
        Button button = new Button("Details");
        String lastItem;

        public XCell() {
            super();
            hbox.setSpacing(2);
            hbox.getChildren().addAll(label, label2, label3, pane, button, editButton, cancelButton);
            HBox.setHgrow(pane, Priority.ALWAYS);
            editButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/ChangeReservationSeat.fxml"));
                    ChangeReservation.reservation = findReservation(label.getText());
                    Parent root1 = null;
                    try {
                        root1 = (Parent) fxmlLoader.load();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Stage stage = new Stage();
                    stage.setTitle("Seat details");
                    stage.setScene(new Scene(root1));
                    stage.showAndWait();
                }
            });

            cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText("Look, a Confirmation Dialog");
                    alert.setContentText("Are you ok with this?");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.get() == ButtonType.OK) {
                        CinemaClient cinemaClient = new CinemaClient();
                        RsiReservation test = findReservation(label.getText());
                        cinemaClient.removeReservation(test.getId().toString());
                        //hello.removeReservation(test);
                        int index = reservationList.indexOf(test);
                        list.remove(index);
                    } else {
                        alert.close();
                    }
                }
            });

            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //https://kbdeveloper.qoppa.com/javafx-pdf-viewer/
                    CinemaClient cinemaClient = new CinemaClient();
                    RsiReservation test = findReservation(label.getText());
                    Response response = cinemaClient.pdfReservation(test.getId().toString());
                    byte[] bais = response.readEntity(byte[].class);
                    File file = new File("itext-test.pdf");
                    FileOutputStream fileout = null;
                    try {
                        fileout = new FileOutputStream(file);
                        fileout.write(bais);
                        PDFViewer m_PDFViewer = new PDFViewer();
                        m_PDFViewer.loadPDF(file.toURL());
                        BorderPane borderPane = new BorderPane(m_PDFViewer);
                        Scene scene = new Scene(borderPane);
                        Stage stage = new Stage();
                        stage.setTitle("Reservation in PDF");
                        stage.setScene(scene);
                        stage.centerOnScreen();
                        stage.show();
                    } catch (IOException | PDFException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                label.setText(item != null ? item : "<null>");
                String[] text = label.getText().split("\t");
                label.setText(text[0]);
                label2.setText(text[1]);
                label3.setText(text[2]);
                setGraphic(hbox);
            }
        }
    }
}
