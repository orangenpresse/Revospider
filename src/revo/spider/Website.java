package revo.spider;
import java.util.HashMap;
import java.util.Set;


public class Website {
	private String ref = null;
	private String url;
	private String content;
	private int statusCode;
	private boolean external;
	private HashMap<Website, String[]> referer = new HashMap<Website, String[]>();
	private HashMap<Website, String[]> links = new HashMap<Website, String[]>();
	
	Website(String url) {
		this.url = url;
		statusCode = 0;
	}
	
	public Set<Website> getReferer() {
		return this.referer.keySet();
	}
	
	public Set<Website> getLinks() {
		return this.links.keySet();
	}
	
	public void addReferer(Website ref, String linkText) {
		this.addLinkStringToWebsiteHashMap(this.referer, ref, linkText);
	}
	
	public void addLink(Website link, String linkText) {
		this.addLinkStringToWebsiteHashMap(this.links, link, linkText);
	}
	
	private void addLinkStringToWebsiteHashMap(HashMap<Website, String[]> map, Website site, String linkText) {
		String[] linkTexts;
		if(map.containsKey(site)){
			linkTexts = map.get(site);
			String[] newLinkTexts = new String[linkTexts.length+1];
			System.arraycopy(linkTexts, 0, newLinkTexts, 0, linkTexts.length);
			linkTexts = newLinkTexts;
		}
		else {
			linkTexts = new String[1];
		}
		
		linkTexts[linkTexts.length-1] = linkText;
		map.put(site, linkTexts);
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
