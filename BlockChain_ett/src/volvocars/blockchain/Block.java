/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package volvocars.blockchain;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pwinzell
 */
public class Block {
	
	public String hash;
	public String previousHash; 
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>(); //our data will be a simple message.
	public long timeStamp; //as number of milliseconds since 1/1/1970.
	public int nonce;
	private static Block genesisBlock = null;
        
        private FileWriter fileWriter;
        private long blockId;
        
	//Block Constructor.  
	public Block(String previousHash) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
                 blockId = BlockChainNetwork.blockHeight;
	}
	
	//Calculate new hash based on blocks contents
	private String calculateHash() {
            
            if (previousHash.equals('0')){
                merkleRoot = "NO TRANSACTIONS IN GENESISBLOCK";
                return "0";
            }
            
            
            String calculatedhash = StringUtil.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				merkleRoot
				);
	   return calculatedhash;
	}
	
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
                hash = calculateHash();  // we don't need proof of work since we are consortie ???
		System.out.println("Block Mined!!! : " + hash);
	}
	
	//Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		
                 //process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;		
		if((!"0".equals(previousHash))) {
			if((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}

		transactions.add(transaction);
		return true;
	}
        
        public void print(){
            
            try {
                
                fileWriter = BlockChainNetwork.getFileWriter();
                
                fileWriter.write("********************************************************************************\n");
                fileWriter.write("Block id: " + blockId +"\n");
                fileWriter.write("Prev Hash:" + this.previousHash + "\n");
                fileWriter.write("Hash: " + hash + "\n");  
                fileWriter.write("MerkleRoot: " + merkleRoot + "\n");
                fileWriter.write(getTransactionsStrings()+"\n");
                fileWriter.write("********************************************************************************\n");
                
                fileWriter.flush();
                
            } catch (IOException ex) {
                Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private String getTransactionsStrings(){
            
            StringBuilder buildStr = new StringBuilder("Transactions: [");
            
            for (Transaction trans: transactions){
                buildStr.append(trans.toString() + ",");
            }
            return buildStr.replace(buildStr.length()-1, buildStr.length(), "]").toString();
        }
	
        // after the voting is done
        public void setBlockId(){
            blockId = BlockChainNetwork.blockHeight;
        }
        // there is only one instance of the genesis block , could be but not for this simulation.
        public  static Block getGenesisBlock(){
            if (genesisBlock == null){
                genesisBlock = new Block("0");
            }
            return genesisBlock;
        }
        
        public boolean validate(){
           
            // calculate the block hash and compare to actual hash.
            
            String mrkRoot = StringUtil.getMerkleRoot(transactions);
            String valHash = StringUtil.applySha256( 
				previousHash +
				Long.toString(timeStamp) +
				Integer.toString(nonce) + 
				mrkRoot
				);
            
            return valHash.equals(hash);
        }
}

