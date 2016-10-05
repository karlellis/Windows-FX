/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxwindows;

import java.io.File;
import static java.lang.StrictMath.floor;
import static java.lang.String.format;
import java.net.URL;
import java.util.ResourceBundle;
import static javafx.application.Platform.runLater;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Duration;
//import static mediaplayerfx.MediaPlayerFX.MP;

/**
 * FXML Controller class
 *
 * @author karl
 */
public class MainViewController implements Initializable {

    MediaPlayer mediaPlayer;
    Duration duration;
    Media media;
    double width;
    double height;
    MediaView mediaView;
    boolean notPause = true;
    Canvas canvas;
    Group rootGrp;
    GraphicsContext gc;

    @FXML
    private Label time;
    @FXML
    Button fullScreenButton;
    @FXML
    private BorderPane mainPanelView;
    @FXML
    private Button filesButton;
    @FXML
    private Button firstButton;
    @FXML
    private Button backButton;
    @FXML
    private Button playButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button forwardButton;
    @FXML
    private Button lastButton;
    @FXML
    private Button reloadButton;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Pane spacePane1;
    @FXML
    private Pane spacePane;
//    @FXML
//    private ImageView snapView;
    @FXML
    private ToolBar toolBar;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        toolBar.setStyle("-fx-background-color: transparent");
        toolBar.setVisible(false);
        mediaView = new MediaView();
//        mediaPlayer = new
        rootGrp = new Group();
        canvas = new Canvas(100, 100);
        gc = canvas.getGraphicsContext2D();
        mainPanelView.setCenter(mediaView);
        mainPanelView.setStyle("-fx-background-color: transparent");
        time.setTextFill(Color.WHITESMOKE);

        volumeSlider.valueProperty().addListener((obs, oldValue, newValue)
                -> volumeSliderStateChanged());

        Image playButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-playback.png"));
        playButton.setGraphic(new ImageView(playButtonImage));
        playButton.setStyle("-fx-background-color: Black");
        playButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
        });
        
        playButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            playButton.setStyle("-fx-background-color: Black");
            playButton.setStyle("-fx-body-color: Black");
        });
        playButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            playButton.setStyle("-fx-background-color: Black");
        });

        Image pausedButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-playback-pause.png"));
        pauseButton.setGraphic(new ImageView(pausedButtonImage));
        pauseButton.setStyle("-fx-background-color: Black");
        pauseButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
        });
        pauseButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            pauseButton.setStyle("-fx-background-color: Black");
            pauseButton.setStyle("-fx-body-color: Black");
        });
        pauseButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            pauseButton.setStyle("-fx-background-color: Black");
        });

        Image filesButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-eject.png"));
        filesButton.setGraphic(new ImageView(filesButtonImage));
        filesButton.setStyle("-fx-background-color: Black");
        filesButton.setOnAction((ActionEvent e) -> {
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter mediaFilter = new FileChooser.ExtensionFilter("Supported Media files (*.media)", "*.ogg", "*.ogv", "*.mp4", "*.m4v", "*.mpg", "*.flv", "*.mov", "*.mp3", "*.wav", "*.wma", "*.m4a");
            FileChooser.ExtensionFilter allFilesFilter = new FileChooser.ExtensionFilter("All files (*.*)", "*.*", "*.*");
            fc.getExtensionFilters().add(mediaFilter);
            fc.getExtensionFilters().add(allFilesFilter);
            File file = fc.showOpenDialog(null);
            if (file != null) {
                String path = file.getAbsolutePath();
                path = path.replace("\\", "/");
                media = new Media(new File(path).toURI().toString());
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                }

                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setAutoPlay(true);
                mediaView.setMediaPlayer(mediaPlayer);
                double sValue = volumeSlider.getValue();
                double volume = sValue / 100f;
                mediaPlayer.setVolume(volume);

                mediaPlayer.currentTimeProperty().addListener((observableValue, oldDuration, newDuration) -> {
                    updateValues();
                });

                mediaPlayer.setOnReady(() -> {
                    duration = mediaPlayer.getMedia().getDuration();
                    updateValues();
                });

                mediaView.fitHeightProperty().bind(mainPanelView.heightProperty());
                mediaView.fitWidthProperty().bind(mainPanelView.widthProperty());
            }
        });
        filesButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            filesButton.setStyle("-fx-background-color: Black");
            filesButton.setStyle("-fx-body-color: Black");
        });
        filesButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            filesButton.setStyle("-fx-background-color: Black");
        });

        Image firstButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-skip-backward.png"));
        firstButton.setGraphic(new ImageView(firstButtonImage));
        firstButton.setStyle("-fx-background-color: Black");
        firstButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
                mediaPlayer.stop();
            }
        });
        firstButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            firstButton.setStyle("-fx-background-color: Black");
            firstButton.setStyle("-fx-body-color: Black");
        });
        firstButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            firstButton.setStyle("-fx-background-color: Black");
        });

        Image backButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-seek-backward.png"));
        backButton.setGraphic(new ImageView(backButtonImage));
        backButton.setStyle("-fx-background-color: Black");
        backButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().divide(1.5));
            }
        });
        backButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            backButton.setStyle("-fx-background-color: Black");
            backButton.setStyle("-fx-body-color: Black");
        });
        backButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            backButton.setStyle("-fx-background-color: Black");
        });

        Image forwardButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-seek-forward.png"));
        forwardButton.setGraphic(new ImageView(forwardButtonImage));
        forwardButton.setStyle("-fx-background-color: Black");
        forwardButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().multiply(1.5));
            }
        });

        forwardButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            forwardButton.setStyle("-fx-background-color: Black");
            forwardButton.setStyle("-fx-body-color: Black");
        });
        forwardButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            forwardButton.setStyle("-fx-background-color: Black");
        });

        Image lastButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/media-skip-forward.png"));
        lastButton.setGraphic(new ImageView(lastButtonImage));
        lastButton.setStyle("-fx-background-color: Black");
        lastButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getTotalDuration());
                mediaPlayer.stop();
            }
        });
        lastButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            lastButton.setStyle("-fx-background-color: Black");
            lastButton.setStyle("-fx-body-color: Black");
        });
        lastButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            lastButton.setStyle("-fx-background-color: Black");
        });

        Image reloadButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/reload_button.png"));
        reloadButton.setGraphic(new ImageView(reloadButtonImage));
        reloadButton.setStyle("-fx-background-color: Black");
        reloadButton.setOnAction((ActionEvent e) -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(mediaPlayer.getStartTime());
            }
        });
        reloadButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            reloadButton.setStyle("-fx-background-color: Black");
            reloadButton.setStyle("-fx-body-color: Black");
        });
        reloadButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            reloadButton.setStyle("-fx-background-color: Black");
        });

        Image fullScreenButtonImage = new Image(getClass().getResourceAsStream("/mediaplayerfx/tango/view-fullscreen.png"));
        fullScreenButton.setGraphic(new ImageView(fullScreenButtonImage));
        fullScreenButton.setStyle("-fx-background-color: Black");
        fullScreenButton.setOnAction((ActionEvent e) -> {
//            if (MP.isFullScreen()) {
//                MP.setFullScreen(false);
//            } else {
//                MP.setFullScreen(true);
//            }
        });
        fullScreenButton.addEventHandler(MouseEvent.MOUSE_ENTERED, (MouseEvent e) -> {
            fullScreenButton.setStyle("-fx-background-color: Black");
            fullScreenButton.setStyle("-fx-body-color: Black");
        });
        fullScreenButton.addEventHandler(MouseEvent.MOUSE_EXITED, (MouseEvent e) -> {
            fullScreenButton.setStyle("-fx-background-color: Black");
        });
    }

    protected void updateValues() {
        if (time != null) {
            runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                time.setText(formatTime(currentTime, duration));

            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60
                - elapsedMinutes * 60;

        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationMinutes * 60;
            if (durationHours > 0) {
                return format("%d:%02d:%02d/%d:%02d:%02d",
                        elapsedHours, elapsedMinutes, elapsedSeconds,
                        durationHours, durationMinutes, durationSeconds);
            } else {
                return format("%02d:%02d/%02d:%02d",
                        elapsedMinutes, elapsedSeconds, durationMinutes,
                        durationSeconds);
            }
        } else if (elapsedHours > 0) {
            return format("%d:%02d:%02d", elapsedHours,
                    elapsedMinutes, elapsedSeconds);
        } else {
            return format("%02d:%02d", elapsedMinutes,
                    elapsedSeconds);
        }
    }

    private void volumeSliderStateChanged() {
        double sValue = volumeSlider.getValue();
        double volume = sValue / 100f;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume);
        }
    }

}
