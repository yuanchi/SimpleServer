package com.jerrylin.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;

public class ServerUtils {
	public static Undertow startLocalServer(HttpHandler rootHandler){
		Undertow undertow = 
			Undertow
				.builder()
				.addHttpListener(8080, "0.0.0.0")
				.setHandler(rootHandler)
				.build();
		undertow.start();
		return undertow;
	}
}
