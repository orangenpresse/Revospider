package revo.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Filesaver {
	private String folder;
	private String standartName;
	
	public Filesaver(String folder) {
		this.folder = folder;
		this.standartName = "index.html";
	}
	
	public Filesaver(String folder, String standartName) {
		this.folder = folder;
		this.standartName = "index.html";
		this.standartName = standartName;
	}
	
	public void saveFile(String filename, String data) {
		try {
			File file = new File(this.createFolders(filename));
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
			writer.write(data);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void saveFile(String filename, byte[] data) {
		try {
			File file = new File(this.createFolders(filename));
			
			FileOutputStream out =  new FileOutputStream(file);
			
			out.write(data);
			out.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String createFolders(String filename) {
		if(filename.equals(""))
			filename = this.standartName;
		
		String path = folder;
		String[] dirs = filename.split("/");
		for( String dir : dirs) {
			path += "/" + dir;
			File file = new File(path);
			
			if(!file.exists() && dirs[dirs.length-1] != dir)
				file.mkdir();

			if(dirs[dirs.length-1] == dir && file.isDirectory())
				path += "/" + this.standartName;
		}
		
		//replace all ? parametes in filename
		return path.replaceAll("\\?.*","");
	}
	
}
