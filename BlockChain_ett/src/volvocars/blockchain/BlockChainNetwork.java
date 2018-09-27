/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.awt.Point;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Peter Winzell
 */
public class BlockChainNetwork<M,L> implements Runnable, BroadCastListener<M,L> {
    
    // The network is represented of a connected undirected Graph.
    
    public static int blockHeight = 0; // genesis block
    
    private final boolean edgeMatrix[][];
    private final boolean visited[];
    private final int networkNodes;
    private static final double forgerProb = 0.1;
    
    private static float minimumTransaction = 0.1f;
    private static FileWriter fileWriter;

    public static float getMinimumTransaction() {
        return minimumTransaction;
    }

    public static void setMinimumTransaction(float minimumTransaction) {
        BlockChainNetwork.minimumTransaction = minimumTransaction;
    }
    
    List<BlockChainNode> nodes;    // Keep track of network players
    List<BlockChainNode> forgers;  // keep track of forgers (block validators)
    List<DfsTraverseListener> travlisteners; // Classes that wants to listen on G traverse for each node.
    List<BroadCastReceiver> mempoolListeners;
            
    public BlockChainNetwork(int networkNodes){
        this.networkNodes = networkNodes;
        
        mempoolListeners = new ArrayList<>();
        travlisteners = new ArrayList<>();
        nodes = new ArrayList<>(networkNodes);
        forgers = new ArrayList<>();
        edgeMatrix = new boolean [networkNodes][networkNodes];
        visited = new boolean [networkNodes];
        
        //init to false;
        noEdges();
        falsify_visited();
        
        for (int i = 0;i < networkNodes;i++){
            BlockChainNode bNode = new BlockChainNode(forgers,i);
            bNode.addBroadCastListener(this);
            nodes.add(bNode);
            edgeMatrix[i][(i+1) % networkNodes] = false;
            
        }    
        blockHeight++;
    }
    
    
    private void noEdges(){
        for (int i = 0; i < networkNodes; i++)
            for (int j = 0; j < networkNodes; j++)
                edgeMatrix[i][j] = false;
    }
            
    public void falsify_visited(){
        for(int i = 0; i < visited.length; i++)
            visited[i] = false;
    }
    
    public void initNetwork(int width, int height){
        setXYCoordinates_2(width, height);
    }
    
    public void setXYCoordinates(double w,double h){
        
        double xstep = w / 3;
        double ystep = h / 3;
        
        double xpos = 40;
        double ypos = 40;
        
        double maxlength = 150;
        
        int nodeIndex = 0;
        BlockChainNode prevnode = null;
        
        while (xpos < w && (nodeIndex < networkNodes)){
            while (ypos < h && (nodeIndex < networkNodes)){
                
                double x = xpos + Math.random()*xstep;
                double y = ypos + Math.random()*ystep;
                
                BlockChainNode centernode = nodes.get(nodeIndex++);
                if (prevnode != null){
                    edgeMatrix[centernode.getEdgeMatrixIndex()][prevnode.getEdgeMatrixIndex()] = true;
                }
                prevnode = centernode;
                
                centernode.setX((int)x);
                centernode.setY((int)y);
                centernode.setForger();
                
                double radians = 0;
                double anglestep = Math.PI / 3;
                
                while (radians <= Math.PI*2 && (nodeIndex < networkNodes)){
                    
                    double length = Math.random() * maxlength;
                    double xprim = x + length*Math.cos(radians);
                    double yprim = y + length*Math.sin(radians); 
                    
                    BlockChainNode nextnode = nodes.get(nodeIndex++);
                    nextnode.setX((int) xprim);
                    nextnode.setY((int) yprim);
                    edgeMatrix[centernode.getEdgeMatrixIndex()][nextnode.getEdgeMatrixIndex()] = true;
                    radians = radians + anglestep;
                    
                }
                ypos = ypos + ystep;
                
            }
            xpos = xpos + xstep;
        }
    }     
    
    public void setXYCoordinates_2(double w, double h) {

        double maxlength = 150;
        final double xinset =  40;
        final double yinset = 40;

        int nodeIndex = 0;
        BlockChainNode prevnode = null;

        while (nodeIndex < networkNodes) {

            double x =  Math.random() * w;
            double y =  Math.random() * h;
            
            x = Math.max(xinset,Math.min(x,w-40));
            y = Math.max(yinset, Math.min(y,h-40));
            
            BlockChainNode centernode = nodes.get(nodeIndex++);
            if (prevnode != null) {
                edgeMatrix[centernode.getEdgeMatrixIndex()][prevnode.getEdgeMatrixIndex()] = true;
            }
            prevnode = centernode;
            
            centernode.setX((int) x);
            centernode.setY((int) y);
            centernode.setForger();
            if (!mempoolListeners.contains(centernode))
                mempoolListeners.add(centernode);
            
            double radians = 0;
            double anglestep = Math.PI / 3;

            while (radians <= Math.PI * 2 && (nodeIndex < networkNodes)) {
                BlockChainNode nextnode = nodes.get(nodeIndex++);
                
                double length = Math.random() * maxlength;
                double xprim = x + length * Math.cos(radians);
                double yprim = y + length * Math.sin(radians);
                // make sure that we don't end up outside the screen
                xprim = Math.max(xinset,Math.min(xprim,w-40));
                yprim = Math.max(yinset, Math.min(yprim,h-40));
                nextnode.setX((int) xprim);
                nextnode.setY((int) yprim);
                
                edgeMatrix[centernode.getEdgeMatrixIndex()][nextnode.getEdgeMatrixIndex()] = true;
                radians = radians + anglestep;
            }

        }
    }
        
        
    private boolean isForger(){
        return ( Math.random() <= forgerProb);
    }

    @Override
    public void MessageNotification(M message) {
        synchronized(this){
            TransactionMessage mess = (TransactionMessage)message;
            for (BroadCastReceiver listener : mempoolListeners){
                listener.receiveMessage(mess);
            }   
        }
    }

    @Override
    public void addBroadCastListener(Object listener) {
  
    }
    
    
    public void traverseNetwork(int startindex){
        depthFirstSearch(startindex,startindex);
        falsify_visited(); // make sure we can do it again
    }
    
    // Recursive depth first search of the graoh
    private void depthFirstSearch(int index,int previndex){
        notifyTraverseListeners(index,previndex);
        previndex = index;
        visited[index] = true;
        for (int otherindex =0; otherindex < networkNodes; otherindex++){
            if ( (visited[otherindex] == false) && (edgeMatrix[index][otherindex] == true || edgeMatrix[otherindex][index]==true))
                depthFirstSearch(otherindex,previndex); // recursive call with the rest of the unvisited graph
        }
    }

    public void notifyTraverseListeners(int index,int previndex){
        for (int j = 0; j < travlisteners.size(); j++){
            System.out.println(index + " " + previndex);
            travlisteners.get(j).apply(nodes.get(index));
        }
    }
    
    public void addTraverseListener(DfsTraverseListener traverseListener){
        travlisteners.add(traverseListener);
    }
    
    public BlockChainNode getNode(int nodeIndex){
        return nodes.get(nodeIndex);
    }
    
    public  List<BlockChainNode> getNodes(){
        return nodes;
    }
    
    public boolean edge(int i,int j){
        return edgeMatrix[i][j];
    }
    
    private void drawNode(GraphicsContext gc,BlockChainNode aNode){
       int x = aNode.getX();
       int y = aNode.getY();
       
       double radius = aNode.getSize();
       if (aNode.isForger())
           gc.setFill(Color.CRIMSON);
       else{
            gc.setFill(Color.YELLOW);
            drawNodeText(gc,aNode,x,y,radius);
       }     
       
       gc.fillOval(x,y,radius,radius); 
    }
    
    private void drawNodeText(GraphicsContext gc,BlockChainNode aNode,int x,int y,double radius){
       Font f = gc.getFont();
       String odometerString = Double.toString(aNode.getOdometer());
       Bounds textBounds = reportSize(odometerString,f);
       
       int fontx = (int)(x - (textBounds.getWidth() - radius) / 2);
       int fonty =(int )(y + textBounds.getHeight() + radius);
       
       gc.fillText(odometerString,fontx,fonty);
    }
    
    private Bounds reportSize(String s, Font myFont) {
        Text text = new Text(s);
        text.setFont(myFont);
        Bounds tb = text.getBoundsInLocal();
        Rectangle stencil = new Rectangle(
            tb.getMinX(), tb.getMinY(), tb.getWidth(), tb.getHeight()
        );

        Shape intersection = Shape.intersect(text, stencil);

       Bounds ib = intersection.getBoundsInLocal();
       // System.out.println( "Text size: " + ib.getWidth() + ", " + ib.getHeight());
       return ib; 
    }
    
    private void drawEdge(GraphicsContext gc,int node1,int node2){
        
        BlockChainNode aNode = nodes.get(node1);
        BlockChainNode anotherNode = nodes.get(node2);
        
        Paint edgeColor = null;
        if (aNode.isForger() || anotherNode.isForger()){
            edgeColor = Color.WHITESMOKE;
        }
        else{
            edgeColor = Color.CHARTREUSE;
        }
        
        gc.beginPath();
        gc.setStroke(edgeColor);
        
        int radius_1 = (int)(aNode.getSize() / 2);
        gc.moveTo(aNode.getX() + radius_1, aNode.getY() + radius_1);
        
        int radius_2= (int)(anotherNode.getSize() / 2);
        gc.lineTo(anotherNode.getX() + radius_2,anotherNode.getY() + radius_2);
        gc.stroke();
         
    }
    
    public void drawNetwork(GraphicsContext gc){
        synchronized (this) {
            for (int i = 0; i < networkNodes; i++) {

                for (int j = 0; j < networkNodes; j++) {
                    if (edge(i, j) || edge(j, i)) {
                        drawEdge(gc, i, j);
                    }
                }
                // drawNode(gc,nodes.get(i));

            }

            for (int i = 0; i < networkNodes; i++) {
                drawNode(gc, nodes.get(i));
            }
        }        
    }
    
    public void invalidateXY(int w, int h){
        initNetwork(w, h);
    }
    
    public boolean inCircle(int mousex,int mousey,int nodex,int nodey,double diameter){
        
        double radius = diameter / 2;
        int dx = Math.abs(mousex-(nodex+ (int)radius));
        int dy = Math.abs(mousey-(nodey+(int)radius));
        
        return ( dx*dx + dy*dy <= (radius*radius) ); // pytagoras...

    }
    
    public BlockChainNode findNodeFromXYPos(int x, int y) {

        boolean found = false;
        int nextNodeIndex = 0;
        BlockChainNode aNode = null;
        
        while (!found && nextNodeIndex < networkNodes) {
            
            
            BlockChainNode tempNode = nodes.get(nextNodeIndex);
            int getNodeX = tempNode.getX();
            int getNodeY = tempNode.getY();
            double diameter = tempNode.getSize();
            found = inCircle(x,y,getNodeX,getNodeY,diameter);
            if (found) aNode = tempNode;
            
            nextNodeIndex++;
        }

        return aNode;
    }
    
    public void drawNodeAndEdges(BlockChainNode node,GraphicsContext gc){ 
        drawNode(gc,node);  
    }
    
    public void startSimulation(){
        ExecutorService service = Executors.newFixedThreadPool(nodes.size());
        nodes.stream().forEach((node) -> {
            service.submit(node);
        });    
        System.out.println("simulationstarted");
    }
    
    public void stopSimulation(){
        nodes.stream().forEach((node) -> {
            node.stop();
        }); 
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    public static FileWriter getFileWriter(){
        if (fileWriter == null){
            try {
                fileWriter = new FileWriter("napblockchain.bl");
            } catch (IOException ex) {
                Logger.getLogger(BlockChainNetwork.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return fileWriter;
    }
    
    private BlockChainNode currentDisplayVehicleNode = null;
    private BlockChainNode currentForger = null; 
    
    /*** used to highlight changes **/
    public void setCurrentTransactionNode(BlockChainNode node){
        synchronized(this){
            currentDisplayVehicleNode = node;
        }
    }
    
    public void setCurrentForgerNode(BlockChainNode node){
        synchronized(this){
            currentForger = node;
        }
    }
    
    private void getCurrentForgerColor(){
        
    }
   
}


