package sample;

import animatefx.animation.*;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Loader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {

   public String username;
   PasswordCrypto crypto = new PasswordCrypto();

    @FXML
    Button signUpPage,loginPage,signUpBtn,loginBtn,adminLoginBtn,adminPaneBack,adminLogin;
    @FXML
    Pane signUpPane,loginPane,adminPane;
    @FXML
    TextField signUpName,signUpUsername,signUpPassword,loginPassword,loginUsername,adminUsername,adminPassword;
    @FXML
    Label errorField,errorLogin,adminError;
    @FXML
    ImageView btnClose;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File file = new File("users.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void handleButtonEvent(ActionEvent event) throws IOException {
        if(event.getSource()==signUpPage){
            signUpName.clear();
            signUpPassword.clear();
            signUpUsername.clear();
            errorField.setText("");
            new JackInTheBox(signUpPane).play();
            signUpPane.toFront();
        }
        if(event.getSource()==adminLogin){
            if(adminUsername.getText().isEmpty() || adminPassword.getText().isEmpty()){
                adminError.setText("Enter all fields");
            }else{
                String u = searchFile(new File("admin.txt"),adminUsername.getText());
                String user[] = u.split("\\|");
                if(adminUsername.getText().equals(user[0].trim())){
                    if (adminPassword.getText().equals(user[1].trim())) {
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage)node.getScene().getWindow();
                        stage.close();
                        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("admindash.fxml")));
                        stage.setScene(scene);
                        stage.show();


                    } else if (!adminPassword.equals(user[2].trim())) {
                        System.out.println("error");
                        adminError.setText("Password wrong.");
                    }
                }else{
                    adminError.setText("Admin doesn't exist.");
                }
            }

        }
        if(event.getSource()==loginPage){
            loginPassword.clear();
            loginUsername.clear();
            errorLogin.setText("");
            new JackInTheBox(loginPane).play();
            loginPane.toFront();
        }if(event.getSource()==adminLoginBtn){
            adminUsername.clear();
            adminPassword.clear();
            adminError.setText("");
            new JackInTheBox(adminPane).play();
            adminPane.toFront();
        }
        if(event.getSource()==adminPaneBack){
            loginPassword.clear();
            loginUsername.clear();
            errorLogin.setText("");
            new JackInTheBox(loginPane).play();
            loginPane.toFront();
        }

        if(event.getSource()==loginBtn){
            errorLogin.setText("");
            if(loginUsername.getText().isEmpty() || loginPassword.getText().isEmpty()){
                errorLogin.setText("Enter all fields");
            }else{
                String u = searchFile(new File("users.txt"),loginUsername.getText());
                String user[] = u.split("\\|");
                System.out.println(user[1]);
                if(loginUsername.getText().equals(user[1].trim())){
                    String dec = new String(crypto.decrypt(user[2].trim().getBytes()));
                    if (loginPassword.getText().equals(dec)) {
                        errorLogin.setText("Login Successful");
                        Node node = (Node) event.getSource();
                        Stage stage = (Stage)node.getScene().getWindow();
                        stage.close();
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("userdash.fxml"));
                        Scene scene = new Scene(loader.load());
                        Userdash d = loader.getController();
                        d.setText(user[0].trim(),loginUsername.getText().trim());
                        stage.setScene(scene);
                        stage.show();

                    } else if (!loginPassword.equals(user[2].trim())) {
                        System.out.println("error");
                        errorLogin.setText("Password wrong.");
                    }
                }else{
                    errorLogin.setText("User doesn't exist.");
                }


            }
        }
        if(event.getSource()==signUpBtn){
            errorField.setText("");
        if(signUpName.getText().isEmpty() || signUpUsername.getText().isEmpty() || signUpPassword.getText().isEmpty()){
            errorField.setText("Enter all fields");
        }else{
            File file = new File("users.txt");
            boolean flag = parseFile(file,signUpUsername.getText());
            if(flag){
                errorField.setText("Username exists!");
            }else {
                if (file.exists()) {
                    FileWriter fr = new FileWriter(file, true);
                    BufferedWriter br = new BufferedWriter(fr);
                    String enc = new String(crypto.encrypt(signUpPassword.getText().trim().getBytes()));
                    br.write(signUpName.getText() + " | " + signUpUsername.getText() + " | " + enc + "\n");
                    br.close();
                    fr.close();
                    signUpName.clear();signUpUsername.clear();signUpPassword.clear();
                    errorField.setText("Account created!");
                }
            }
        }
    }


    }
    public boolean parseFile(File fileName,String searchStr) throws FileNotFoundException {
        boolean f = false;
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(searchStr)) {
                f = true;
            }
        }
        scan.close();
        return f;

    }
    public String searchFile(File fileName,String searchStr) throws FileNotFoundException {
        String s = "Item doesn't exist";
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.toLowerCase().contains(searchStr.toLowerCase())) {
                s = line;
            }
        }
        scan.close();
        return s;
    }

    public void handleMouseEvent(MouseEvent event) {
        if(event.getSource()==btnClose){
            System.exit(1);
        }
    }
}
