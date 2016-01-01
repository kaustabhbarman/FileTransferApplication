/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package filetransferapplication;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author kaustabh
 */
public class FTClient extends JFrame implements ActionListener{
    
    JFrame frame = new JFrame();
    private JTextField txtFile;
    BufferedReader in;
    PrintWriter out;
    String serverAddress = getServerAddress();
    Socket socket;
    
    public void Display(){  
        frame.setTitle("Client");  
   
        FlowLayout layout = new FlowLayout();  
        layout.setAlignment(FlowLayout.LEFT);  
   
        JLabel lblFile = new JLabel("Filename:");  
         
        txtFile = new JTextField();  
        txtFile.setPreferredSize(new Dimension(150,30));  
   
        JButton btnTransfer = new JButton("Transfer");  
        btnTransfer.addActionListener(this); 
        
        JButton btnReceive = new JButton("Receive");
        btnReceive.addActionListener(new CustomActionListener());
   
        JPanel mainPanel = new JPanel();  
        mainPanel.setLayout(layout);  
        mainPanel.add(lblFile);  
        mainPanel.add(txtFile);  
        mainPanel.add(btnTransfer);
        mainPanel.add(btnReceive);
   
        frame.getContentPane().add(mainPanel);  
        frame.pack();  
        frame.setVisible(true);  
    }
    @Override
    public void actionPerformed(ActionEvent e) {  
   
        /* File Open Dialog box allows the user to select a file */  
   
        JFileChooser fileDlg = new JFileChooser();  
        fileDlg.showOpenDialog(this);  
        String filename = fileDlg.getSelectedFile().getAbsolutePath();  
        txtFile.setText(filename);  
   
        try{  
   
            /* Try to connect to the server on localhost, port 5555 */  
   
              
            OutputStream output = socket.getOutputStream();  
   
            /* Send filename to server */  
   
            OutputStreamWriter outputStream = new OutputStreamWriter(socket.getOutputStream());  
            outputStream.write(fileDlg.getSelectedFile().getName() + "\n");  
            outputStream.flush();  
   
            /* Get reponse from server */  
   
            BufferedReader inReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  
   
            String serverStatus = inReader.readLine(); // Read the first line  
   
            /* If server is ready, send the file */  
   
            if ( serverStatus.equals("READY") ){  
   
                FileInputStream file = new FileInputStream(filename);  
   
                byte[] buffer = new byte[socket.getSendBufferSize()];  
   
                int bytesRead = 0;  
   
                while((bytesRead = file.read(buffer))>0)  
                {  
                    output.write(buffer,0,bytesRead);  
                }  
                JOptionPane.showMessageDialog(this, "Transfer complete");  
            }  
        }  
        catch (Exception ex){  
            /* Catch any errors */  
            JOptionPane.showMessageDialog(this, ex.getMessage());  
        }  
    }
    
    class CustomActionListener implements ActionListener{public void actionPerformed(ActionEvent e){
        JFileChooser fileDlg = new JFileChooser("Save file to");  
        fileDlg.showOpenDialog(fileDlg);  
        String filename = fileDlg.getSelectedFile().getName();
        int bytesRead;
    int current = 0;
    FileOutputStream fos = null;
    BufferedOutputStream bos = null;
        String receivedPath = "C:\\Users\\user\\Documents\\Received"+filename; // you may change this, I give a
                                                            // different name because i don't want to
                                                            // overwrite the one used by server...
            int FILE_SIZE = 999999999;        // file size temporary hard coded. should bigger than the file to be downloaded
        try{
            // receive file
      byte [] mybytearray  = new byte [FILE_SIZE];
      InputStream is = socket.getInputStream();
      fos = new FileOutputStream(receivedPath);
      bos = new BufferedOutputStream(fos);
      bytesRead = is.read(mybytearray,0,mybytearray.length);
      current = bytesRead;
      
      do {
         bytesRead =
            is.read(mybytearray, current, (mybytearray.length-current));
         if(bytesRead >= 0) current += bytesRead;
      } while(bytesRead > -1);
      bos.write(mybytearray, 0 , current);
      bos.flush();
            
        }catch(IOException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Couldn't Receive File From Server");
        }finally {
            try{
      if (fos != null) fos.close();
      if (bos != null) bos.close();
            }catch(IOException ex){
                ex.printStackTrace();
            }
        }
        
        JOptionPane.showMessageDialog(null, "File Downloaded.");
         
       }
    }
    
    //Prompt for and return the address of the server.
     
    private String getServerAddress() {
        return JOptionPane.showInputDialog(
            frame,
            "Enter IP Address of the Server:",
            "Welcome to the File Transfer",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    private String getServerPassword(){
        return JOptionPane.showInputDialog(
        frame,
                "Enter Password for Server:",
                "Password",
                JOptionPane.QUESTION_MESSAGE);
    }
    
    /**
     * Prompt for and return the desired screen name.
     */
    private String getClientName() {
        return JOptionPane.showInputDialog(
            frame,
            "Choose a screen name:",
            "Screen name selection",
            JOptionPane.PLAIN_MESSAGE);
    }
    
    private void run() throws IOException{
        // Make connection and initialize streams
        
        boolean flag = true;
        while(flag=true){
            String password = getServerPassword();
            if(password.equals("1234")){
                flag = false;
             socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while(true){
            String line = in.readLine();
            if (line.startsWith("SUBMITNAME")) {
                out.println(getClientName());
            }else
                break;
        
    }
            }
            else{
                JOptionPane.showMessageDialog(frame, "Wrong Password");
            }
            break;
        }
    }
    
     public static void main(String args[]) throws Exception{  
   
        /* Create and display the client form */  
   
        FTClient clientForm = new FTClient();
        clientForm.run();
        clientForm.Display();
        clientForm.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        clientForm.frame.setVisible(true);
        
    }
    
}
