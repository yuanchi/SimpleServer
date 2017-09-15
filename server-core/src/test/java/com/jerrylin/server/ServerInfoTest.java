package com.jerrylin.server;

import java.io.IOException;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.Headers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ServerInfoTest {
	public static void main(String[]args){
		serverInfo();
	}
	public static void serverInfo(){
		HttpHandler defaultHandler = 
			new HttpHandler(){
				@Override
				public void handleRequest(HttpServerExchange exchange){
					exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
					exchange.getResponseSender().send("message sent");
				}
			};
		PathHandler pathHandler1 = 
			Handlers.path()
				.addExactPath("/AAA", defaultHandler);
		PathHandler pathHandler2 = 
			Handlers.path()
				.addExactPath("/BBB", defaultHandler);
		Undertow undertow = 
			Undertow.builder()
				.addHttpListener(8080, "0.0.0.0", pathHandler1)
				.addHttpListener(8081, "0.0.0.0", pathHandler2)
				.build();
		undertow.start();
		
		ClientUtils.postJson("http://localhost:8080/AAA", "ss",
			new Callback(){
				@Override
				public void onFailure(Call call, IOException e) {
					System.out.println("failed");
				}
				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					System.out.println("responsed");
					undertow.getListenerInfo().forEach(info->{
						System.out.println(info);
					});
					undertow.stop();
				}
		});
	}
}
