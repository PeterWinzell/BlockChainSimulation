/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author Peter Winzell
 */
public class BlockChain_1 extends Application {

    private Canvas canvas;

    @Override
    public void start(Stage primaryStage) {

        Pane wrapperPane = new Pane();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(wrapperPane);

        primaryStage.setTitle("Drawing Operations Test");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        System.out.println(" xres: " + primaryScreenBounds.getMaxX() + " yres: " + primaryScreenBounds.getMaxY());
        
        canvas = new Canvas(300, 250);
        // Put canvas in the center of the window
        wrapperPane.getChildren().add(canvas);
        
        
        
        // Bind the width/height property to the wrapper Pane
        canvas.widthProperty().bind(wrapperPane.widthProperty());
        canvas.heightProperty().bind(wrapperPane.heightProperty());
        // redraw when resized
        canvas.widthProperty().addListener(event -> drawShapes(canvas));
        canvas.heightProperty().addListener(event -> drawShapes(canvas));
     

        
        primaryStage.setScene(new Scene(borderPane));
        primaryStage.show();
        

    }

    private void drawShapes(Canvas canvas) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        System.out.println(canvas.getHeight());

        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);

        gc.setLineWidth(5);
        gc.strokeLine(40, 10, 10, 40);
        gc.fillOval(10, 60, 30, 30);
        gc.strokeOval(60, 60, 30, 30);
        gc.fillRoundRect(110, 60, 30, 30, 10, 10);
        gc.strokeRoundRect(160, 60, 30, 30, 10, 10);

        gc.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        gc.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        gc.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);

        gc.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        gc.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
