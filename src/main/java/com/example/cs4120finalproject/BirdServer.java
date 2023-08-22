package com.example.cs4120finalproject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.sql.*;

public class BirdServer {
    private Statement stmt;
    private ObjectInputStream inputFromClient;
    private ObjectOutputStream outputToClient;

    public static void main(String[] args) {new BirdServer();}

    public BirdServer() {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Bird server started");

            //Connect to Database
            initializeDB();

            // Listen for a new connection request
            Socket socket = serverSocket.accept();

            System.out.println("socket created");
            System.out.println();

            //Create Input/Output Streams
            outputToClient = new ObjectOutputStream(socket.getOutputStream());
            inputFromClient = new ObjectInputStream(socket.getInputStream());

            while (true) {
                int requestType = inputFromClient.read();
                if(requestType == 0) {
                    // Read object from input
                    Object object = inputFromClient.readObject();

                    //Cast object to Bird type
                    Bird bird = (Bird) object;

                    String insertBird = "INSERT INTO birds (birdType, sex, dateObserved) " +
                            "VALUES ('" + bird.getType() + "', '" + bird.getSex() + "', '" + bird.getDate() + "');";

                    stmt.executeUpdate(insertBird);

                    System.out.println(bird);
                }else if(requestType == 1){
                    System.out.println("Send birds");

                    String querystmt = "select birdType, sex, dateObserved from birds";
                    ResultSet resultSet = stmt.executeQuery(querystmt);

                    ArrayList<Bird> birds = new ArrayList<>();
                    while (resultSet.next()){
                        birds.add(new Bird(resultSet.getString(1), resultSet.getString(2), resultSet.getDate(3).toLocalDate()));
                    }
                    outputToClient.writeObject(birds);
                    outputToClient.flush();
                }else if(requestType == 2){
                    String deleteBirds = "DELETE FROM birds";
                    stmt.executeUpdate(deleteBirds);
                    System.out.println("Delete Birds");
                }
            }
        }
        catch(ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        catch(IOException ex) {
            ex.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                inputFromClient.close();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void initializeDB() {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded");

            // Establish a connection
            Connection connection = DriverManager.getConnection
                    ("jdbc:mysql://localhost/birddb", "root", "root");
            System.out.println("Database connected");

            // Create a statement
            stmt = connection.createStatement();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
