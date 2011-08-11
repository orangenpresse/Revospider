package revo.web;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import revo.spider.Output;

public class FTPUploader {
	private Output output;
	
	private String location;
	
	private FTPClient ftpclient = new FTPClient();
	
	public FTPUploader(String source, String dest, String server, String user, String pass) {
		this(source, dest, server, user, pass, null);
		
		class out implements Output {
			@Override
			public void write(String message) {
				System.out.println(message);		
			}
		};

		this.output = new out();
	};
	
	
	public FTPUploader(String source, String dest, String server, String user, String pass, Output output) {
		location = source;
		
		try {
			ftpclient.connect(server);
			ftpclient.login(user, pass);
			
			ftpclient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpclient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
			
			ftpclient.changeWorkingDirectory(dest);	
					
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void copy(File file) {
		try {
			if(ftpclient.storeFile(file.getPath().replaceAll(location, ""), new DataInputStream(new FileInputStream(file))))
				output.write("file: " + file.getPath().replaceAll(location,"") + " created");
			else
				output.write("error: can't create file: " + file.getPath().replaceAll(location,""));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void mkdir(File file) {
		try {
			if(ftpclient.makeDirectory(file.getPath().replaceAll(location, "")))
				output.write("dir: " + file.getPath().replaceAll(location,"") + " created");
			else
				output.write("error: can't create directory: " + file.getPath().replaceAll(location,""));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startUpload() {
		findFiles(new File(location));
		output.write("--------------====((( FTP upload done )))====--------------");
	}
	
	private void findFiles(File file) {
		if(file.isDirectory() && file.canRead()) {
			File[] files = file.listFiles();
			for(int i = 0; i < files.length; i++) {
				if(files[i].isDirectory()) {
					mkdir(files[i]);
					findFiles(files[i]);
				}
				else if(files[i].isFile()) {
					copy(files[i]);
				}
			}
		}
	}
}
