/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.paint.Color;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 *
 * @author Peter Winzell
 */
public class BlockChain_11 extends Application implements DfsTraverseListener<BlockChainNode> {

    private Canvas canvas;
    private BlockChainNetwork blockChainNetwork;
    private BlockChainNode dragNode = null;
    private int height = 800;
    private int width = 1000;
    
    // Keep track of mouse pointer while dragging
    private double  from_x = 0;
    private double  from_y = 0;
    private double  to_x = 0;
    private double  to_y = 0;
    private int     line_no = 1;
    
    private GraphicsContext gc;
    
    @Override
    public void start(Stage primaryStage) {
        
        // bug ...
        final boolean resizable = primaryStage.isResizable();
        primaryStage.setResizable(!resizable);
        primaryStage.setResizable(resizable);
        
        
        blockChainNetwork = new BlockChainNetwork(10);
        
        StackPane wrapperPane = new StackPane();
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(wrapperPane);
        
        
        primaryStage.setTitle("Drawing Operations Test");

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        height = (int)primaryScreenBounds.getMaxY() - 50;
        width =  (int) primaryScreenBounds.getMaxX() - 50;
        // System.out.println(" xres: " + primaryScreenBounds.getMaxX() + " yres: " + primaryScreenBounds.getMaxY());
        
        canvas = new Canvas(width, height);
        // Put canvas in the center of the window
        wrapperPane.getChildren().add(canvas);
        
        blockChainNetwork.initNetwork(width, height);
        blockChainNetwork.addTraverseListener(this);
        
        canvas.setOnMousePressed((event) -> this.setFromPos(event));
        
        canvas.setOnMouseDragged((event) -> {
            
            if (dragNode != null){
                
               this.setToPos(event);
               dragNode.setX((int)this.to_x);
               dragNode.setY((int)this.to_y);
               drawNetwork(canvas);
               //blockChainNetwork.drawNodeAndEdges(dragNode,gc);
               
            }
           
            
        }); 
        
        canvas.setOnMouseReleased((event) -> {
            
            dragNode.setX((int)event.getX());
            dragNode.setY((int)event.getY());
            
            drawNetwork(canvas);
            /*final Canvas new_line = new Canvas(400, 400);
            final GraphicsContext gc = new_line.getGraphicsContext2D();
            this.setToPos(event);
            this.drawLine(gc);
            //final new stright line
            root.getChildren().add(line_no++,new_line); */
        });
        
        final ChangeListener<Number> listener = new ChangeListener<Number>() {
            final Timer timer = new Timer(); // uses a timer to call your resize method
            TimerTask task = null; // task to execute after defined delay
            final long delayTime = 200; // delay that has to pass in order to consider an operation done

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
                if (task != null) { // there was already a task scheduled from the previous operation ...
                    task.cancel(); // cancel it, we have a new size to consider
                }

                task = new TimerTask() // create new task that calls your resize operation
                {
                    @Override
                    public void run() {
                        // here you can place your resize code
                        //System.out.println("resize to " + primaryStage.getWidth() + " " + primaryStage.getHeight());
                        blockChainNetwork.invalidateXY((int)primaryStage.getWidth(), (int)primaryStage.getHeight());
                        drawNetwork(canvas);
                    }
                };
                // schedule new task
                timer.schedule(task, delayTime);
            }
        };
        
        primaryStage.widthProperty().addListener(listener);
        primaryStage.heightProperty().addListener(listener);
        primaryStage.setScene(new Scene(borderPane,width,height));
        primaryStage.show();
        

    }

   
  
    private void drawNetwork(Canvas canvas) {
        // get the context
        gc = canvas.getGraphicsContext2D();
        // lets go through the nodes and be notfied for each node.
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        blockChainNetwork.drawNetwork(gc);
        //gc.stroke();
        
          
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void apply(BlockChainNode aNode) {
       
    }
    
    private void setFromPos(MouseEvent event) {
        this.from_x = event.getSceneX();
        this.from_y = event.getSceneY();
        dragNode = blockChainNetwork.findNodeFromXYPos((int)from_x, (int)from_y);
    }

    private void setToPos(MouseEvent event) {
        this.to_x = event.getSceneX();
        this.to_y = event.getSceneY();
    }   

}
