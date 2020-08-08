package sample;

import animatefx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;

public class adminControl implements Initializable {

    Controller c = new Controller();

    @FXML
    Label noUsers,noMovies,addMovieStat,idLbl,nameLbl,dirLbl,genreLbl,yearLbl,deleteStat,topMovieStat;
    @FXML
    Button addMovie,dashboardBtn,addMovieBtn,viewMovieBtn,deleteMovieBtn,deleteMovie,adminLogout,topMovieBtn,addTopMvBtn;
    @FXML
    TextField mvID,mvName,mvDir,mvYear,mvGenre,deleteSearch,topMovieBox;
    @FXML
    Pane dashboardPane,addMoviePane,allMoviePane,deleteMoviePane,deleteTrue,topMoviePane;
    @FXML
    TableView<MovieData> movieTable;
    @FXML
    ImageView imageView,btnClose;
    @FXML
    TableColumn<MovieData,String> col_id, col_name,col_dir,col_genre,col_year;





    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setDash();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void getTable() throws IOException {
        Collection<MovieData> list = Files.readAllLines(new File("movies.txt").toPath())
                .stream()
                .map(line -> {
                    String[] details = line.split("\\|");
                    MovieData cd = new MovieData();
                    cd.setId(details[0]);
                    cd.setName(details[1]);
                    cd.setDir(details[2]);
                    cd.setGenre(details[3]);
                    cd.setYear(details[4]);
                    return cd;
                })
                .collect(Collectors.toList());

        ObservableList<MovieData> details = FXCollections.observableArrayList(list);
        col_id.setCellValueFactory(data -> data.getValue().movieProperty());
        col_name.setCellValueFactory(data -> data.getValue().nameProperty());
        col_dir.setCellValueFactory(data -> data.getValue().dirProperty());
        col_genre.setCellValueFactory(data -> data.getValue().genreProperty());
        col_year.setCellValueFactory(data -> data.getValue().yearProperty());

        movieTable.setItems(details);
    }


    public void setDash() throws FileNotFoundException {
        noUsers.setText(dashboard(new File("users.txt")));
        noMovies.setText(dashboard(new File("movies.txt")));
    }
    public static void removeItem(String filepath, String id, int positionOfItem){
        int position = positionOfItem - 1;
        String tempFile = "temp.txt";
        File oldFile = new File(filepath);
        File newFile = new File(tempFile);

        String currentLine;
        String data[];
        try{
            FileWriter fw = new FileWriter(tempFile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter pw = new PrintWriter(bw);

            FileReader fr = new FileReader(filepath);
            BufferedReader br = new BufferedReader(fr);
            while((currentLine=br.readLine())!=null){
                data=currentLine.split(" | ");
                if(!(data[position].equalsIgnoreCase(id))){
                    pw.println(currentLine);
                }
            }
            pw.flush();
            pw.close();
            br.close();
            bw.close();
            fr.close();
            fw.close();

            System.gc();
            oldFile.delete();
            File dump = new File(filepath);
            newFile.renameTo(dump);


        }catch (Exception e){
            System.out.println(e);
        }
    }




    public String dashboard(File file) throws FileNotFoundException {
        Scanner f = new Scanner(file);
        int count = 0;
        while (f.hasNextLine()) {
            count++;
            f.nextLine();
        }
        f.close();
        return Integer.toString(count);
    }

    public void handleButtonEvent(ActionEvent event) throws IOException {
        if(event.getSource()==addMovieBtn){
            addMovieStat.setText("");
            new ZoomIn(addMoviePane).play();
            addMoviePane.toFront();
        }

        if(event.getSource()==adminLogout){
            Node node = (Node) event.getSource();
            Stage stage = (Stage)node.getScene().getWindow();
            stage.close();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("sample.fxml")));
            stage.setScene(scene);
            stage.show();
        }
        if(event.getSource()==viewMovieBtn){

            new ZoomIn(allMoviePane).play();

            allMoviePane.toFront();
            getTable();


        }
        if(event.getSource()==topMovieBtn){
            topMovieStat.setText("");
            new ZoomIn(topMoviePane).play();
            topMoviePane.toFront();
            topMovieBox.clear();

        }
        if(event.getSource()==dashboardBtn){
            setDash();
            new ZoomIn(dashboardPane).play();
            dashboardPane.toFront();
        }
        if(event.getSource()==deleteMovieBtn){
            deleteSearch.clear();
            Image fImg = new Image("main.gif");
            imageView.setImage(fImg);
            deleteStat.setText("Enter movie id to search and delete");
            new ZoomIn(deleteMoviePane).play();
            deleteMoviePane.toFront();

        }
        if(event.getSource()==deleteMovie){
            deleteTrue.setVisible(false);
            removeItem("movies.txt","MV"+deleteSearch.getText(),1);
            imageView.setVisible(true);
            Image fImage = new Image("dust2.gif");
            imageView.setImage(fImage);
            deleteStat.setTextFill(Color.GREEN);
            deleteStat.setText("Movie deleted from record!");
        }
        if(event.getSource()==deleteSearch){
            imageView.setVisible(false);
            deleteTrue.setVisible(false);
            if(deleteSearch.getText().isEmpty()){
                deleteStat.setTextFill(Color.TOMATO);
                deleteStat.setText("Enter search term...");
            }else {
                File file = new File("movies.txt");
                boolean f = c.parseFile(file, deleteSearch.getText().trim());
                String str = findMovie(file,"MV"+deleteSearch.getText().trim());
                String[] item = str.split("\\|");
                if (f==true){
                    deleteTrue.setVisible(true);
                    idLbl.setText(item[0]);
                    nameLbl.setText(item[1]);
                    dirLbl.setText(item[2]);
                    genreLbl.setText(item[3]);
                    yearLbl.setText(item[4]);
                    deleteStat.setText("");

                } else {
                    imageView.setVisible(true);
                    Image fImage = new Image("file404.gif");
                    imageView.setImage(fImage);
                    deleteStat.setTextFill(Color.TOMATO);
                    deleteStat.setText("Movie is not in the record.");
                }
            }
        }
        if(event.getSource()==addMovie) {
            String genre;
            if (mvID.getText().isEmpty() || mvName.getText().isEmpty() || mvDir.getText().isEmpty() || mvGenre.getText().isEmpty() || mvYear.getText().isEmpty()) {
                addMovieStat.setText("Enter all details");
                addMovieStat.setTextFill(Color.TOMATO);
            } else {
                File file = new File("movies.txt");
                file.createNewFile();
                boolean flag = c.parseFile(file, "MV"+mvID.getText());
                if (flag) {
                    addMovieStat.setText("Movie already exists!");
                    addMovieStat.setTextFill(Color.TOMATO);
                } else {
                    if (file.exists()) {
                        switch(mvGenre.getText().trim().toLowerCase()){
                            case "action":
                                genre="Ac";
                                break;
                            case "thriller":
                                genre="Th";
                                break;
                            case "drama":
                                genre="Dr";
                                break;
                            case "adventure":
                                genre="Ad";
                                break;
                            case "animation":
                                genre="An";
                                break;
                            case "fantasy":
                                genre="Fa";
                                break;
                            case "sci-fi":
                                genre="Sci";
                                break;
                            case "comedy":
                                genre="Co";
                                break;
                            case "horror":
                                genre="Ho";
                                break;
                            case "romance":
                                genre="Ro";
                                break;
                            case "crime":
                                genre="Cr";
                                break;
                            case "mystery":
                                genre="My";
                                break;
                            default:genre=mvGenre.getText().trim();
                        }
                        FileWriter fr = new FileWriter(file, true);
                        BufferedWriter br = new BufferedWriter(fr);
                        br.write("MV" + mvID.getText() + " | " + mvName.getText() + " | " + mvDir.getText() + " | " + genre + " | " + mvYear.getText() + "\n");
                        br.close();
                        fr.close();
                        mvID.clear();mvName.clear();mvYear.clear();mvGenre.clear();mvDir.clear();
                        addMovieStat.setTextFill(Color.GREEN);
                        addMovieStat.setText("Movie added successfully.");
                    }
                }
            }
        }
    }


    public void handleMouseEvent(MouseEvent event) {
        if(event.getSource()==btnClose){
            System.exit(1);
        }
    }

    public boolean topMovieParse(File fileName,String searchStr) throws FileNotFoundException {
        boolean f = false;
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.equalsIgnoreCase(searchStr)) {
                f = true;
            }
        }
        scan.close();
        return f;

    }
    public void addTopMovie() throws IOException {
        boolean f = c.parseFile(new File("movies.txt"),"MV"+topMovieBox.getText().trim());
        if(f){
            String s = c.searchFile(new File("movies.txt"),"MV"+topMovieBox.getText().trim());
            String[] item = s.split("\\|");
            File file = new File("topMovie.txt");
            file.createNewFile();
            boolean flag = topMovieParse(file,"MV"+topMovieBox.getText().trim());
            if (flag) {
                System.out.println("thannks help");
                topMovieStat.setTextFill(Color.TOMATO);
                topMovieStat.setText("Movie already in top list.");

            } else {
                int count = getLineCount("topMovie.txt");
                if(count>=0 && count<6) {
                    if (file.exists()) {
                        FileWriter fr = new FileWriter(file, true);
                        BufferedWriter br = new BufferedWriter(fr);
                        br.write("MV" + topMovieBox.getText().trim() + "\n");
                        br.close();
                        fr.close();
                        topMovieBox.clear();
                        topMovieStat.setTextFill(Color.GREEN);
                        topMovieStat.setText("Movie added in Showcase");
                    }
                }else{
                    topMovieStat.setTextFill(Color.TOMATO);
                    topMovieStat.setText("Showcase slot full");
                }
            }
        }else{
            topMovieStat.setTextFill(Color.TOMATO);
            topMovieStat.setText("Movie not in record.");
        }


    }

    public int getLineCount(String filename) throws IOException {
        String curLine;
        int count =0;
        BufferedReader br = new BufferedReader(new FileReader(filename));
        while((curLine=br.readLine())!=null)
            count++;
        br.close();
        return count;
    }
    public String findMovie(File fileName,String searchStr) throws FileNotFoundException {
        String s = "Item doesn't exist";
        Scanner scan = new Scanner(fileName);
        while (scan.hasNext()) {
            String line = scan.nextLine();
            if (line.contains(searchStr)) {
                s = line;
                break;
            }
        }
        scan.close();
        return s;
    }
}


class MovieData {
    StringProperty id = new SimpleStringProperty();
    StringProperty name = new SimpleStringProperty();
    StringProperty dir = new SimpleStringProperty();
    StringProperty genre = new SimpleStringProperty();
    StringProperty year = new SimpleStringProperty();
    public final StringProperty movieProperty() {
        return this.id;
    }

    public final java.lang.String getId() {
        return this.movieProperty().get();
    }

    public final void setId(final java.lang.String id) {
        this.movieProperty().set(id);
    }

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final java.lang.String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final java.lang.String name) {
        this.nameProperty().set(name);
    }

    public final StringProperty dirProperty() {
        return this.dir;
    }

    public final java.lang.String getDir() {
        return this.dirProperty().get();
    }

    public final void setDir(final java.lang.String dir) {
        this.dirProperty().set(dir);
    }

    public final StringProperty genreProperty() {
        return this.genre;
    }

    public final java.lang.String getGenre() {
        return this.genreProperty().get();
    }

    public final void setGenre(final java.lang.String genre) {
        this.genreProperty().set(genre);
    }
    public final StringProperty yearProperty() {
        return this.year;
    }

    public final java.lang.String getYear() {
        return this.yearProperty().get();
    }

    public final void setYear(final java.lang.String year) {
        this.yearProperty().set(year);
    }



}



