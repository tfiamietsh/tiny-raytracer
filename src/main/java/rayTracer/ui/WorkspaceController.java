package rayTracer.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import rayTracer.tracer.misc.Texture;
import rayTracer.tracer.script.Script;
import rayTracer.util.Log;

import java.awt.Dimension;
import java.awt.Desktop;
import java.io.File;

public class WorkspaceController {
    public void setPrimaryStage(Stage primaryStage) { this.primaryStage = primaryStage; }

    public void initialize() {
        prerenderImageView.setImage(new Image(new File(defaultSmallFilename).toURI().toString()));
        renderImageView.setImage(new Image(new File(defaultFilename).toURI().toString()));

        String docsText = rayTracer.util.File.read("data/docs.txt");
        docsTextArea.setText(docsText);

        String defaultOutputDirectory = System.getProperty("user.dir");
        directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File(defaultOutputDirectory));
        outputDirectoryTextField.setText(defaultOutputDirectory);
        String defaultResourcesDirectory = defaultOutputDirectory + "\\resources";
        resourcesDirectoryTextField.setText(defaultResourcesDirectory);
        Texture.setDirectory(defaultResourcesDirectory);

        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(defaultOutputDirectory + "\\scripts"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("RTS files (*.rts)", "*.rts"));
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt"));

        int coresCount = Runtime.getRuntime().availableProcessors();
        coresSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, coresCount));

        shadowSamplesNumChoiceBox.setItems(FXCollections.observableArrayList(
                "1x", "4x", "9x", "16x", "25x", "36x", "49x"));
        shadowSamplesNumChoiceBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void onClickNewScript() { scriptTextArea.setText(""); }

    @FXML
    public void onClickOpenScript() {
        File file = fileChooser.showOpenDialog(primaryStage);
        String text = rayTracer.util.File.read(file);
        scriptTextArea.setText(text);
        errorsTextArea.setText("");
    }

    @FXML
    public void onClickSaveScript() {
        File file = fileChooser.showSaveDialog(primaryStage);
        rayTracer.util.File.write(file, scriptTextArea.getText());
    }

    @FXML
    public void onClickExit() { primaryStage.close(); }

    @FXML
    public void onClickAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Tiny Ray Tracer on Pure Java ver .98 2nd Edition");
        alert.setContentText("by tfiamietsh, 2022-2023");
        alert.showAndWait();
    }

    @FXML
    public void onClickPrerender() {
        String outputFullPath = outputDirectoryTextField.getText() + "\\pre" +
                filenameTextField.getText() + ".png";
        Script scriptExecutor = new Script();
        String shadowSamplesNumString = shadowSamplesNumChoiceBox.getValue();
        int shadowSamplesNum = (int) Math.sqrt(Integer.parseInt(shadowSamplesNumString.substring(0,
                shadowSamplesNumString.length() - 1)));
        String errors = scriptExecutor.tryToExecuteScript(outputFullPath, scriptTextArea.getText(),
                new Dimension(221, 147), coresSpinner.getValue(),
                (int) maxRayDepthSlider.getValue(), shadowSamplesNum);

        if (!errors.equals("")) {
            errorsTextArea.setStyle("-fx-text-fill: #EE0000;");
            errorsTextArea.setText(errors);
            prerenderImageView.setImage(new Image(new File(defaultSmallFilename).toURI().toString()));
        } else {
            errorsTextArea.setStyle("-fx-text-fill: #777777;");
            errorsTextArea.setText("No problems");
            prerenderImageView.setImage(new Image(new File(outputFullPath).toURI().toString()));
        }
    }

    @FXML
    public void onClickRender() {
        String outputFullPath = outputDirectoryTextField.getText() + '\\' +
                filenameTextField.getText() + ".png";
        Script scriptExecutor = new Script();
        String shadowSamplesNumString = shadowSamplesNumChoiceBox.getValue();
        int shadowSamplesNum = (int) Math.sqrt(Integer.parseInt(shadowSamplesNumString.substring(0,
                shadowSamplesNumString.length() - 1)));
        String errors = scriptExecutor.tryToExecuteScript(outputFullPath, scriptTextArea.getText(),
                new Dimension(Integer.parseInt(dimensionWidthTextField.getText()),
                Integer.parseInt(dimensionHeightTextField.getText())),
                coresSpinner.getValue(), (int) maxRayDepthSlider.getValue(), shadowSamplesNum);

        if (!errors.equals("")) {
            errorsTextArea.setStyle("-fx-text-fill: #EE0000;");
            errorsTextArea.setText(errors);
            renderImageView.setImage(new Image(new File(defaultFilename).toURI().toString()));
        } else {
            errorsTextArea.setStyle("-fx-text-fill: #777777;");
            errorsTextArea.setText("No problems");
            renderImageView.setImage(new Image(new File(outputFullPath).toURI().toString()));
        }
    }

    @FXML
    public void onClickSelectOutputDirectory() {
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null)
            outputDirectoryTextField.setText(selectedDirectory.getPath());
    }

    @FXML
    public void onClickOpenOutputDirectory() {
        try { Desktop.getDesktop().open(new File(outputDirectoryTextField.getText())); }
        catch (Exception e) { Log.println(e.getMessage()); }
    }

    @FXML
    public void onClickSelectResourcesDirectory() {
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            resourcesDirectoryTextField.setText(selectedDirectory.getPath());
            Texture.setDirectory(selectedDirectory.getPath());
        }
    }

    @FXML
    public void onClickOpenResourcesDirectory() {
        try { Desktop.getDesktop().open(new File(resourcesDirectoryTextField.getText())); }
        catch (Exception e) { Log.println(e.getMessage()); }
    }

    private Stage primaryStage;
    private static final String defaultFilename = "data/renderThumbnail.png",
            defaultSmallFilename = "data/prerenderThumbnail.png";
    @FXML private TextArea scriptTextArea, errorsTextArea, docsTextArea;
    @FXML private TextField dimensionWidthTextField, dimensionHeightTextField,
            filenameTextField, outputDirectoryTextField, resourcesDirectoryTextField;
    @FXML private ImageView prerenderImageView, renderImageView;
    @FXML private Spinner<Integer> coresSpinner;
    @FXML private Slider maxRayDepthSlider;
    @FXML private ChoiceBox<String> shadowSamplesNumChoiceBox;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;
}
