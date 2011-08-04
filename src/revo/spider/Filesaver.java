package revo.spider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class Filesaver {
	private String folder;
	
	public Filesaver(String folder) {
		this.folder = folder;
	}
	
	public void saveFile(String filename, String data) {
		try {
			if(filename.equals(""))
				//TODO Einstellbarer Wert
				filename = "index.html";
			
			String path = folder;
			String[] dirs = filename.split("/");
			for( String dir : dirs) {
				path += "/" + dir;
				File file = new File(path);
				
				if(!file.exists() && dirs[dirs.length-1] != dir)
					file.mkdir();

				if(dirs[dirs.length-1] == dir && file.isDirectory())
					path += "/" + "index.html";
			}

			File file = new File(path);
			
			OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
			writer.write(data);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
