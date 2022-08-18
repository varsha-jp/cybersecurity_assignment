import java.io.*;
import java.io.DataOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.interfaces.RSAPrivateKey;
import java.util.Scanner;

public class Client
{
	public static void main(String [] args) throws Exception {

		//Taking command line argument
		String host = args[0]; 
		int port = Integer.parseInt(args[1]);
		String userid = args[2];
		
		Socket s = new Socket(host, port);
		DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		DataInputStream dis = new DataInputStream(s.getInputStream());

		//Input from the user aka the message 
		Scanner sc = new Scanner(System.in);
		String msg = sc.nextLine();
		
		//Computing the SHA-256 digest of the message
		MessageDigest md = MessageDigest.getInstance("SHA-256");
	    byte[] d1 = msg.getBytes();
	    md.update(d1);
	    
	    //Result of digest converted to BigInterger
	    BigInteger x = new BigInteger(1, d1);
	    
	    try {
		
	    //Reading the private key from the user's private key file
		ObjectInputStream dis1 = new ObjectInputStream(new FileInputStream(userid+".prv"));
        RSAPrivateKey privKey = (RSAPrivateKey) dis1.readObject();
        dis1.close();
       
        //Extracting the RSA modulus and private exponent
        BigInteger prKeyModulus = privKey.getModulus() ;
        BigInteger prKeyExponent = privKey.getPrivateExponent();
    
        //Computing signature y
        BigInteger y = x.modPow(prKeyExponent, prKeyModulus);
        String y1 = y.toString();
	
		while(true)
		{
	        String toserver = userid+"$"+msg+"$"+y1;
	        //System.out.print(toserver);
		
	        dos.writeUTF(toserver);		
		}
	    }
	    
	    //exception handling when user's private key not found 
	    catch (Exception e)
	    {
	    	dos.writeUTF(userid);
	    }
	}
}
