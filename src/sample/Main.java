package sample;

import cinemaclient.CinemaClient;
import nothing.ResponseList;
import nothing.RsiAuditorium;

import javax.ws.rs.core.Response;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        System.out.println("start");
        CinemaClient client2 = new CinemaClient();
        ResponseList responseList = client2.getAuditoriums2(ResponseList.class);
        for (RsiAuditorium a : responseList.getAuditoriums()){
            System.out.println(a.getName());
        }
        System.out.println("xd");
        client2 = new CinemaClient("test","test");
        Response response = client2.authenticateClient(Response.class);
        System.out.println(response);
        client2 = new CinemaClient("test2","test2");
        response = client2.authenticateClient(Response.class);
        System.out.println(response);
    }
}
