/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rubberbandingexample;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Peter Winzell
 */
public class RubberBandingExample
        extends Application {

    private double from_x = 0;
    private double from_y = 0;
    private double to_x = 0;
    private double to_y = 0;
    private int line_no = 1;

    @Override
    public void start(Stage primaryStage) {

        StackPane root2 = new StackPane();
        
        
        BorderPane border = new BorderPane();
        HBox hbox = addHBox();
        border.setTop(hbox);
        
        border.setLeft(addVBox());
        addStackPane(hbox);         // Add stack to HBox in top region

        border.setCenter(root2);
        border.setRight(addFlowPane());
       
        root2.setOnMousePressed((event) -> this.setFromPos(event));
        
        root2.setOnMouseDragged((event) -> {
            
            root2.getChildren().remove(0);
            
            final Canvas temp_canvas = new Canvas(root2.getWidth(), root2.getHeight());
            final GraphicsContext gc = temp_canvas.getGraphicsContext2D();
            this.setToPos(event);
            this.drawLine(gc);
            
            root2.getChildren().add(0,temp_canvas);
            
        }); 
        
        root2.setOnMouseReleased((event) -> {
            final Canvas new_line = new Canvas(root2.getWidth(), root2.getHeight());
            final GraphicsContext gc = new_line.getGraphicsContext2D();
            this.setToPos(event);
            this.drawLine(gc);
            this.drawSpiderWebU(gc);
            //final new stright line
            root2.getChildren().add(line_no++,new_line);             
        });

        Scene scene = new Scene(border, 1000, 1000);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
        
        primaryStage.setTitle("JAVAFX tests...");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Bounds bounds = border.getCenter().getBoundsInLocal();
        Bounds bounds2 = border.getCenter().getBoundsInParent();
        Canvas canvas = new Canvas(bounds2.getWidth(),bounds2.getHeight());
        
        final GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        root2.getChildren().addAll( new Canvas(), canvas); 
        
        this.initDraw(graphicsContext);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void drawLine(GraphicsContext gc) {
        //gc.setFill(Color.RED);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.strokeLine(from_x, from_y, to_x, to_y);      
    }

    private void initDraw(GraphicsContext gc){
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.BLUE);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(10);
        System.out.println(canvasWidth + "CW" + canvasHeight);
        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle
    }

    private void setFromPos(MouseEvent event) {
        this.from_x = event.getX();
        this.from_y = event.getY();
    }

    private void setToPos(MouseEvent event) {
        this.to_x = event.getX();
        this.to_y = event.getY();
    }   
    
    public HBox addHBox() {
        
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");

        Button buttonStart = new Button("Start");
        buttonStart.setPrefSize(100, 20);

        Button buttonStop = new Button("Stop");
        buttonStop.setPrefSize(100, 20);
        
        hbox.getChildren().addAll(buttonStart, buttonStop);

        return hbox;
    }
    
    public void addStackPane(HBox hb) {
        StackPane stack = new StackPane();
        Rectangle helpIcon = new Rectangle(30.0, 25.0);
        helpIcon.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop[]{
                    new Stop(0, Color.web("#4977A3")),
                    new Stop(0.5, Color.web("#B0C6DA")),
                    new Stop(1, Color.web("#9CB6CF")),}));
        helpIcon.setStroke(Color.web("#D0E6FA"));
        helpIcon.setArcHeight(3.5);
        helpIcon.setArcWidth(3.5);

        Text helpText = new Text("?");
        helpText.setFont(Font.font("Verdana", FontWeight.BOLD, 18));
        helpText.setFill(Color.WHITE);
        helpText.setStroke(Color.web("#7080A0"));

        stack.getChildren().addAll(helpIcon, helpText);
        stack.setAlignment(Pos.CENTER_RIGHT);     // Right-justify nodes in stack
        StackPane.setMargin(helpText, new Insets(0, 10, 0, 0)); // Center "?"

        hb.getChildren().add(stack);            // Add to HBox from Example 1-2
        HBox.setHgrow(stack, Priority.ALWAYS);    // Give stack any extra space
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
    
    public FlowPane addFlowPane() {
        FlowPane flow = new FlowPane();
        flow.setPadding(new Insets(5, 0, 5, 0));
        flow.setVgap(4);
        flow.setHgap(4);
        flow.setPrefWrapLength(170); // preferred width allows for two columns
        flow.setStyle("-fx-background-color: DAE6F3;");

        
        return flow;
    }
    
    
    public void drawSpiderWebU(GraphicsContext gc){
        
        double w = gc.getCanvas().getWidth();
        double h = gc.getCanvas().getHeight();
        
        double xstep = w / 3;
        double ystep = h / 3;
        
        double xpos = 0;
        double ypos = 0;
        double maxlength = 150;
        
        
        while (xpos < w){
            while (ypos < h){
                double x = xstep + Math.random()*xstep;
                double y = ystep + Math.random()*ystep;
                double radians = 0;
                double anglestep = Math.PI / 12;
                while (radians <= Math.PI*2){
                    double length = Math.random() * maxlength;
                    double xprim = x + length*Math.cos(radians);
                    double yprim = y + length*Math.sin(radians); 
                    gc.strokeLine(x, y, xprim, yprim);
                    radians = radians + anglestep;
                }
                ypos = ypos + ystep;
            }
            xpos = xpos + xstep;
        }
        gc.stroke();
        
    }
    
    
}