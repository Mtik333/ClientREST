package controllers;

import cinemaclient.CinemaClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import nothing.ResponseList;
import nothing.RsiClient;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((String hostname, javax.net.ssl.SSLSession sslSession) -> {
            return hostname.equals("localhost");
        });
    }

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    @FXML
    public void tryLogin() throws IOException, KeyManagementException, NoSuchAlgorithmException {
        String username2 = username.getText();
        String password2 = password.getText();
        System.out.println("xd");
            CinemaClient cinemaClient = new CinemaClient(username2,password2);
            Response response = cinemaClient.authenticateClient(Response.class);
            if (response.getStatus()==Response.Status.ACCEPTED.getStatusCode()){
                ResponseList responseList = cinemaClient.getClients(ResponseList.class);
                List<RsiClient> clients = responseList.getClients();
                Everything.setClient(clients, username2);
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../fxmls/FXMLDocument.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                Stage stage = new Stage();
                stage.setTitle("RSI Cinema - Movies");
                stage.setScene(new Scene(root1));
                stage.show();
                dismiss();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid credentials");
                alert.setContentText("You entered invalid credentials. Try again");
                alert.showAndWait();
            }
    }

    private void dismiss() {
        Stage stage = (Stage) username.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
