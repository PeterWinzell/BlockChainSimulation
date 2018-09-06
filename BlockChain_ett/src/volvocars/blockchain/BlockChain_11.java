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
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.ArcType;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


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
        
        // bug workaround...
        final boolean resizable = primaryStage.isResizable();
        primaryStage.setResizable(!resizable);
        primaryStage.setResizable(resizable);
        
        
        blockChainNetwork = new BlockChainNetwork(30);
        
        StackPane wrapperPane = new StackPane();
        BorderPane borderPane = new BorderPane();
        StackPane bottomPane = new StackPane();
        wrapperPane.setMaxSize(1000,1000);
        wrapperPane.setPrefSize(1000,1000);
        wrapperPane.setMinSize(500,500);
        borderPane.setCenter(wrapperPane);
       
        borderPane.setLeft(addVBox());
        borderPane.setRight(addVBox());
        bottomPane.getChildren().add(getBottomNode());
        borderPane.setBottom(bottomPane);
       
        primaryStage.setTitle("BlockChain Network Simulation");
        
        
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
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                   wrapperPane.getChildren().remove(0);
                                    
                                     //Bounds bounds = getVisibleBounds(wrapperPane);
                                     //if (bounds == null)
                                     Bounds   bounds = wrapperPane.getBoundsInParent();
                                   
                                    int width = (int)bounds.getWidth();
                                    int height = (int)bounds.getHeight();
                                    
                                    final Canvas temp_canvas = new Canvas(width, height); 
                                    blockChainNetwork.invalidateXY(width, height);
                                    drawNetwork(temp_canvas);
                                    wrapperPane.getChildren().add(0,temp_canvas);
                                    
                            }
                        });   
                        
                        
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
        
        //Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        //height = (int)primaryScreenBounds.getMaxY() - 50;
        //width =  (int) primaryScreenBounds.getMaxX() - 50;
        // System.out.println(" xres: " + primaryScreenBounds.getMaxX() + " yres: " + primaryScreenBounds.getMaxY());
        
        Bounds bounds = borderPane.getCenter().getBoundsInLocal();
        height = (int)bounds.getHeight();
        width =  (int)bounds.getWidth();
        
        canvas = new Canvas(width, height);
        // Put canvas in the center of the window
        
        wrapperPane.getChildren().add(canvas);
        
        blockChainNetwork.initNetwork(width, height);
        blockChainNetwork.addTraverseListener(this);
        
        wrapperPane.setOnMousePressed((event) -> this.setFromPos(event));       
        wrapperPane.setOnMouseDragged((event) -> {
            
            //final GraphicsContext gc = temp_canvas.getGraphicsContext2D();
            if (dragNode != null){
                
               wrapperPane.getChildren().remove(0);
               final Canvas temp_canvas = new Canvas(wrapperPane.getWidth(), wrapperPane.getHeight()); 
               
               this.setToPos(event);
               dragNode.setX((int)this.to_x);
               dragNode.setY((int)this.to_y);
               drawNetwork(temp_canvas);
               canvas = temp_canvas;
               
               wrapperPane.getChildren().add(0,temp_canvas);
            }
           
            
        }); 
        
        wrapperPane.setOnMouseReleased((event) -> {
            
            if (dragNode != null){
                wrapperPane.getChildren().remove(0);
                final Canvas temp_canvas = new Canvas(wrapperPane.getWidth(), wrapperPane.getHeight()); 
                
                dragNode.setX((int)event.getX());
                dragNode.setY((int)event.getY());
                drawNetwork(temp_canvas);
                canvas = temp_canvas;
                
                wrapperPane.getChildren().add(0,temp_canvas);
            }    
            
        });

    }

   public VBox addVBox()

    {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("NAP BlockChain");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        Hyperlink options[] = new Hyperlink[]{
            new Hyperlink("Transactions"),
            new Hyperlink("Ledger"),
            new Hyperlink("ICO"),
            new Hyperlink("Genesis block")};

        for (int i = 0; i < 4; i++) {
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(options[i]);
        }

        return vbox;
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
        this.from_x = event.getX();
        this.from_y = event.getY();
        dragNode = blockChainNetwork.findNodeFromXYPos((int)from_x, (int)from_y);
    }

    private void setToPos(MouseEvent event) {
        this.to_x = event.getX();
        this.to_y = event.getY();
    }   
    
    public static Bounds getVisibleBounds(Node aNode) {
        // If node not visible, return empty bounds
        if (!aNode.isVisible()) {
            return new BoundingBox(0, 0, -1, -1);
        }

        // If node has clip, return clip bounds in node coords
        if (aNode.getClip() != null) {
            return aNode.getClip().getBoundsInParent();
        }

       
        // If node has parent, get parent visible bounds in node coords
        Bounds bounds = aNode.getParent() != null ? getVisibleBounds(aNode.getParent()) : null;
        if (bounds != null && !bounds.isEmpty()) {
            bounds = aNode.parentToLocal(bounds);
        }
        return bounds;
    }
    
    private Node getBottomNode(){
        Canvas c = new Canvas(500,100);
        gc = c.getGraphicsContext2D();
        gc.strokeText(" Here we will have a bottom transaction ticker... ",200,50 );
        return c;
    }

}
