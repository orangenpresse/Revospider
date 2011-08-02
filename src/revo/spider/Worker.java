package revo.spider;

public class Worker extends Thread {
	private Website site;
	Spider spider;
	
	Worker(Website site, Spider spider) {
		this.site = site;
		this.spider = spider;
	}

	public void run() {
		try {
			//scan the website
			new URLRequest(this.site).execute(spider.MAX_FILESIZE);
			
			//only follow interlan websites
			if(!this.site.isExternal())
				spider.parseWebsite(site);
			
			//set the website scanned
			spider.websiteScanned(this.site);
		} catch (URLRequestException e) {
			//e.printStackTrace();
			//set the website scanned ***Should be changed here***
			spider.websiteScanned(this.site);
		}
	}
}
