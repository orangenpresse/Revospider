package revo.spider;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


public class Website extends Content {
	private HashMap<Website, String[]> referer = new HashMap<Website, String[]>();
	private HashMap<Website, String[]> links = new HashMap<Website, String[]>();
	private Vector<Stylesheet> stylesheets = new Vector<Stylesheet>();
	private Vector<Image> images = new Vector<Image>();
	private Vector<Script> scripts = new Vector<Script>();
	
	Website(String url) {
		super(url);
	}
	
	public Vector<Stylesheet> getStylesheets() {
		return this.stylesheets;
	}
	
	public Vector<Image> getImages() {
		return this.images;
	}
	
	public Vector<Script> getScripts() {
		return this.scripts;
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
	
}
