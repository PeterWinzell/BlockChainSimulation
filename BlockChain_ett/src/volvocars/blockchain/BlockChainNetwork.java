/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 *
 * @author Peter Winzell
 */
public class BlockChainNetwork implements BroadCastListener {
    
    // The network is represented of a connected undirected Graph.
    private final boolean edgeMatrix[][];
    private final boolean visited[];
    private final int networkNodes;
    private static final double forgerProb = 0.1;
    
    List<BlockChainNode> nodes;    // Keep track of network players
    List<BlockChainNode> forgers;  // keep track of forgers (block validators)
    List<DfsTraverseListener> travlisteners; // Classes that wants to listen on G traverse for each node.
    
            
    public BlockChainNetwork(int networkNodes){
        this.networkNodes = networkNodes;
        
        travlisteners = new ArrayList<>();
        nodes = new ArrayList<>(networkNodes);
        forgers = new ArrayList<>();
        edgeMatrix = new boolean [networkNodes][networkNodes];
        visited = new boolean [networkNodes];
        
        //init to false;
        noEdges();
        falsify_visited();
        
        for (int i = 0;i < networkNodes;i++){
            BlockChainNode bNode = new BlockChainNode(isForger(),forgers,i);
            bNode.addBroadCastListener(this);
            nodes.add(bNode);
            edgeMatrix[i][(i+1) % networkNodes] = false;    
        }    
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
    public void MessageNotification(Object message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
       
       double radious = aNode.getSize();
       if (aNode.isForger())
           gc.setFill(Color.CRIMSON);
        else
            gc.setFill(Color.YELLOW);
       
       gc.fillOval(x,y,radious,radious); 
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
        gc.moveTo(aNode.getX() + 5, aNode.getY() + 5);
        gc.lineTo(anotherNode.getX() + 5,anotherNode.getY() + 5);
        gc.stroke();
         
    }
    
    public void drawNetwork(GraphicsContext gc){
  
        for (int i = 0; i < networkNodes;i++){
            
            for (int j = 0 ; j < networkNodes;j++){
                if (edge(i,j) || edge(j,i) ){
                    drawEdge(gc,i,j);
                }
            }   
           // drawNode(gc,nodes.get(i));
            
        }
        
        
        for (int i = 0; i < networkNodes; i++)
            drawNode(gc,nodes.get(i));
                    
    }
    
    public void invalidateXY(int w, int h){
        initNetwork(w, h);
    }
    
    public boolean inCircle(int mousex,int mousey,int nodex,int nodey,double diameter){
        
        int dx = Math.abs(mousex-(nodex+5));
        int dy = Math.abs(mousey-(nodey+5));
        double radius = diameter / 2;
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
        
        System.out.println("start Action");
    }
    
    public void stopSimulation(){
        nodes.stream().forEach((node) -> {
            node.stop();
        }); 
    }
}


