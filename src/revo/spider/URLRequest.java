package revo.spider;
import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class URLRequest {
	private Content site;
	
	public void execute(int maxFileSize) throws URLRequestException {
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(this.site.getUrl());
			HttpHead httphead = new HttpHead(this.site.getUrl());
			HttpResponse response;
			HttpEntity entity;
			
			//get Head and get size
			response = httpclient.execute(httphead);
			int contentLength = 0;
			for(Header h : response.getAllHeaders()) {
				if(h.getName().compareToIgnoreCase("Content-Length") == 0) {
					contentLength = Integer.parseInt(h.getValue());
					break;
				}
			}
			
			response = httpclient.execute(httpget);
			entity = response.getEntity();

			this.site.setStatusCode(response.getStatusLine().getStatusCode());
			if(entity != null && contentLength < maxFileSize) {				
				this.site.setMimeType(EntityUtils.getContentMimeType(entity));

				//if(this.site.getMimeType().matches("image/jpeg|image/png|image/gif|text/plain|application/pdf|application/msword|null")) {
				if(!this.site.getMimeType().matches("text/html|application/x-javascript|text/css|text/xml")) {
					this.site.setData(EntityUtils.toByteArray(entity));
				}
				else {
					//this.site.setContent(EntityUtils.toString(entity,"UFT-8"));
					this.site.setContent(EntityUtils.toString(entity));
				}
			}
			else {
				System.out.println("FAIL:" + site.getUrl());
				this.site.setContent("");
			}
		}
		catch (IOException e){
			throw new URLRequestException();
		}
	}

	URLRequest(Content website) {
		this.site = website;
	}
	
	public Content getSite() {
		return this.site;
	}
}
