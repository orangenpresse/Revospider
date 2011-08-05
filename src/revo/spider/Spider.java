package revo.spider;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringEscapeUtils;

public class Spider extends Thread {
	//config parameters
	
	//max Filesize which will be downloaded
	private int maxFilesize = 104857600;
	
	//max depth to scan in a websute
	private long maxDepth = 10;
	
	//delete the content after scan?
	private boolean deleteContent = true;
	
	//max time to wait for tasks
	private int maxWaitTime = 60;
	
	//min active threads in pool
	private int corePoolSize = 30;
	
	//max alowed threads in pool
	private int maxPoolSize = 30;
	
	
	//internnal variables
	private int depth = 0;
	private Output output;
	private String base;
	private int sitesFound = 1;
	private int sitesScanned = 0;
	private ConcurrentHashMap<String, Content> sites = new ConcurrentHashMap<String, Content>();
	private ConcurrentHashMap<String, Content> sitesDone = new ConcurrentHashMap<String, Content>();
	private Filesaver filesaver;
	private String newBase;
	private ThreadPoolExecutor threadPool = null;
		
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

	public Spider(String baseUrl, Output output, File outFolder, String newBase) {
		this.base = baseUrl;
		this.output = output;
		filesaver = new Filesaver(outFolder.toString());
		this.newBase = newBase;
	}
	
	public void run() {
		//create inital website
		Website w = new Website(this.base);
		this.sites.put(w.getUrl(), w);

		//start the scan process
		while(this.depth <= maxDepth && this.sitesFound != this.sitesScanned) {
			threadPool = new ThreadPoolExecutor(corePoolSize, 
												maxPoolSize, 
												this.maxWaitTime, 
												TimeUnit.SECONDS,  
												new ArrayBlockingQueue<Runnable>(this.sitesFound-this.sitesScanned));
			this.depth++;
			
			scanNextSites();
			
			//wait for closing all threads
			threadPool.shutdown();
			try {
				threadPool.awaitTermination(this.maxWaitTime, TimeUnit.SECONDS);
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
			Content site = sites.get(url);
			sites.remove(url);
			sitesDone.put(url, site);
			startThread(site);
		}		
	}
	
	private void startThread(Content site) {		
		Worker w = new Worker(site, this);
		startThread(w);
	}
	
	private void startThread(Runnable object) {
		threadPool.execute(object);
	}
	
	public void websiteScanned(Content site) {
		this.sitesScanned++;
		this.output.write("found: " + this.sitesFound + " scanned: " + this.sitesScanned +  " tiefe: " + this.depth  + " url: " + site.getUrl() + " status: " + site.getStatusCode());
	}
	
	//find Links in Website
	public void parseWebsite(Content site) {
		if(site instanceof Website && site.getContent() != null) {
			Website website = (Website) site;
			this.findLinks(website);
			this.findStylesheets(website);
			this.findScripts(website);
			this.findImages(website);
			this.findCssImages(site);
		}
		else if(site instanceof Stylesheet && site.getContent() != null) {
			this.findCssImages(site);
			this.findStylesheets((Stylesheet)site);
		}

		if(this.filesaver != null)
			this.saveSite(site);
		
		//delete Content
		if(this.deleteContent)
			site.clearContent();
	}

	
	private void saveSite(Content site) {
		String filename = site.getUrl().replaceAll(base, "").replaceAll("#.*$", "");
		
		if(site.getData() != null)
			filesaver.saveFile(filename, site.getData());
		else if(site.getContent() != null) {
			filesaver.saveFile(filename, this.rewriteBase(site.getContent()));
		}
	}
	
	private Content addContent(Content site, Content content) {
			//Site already in a map?
			if(!this.sites.containsKey(content.getUrl()) && !this.sitesDone.containsKey(content.getUrl())) {
				
				//check whether site is external
				if(!content.getUrl().matches(this.base+".*")) {
					content.setExternal(true);
				}
				
				//check if there is a base href
				if(site.getContent() != null) {
					Pattern basePattern = Pattern.compile( "<base [^>]*?href=\"(.*?)\".*?/>" ); 
					Matcher baseMatcher = basePattern.matcher( site.getContent()  ); 
					if(baseMatcher.find())
						content.setRef(baseMatcher.group(1));
					else
						content.setRef(content.getUrl());
				}
				
				//add site to map for scanning
				this.sitesFound++;
				this.sites.put(content.getUrl(), content);
				
				return content;
			}
			
			return content;
	}
	
	private void findCssImages(Content content) {
		Pattern pattern = Pattern.compile( "background.*?url\\((.*?)\\)" ); 
		Matcher matcher = pattern.matcher( content.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), content.getUrl());
			//TODO add Image to Site 
			
			addContent(content, new Image(url));
		}
	}
	
	private void findImages(Website site) {
		//TODO‚ alt tags einfügen
		//find Images in Website
	    Pattern pattern = Pattern.compile( "<img [^>]*?src=\"(.*?)\".*?(.*?)/>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;

			//TODO add Image to Site 
			
			addContent(site, new Image(url));
		}
	}
	
	private void findStylesheets(Stylesheet style) {
		Pattern pattern = Pattern.compile( "import .*?url\\((.*?)\\)" ); 
		Matcher matcher = pattern.matcher( style.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), style.getUrl());
			
			//TODO add Stylesheet so site
			
			addContent(style, new Stylesheet(url));
		}
	}
	
	private void findStylesheets(Website site) {
		//TODO, Import Stylesheets suchen
		
		//find Stylesheets in Website
	    Pattern pattern = Pattern.compile( "<link [^>]*?href=\"(.*?)\".*?(.*?)/>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;
			
			//TODO add Stylesheet to site
			
			addContent(site, new Stylesheet(url));
		}
	}
	
	private void findScripts(Website site) {
		//find Scripts in Website
	    Pattern pattern = Pattern.compile( "<script [^>]*?src=\"(.*?)\".*?>(.*?)</script>" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 
		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(1), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;

			//TODO add Script to site
			
			addContent(site, new Script(url));
		}

	}
	
	private void findLinks(Website site) {
		StringBuffer result = new StringBuffer();
		//result.append(site.getContent());
		//find Links in Website
	    Pattern pattern = Pattern.compile( "(<a [^>]*?href=\")((?!mailto|#|skype|javascript).*?)(\".*?>)(.*?)(</a>)" ); 
		Matcher matcher = pattern.matcher( site.getContent()  ); 

		while ( matcher.find() ) {
			String url = parseUrl(matcher.group(2), site.getRef());

			//no # loops
			if(url.matches(".*#.*#.*"))
				return;
			
			//avoid parameter loops
			/*
			Pattern loopPattern = Pattern.compile("(&.{2,}=){2,}");
			Matcher loopMatcher = loopPattern.matcher( url );
			if(loopMatcher.find()) {
				break;
			}
			*/
			
			//replace href to base relative version
			String newHref = url.replaceAll(base, "");

			String replacement = matcher.group(1) + newHref + matcher.group(3) + matcher.group(4) + matcher.group(5);
			matcher.appendReplacement(result, replacement);
			
			
			Website newSite;
			
			//Site already in a map?
			if(this.sites.containsKey(url))
				newSite = (Website) this.sites.get(url);
			else if(this.sitesDone.containsKey(url))
				newSite = (Website) this.sitesDone.get(url);
			else {
				newSite = (Website) addContent(site, new Website(url));
			}
			
			//build graph
			site.addLink(newSite, matcher.group(4));
			newSite.addReferer(site,  matcher.group(4));

		}

		matcher.appendTail(result);
		site.setContent(result.toString());
	}
	
	private String rewriteBase(String content) {
		return content.replaceAll("<base [^>]*?href=\"(.*?)\".*?/>", "<base href=\""+this.newBase+"\" />");
	}
	
	//make all URLs to absolute URLs
	public String parseUrl(String url, String ref) {
		try {
			url = url.replaceAll(" ", "+").replaceAll("'", "");
			URL u = new URL(new URL(ref),url);
			return StringEscapeUtils.unescapeHtml(u.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "";
		}
	}

	public int getMaxFilesize() {
		return maxFilesize;
	}

	public void setMaxFilesize(int maxFilesize) {
		this.maxFilesize = maxFilesize;
	}

	public long getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(long maxDepth) {
		this.maxDepth = maxDepth;
	}

	public boolean isDeleteContent() {
		return deleteContent;
	}

	public void setDeleteContent(boolean deleteContent) {
		this.deleteContent = deleteContent;
	}

	public int getMaxWaitTime() {
		return maxWaitTime;
	}

	public void setMaxWaitTime(int maxWaitTime) {
		this.maxWaitTime = maxWaitTime;
	}

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}
}
