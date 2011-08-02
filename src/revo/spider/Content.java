package revo.spider;

public abstract class Content {
	private String ref = null;
	private String url;
	private String content;
	private int statusCode;
	private boolean external;
	
	Content(String url) {
		this.url = url;
		statusCode = 0;
	}
	
	public void clearContent() {
		this.content = "";
	}
	public String getRef() {
		if(ref == null)
			return url;
		else
			return ref;
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
	
	
}