package revo.web;

import java.io.File;

import javax.swing.JOptionPane; 
import javax.xml.ws.Endpoint; 

import revo.spider.Spider;

public class Server {
	  public static void main( String[] args ) 
	  { 
		Spider spider  = new Spider("http://kix.dcn.de/",new File("/Users/Benedict/Test/"),"http://www.dcn.name/");
		spider.run();
		  
		FTPUploader upload = new FTPUploader("/Users/Benedict/Test/","/dcn.name/","ws.udag.de","30027.webmaster","F2NAB1Ze");
		upload.startUpload();
		
		
	    //Endpoint endpoint = Endpoint.publish( "http://localhost:8080/services", 
		//new Websitecopy() ); 
	    //JOptionPane.showMessageDialog( null, "Server beenden" ); 
	    //endpoint.stop(); 
	  } 
}
