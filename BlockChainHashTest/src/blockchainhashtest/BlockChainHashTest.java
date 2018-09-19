/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blockchainhashtest;


/**
 *
 * @author Peter Winzell
 */
public class BlockChainHashTest {

   
    DataBlock a = new DataBlock();
    DataBlock b = new DataBlock();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        DataBlock a = new DataBlock();
        DataBlock b = new DataBlock();
        
         a.setFlag(1);
         b.setTransactionType(1);
        
        int hash_a = a.getBlockHash_1();
        int hash_b = b.getBlockHash_1();
        
        String hash_256_a = a.getSHA256Hash();
        String hash_256_b = b.getSHA256Hash();
        
        System.out.println("hash a = "+ hash_a +" " + "hash b = " + hash_b);
        System.out.println("256 hash a = "+ hash_256_a +" " + "256 hash b = " + hash_256_b);
        System.out.println("***********************************************************************************************************************************");
        System.out.println("***********************************************************************************************************************************");
        
        a = new DataBlock();
        b = new DataBlock();
        
        a.setName("A");
        b.setOwnerId("A");
        
        hash_a = a.getBlockHash_1();
        hash_b = b.getBlockHash_1();
        
        hash_256_a = a.getSHA256Hash();
        hash_256_b = b.getSHA256Hash();
        
        System.out.println("hash a = "+ hash_a +" " + "hash b = " + hash_b);
        System.out.println("256 hash a = "+ hash_256_a +" " + "256 hash b = " + hash_256_b);
        System.out.println("***********************************************************************************************************************************");
        System.out.println("***********************************************************************************************************************************");
        
        a = new DataBlock();
        b = new DataBlock();
        
        a.setName("A");
        a.setOwnerId("BBA");
        
        b.setName("AB");
        b.setOwnerId("BA");
        
        hash_a = a.getBlockHash_1();
        hash_b = b.getBlockHash_1();
        
        hash_256_a = a.getSHA256Hash();
        hash_256_b = b.getSHA256Hash();
        
        System.out.println("hash a = "+ hash_a +" " + "hash b = " + hash_b);
        System.out.println("256 hash a = "+ hash_256_a +" " + "256 hash b = " + hash_256_b);
        System.out.println("***********************************************************************************************************************************");
        System.out.println("***********************************************************************************************************************************");
        
    }
    
}
