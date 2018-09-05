/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

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
            edgeMatrix[i][(i+1) % networkNodes] = true;    
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
        // give the network nodes a representation in the 2D plane.
        for (BlockChainNode bNode:nodes){
            bNode.setX((int)  Math.ceil(Math.random() * width));
            bNode.setY( (int) Math.ceil(Math.random() * height));
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
       
       if (aNode.isForger())
           gc.setFill(Color.CRIMSON);
        else
            gc.setFill(Color.BLACK);
       
       gc.fillOval(x,y,10.0,10.0); 
    }
    
    private void drawEdge(GraphicsContext gc,int node1,int node2){
        
        BlockChainNode aNode = nodes.get(node1);
        BlockChainNode anotherNode = nodes.get(node2);
        
        gc.beginPath();
        gc.moveTo(aNode.getX() + 5, aNode.getY() + 5);
        gc.lineTo(anotherNode.getX() + 5,anotherNode.getY() + 5);
        gc.stroke();
         
    }
    
    public void drawNetwork(GraphicsContext gc){
  
        for (int i = 0; i < networkNodes;i++){
            drawNode(gc,nodes.get(i));
            for (int j = i+1 ; j < networkNodes;j++)
                if (edge(i,j)){
                    drawEdge(gc,i,j);
                }
        }
                    
    }
    
    public void invalidateXY(int w, int h){
        initNetwork(w, h);
    }
    
    public boolean inCircle(int mousex,int mousey,int nodex,int nodey){
        
        int dx = Math.abs(mousex-nodex);
        int dy = Math.abs(mousey-nodey);
        
        return ( dx*dx + dy*dy <= 100 );

    }
    
    public BlockChainNode findNodeFromXYPos(int x, int y) {

        boolean found = false;
        int nextNodeIndex = -1;
        BlockChainNode aNode = null;
        
        while (!found && nextNodeIndex < networkNodes) {
            nextNodeIndex++;
            
            BlockChainNode tempNode = nodes.get(nextNodeIndex);
            int getNodeX = tempNode.getX();
            int getNodeY = tempNode.getY();
            
            found = inCircle(x,y,getNodeX,getNodeY);
            if (found) aNode = tempNode;
        }

        return aNode;
    }
    
    public void drawNodeAndEdges(BlockChainNode node,GraphicsContext gc){
        
        /*for (int i = 0; i < networkNodes; i++){
            BlockChainNode anotherNode = nodes.get(i);
            if ((node != anotherNode) && node.hasEdgeTo(anotherNode)){
               this.drawEdge(gc,node.index, i);    
            }
        }*/
        
        drawNode(gc,node);
       
    }
}


