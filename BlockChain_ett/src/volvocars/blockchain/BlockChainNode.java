/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.util.List;

/**
 *
 * @author Peter Winzell
 */
public class BlockChainNode<M,L> extends Thread implements BroadCastListener<M,L>{
    protected int i;
    
    //2d reps.
    private int x;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    private int y;
    
    private MemoryPool memPool;
    private boolean isForger;
    private List<BlockChainNode> forgerS;
    private boolean isedge = false;
    
    private boolean visited = false;
    private L listener; 
    
    public BlockChainNode(boolean forger,List<BlockChainNode> forgerList, int index){
        
        this.isForger = forger;
        this.forgerS = forgerList;
        
        if (isForger) forgerS.add(this);
        
        memPool = new MemoryPool();
    }
    
    public void setEdgeMatrixIndex(int i) {
        this.i = i;
    }

    

    public int getEdgeMatrixIndex() {
        return i;
    }
    
    public boolean getVisitited(){
        return visited;
    }

    public void setVisitied(boolean v){
        this.visited = v;
    }

    @Override
    public void MessageNotification(M message) {
       
    }

    @Override
    public void addBroadCastListener(L listener) {
        this.listener = listener;
    }
    
    
    public void broadCastMessage(Transaction m){
        ((BroadCastListener)listener).MessageNotification(m);
    }
   
    
    
    
        
}
