package revo.spider;

public abstract class Content {
	private String ref = null;
	private String url;
	private String newUrl;
	private String content;
	private String mimeType;
	private byte[] data;
	private int statusCode;
	private boolean external;
	
	public Content() {
		this.statusCode = 0;
	}
	
	public Content(String url) {
		this.url = url;
		this.statusCode = 0;
	}
	
	public void clearContent() {
		this.content = null;
		this.data = null;
	}
	public String getRef() {
		if(ref == null)
			return url;
		else
			return ref;
	}
	
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public void setRef(String ref) {
		this.ref = ref;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isExternal() {
		return external;
	}
	public void setExternal(boolean external) {
		this.external = external;
	}
	public String getNewUrl() {
		if (this.newUrl == null)
			return this.url;
		else
			return newUrl;
	}
	public void setNewUrl(String newUrl) {
		this.newUrl = newUrl;
	}
	
}
