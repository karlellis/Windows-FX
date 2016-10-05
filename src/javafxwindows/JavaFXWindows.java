/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxwindows;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.MinimizeIcon;
import jfxtras.scene.control.window.Window;

public class JavaFXWindows extends Application {

    private static int counter = 1;
    BorderPane videoPane;
//    MainViewController mainView;
    BorderPane imagePane;
    BorderPane controlPane;
    AnchorPane mainWindow;
    static Stage primaryStage;
    Stage invisibleStage;
    Group iRoot;
    Scene liveVideo;
    private File aPipe = null;
    private File vPipe = null;
    private java.io.OutputStream audioOutput = null;

    private void init(Stage primaryStage) throws IOException {
        final Group root = new Group();
        try {
            vPipe = createVPipeFile();
            aPipe = createAPipeFile();
        } catch (IOException ex) {
            Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
        }
//        root.setAutoSizeChildren(false);
//        root.prefWidth(1024);
//        root.prefHeight(768);
//        root.setStyle("-fx-background-color: black");
//        Button videoButton = new Button("Add Video");
//        videoButton.setLayoutX(20);
//        videoButton.setLayoutY(20);
//
//        Button imageButton = new Button("Add Image");
//        imageButton.layoutXProperty().set(115);
//        imageButton.layoutYProperty().set(20);
//
//        Button snapButton = new Button("SnapShot");
//        snapButton.layoutXProperty().set(214);
//        snapButton.layoutYProperty().set(20);
//
//        Button closeButton = new Button("Close");
//        closeButton.layoutXProperty().set(304);
//        closeButton.layoutYProperty().set(20);
//
//        root.getChildren().addAll(videoButton, imageButton, closeButton, snapButton);
//        root.getChildren().add(imageButton);

//        primaryStage.setResizable(false);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafxwindows/MainWindow.fxml"));
        mainWindow = (AnchorPane) fxmlLoader.load();
        MainWindowController mainWinCtrl = fxmlLoader.<MainWindowController>getController();
        mainWinCtrl.centerPane.getChildren().add(root);
        primaryStage.initStyle(StageStyle.UNDECORATED);
        liveVideo = new Scene(mainWindow, 1024, 768, Color.BLACK);
        primaryStage.setScene(liveVideo);

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//                System.out.println("Width: " + newSceneWidth);
                invisibleStage.setWidth((double) newSceneWidth);
            }
        });

        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
//                System.out.println("Height: " + newSceneHeight);
                invisibleStage.setHeight((double) newSceneHeight - 35);
            }
        });

        primaryStage.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneX, Number newSceneX) {
//                System.out.println("X: " + newSceneX);
                invisibleStage.setX((double) newSceneX);
            }
        });

        primaryStage.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneY, Number newSceneY) {
//                System.out.println("Y: " + newSceneY);
                invisibleStage.setY((double) newSceneY + 35);
            }
        });

        mainWinCtrl.closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                primaryStage.close();
            }
        });

        mainWinCtrl.snapButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                SnapshotParameters parameters = new SnapshotParameters();
                WritableImage snapshot = new WritableImage((int) mainWinCtrl.centerPane.getWidth(), (int) mainWinCtrl.centerPane.getHeight());
                mainWinCtrl.centerPane.snapshot(parameters, snapshot);
                File file = new File("image.png");
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // to bufferedimage ...
//        WritableImage snapshot = liveVideo.snapshot(null);
//        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(snapshot, null);

        mainWinCtrl.imageButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
//                // create a window with title "My Window"

                Window w = new Window("");
                Window wBorders = new Window("");
                ImageViewController mainView = null;
                MainControlsController mControl = null;
                wBorders.setPickOnBounds(false);

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafxwindows/ImageView.fxml"));
                    imagePane = (BorderPane) fxmlLoader.load();
                    mainView = fxmlLoader.<ImageViewController>getController();
//                    imagePane = (BorderPane) FXMLLoader.load(getClass().getResource("/javafxwindows/ImageView.fxml"));
                    FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/javafxwindows/MainControls.fxml"));
                    controlPane = (BorderPane) fxmlLoader2.load();
                    mControl = fxmlLoader2.getController();
                    mControl.setMediaPlayer(null);
                    mControl.setImageView(mainView.mediaView);
                    mControl.setBorderPane(imagePane);
                    mControl.setMediaType("Image");
//                    MainControlsController mainControl = new MainControlsController(null, mainView.mediaView, imagePane,"Image");
//                    mainControl = mControl;
//                    mainControls = fxmlLoader2.<MainControlsController>getController();
                } catch (IOException ex) {
                    Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
                }
                final ImageViewController imageView = mainView;
                final MainControlsController mainControl = mControl;
//                final MainControlsController mainControl = new MainControlsController(null, mainView.mediaView, imagePane,"Image");

                w.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//                        System.out.println("Width: " + newSceneWidth);
                        wBorders.setPrefWidth((double) newSceneWidth);
                    }
                });

                w.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
//                        System.out.println("Height: " + newSceneHeight);
                        wBorders.setPrefHeight((double) newSceneHeight);
                    }
                });

                w.layoutXProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneX, Number newSceneX) {
                        System.out.println("X: " + newSceneX);
                        wBorders.setLayoutX((double) newSceneX);
                    }
                });

                w.layoutYProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneY, Number newSceneY) {
                        System.out.println("Y: " + newSceneY);
                        wBorders.setLayoutY((double) newSceneY);
                    }
                });

                w.setOnCloseAction((ActionEvent e) -> {
                    wBorders.close();
                    if (mainControl.mediaView != null) {
                        mainControl.mediaView = null;
                    }
                });

                // set the window position to 10,10 (coordinates inside canvas)
                w.setLayoutX(10);
                w.setLayoutY(10);
                // define the initial window size
                w.setPrefSize(300, 200);
//                w.setOpacity(1.0);
                w.getStylesheets().add(getClass().getResource("NoTitleNBorders.css").toExternalForm());
                wBorders.getStylesheets().add(getClass().getResource("NoTitle.css").toExternalForm());
                // either to the left
                wBorders.getLeftIcons().add(new CloseIcon(w));
                // .. or to the right
                wBorders.getRightIcons().add(new MinimizeIcon(w));
                // add some content
                w.minWidth(1);
                w.minHeight(1);
                w.isResizable();
                wBorders.resizeableWindowProperty().set(false);

//                mediaView.fitWidthProperty().bind(w.WidthProperty());
//                mediaView.fitHeightProperty().bind(w.heightProperty());
                AnchorPane.setTopAnchor(imagePane, 0.0);
                AnchorPane.setBottomAnchor(imagePane, 0.0);
                AnchorPane.setLeftAnchor(imagePane, 0.0);
                AnchorPane.setRightAnchor(imagePane, 0.0);
//                w.getContentPane().getChildren().add(new Label("Content... \nof the window#" + counter++));
                // add the window to the canvas
                w.getContentPane().getChildren().add(imagePane);
                wBorders.getContentPane().getChildren().add(controlPane);

                root.getChildren().add(w);
                iRoot.getChildren().add(wBorders);
//                root.getChildren().add(constructWindow());
            }
        });

        mainWinCtrl.captureToggle.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
                Thread capture = null;
//                Thread liveView = null;

                if (mainWinCtrl.captureToggle.isSelected()) {
                    Task task = new Task<Void>() {

                        @Override
                        public Void call() throws IOException, InterruptedException {
                            java.io.OutputStream output = new FileOutputStream(vPipe);

                            new AnimationTimer() {
                                private long lastUpdate = 0;

                                @Override
                                public void handle(long currentNanoTime) {
                                    long frameDelay = (1000 / 25) * 1000000;
                                    if (currentNanoTime - lastUpdate >= frameDelay) {
                                        try {
                                            SnapshotParameters parameters = new SnapshotParameters();
                                            int w = (int) mainWinCtrl.centerPane.getWidth();
                                            int h = (int) mainWinCtrl.centerPane.getHeight();
                                            WritableImage snapshot = new WritableImage(w, h);
                                            mainWinCtrl.centerPane.snapshot(parameters, snapshot);

                                            BufferedImage byteImg = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
                                            Graphics2D graphics = byteImg.createGraphics();
                                            graphics.drawImage(SwingFXUtils.fromFXImage(snapshot, null), 0, 0, w, h, null);

                                            byte[] imageBytes = ((DataBufferByte) byteImg.getRaster().getDataBuffer()).getData();
                                            output.write(imageBytes);
                                            output.flush();
                                            lastUpdate = currentNanoTime;
                                        } catch (FileNotFoundException ex) {
                                            Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                    }
                                }
                            }.start();
                            return null;
                        }

                    };
                    
                    Thread liveView = null;
                    Task taskView = new Task<Void>() {

                        @Override
                        public Void call() throws IOException {
                            String ffplayCommand = "ffplay -loglevel panic -autoexit -window_title LiVE-View -s " + (int) mainWinCtrl.centerPane.getWidth() + "x" + (int) mainWinCtrl.centerPane.getHeight() + " -f rawvideo -pix_fmt bgr24 -i /tmp/fxwindows_video.bgr24";
                            Runtime.getRuntime().exec(ffplayCommand);
                            System.out.println("StringCommand="+ffplayCommand);
                            Tools.sleep(500);
                            Runtime.getRuntime().exec("wmctrl -r LiVE-View -e 0,0,0,360,240");
                            Runtime.getRuntime().exec("wmctrl -r LiVE-View -b add,above");
                            return null;
                        }

                    };
                    capture = new Thread(task);
                    capture.setDaemon(true);
                    capture.start();

                    liveView = new Thread(taskView);
                    liveView.setDaemon(true);
                    liveView.start();
                } else {
                    capture = null;
//                    liveView = null;
                }

            }

        });

        mainWinCtrl.videoButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {
//                // create a window with title "My Window"

                Window w = new Window("");
                Window wBorders = new Window("");
                MainViewController mainView = null;
                MainControlsController mControl = null;
                wBorders.setPickOnBounds(false);

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/javafxwindows/MainView.fxml"));
                    videoPane = (BorderPane) fxmlLoader.load();
                    mainView = fxmlLoader.<MainViewController>getController();

//                    videoPane = (BorderPane) FXMLLoader.load(getClass().getResource("/javafxwindows/MainView.fxml"));
//                    controlPane = (BorderPane) FXMLLoader.load(getClass().getResource("/javafxwindows/MainControls.fxml"));
                    FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("/javafxwindows/MainControls.fxml"));
                    controlPane = (BorderPane) fxmlLoader2.load();
                    mControl = fxmlLoader2.getController();
                    mControl.setMediaPlayer(mainView.mediaPlayer);
                    mControl.setMediaView(mainView.mediaView);
                    mControl.setImageView(null);
                    mControl.setBorderPane(videoPane);
                    mControl.setMediaType("video");
                } catch (IOException ex) {
                    Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
                }
                final MainViewController videoView = mainView;
                final MainControlsController mainControl = mControl;

                w.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//                        System.out.println("Width: " + newSceneWidth);
                        wBorders.setPrefWidth((double) newSceneWidth);
                    }
                });

                w.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
//                        System.out.println("Height: " + newSceneHeight);
                        wBorders.setPrefHeight((double) newSceneHeight);
                    }
                });

                w.layoutXProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneX, Number newSceneX) {
                        System.out.println("X: " + newSceneX);
                        wBorders.setLayoutX((double) newSceneX);
                    }
                });

                w.layoutYProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneY, Number newSceneY) {
                        System.out.println("Y: " + newSceneY);
                        wBorders.setLayoutY((double) newSceneY);
                    }
                });

                w.setOnClosedAction((ActionEvent e) -> {
                    wBorders.close();
                    if (mainControl.mediaPlayer != null) {
                        mainControl.mediaPlayer.stop();
                    }
                });
                // set the window position to 10,10 (coordinates inside canvas)
                w.setLayoutX(10);
                w.setLayoutY(10);

                // define the initial window size
                w.setPrefSize(300, 200);

//                w.setOpacity(1.0);
                w.getStylesheets().add(getClass().getResource("NoTitleNBorders.css").toExternalForm());
                wBorders.getStylesheets().add(getClass().getResource("NoTitle.css").toExternalForm());
                // either to the left
                wBorders.getLeftIcons().add(new CloseIcon(w));
                // .. or to the right
                wBorders.getRightIcons().add(new MinimizeIcon(w));
                // add some content
                w.minWidth(1);
                w.minHeight(1);
                w.isResizable();
                wBorders.resizeableWindowProperty().set(false);
//                mediaView.fitWidthProperty().bind(w.WidthProperty());
//                mediaView.fitHeightProperty().bind(w.heightProperty());
                AnchorPane.setTopAnchor(videoPane, 0.0);
                AnchorPane.setBottomAnchor(videoPane, 0.0);
                AnchorPane.setLeftAnchor(videoPane, 0.0);
                AnchorPane.setRightAnchor(videoPane, 0.0);
//                w.getContentPane().getChildren().add(new Label("Content... \nof the window#" + counter++));
                // add the window to the canvas
                w.getContentPane().getChildren().add(videoPane);
                wBorders.getContentPane().getChildren().add(controlPane);
                root.getChildren().add(w);
                iRoot.getChildren().add(wBorders);
//                root.getChildren().add(constructWindow());
            }
        });
    }

    public double getSampleWidth() {
        return 600;
    }

    public double getSampleHeight() {
        return 500;
    }

    @Override
    public void start(Stage pStage) throws Exception {
        primaryStage = pStage;
        init(primaryStage);

        invisibleStage = new Stage();
        invisibleStage.initOwner(primaryStage);

//        invisibleStage.initStyle(StageStyle.UNDECORATED);
        invisibleStage.initStyle(StageStyle.TRANSPARENT);
//        invisibleStage.initStyle(StageStyle.UTILITY);
//        invisibleStage.setAlwaysOnTop(true);

//        invisibleStage.initModality(Modality.WINDOW_MODAL);
        iRoot = new Group();
        iRoot.setStyle("-fx-background-color: transparent; -fx-border-width: 5; -fx-border-color: red");
        Scene scene = new Scene(iRoot, 1024, 768, Color.TRANSPARENT);
        scene.setFill(null);
        invisibleStage.setScene(scene);

//        invisibleStage.widthProperty().addListener(listener);
//        invisibleStage.widthProperty().addListener(listener);
//    primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();
        invisibleStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    private InternalWindow constructWindow() {
        // content
//        try {
//            anchor = (BorderPane) FXMLLoader.load(getClass().getResource("/javafxwindows/MainView.fxml"));
//        } catch (IOException ex) {
//            Logger.getLogger(JavaFXWindows.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        AnchorPane.setTopAnchor(anchor, 0.0);
//        AnchorPane.setBottomAnchor(anchor, 0.0);
//        AnchorPane.setLeftAnchor(anchor, 0.0);
//        AnchorPane.setRightAnchor(anchor, 0.0);

        ImageView imageView = new ImageView("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a9/Cheetah4.jpg/250px-Cheetah4.jpg");
        // title bar
        BorderPane titleBar = new BorderPane();
        titleBar.setStyle("-fx-background-color: transparent; -fx-padding: 3");
        BorderPane bottomBar = new BorderPane();
        bottomBar.setStyle("-fx-background-color: transparent; -fx-padding: 3");
        BorderPane leftBar = new BorderPane();
        leftBar.setStyle("-fx-background-color: transparent; -fx-padding: 3");
        BorderPane rightBar = new BorderPane();
        rightBar.setStyle("-fx-background-color: transparent; -fx-padding: 3");

//        Label label = new Label("header");
//        titleBar.setLeft(label);
//        Button closeButton = new Button("x");
//        titleBar.setRight(closeButton);
        // title bar + content
        BorderPane windowPane = new BorderPane();
        windowPane.setStyle("-fx-border-width: 2; -fx-border-color: red");
        windowPane.setTop(titleBar);
        windowPane.setBottom(bottomBar);
        windowPane.setRight(rightBar);
        windowPane.setLeft(leftBar);
        windowPane.setCenter(imageView);

        imageView.isPreserveRatio();
        imageView.fitWidthProperty().bind(windowPane.prefWidthProperty());
        imageView.fitHeightProperty().bind(windowPane.prefHeightProperty());

        //apply layout to InternalWindow
        InternalWindow internalWindow = new InternalWindow();
        internalWindow.setRoot(windowPane);
        //drag only by title
//        internalWindow.makeThisDragable();windowPane
//        internalWindow.makeDragable(windowPane);
        internalWindow.makeDragable(titleBar);
        internalWindow.makeDragable(bottomBar);
        internalWindow.makeDragable(leftBar);
        internalWindow.makeDragable(rightBar);
//        internalWindow.makeDragable(label);
        internalWindow.makeResizable(20);
        internalWindow.makeFocusable();
        return internalWindow;
    }

    private File createVPipeFile() throws IOException, InterruptedException {
        File tempFile = new File("/tmp/fxwindows_video.bgr24");
        tempFile.deleteOnExit();
        java.lang.Process p = Runtime.getRuntime().exec("/usr/bin/mkfifo /tmp/fxwindows_video.bgr24");
//        p.waitFor();
        p.destroy();
        System.out.println("Output VideoPipe: " + tempFile.getAbsolutePath());
        return tempFile;
    }

    private File createAPipeFile() throws IOException, InterruptedException {
        File tempFile = new File("/tmp/fxwindows_audio.raw");
        tempFile.deleteOnExit();
        java.lang.Process p = Runtime.getRuntime().exec("/usr/bin/mkfifo /tmp/fxwindows_audio.raw");
//        p.waitFor();
        p.destroy();
        System.out.println("Output AudioPipe: " + tempFile.getAbsolutePath());
        return tempFile;
    }

}
