package sample;

import animatefx.animation.*;
import com.jfoenix.controls.JFXToggleButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import org.controlsfx.control.Rating;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import javafx.scene.control.ScrollPane;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Userdash implements Initializable {

    Controller c = new Controller();
    String user;
    Scanner x;
    adminControl ac= new adminControl();

    @FXML
    TextField searchBox;
    @FXML
    JFXToggleButton watchToggle;
    @FXML
    Button logoutBtn,topMovieBtn,mv1Btn,mv2Btn,mv3Btn,mv4Btn,mv5Btn,mv6Btn,watchListBtn;
    @FXML
    Label welcomeLbl,movieName,dirLbl,genreLbl,yearLbl,userName,infoLbl,topLbl;
    @FXML
    ImageView moviePoster,mv1Img,mv2Img,mv3Img,mv4Img,mv5Img,mv6Img;
    @FXML
    Rating myRating,avgRating;
    @FXML
    TableView<WatchList> watchListTable;
    @FXML
    TableColumn<WatchList,String> movie_col;
    @FXML
    Pane mv1Pane,mv2Pane,mv3Pane,mv4Pane,mv5Pane,mv6Pane,errorPane,viewMoviePane,showcasePane,watchListPane;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        infoLbl.setText("Here are the top movies! Enjoy!");
        File file = new File("watchList.txt");
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            showcasePane();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    

    private void showcasePane() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("topMovie.txt"));
        String curLine;
        int imgCount =0;
        Pane[] mvPane = {mv1Pane, mv2Pane,mv3Pane,mv4Pane,mv5Pane,mv6Pane};
        ImageView[] myImg = {mv1Img,mv2Img,mv3Img,mv4Img,mv5Img,mv6Img};
        int count = ac.getLineCount("topMovie.txt");
        for(int i=0;i<count;i++){
            mvPane[i].setVisible(true);
        }
        while((curLine=br.readLine())!=null && imgCount<count){
            String fname = curLine.trim()+".jpg";
            Image image = new Image(fname);
            myImg[imgCount].setImage(image);
            imgCount++;

        }
        br.close();
    }

   public void showMovieInfo(String searchTerm) throws IOException {
        watchToggle.setText("Not Watched!");
        new FadeIn(viewMoviePane).play();
        viewMoviePane.toFront();
       myRating.setRating(0);
       infoLbl.setText("Now watching : " + searchBox.getText().trim());
       boolean sa = getparseRating(new File("watchList.txt"),user,searchBox.getText().trim().toLowerCase());
       if(sa){
           watchToggle.setSelected(true);
           watchToggle.setText("Watched!");
       }else{
           watchToggle.setSelected(false);
       }
           boolean f = searchMovie("movies.txt",searchTerm);
           if(f){
               String genre;
               String s = returnMovie(new File("movies.txt"),searchTerm.trim());
               String[] item = s.split("\\|");
               switch(item[3].trim()){
                   case "Ac":
                       genre="Action";
                       break;
                   case "Th":
                       genre="Thriller";
                       break;
                   case "Dr":
                       genre="Drama";
                       break;
                   case "Ad":
                       genre="Adventure";
                       break;
                   case "An":
                       genre="Animation";
                       break;
                   case "Fa":
                       genre="Fantasy";
                       break;
                   case "Sci":
                       genre="Sci-Fi";
                       break;
                   case "Co":
                       genre="Comedy";
                       break;
                   case "Ho":
                       genre="Horror";
                       break;
                   case "Ro":
                       genre="Romance";
                       break;
                   case "Cr":
                       genre="Crime";
                       break;
                   case "My":
                       genre="Mystery";
                       break;
                   default:genre=item[3];
               }
               movieName.setText(item[1]);
               dirLbl.setText(item[2]);
               genreLbl.setText(genre);
               yearLbl.setText(item[4]);
               Image fImg = new Image(item[0].trim()+".jpg");
               moviePoster.setImage(fImg);
               float sum = getAverageRating("rating.txt",item[1].trim());
               int count = getRatingCount(new File("rating.txt"),item[1].trim());
               float avgRate = sum/count;
               avgRating.setRating(avgRate);
               boolean r = getparseRating(new File("rating.txt"),user,item[1].trim());
               if (r) {
                   String movieExist = getRating(new File("rating.txt"),user,item[1].trim());
                   String[] rating = movieExist.split("[,\n]");
                   myRating.setRating(Double.parseDouble(rating[2]));

               }else{
                   myRating.setRating(0);
               }
           }else{
               new FadeIn(errorPane).play();
               errorPane.toFront();

       }
   }

    public boolean searchMovie(String filename,String pattern) throws IOException {
        String curLine;
        boolean f=false;
        String []d;
        BufferedReader br = new BufferedReader(new FileReader(filename));
        while((curLine=br.readLine())!=null) {
            d=curLine.split("\\|");
            if(d[1].trim().equalsIgnoreCase(pattern)) {
                f = true;
                break;
            }
        }
        br.close();
        return f;
    }

    public String returnMovie(File fileName,String searchStr) throws FileNotFoundException {
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


    public void handleButtonEvent(ActionEvent event) throws IOException {
        if(event.getSource()==logoutBtn){
            Node node = (Node) event.getSource();
            Stage stage = (Stage)node.getScene().getWindow();
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("sample.fxml")));
            stage.setScene(scene);
            stage.show();
        }

        if(event.getSource()==searchBox){
            showMovieInfo(searchBox.getText().trim());
            }
        if(event.getSource()==watchListBtn){
            new FadeIn(watchListPane).play();
            watchListPane.toFront();
            getTable();
            infoLbl.setText("These are the movies you've watched!");
            topLbl.setText("Watch List");
        }
        if(event.getSource()==topMovieBtn){
            new FadeIn(showcasePane).play();
            showcasePane.toFront();
            infoLbl.setText("Here are the top movies! Enjoy!");
            topLbl.setText("Top Movies");
        }

        if(event.getSource()==mv1Btn){
            goToBtn(1);
        }if(event.getSource()==mv2Btn){
            goToBtn(2);
        }if(event.getSource()==mv3Btn){
            goToBtn(3);
        }if(event.getSource()==mv4Btn){
            goToBtn(4);
        }
        if(event.getSource()==mv5Btn){
            goToBtn(5);
        }
        if(event.getSource()==mv6Btn){
            goToBtn(6);
        }
        }

        public void goToBtn(int line) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader("topMovie.txt"));
            String []data;
            int l=1;
            String mvName;
            String curLine;
            while((curLine=br.readLine())!=null){
                if(line==l) {
                    String s = getShowcaseMovie(new File("movies.txt"),curLine.trim());
                    data = s.split("\\|");
                    mvName = data[1].trim();
                    searchBox.setText(data[1].trim());
                    showMovieInfo(mvName);
                    break;
                }
                l++;
            }
            br.close();
        }

        public String getShowcaseMovie(File fileName,String searchStr) throws FileNotFoundException {
            String s = "Item doesn't exist";
            String data[];
            Scanner scan = new Scanner(fileName);
            while (scan.hasNext()) {
                String line = scan.nextLine();
                data=line.split("\\|");
                if (data[0].trim().equals(searchStr)) {
                    s = line;
                    break;
                }
            }
            scan.close();
            return s;
        }





    public void setText(String name,String username){
        welcomeLbl.setText("Welcome back, " + name);
        userName.setText(name);
        this.user= username;
    }




    public void setmyRating(MouseEvent event) throws IOException {
        String s = c.searchFile(new File("movies.txt"),searchBox.getText());
        String[] item = s.split("\\|");
        File file = new File("rating.txt");
        file.createNewFile();
        boolean flag = getparseRating(file,user,item[1].trim());
        if (flag) {
            System.out.println("thannks help");
            String temp = "temp.txt";
            File oldFile = new File("rating.txt");
            File newFile = new File(temp);
            String username = ""; String movie = ""; String rating = "";
            FileWriter fw = new FileWriter(temp,true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);
            x = new Scanner(new File("rating.txt"));
            x.useDelimiter("[,\n]");
            while(x.hasNext()){
                username = x.next();
                movie = x.next();
                rating = x.next();
                if(movie.equals(item[1].trim()) && username.equals(user)){
                    pw.print(username +","+ movie+","+myRating.getRating()+"\n");
                }else{
                    pw.print(username+","+movie+","+rating+"\n");
                }
            }
            x.close();
            pw.flush();
            pw.close();
            oldFile.delete();
            File dump = new File("rating.txt");
            newFile.renameTo(dump);
            fw.close();
            bw.close();



        } else {
            if (file.exists()) {
                FileWriter fr = new FileWriter(file, true);
                BufferedWriter br = new BufferedWriter(fr);
                br.write(user.trim()+","+item[1].trim()+","+myRating.getRating()+"\n");
                br.close();
                fr.close();

            }
        }
    }

    public String getRating(File fileName,String searchStr,String movieName) throws FileNotFoundException {
        String s = "Item doesn't exist";
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(searchStr) && line.contains(movieName)) {
                s = line;
            }
        }
        scan.close();
        return s;
    }

    public ArrayList<String> getWatchList(File fileName,String searchStr) throws FileNotFoundException {
        ArrayList<String> list = new ArrayList<>();
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(searchStr)) {
                list.add(line);
            }
        }
        scan.close();
        return list;
    }
    public void getTable() throws IOException {
        Collection<WatchList> list = getWatchList(new File("watchList.txt"),user)
                .stream()
                .map(line -> {
                    String[] details = line.split(",");
                    WatchList cd = new WatchList();
                    cd.setId(details[1].toUpperCase());
                    return cd;
                })
                .collect(Collectors.toList());

        ObservableList<WatchList> details = FXCollections.observableArrayList(list);
        movie_col.setCellValueFactory(data -> data.getValue().movieProperty());

        watchListTable.setItems(details);
    }



    public boolean getparseRating(File fileName,String searchStr, String movieName) throws FileNotFoundException {
        boolean f = false;
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(searchStr) && line.contains(movieName)) {
                f = true;
            }
        }
        scan.close();
        return f;

    }



    public int getRatingCount(File file,String movieName) throws IOException {
        int f = 0;
        Scanner scan = new Scanner(file);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(movieName)) {
                f++;
            }
        }
        scan.close();
        return f;

    }
    public int getAverageRating(String filepath,String movieName) throws IOException {
        File file = new File(filepath);
        String currentLine;
        String data[];
        int sum=0;

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        while((currentLine=br.readLine())!=null){
            data=currentLine.split(",");
            if(data[1].equals(movieName)){
                sum+=Float.parseFloat(data[2].trim());
            }
        }
        br.close();
        fr.close();
        return sum;
    }

    public void addWatchList(ActionEvent event) throws IOException {
        File file = new File("watchList.txt");
        FileWriter fw = new FileWriter(file, true);
        BufferedWriter bw = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bw);
        if(watchToggle.isSelected()) {
            if (file.exists()) {
                pw.println(user + "," + searchBox.getText().trim().toLowerCase());
                watchToggle.setText("Watched!");
                System.out.println("okay");


            }


            } else if (!watchToggle.isSelected()) {
                if(file.exists()) {
                    System.out.println("delete");
                    pw.flush();
                    pw.close();
                    fw.close();
                    bw.close();
                    boolean sa = getparseRating(file, user, searchBox.getText().trim().toLowerCase());
                    if (sa) {
                        removeMovie("watchList.txt", user, searchBox.getText().trim().toLowerCase());
                        watchToggle.setText("Not Watched!");

                    }
                }
        }
        pw.flush();
        pw.close();
        fw.close();
        bw.close();

    }

    public static void removeMovie(String filePath, String user, String movie){
        String tempFile = "help.txt";
        File newFile = new File(tempFile);
        File oldFile = new File(filePath);

        String currentLine;
        try{
            FileWriter fa = new FileWriter(tempFile, true);
            BufferedWriter ba = new BufferedWriter(fa);
            PrintWriter pa = new PrintWriter(ba);

            FileReader fs = new FileReader(oldFile);
            BufferedReader bs = new BufferedReader(fs);
            while((currentLine=bs.readLine())!=null){
                if(!(currentLine.equalsIgnoreCase(user+","+movie))){
                    pa.println(currentLine);
                }
            }

            pa.flush();
            pa.close();
            bs.close();
            ba.close();
            fs.close();
            fa.close();

            System.gc();
            oldFile.delete();
            File dump = new File(filePath);
            newFile.renameTo(dump);

        }catch (Exception e){
            System.out.println(e);
        }
    }


}

class WatchList {
    StringProperty id = new SimpleStringProperty();
    public final StringProperty movieProperty() {
        return this.id;
    }

    public final java.lang.String getId() {
        return this.movieProperty().get();
    }

    public final void setId(final java.lang.String id) {
        this.movieProperty().set(id);
    }

}

