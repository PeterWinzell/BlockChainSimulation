/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javafx.animation.AnimationTimer;
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
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
//import jfxtras.labs.scene.control.gauge.Content;


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
    
    private long daycalc = 0;
    private long daycalcspan = 0;
    
    // Keep track of mouse pointer while dragging
    private double  from_x = 0;
    private double  from_y = 0;
    private double  to_x = 0;
    private double  to_y = 0;
    
    private double  startDragX = 0;
    private double  startDragY = 0;
    
    private int     line_no = 1;
    
    private GraphicsContext gc;
    
    private Button startButton;
    private Button pauseButton;
    
    private Hyperlink headerInfo = new Hyperlink("Genesis not created");
    private Hyperlink nodeInfo1  = new Hyperlink("-------------------");
    private Hyperlink nodeInfo2  = new Hyperlink("-------------------");
    private Hyperlink nodeInfo3  = new Hyperlink("-------------------");  
    private Hyperlink blockInfo =  new Hyperlink("Blockheight is 0 ");
    
    static int runningTime = 0;
     
    @Override
    public void start(Stage primaryStage) {
        
        // bug workaround...
        final boolean resizable = primaryStage.isResizable();
        primaryStage.setResizable(!resizable);
        primaryStage.setResizable(resizable);
        
        //Buttons
        startButton = new Button("Start simulation");
        pauseButton = new Button("Pause simulation");
        
        blockChainNetwork = new BlockChainNetwork(30);
        
        StackPane wrapperPane = new StackPane();
        wrapperPane.getStylesheets().add(this.getClass().getResource("backgroundCSS.css").toExternalForm());
        BorderPane borderPane = new BorderPane();
        StackPane bottomPane = new StackPane();
       
        wrapperPane.setId("wrapperPane");
        wrapperPane.setMaxSize(1000,1000);
        wrapperPane.setPrefSize(1000,1000);
        wrapperPane.setMinSize(500,500);
        borderPane.setCenter(wrapperPane);
       
        borderPane.setLeft(addVBox_2());
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
                                    System.out.println("drawing");
                                    
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
        
        Scene scene = new Scene(borderPane,width,height);
        
        
        primaryStage.setScene(scene);
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
        
        wrapperPane.setOnMousePressed((event) -> 
        {    
            this.setFromPos(event);
            
            startDragX = this.to_x;
            startDragY = this.to_y;
        });       
        
        wrapperPane.setOnMouseDragged((event) -> {
            
            //final GraphicsContext gc = temp_canvas.getGraphicsContext2D();
            if (dragNode != null){
                
               wrapperPane.getChildren().remove(0);
               final Canvas temp_canvas = new Canvas(wrapperPane.getWidth(), wrapperPane.getHeight()); 
               
               Bounds innerBounds = wrapperPane.getBoundsInParent();
              
               
               this.setToPos(event,innerBounds);
               
               double deltax = this.to_x - startDragX;
               double deltay = this.to_y - startDragY;
               
               System.out.println(" node x " + dragNode.getX() );
               System.out.println(" deltax " + deltax);
               
               dragNode.setX(dragNode.getX() + (int)deltax);
               dragNode.setY(dragNode.getY() + (int)deltay);
               
               startDragX = this.to_x;
               startDragY = this.to_y;
               
               //dragNode.setX((int)this.to_x - deltax);
               //dragNode.setY((int)this.to_y - deltay);
               
               drawNetwork(temp_canvas);
               canvas = temp_canvas;
               
               wrapperPane.getChildren().add(0,temp_canvas);
            }
           
            
        }); 
        
        wrapperPane.setOnMouseReleased((event) -> {
            
            if (dragNode != null){
                wrapperPane.getChildren().remove(0);
                final Canvas temp_canvas = new Canvas(wrapperPane.getWidth(), wrapperPane.getHeight()); 
                
                Bounds innerBounds = wrapperPane.getBoundsInParent();
                
               this.setToPos(event,innerBounds);
                
               double deltax = this.to_x - startDragX;
               double deltay = this.to_y - startDragY;
               
               dragNode.setX(dragNode.getX() + (int)deltax);
               dragNode.setY(dragNode.getY() + (int)deltay);
               
               drawNetwork(temp_canvas);
               canvas = temp_canvas;
                
               wrapperPane.getChildren().add(0,temp_canvas);
            }    
            
        });
        
        startButton.setOnAction((event) -> {
           daycalc = System.currentTimeMillis();
           blockChainNetwork.startSimulation();
           simulating = true;
        });
        
        pauseButton.setOnAction((event) -> {
            blockChainNetwork.stopSimulation();
            simulating = false;
        });
        
      
        // game loop, repanting the graph every 40ms 
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                runningTime++;
                if (runningTime % 3 == 0){
                    blockInfo.setText(new String("Blockheight is " + BlockChainNetwork.blockHeight));
                    draw(wrapperPane,bottomPane);
                }
            }
        }.start();

    }
    
    
    private boolean simulating = false;
    
    private boolean simulationIsOn(){
        return simulating;
    }
    
    private long getDayCalc(){
        if (daycalc == 0)
            return daycalc;
        long timepast = 0;
        
        if (simulationIsOn()){
            timepast = System.currentTimeMillis();
            long day = (timepast - daycalc)/1000;
            System.out.println(day);
            return day;
        }    
        
        return 0;
    }

    private void draw(StackPane wrapperPane,StackPane bottomPane){
        wrapperPane.getChildren().remove(0);
        final Canvas temp_canvas = new Canvas(wrapperPane.getWidth(), wrapperPane.getHeight()); 
               
        Bounds   innerBounds = wrapperPane.getBoundsInParent();
        drawNetwork(temp_canvas);
        canvas = temp_canvas; 
        
        wrapperPane.getChildren().add(0,temp_canvas);
        
        // change the day text
        bottomPane.getChildren().remove(0);
        bottomPane.getChildren().add(0,getBottomNode());
    }
    
   public VBox addVBox()

    {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setSpacing(8);

        Text title = new Text("NAP BlockChain");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox.getChildren().add(title);

        
        vbox.getChildren().add(startButton);
        
        
        vbox.getChildren().add(pauseButton);
        
        Hyperlink options[] = new Hyperlink[]{
            headerInfo,
            nodeInfo1,
            nodeInfo2,
            nodeInfo3};

        for (int i = 0; i < 4; i++) {
            VBox.setMargin(options[i], new Insets(0, 0, 0, 8));
            vbox.getChildren().add(options[i]);
        }

        return vbox;
    }
  
   public VBox addVBox_2(){
        VBox vbox_2 = new VBox();
        vbox_2.setPadding(new Insets(10));
        vbox_2.setSpacing(8);

        Text title = new Text("NAP BlockChain");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        vbox_2.getChildren().add(title);

        
       
        Hyperlink options[] = new Hyperlink[]{
            blockInfo
        };

       
       VBox.setMargin(options[0], new Insets(0, 0, 0, 8));
       vbox_2.getChildren().add(options[0]);
       

        return vbox_2;
   }
    
   
   private synchronized void drawNetwork(Canvas canvas) {
        // get the context
        gc = canvas.getGraphicsContext2D();
        // lets go through the nodes and be notfied for each node.
        gc.clearRect(0,0,canvas.getWidth(),canvas.getHeight());
        blockChainNetwork.drawNetwork(gc);        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String value = System.getProperty("trans_print");
        System.out.println(value);
        launch(args);
    }

    @Override
    public void apply(BlockChainNode aNode) {
       // do something 
    }
    
    private void setFromPos(MouseEvent event) {
        
        this.to_x = this.from_x = event.getX();
        this.to_y = this.from_y = event.getY();
        
        dragNode = blockChainNetwork.findNodeFromXYPos((int)from_x, (int)from_y);
        
        showNodeInfo(dragNode);
    }
    
    private void showNodeInfo(BlockChainNode node){
        
        if (node != null){
            if (node.isForger()){
                    headerInfo.setText("Validator");
                    nodeInfo1.setText(new String("Id: " + node.index));
                    nodeInfo2.setText("-------");
                    nodeInfo3.setText("-------");
                }
            else{
                    headerInfo.setText("Vehicle wallet");
                    nodeInfo1.setText(new String("Id: " + node.index));
                    nodeInfo2.setText(new String("Eco tokens: " + Math.rint(node.getWallet().getNapWealth())));
                    nodeInfo3.setText(new String("Odometer: " + node.getOdometer() + " km"));
           }
        }   
    }

    private void setToPos(MouseEvent event,Bounds currentBounds) {
        
        this.to_x = getMoveX((int)event.getX(),0, (int)currentBounds.getWidth());
        this.to_y = getMoveY((int)event.getY(),(int)currentBounds.getMinY(),(int)(currentBounds.getMinY() + currentBounds.getHeight()));
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
        gc.setFont(Font.font("Verdana", FontPosture.ITALIC, 20));
        gc.strokeText("Day " + getDayCalc(),200,50 );
        
        return c;
    }
    
    private int getMoveX(int x,int minX,int maxX){
        return Math.min(Math.max(minX + 40,x),maxX - 40);
    }
    
    private int getMoveY(int y,int minY,int maxY){
        return Math.min(Math.max(minY + 40,y),maxY - 40);
    }
    
    
    private int getoffSetX(int cx,int tox){
        return Math.abs(tox - cx);
        
    }
    
    private int getoffSetY(int cy,int toy){
        return Math.abs(toy - cy);     
    }
    
    

}
