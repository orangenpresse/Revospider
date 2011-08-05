package revo.spider;

public class Worker extends Thread {
	private Content site;
	Spider spider;
	
	Worker(Content site, Spider spider) {
		this.site = site;
		this.spider = spider;
	}

	public void run() {
		try {
			//scan the website
			new URLRequest(this.site).execute(spider.getMaxFilesize());
			
			//only follow internal websites
			if(!this.site.isExternal())
				spider.parseWebsite(site);
			
			//set the website scanned
			spider.websiteScanned(this.site);
		} catch (URLRequestException e) {
			//e.printStackTrace();
			//TODO set the website scanned ***Should be changed here***
			spider.websiteScanned(this.site);
		}
	}
}
