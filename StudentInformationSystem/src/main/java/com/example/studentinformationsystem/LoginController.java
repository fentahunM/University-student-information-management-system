package com.example.studentinformationsystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class LoginController {
    private Stage stage;
    private Parent root;
    private Scene scene;
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;
    private Statement statement;

    @FXML
    private PasswordField password;

    @FXML
    private TextField username;
    @FXML
    private ComboBox cmbLoginType;
    @FXML
    private Button btn_login;

    public void initialize() {
        ObservableList<String> list = FXCollections.observableArrayList("Admin", "Student", "Teacher");
        cmbLoginType.setItems(list);
    }

    public String  getCurrentUsername() {
        return username.getText();
    }

    @FXML
    void handleLogin(ActionEvent event) throws SQLException {

        if (cmbLoginType.getValue().toString() == "Admin") {
            String sql = "SELECT * FROM admin WHERE first_name = ? and admin_id = ?";
            connect = Database.connectDb();
            try {
                Alert alert;

                prepare = connect.prepareStatement(sql);
                prepare.setString(1, username.getText());
                prepare.setString(2, password.getText());

                result = prepare.executeQuery();
//            CHECK IF FIELDS ARE EMPTY
                if (username.getText().isEmpty() || password.getText().isEmpty()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill all blank fields");
                    alert.showAndWait();
                } else {
                    if (result.next()) {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("admin_page.fxml"));
                        root = loader.load();
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Wrong Username/Password");
                        alert.showAndWait();

                    }
                }
                connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } else if (cmbLoginType.getValue().toString() == "Student") {

            String username = getCurrentUsername();
            String password = this.password.getText();
            // Check if username and password exist in the student table
            String studentQuery = "SELECT * FROM student WHERE first_name = ? AND student_id = ?";
            connect = Database.connectDb();
            try {
                prepare = connect.prepareStatement(studentQuery);
                prepare.setString(1, username);
                prepare.setString(2, password);

                result = prepare.executeQuery();

                if (result.next()) {
                    // Username and password are found in the student table,
                    // so insert them into the user table for authentication

                    // Check if the user already exists in the user table
                    String userQuery = "SELECT * FROM user WHERE user_name = ?";
                    prepare = connect.prepareStatement(userQuery);
                    prepare.setString(1, username);

                    ResultSet userResult = prepare.executeQuery();

                    if (!userResult.next()) {
                        // User does not exist in the user table, so insert them
                        String insertQuery = "INSERT INTO user (student_id,user_name, password) VALUES (?,?, ?)";
                        prepare = connect.prepareStatement(insertQuery);
                        prepare.setString(1, result.getString("student_id"));
                        prepare.setString(2, username);
                        prepare.setString(3, password);

                        int rowsAffected = prepare.executeUpdate();

                        if (rowsAffected > 0) {
                            System.out.println("User inserted successfully.");
                        } else {
                            System.out.println("User insertion failed.");
                        }
                    }

                    // Now, use the user table to authenticate the user
                    String authQuery = "SELECT * FROM user WHERE user_name = ? AND password = ?";
                    prepare = connect.prepareStatement(authQuery);
                    prepare.setString(1, username);
                    prepare.setString(2, password);

                    ResultSet authResult = prepare.executeQuery();

                    if (authResult.next()) {
                        // User authenticated successfully
                        String currentUsername = getCurrentUsername();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("student_page.fxml"));
                        root = loader.load();
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        student_page_controller studentPageController = loader.getController();

                        // Pass the currentUsername to the ViewGradeController
                        studentPageController.setLoggedInUsername(currentUsername);

                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        // Invalid username or password
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Wrong Username/Password");
                        alert.showAndWait();

                    }
                } else {
                    // Subsequent logins, check the user table
                    String authQuery = "SELECT * FROM user WHERE user_name = ? AND password = ?";
                    prepare = connect.prepareStatement(authQuery);
                    prepare.setString(1, username);
                    prepare.setString(2, password);

                    result = prepare.executeQuery();

                    if (result.next()) {
                        // User authenticated successfully
                        String currentUsername = getCurrentUsername();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("student_page.fxml"));
                        root = loader.load();
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        student_page_controller studentPageController = loader.getController();

                        // Pass the currentUsername to the ViewGradeController
                        studentPageController.setLoggedInUsername(currentUsername);

                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();
                    } else {
                        // Invalid username or password
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Wrong Username/Password");
                        alert.showAndWait();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            connect.close();
        }


         else if (cmbLoginType.getValue().toString() == "Teacher") {
            String sql = "SELECT * FROM teacher WHERE first_name = ? and teacher_id = ?";
            connect = Database.connectDb();
            try {
                Alert alert;

                prepare = connect.prepareStatement(sql);
                prepare.setString(1, username.getText());
                prepare.setString(2, password.getText());

                result = prepare.executeQuery();
//            CHECK IF FIELDS ARE EMPTY
                if (username.getText().isEmpty() || password.getText().isEmpty()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Please fill all blank fields");
                    alert.showAndWait();
                } else {
                    if (result.next()) {
                        String currentUsername = getCurrentUsername();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("teacher_page.fxml"));
                        root = loader.load();
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        teacher_page_controller teacher_page_controller = loader.getController();
                        // Pass the currentUsername to the ViewGradeController
                        teacher_page_controller.setLoggedInUsername(currentUsername);
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                    } else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Wrong Username/Password");
                        alert.showAndWait();

                    }
                }
                connect.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void handleBack(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}



