package com.example.cs4120finalproject;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.ArrayList;

public class BirdClient extends Application {
    ObjectOutputStream toServer = null;
    ObjectInputStream fromServer = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Bird Catalogue"); // Set the stage title

        //Create Grid Pane to hold Labels and TextFields
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //Bird Type Label and TextField
        Label typeLabel = new Label("Bird Type:");
        grid.add(typeLabel, 0, 0);
        TextField typeTF = new TextField();
        grid.add(typeTF, 1, 0);

        //Sex Label and TextField
        Label sexLabel = new Label("Sex:");
        grid.add(sexLabel, 2, 0);
        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Male",
                        "Female"
                );
        final ComboBox sexBox = new ComboBox(options);
        sexBox.setValue("Male");
        grid.add(sexBox, 3, 0);

        //Date Label and Date Picker
        Label dateLabel = new Label("Date Observed:");
        grid.add(dateLabel, 4, 0);
        DatePicker birdDatePicker = new DatePicker();
        birdDatePicker.setValue(LocalDate.now());
        grid.add(birdDatePicker, 5, 0);

        //Submit Button
        Button submitBird = new Button("Submit Bird");
        HBox submitBirdHB = new HBox(10);
        submitBirdHB.setAlignment(Pos.BOTTOM_RIGHT);
        submitBirdHB.getChildren().add(submitBird);
        grid.add(submitBirdHB, 5, 1);

        //See birds button
        Button seeBirds = new Button("See Birds");
        HBox seeBirdsHB = new HBox(10);
        seeBirdsHB.setAlignment(Pos.BOTTOM_RIGHT);
        seeBirdsHB.getChildren().add(seeBirds);
        grid.add(seeBirdsHB, 3, 1);

        //Delete Birds Button
        Button deleteBirds = new Button("Delete Birds");
        HBox deleteBirdsHB = new HBox(10);
        deleteBirdsHB.setAlignment(Pos.BOTTOM_RIGHT);
        deleteBirdsHB.getChildren().add(deleteBirds);
        grid.add(deleteBirdsHB, 0, 1);


        //Submission Alert
        Alert a = new Alert(Alert.AlertType.NONE);


        Scene scene = new Scene(grid, 725, 200);
        primaryStage.setScene(scene);

        primaryStage.show(); // Display the stage

        submitBird.setOnAction(e -> {
            try {
                if(typeTF.getText() == ""){
                    a.setAlertType(Alert.AlertType.WARNING);
                    a.setContentText("Please enter the type of bird.");
                    a.show();
                    return;
                }
                //Get Bird Type from TextField
                String birdType = typeTF.getText().trim();
                //Get Sex from TextField
                String birdSex = sexBox.getValue().toString();
                //Get Date from DatePicker
                LocalDate birdDate = birdDatePicker.getValue();


                System.out.println(birdType);
                System.out.println(birdSex);
                System.out.println(birdDate);

                //Create Bird object from given inputs
                Bird newBird = new Bird(birdType, birdSex, birdDate);

                //Send the new Bird object to the server
                toServer.write(0);
                toServer.flush();
                toServer.writeObject(newBird);
                toServer.flush();

                a.setAlertType(Alert.AlertType.INFORMATION);
                a.setContentText("Bird Successfully Submitted");
                a.show();
            }
            catch (IOException ex) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.show();
                System.err.println(ex);
            }
        });

        seeBirds.setOnAction(e -> {

            try {
                System.out.println("Show Birds");
                toServer.write(1);
                toServer.flush();



                Object results = fromServer.readObject();

                ArrayList<Bird> birds = (ArrayList<Bird>) results;

                Stage secondaryStage = new Stage();
                secondaryStage.setTitle("Bird List");
                TableView tbv = new TableView();
                TableColumn<String, Bird> cl1 = new TableColumn<>("Bird Type");
                cl1.setCellValueFactory(new PropertyValueFactory<>("type"));
                TableColumn<String, Bird> cl2 = new TableColumn<>("Sex");
                cl2.setCellValueFactory(new PropertyValueFactory<>("sex"));
                TableColumn<LocalDate, Bird> cl3 = new TableColumn<>("Date Observed");
                cl3.setCellValueFactory(new PropertyValueFactory<>("date"));
                tbv.getColumns().add(cl1);
                tbv.getColumns().add(cl2);
                tbv.getColumns().add(cl3);
                for (Bird bird : birds){
                    tbv.getItems().add(bird);
                }
                VBox vbox = new VBox();
                vbox.getChildren().addAll(tbv);
                vbox.setSpacing(10);
                vbox.setAlignment(Pos.CENTER);
                Scene scene2 = new Scene(vbox);
                secondaryStage.setScene(scene2);
                secondaryStage.show();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        });

        deleteBirds.setOnAction(e -> {
            try {

                System.out.println("Delete Birds");
                toServer.write(2);
                toServer.flush();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        try {
            // Create a socket to connect to the server
            Socket socket = new Socket("localhost", 8000);
            InetAddress inetAddress = socket.getInetAddress();


            System.out.println("Client's host name is " +
                    inetAddress.getHostName());
            System.out.println("Client's IP Address is " +
                    inetAddress.getHostAddress());
            //Create output stream
            toServer = new ObjectOutputStream(socket.getOutputStream());
            //Create input stream
            fromServer = new ObjectInputStream(socket.getInputStream());
        }
        catch (IOException ex) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("Check server connection");
            a.show();
            System.err.println(ex);
        }
    }

    /**
     * The main method is only needed for the IDE with limited
     * JavaFX support. Not needed for running from the command line.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
