package revo.spider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;

import revo.gui.Linkchecker;


public class Spider extends Thread {
	public  final int MAX_FILESIZE = 100000;
	public final long MAX_DEPTH = 10;
	public final int MAX_WAITTIME = 60;
	public final boolean DELETE_CONTENT = false;
	private int depth = 0;
	private Output output;
	private String base;
	private int sitesFound = 1;
	private int sitesScanned = 0;
	private ConcurrentHashMap<String, Website> sites = new ConcurrentHashMap<String, Website>();
	private ConcurrentHashMap<String, Website> sitesDone = new ConcurrentHashMap<String, Website>();

	//Parallel running Threads(Executor) on System
    int corePoolSize = 30;
    //Maximum Threads allowed in Pool
    int maxPoolSize = 30;
    //Keep alive time for waiting threads for jobs(Runnable)
    long keepAliveTime = 100;
    //This is the one who manages and start the work
    ThreadPoolExecutor threadPool = null;
	
	
	public static void main(String[] args) throws MalformedURLException {
		new Linkchecker();
	}
		
	public Spider(String baseUrl) {
		this(baseUrl, null);
		
		class out implements Output {
			@Override
			public void write(String message) {
				System.out.println(message);		
			}
		};

		this.output = new out();
	};
	
	public Spider(String baseUrl, Output output) {
		this.base = baseUrl;
		this.output = output;
	}

	public void run() {
		//create inital website
		Website w = new Website(this.base);
		this.sites.put(w.getUrl(), w);

		//start the scan process
		while(this.depth <= MAX_DEPTH && this.sitesFound != this.sitesScanned) {
			threadPool = new ThreadPoolExecutor(corePoolSize, 
												maxPoolSize, 
												this.MAX_WAITTIME, 
												TimeUnit.SECONDS,  
												new ArrayBlockingQueue<Runnable>(this.sitesFound-this.sitesScanned));
			this.depth++;
			
			scanNextSites();
			
			threadPool.shutdown();
			try {
				threadPool.awaitTermination(this.MAX_WAITTIME, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.output.write("--------------====((( scan done )))====--------------");
		
		/*
		for(String url : sitesDone.keySet()) {
			this.output.write(url + " => " + sitesDone.get(url).getStatusCode());
		}
		*/
		this.output.write("sites scanned: " + new Integer(sitesDone.size()).toString());
		
	}
	
	private void scanNextSites() {		
		for(String url : sites.keySet()) {
			Website site = sites.get(url);
			sites.remove(url);
			sitesDone.put(url, site);
			startThread(site);
		}		
	}
	
	private void startThread(Website site) {		
		Worker w = new Worker(site, this);
		startThread(w);
	}
	
	private void startThread(Runnable object) {
		threadPool.execute(object);
	}
	
	public void websiteScanned(Website site) {
		this.sitesScanned++;
		//this.output.write("found: " + this.sitesFound + " scanned: " + this.sitesScanned +  " tiefe: " + this.depth  + " url: " + site.getUrl() + " status: " + site.getStatusCode());

	}
	
	//find Links in Website
	public void parseWebsite(Website site) {
		
		this.findLinks(site);
		this.findStylesheets(site);
		this.findScripts(site);
		this.findImages(site);
		
		//delete Content
		if(this.DELETE_CONTENT)
			site.clearContent();
	}

	
	private void findImages(Website site) {
		//TODO‚ alt tags einfügen
		//find Images in Website
	    Pattern pattern = Pattern.compile( "<img [^>]*?src=\"((?!mailto|#|skype|javascript).*?)\".*?>(.*?)/>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;
			
			//avoid parameter loops
			Pattern loopPattern = Pattern.compile("(&.{2,}=){2,}");
			Matcher loopMatcher = loopPattern.matcher( url );
			if(loopMatcher.find()) {
				break;
			}
			
			Image image;
			
			//create new site
			image = new Image(url);
			
			//check whether site is external
			if(!image.getUrl().matches(this.base+".*")) {
				image.setExternal(true);
			}
			
			//check if there is a base href
			Pattern basePattern = Pattern.compile( "<base [^>]*?href=\"((?!mailto|#|skype).*?)\".*?/>" ); 
			Matcher baseMatcher = basePattern.matcher( site.getContent()  ); 
			if(baseMatcher.find())
				image.setRef(baseMatcher.group(1));
			else
				image.setRef(url);
			
			site.addImage(image);
		}
	}
	
	private void findStylesheets(Website site) {
		//TODO, Import Stylesheets suchen
		//TODO, url() suchen umschreiben‚
		//find Stylesheets in Website
	    Pattern pattern = Pattern.compile( "<link [^>]*?href=\"((?!mailto|#|skype|javascript).*?)\".*?>(.*?)/>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;
			
			//avoid parameter loops
			Pattern loopPattern = Pattern.compile("(&.{2,}=){2,}");
			Matcher loopMatcher = loopPattern.matcher( url );
			if(loopMatcher.find()) {
				break;
			}
			
			Stylesheet style;
			
	
			//create new site
			style = new Stylesheet(url);
			
			//check whether site is external
			if(!style.getUrl().matches(this.base+".*")) {
				style.setExternal(true);
			}
			
			//check if there is a base href
			Pattern basePattern = Pattern.compile( "<base [^>]*?href=\"((?!mailto|#|skype).*?)\".*?/>" ); 
			Matcher baseMatcher = basePattern.matcher( site.getContent()  ); 
			if(baseMatcher.find())
				style.setRef(baseMatcher.group(1));
			else
				style.setRef(url);
			
			site.addStylesheet(style);
		}
	}
	
	private void findScripts(Website site) {
		//find Scripts in Website
	    Pattern pattern = Pattern.compile( "<script [^>]*?src=\"((?!mailto|#|skype|javascript).*?)\".*?>(.*?)</script>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;
			
			//avoid parameter loops
			Pattern loopPattern = Pattern.compile("(&.{2,}=){2,}");
			Matcher loopMatcher = loopPattern.matcher( url );
			if(loopMatcher.find()) {
				break;
			}
			
			Script script;
			
			//create new script
			script = new Script(url);
			
			//check whether site is external
			if(!script.getUrl().matches(this.base+".*")) {
				script.setExternal(true);
			}
			
			//check if there is a base href
			Pattern basePattern = Pattern.compile( "<base [^>]*?href=\"((?!mailto|#|skype).*?)\".*?/>" ); 
			Matcher baseMatcher = basePattern.matcher( site.getContent()  ); 
			if(baseMatcher.find())
				script.setRef(baseMatcher.group(1));
			else
				script.setRef(url);
			
			site.addScript(script);
		}

	}
	
	private void findLinks(Website site) {
		//TODO Rewrite und speichern implementiern
		//find Links in Website
	    Pattern pattern = Pattern.compile( "<a [^>]*?href=\"((?!mailto|#|skype|javascript).*?)\".*?>(.*?)</a>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;
			
			//avoid parameter loops
			Pattern loopPattern = Pattern.compile("(&.{2,}=){2,}");
			Matcher loopMatcher = loopPattern.matcher( url );
			if(loopMatcher.find()) {
				break;
			}
			
			Website newSite;
			
			//Site already in a map?
			if(this.sites.containsKey(url))
				newSite = this.sites.get(url);
			else if(this.sitesDone.containsKey(url))
				newSite = this.sitesDone.get(url);
			else {
				//create new site
				newSite = new Website(url);
				
				//check whether site is external
				if(!newSite.getUrl().matches(this.base+".*")) {
					newSite.setExternal(true);
				}
				
				//check if there is a base href
				Pattern basePattern = Pattern.compile( "<base [^>]*?href=\"((?!mailto|#|skype).*?)\".*?/>" ); 
				Matcher baseMatcher = basePattern.matcher( site.getContent()  ); 
				if(baseMatcher.find())
					newSite.setRef(baseMatcher.group(1));
				else
					newSite.setRef(url);
				
				//add site to map for scanning
				this.sitesFound++;
				this.sites.put(url, newSite);
			}
			
			//build graph
			site.addLink(newSite, matcher.group(2));
			newSite.addReferer(site,  matcher.group(2));
		}
	}
	
	//make all URLs to absolute URLs
	public String parseUrl(String url, String ref) {
		try {
			url = url.replaceAll(" ", "+");
			URL u = new URL(new URL(ref),url);
			return StringEscapeUtils.unescapeHtml(u.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
	}
}
