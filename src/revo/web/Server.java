package revo.web;

import javax.xml.ws.Endpoint; 

public class Server {
	  public static void main( String[] args ) 
	  { 
		  if(args.length < 1)
			  System.out.println("no address given");
		  else
			  Endpoint.publish(args[0], new Websitecopy() ); 
	  } 
	  
}
