import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.interfaces.RSAPublicKey;

public class Server {
	public static void main(String [] args) throws Exception {

		//Taking command line argument
		int port = Integer.parseInt(args[0]);
		
		ServerSocket ss = new ServerSocket(port);
		
		while(true) {
			
			Socket s = ss.accept();
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());

			String fromclient = dis.readUTF();
			int len = fromclient.length();
			
			String userid = "";
			String msg = "";
			String sy = "";
			BigInteger y ;
			int i = 0, j = 0, k = 0;
			
			try {
			
			//Extracting the userid, message and BigInterger from the client
			while(fromclient.charAt(i) != '$')
			{
				char ud = fromclient.charAt(i);
				userid = userid + ud;
				i++;
			}
			i++;
			j=i;
			while(fromclient.charAt(j) != '$')
			{
				char s1 = fromclient.charAt(j);
				msg = msg + s1;
				j++;
			}
			j++;
			k=j;
			
			do {				
				sy = sy + fromclient.charAt(k);
				k++;
			}while(k!=len);
			
			//sy = fromclient.substring(k, len);
			
			y = new BigInteger(sy);
			
			//Reading the public key from the user's public key file
			ObjectInputStream dis1 = new ObjectInputStream(new FileInputStream(userid+".pub"));
	        RSAPublicKey pubKey = (RSAPublicKey) dis1.readObject();
	        dis1.close();
	        
	        //Extracting the RSA modulus and public exponent
	        BigInteger pbKeyModulus = pubKey.getModulus() ;
	        BigInteger pbKeyExponent = pubKey.getPublicExponent();
	        
	        //Computing BigInteger x 
	        BigInteger  x = y.modPow(pbKeyExponent, pbKeyModulus);
	        
	        //Computing the SHA-256 digest of the received message	        
	        MessageDigest md = MessageDigest.getInstance("SHA-256");
	        byte[] z1 = msg.getBytes();
	        md.update(z1);
		
	        //Result of digest converted to BigInterger 
	        BigInteger z = new BigInteger(1, z1);
	        
	       
	        
	        //Checking if signature is verified (z should be equal to x)
	        if (x.equals(z))
	        {
	        	//System.out.print("z = "+z+"\n"+"x = "+x+"\n"+"msg = "+msg+"\n");
	        	System.out.print(userid+" : "+msg+"\n");
	        }
	        else
		        System.out.print(userid+" : Signature not Verified \n");
	        }
	
			//exception handling when user's public key not found 
			catch(Exception e) 
			{
			System.out.print(fromclient+" : Signature Not Verified \n" );
			}
		}
	}
}
