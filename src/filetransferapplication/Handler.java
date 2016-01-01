
package filetransferapplication;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author kaustabh
 */
public class Handler extends Thread{
    private String name;
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        BufferedReader inReader;
        BufferedWriter outReader;
        InputStream input ;
        
        
        
        public Handler(Socket socket) {
            this.socket = socket;
        }
        
    @Override
        public void run(){
            try{
                in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                input = socket.getInputStream();  
        inReader = new BufferedReader(new InputStreamReader(input));  
        outReader = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));     
        
        //getting a client name
        while (true) {
                    out.println("SUBMITNAME");
                    System.out.println("Server acknowledgment: SUBMITNAME");
                    name = in.readLine();
                    if (name == null) {
                        return;
                    }
                    else
                        break;
                }
        //acknowledging client
        out.println("NAMEACCEPTED");
        System.out.println("Server acknowledgment: NAMEACCCEPTED");
        
        //Getting a file name
         String filename = inReader.readLine();
         if ( !filename.equals("") ){  
   
            /* Reply back to client with READY status */  
   
            outReader.write("READY\n");  
            outReader.flush();  
        }  
         /* Create a new file in the tmp directory using the filename */
         String path = "C:\\Users\\user\\Documents\\" + filename;
        FileOutputStream wr = new FileOutputStream(new File(path));  
   
        byte[] buffer = new byte[socket.getReceiveBufferSize()];  
   
        int bytesReceived = 0;  
   
        while((bytesReceived = input.read(buffer))>0)  
        {  
            /* Write to the file */  
           wr.write(buffer,0,bytesReceived);  
        } 
        
        //Sending file to client
        FileInputStream fis ;
    BufferedInputStream bis ;
    OutputStream os ;
            File myFile = new File (path);
          byte [] mybytearray  = new byte [(int)myFile.length()];
          fis = new FileInputStream(myFile);
          bis = new BufferedInputStream(fis);
          bis.read(mybytearray,0,mybytearray.length);
          os = socket.getOutputStream();
           System.out.println("Sending " + path + "(" + mybytearray.length + " bytes)");
          os.write(mybytearray,0,mybytearray.length);
          os.flush();
          System.out.println("Done.");
            }catch (IOException e) {
                System.out.println(e);
        }
    }
}
