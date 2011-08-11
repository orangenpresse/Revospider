package revo.web;

import java.io.File;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import revo.spider.Spider;

@WebService(name="Revospider")
@SOAPBinding(style=Style.RPC )


public class Websitecopy {
	public void scanWebsite(String url, String newbase) {
		Spider spider = new Spider(url,new File("/tmp"),newbase);
		spider.run();
	}
}
