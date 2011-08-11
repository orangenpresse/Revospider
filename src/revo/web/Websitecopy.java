package revo.web;

import java.io.File;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import revo.spider.Spider;

@WebService(name="Revospider")
@SOAPBinding(style=Style.RPC )


public class Websitecopy implements Runnable{
	private String url;
	private String newbase;
	private String destFolder;
	private String ftpServer;
	private String ftpUser;
	private String ftpPass;
	private String[] customContent;
	
	public String copyWebsite(String url, String newbase, String destFolder, String ftpServer, String ftpUser, String ftpPass, String customContent) {
		
		this.url = url;
		this.newbase = newbase;
		this.destFolder = destFolder;
		this.ftpServer = ftpServer;
		this.ftpUser = ftpUser;
		this.ftpPass = ftpPass;
		this.customContent = customContent.split(",");
		
		Thread thread = new Thread(this);
		thread.start();
		
		return "Job started";
	}

	public void run() {
		File workingDir = new File("Websites");
		if(!workingDir.exists())
			workingDir.mkdir();
		
		workingDir = new File(workingDir,Integer.toString(url.hashCode()));
		if(!workingDir.exists())
			workingDir.mkdir();
		
		Spider spider  = new Spider(url,workingDir,newbase);
		
		for(String content : customContent)
			spider.addCustomContent(content);
		
		spider.run();

		FTPUploader upload = new FTPUploader(workingDir.getPath()+"/",destFolder,ftpServer,ftpUser,ftpPass);
		upload.startUpload();
	}
	
}
