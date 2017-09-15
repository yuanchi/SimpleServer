package com.jerrylin.server.util;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.util.AttachmentKey;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import com.jerrylin.server.ClientUtils;
import com.jerrylin.server.ServerUtils;

public class ExchangeTest {
	public static void main(String[]args){
		testJsonParser();
	}
	public static void sendPostTo(String url){
		
	}
	public static void testJsonParser(){
		HttpHandler printInputStreamAsMap = 
			new HttpHandler(){
				@Override
				public void handleRequest(HttpServerExchange exchange)
						throws Exception {
					Map<?, ?> obj = Exchange.body().parseJsonBlocked(exchange, Map.class);
					
					String expected = "{A=1.0, B=2.0}";
					String result = obj.toString();
					if(expected.equals(result)){
						System.out.println("parsed successfully:\n" + obj);
					}else{
						throw new RuntimeException("parsed failed:\n" + obj);
					}
					
					exchange.endExchange();
				}			
			};
			
		BlockingHandler blocking = new BlockingHandler();
		blocking.setRootHandler(printInputStreamAsMap);
		
		HttpHandler nonblocking = 
			new HttpHandler(){
				@Override
				public void handleRequest(HttpServerExchange exchange){
					AttachmentKey<Map> key = AttachmentKey.create(Map.class);
					Exchange.body().parseJsonNotBlocked(exchange, Map.class, key);
					
					ExchangeCompletionListener whenComplete = 
						new ExchangeCompletionListener(){
							@Override
							public void exchangeEvent(
									HttpServerExchange exchange,
									NextListener nextListener) {
								Map<?, ?> obj = exchange.getAttachment(key);
								System.out.println(obj);
								nextListener.proceed();
							}
						
					};
					exchange.addExchangeCompleteListener(whenComplete);
					exchange.endExchange();
				}
			}; 
		
		PathHandler path = 
			Handlers.path()
				.addExactPath("/blocking", blocking)
				.addExactPath("/nonblocking", nonblocking);
		
		Undertow undertow = 
			ServerUtils.startLocalServer(path);
		
		String json = "{\"A\": 1, \"B\": 2}";
		ClientUtils.postJson("http://localhost:8080/blocking", json,
			new Callback(){
				@Override
				public void onFailure(Call call, IOException e) {
					System.out.println("blocking failed");
				}
				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					System.out.println("blocking responsed");
				}
			});
		ClientUtils.postJson("http://localhost:8080/nonblocking", json,
			new Callback(){
				@Override
				public void onFailure(Call call, IOException e) {
					System.out.println("nonblocking failed");
				}
				@Override
				public void onResponse(Call call, Response response)
						throws IOException {
					System.out.println("nonblocking responsed");
				}
			});
	}
}
