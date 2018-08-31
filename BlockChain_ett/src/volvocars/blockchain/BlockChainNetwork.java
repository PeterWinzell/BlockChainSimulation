/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.util.ArrayList;
import java.util.List;

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
        falsify_visited();
        for (int i = 0;i < networkNodes;i++){
            BlockChainNode bNode = new BlockChainNode(isForger(),forgers,i);
            bNode.addBroadCastListener(this);
            nodes.add(bNode);
            for (int j = 0 ; j < networkNodes;j++){
                edgeMatrix[i][j] = true;
            }
        }    
    }
    
    public void falsify_visited(){
        for(int i = 0; i < visited.length; i++)
            visited[i] = false;
    }
    
    public void initNetwork(int width, int height){
        // give the network nodes a representation in the 2D plane.
        for (int i = 0 ; i < networkNodes; i++){
            BlockChainNode bNode = nodes.get(i);
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
    
    
    public void depthFirstSearch(int index){
        notifyTraverseListeners(index);
        visited[index] = true;
        for (int otherindex =0; otherindex < networkNodes; otherindex++){
            if ( (visited[otherindex] == false) && edgeMatrix[index][otherindex] == true)
                depthFirstSearch(otherindex); // recursive call with the rest of the unvisited graph
        }
    }

    public void notifyTraverseListeners(int index){
        for (int j = 0; j < travlisteners.size(); j++){
            travlisteners.get(j).apply(nodes.get(index));
        }
    }
    public void addTraverseListener(DfsTraverseListener traverseListener){
        travlisteners.add(traverseListener);
    }
}
