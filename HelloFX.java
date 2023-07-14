import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.apache.logging.log4j.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class HelloFX extends Application {

    private static final Logger log = LogManager.getLogger(HelloFX.class);

    int elementCount;
    String textToEnc, paintingPath;
    String textToDec = "";

    @Override
    public void start (Stage primaryStage) {

        primaryStage.setTitle("Cryptography");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 0, 0, 50));

        RadioButton Enc = new RadioButton("Encryption");
        grid.add(Enc, 0, 0);
        RadioButton Dec = new RadioButton("Decryption");
        grid.add(Dec, 0, 1);
        ToggleGroup Chose = new ToggleGroup();
        Enc.setToggleGroup(Chose);
        Dec.setToggleGroup(Chose);

        Button openFolderButton = new Button("Open file");
        HBox openButton = new HBox(10);
        openButton.setAlignment(Pos.BOTTOM_LEFT);
        openButton.getChildren().add(openFolderButton);
        grid.add(openButton, 0, 2);
        openFolderButton.setVisible(false);

        Button confirmButton = new Button("Ready");
        HBox confButton = new HBox(10);
        confButton.setAlignment(Pos.BOTTOM_LEFT);
        confButton.getChildren().add(confirmButton);
        grid.add(confButton, 0, 3);
        confButton.setVisible(false);

        ImageView imageShow = new ImageView();
        imageShow.setFitWidth(400);
        imageShow.setFitHeight(300);
        grid.add(imageShow, 0, 6);

        Text textAction = new Text();
        grid.add(textAction, 2, 4);

        openFolderButton.setOnAction(event -> {
            textAction.setVisible(false);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File("C:\\"));
            fileChooser.setTitle("Open Folder");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Paintings", "*.png", "*.jpg")
            );
            File selectedFolder = fileChooser.showOpenDialog(primaryStage);
            paintingPath = selectedFolder.getAbsolutePath();
            Image image = new Image(selectedFolder.toURI().toString());
            imageShow.setImage(image);
            confButton.setVisible(true);
        });
        Label textText = new Label("Text:");
        grid.add(textText, 1, 0);
        TextField textEnc = new TextField();
        grid.add(textEnc, 1, 1);
        textText.setVisible(false);
        textEnc.setVisible(false);

        Label textElemnts = new Label("Number of characters :");
        grid.add(textElemnts, 1, 0);
        TextField simbsDec = new TextField();
        grid.add(simbsDec, 1, 1);
        textElemnts.setVisible(false);
        simbsDec.setVisible(false);

        Text textDec = new Text();
        textDec.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(textDec, 1, 6);
        Text numberElements = new Text();
        grid.add(numberElements, 1, 2);
        numberElements.setVisible(false);

        confirmButton.setOnAction(event -> {
            if (Chose.getSelectedToggle() == Enc) {
                textToEnc = textEnc.getText();
                elementCount = textToEnc.length();
                numberElements.setText("Number of characters " + elementCount);
                if (elementCount == 0) {
                    numberElements.setFill(Color.RED);
                } else {
                    if (paintingEncryption()) {
                        textAction.setText("Done");
                    } else {
                        textAction.setText("Some problems :(");
                    }
                }
                numberElements.setVisible(true);
                textAction.setVisible(true);
            } else{
                textAction.setVisible(false);
                elementCount = Integer.parseInt(simbsDec.getText());
                if (paintingDecryption()){
                    textDec.setText("Text: " + textToDec);
                    textDec.setVisible(true);
                }
                else{
                    textAction.setText("Some problems :(");
                    textAction.setVisible(true);
                }
            }
        });

        Enc.setOnAction(event -> {
            textDec.setVisible(false);
            textText.setVisible(true);
            textAction.setVisible(false);
            textEnc.setVisible(true);
            openFolderButton.setVisible(true);
            textElemnts.setVisible(false);
            simbsDec.setVisible(false);
            simbsDec.clear();
        });

        Dec.setOnAction(event -> {
            textText.setVisible(false);
            textEnc.setVisible(false);
            textAction.setVisible(false);
            openFolderButton.setVisible(true);
            textElemnts.setVisible(true);
            simbsDec.setVisible(true);
            numberElements.setVisible(false);
            textEnc.clear();
        });
        Scene scene = new Scene(grid, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Boolean paintingEncryption() {
        try {
            int[][] coordinates = new int[elementCount][2];
            BufferedImage paintingFile = ImageIO.read(new File(paintingPath));

            int x, y, alpha, red, green, blue, colorModel, newColorModel;
            for (int i = 0; i < elementCount; i++){
                x =((int) (Math.random() * (paintingFile.getWidth()) - 2 * elementCount)) % paintingFile.getWidth();
                y =((int) (Math.random() * paintingFile.getHeight())) % paintingFile.getHeight();
                coordinates[i][0] = x;
                coordinates[i][1] = y;
                colorModel = paintingFile.getRGB(x,y);

                alpha = (colorModel >> 24) & 0xFF;
                red = (colorModel >> 16) & 0xFF;
                green = (colorModel >> 8) & 0xFF;
                blue = textToEnc.charAt(i);
                newColorModel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                paintingFile.setRGB(x, y, newColorModel);
            }

            int j = 0;
            int k = 0;
            for (int i = paintingFile.getWidth() - 1; i > paintingFile.getWidth() - 2 * elementCount - 1; i--){
                x = i;
                y = 0;
                colorModel = paintingFile.getRGB(x,y);
                alpha = (colorModel >> 24) & 0xFF;
                red = (colorModel >> 16) & 0xFF;
                if (j % 2 == 0){
                    green = coordinates[k][0] / 256;
                    blue = coordinates[k][0] % 256;
                } else {
                    green = coordinates[k][1] / 256;
                    blue = coordinates[k][1] % 256;
                    k++;
                }
                newColorModel = (alpha << 24) | (red << 16) | (green << 8) | blue;
                paintingFile.setRGB(x, y, newColorModel);
                j++;
            }
            ImageIO.write(paintingFile, "png", new File(paintingPath));
        } catch (IOException e) {
            log.error("an unexpected error has occurred", e);
            return false;
        }
        return true;
    }

    public Boolean paintingDecryption(){
        try{
            int[][] coordinates = new int[elementCount][2];
            BufferedImage paintingFile = ImageIO.read(new File(paintingPath));

            int x, y, green, blue, colorModel;
            int j = 0;
            int k = 0;
            for (int i = paintingFile.getWidth() - 2 * elementCount; i < paintingFile.getWidth(); i++){
                x = i;
                y = 0;
                colorModel = paintingFile.getRGB(x,y);
                green = (colorModel >> 8) & 0xFF;
                blue = colorModel & 0xFF;
                if (j % 2 == 0){
                    coordinates[k][1] = 256 * green + blue;
                } else {
                    coordinates[k][0] = 256 * green + blue;
                    k++;
                }
                j++;
            }
            for (int i = 0; i < elementCount; i++){
                x = coordinates[i][0];
                y = coordinates[i][1];
                colorModel = paintingFile.getRGB(x,y);
                textToDec = (char)(colorModel & 0xFF) + textToDec;
            }
        } catch (IOException e){
            log.error("an unexpected error has occurred", e);
            return false;
        }
        return true;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
