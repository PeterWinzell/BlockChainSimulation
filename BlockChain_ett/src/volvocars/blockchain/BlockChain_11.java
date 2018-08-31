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
public class BlockChain_11 extends Application implements DfsTraverseListener<BlockChainNode> {

    private Canvas canvas;
    private BlockChainNetwork blockChainNetwork;
    private int height = 800;
    private int width = 1000;
    
    private BlockChainNode prevNode = null;
    
    @Override
    public void start(Stage primaryStage) {
        
        // bug ...
        final boolean resizable = primaryStage.isResizable();
        primaryStage.setResizable(!resizable);
        primaryStage.setResizable(resizable);
        
        
        blockChainNetwork = new BlockChainNetwork(20);
        
        Pane wrapperPane = new Pane();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(wrapperPane);
        
        
        primaryStage.setTitle("Drawing Operations Test");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        System.out.println(" xres: " + primaryScreenBounds.getMaxX() + " yres: " + primaryScreenBounds.getMaxY());
        
        canvas = new Canvas(width, height);
        // Put canvas in the center of the window
        wrapperPane.getChildren().add(canvas);
        
        
        
        // Bind the width/height property to the wrapper Pane
        canvas.widthProperty().bind(wrapperPane.widthProperty());
        canvas.heightProperty().bind(wrapperPane.heightProperty());
        // redraw when resized
        canvas.widthProperty().addListener(event -> drawShapes(canvas));
        canvas.heightProperty().addListener(event -> drawShapes(canvas));
     
        blockChainNetwork.initNetwork(width, height);
        blockChainNetwork.addTraverseListener(this);
        
        primaryStage.setScene(new Scene(borderPane,width,height));
        primaryStage.show();
        

    }

    private void drawShapes(Canvas canvas) {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        blockChainNetwork.depthFirstSearch(0);
        gc.stroke();
        blockChainNetwork.falsify_visited();
        prevNode = null;
          
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void apply(BlockChainNode aNode) {
        
       GraphicsContext gc = canvas.getGraphicsContext2D();
       
       int x = aNode.getX();
       int y = aNode.getY();
       
       gc.setFill(Color.BLACK);
       gc.fillOval(x - 10.0,y-10.0,10.0,10.0);
       
       //drawEdge
       if (prevNode == null){
            prevNode = aNode;
       }
       else{
           int x2 = prevNode.getX();
           int y2 = prevNode.getY();
           gc.setStroke(Color.RED);
           gc.moveTo(x2-5, y2-5);
           gc.lineTo(x-5, y-5);
           prevNode = aNode;
       }
       
    }

}
