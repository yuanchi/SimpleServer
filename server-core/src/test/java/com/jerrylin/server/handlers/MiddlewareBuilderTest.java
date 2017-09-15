package com.jerrylin.server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

import org.junit.Test;

public class MiddlewareBuilderTest {
	/**
	 * printing results:<br>
	 * BeginHandler begin...<br>
	 * Next1Handler begin...<br>
	 * Next2Handler begin...<br>
	 * CompleteHandler begin...<br>
	 * return to CompleteHandler...<br>
	 * return to Next2Handler...<br>
	 * return to Next1Handler...<br>
	 * return to BeginHandler...<br>
	 */
	@Test
	public void composeWithInitializingOrder()throws Exception{
		CompleteHandler completeHandler = 
			new CompleteHandler();
		
		Next2Handler next2Handler = 
			new Next2Handler(completeHandler);
		
		Next1Handler next1Handler = 
			new Next1Handler(next2Handler);
		
		BeginHandler rootHandler = 
			new BeginHandler(next1Handler);
		
		rootHandler.handleRequest(null);
	}
	/**
	 * printing results same as {@link #composeWithInitializingOrder()}
	 */
	@Test
	public void composeWithNested()throws Exception{		
		BeginHandler rootHandler = 
			new BeginHandler(
				new Next1Handler(
					new Next2Handler(
						new CompleteHandler()	
					)	
				)
			);
		
		rootHandler.handleRequest(null);
	}
	/**
	 * printing results same as {@link #composeWithInitializingOrder()}
	 */
	@Test
	public void buildWithConstructor()throws Exception{
		HttpHandler rootHandler = 
			MiddlewareBuilder
				.begin(BeginHandler::new)
				.next(Next1Handler::new)
				.next(Next2Handler::new)
				.complete(new CompleteHandler());
		
		rootHandler.handleRequest(null);
	}
	/**
	 * printing results same as {@link #composeWithInitializingOrder()}
	 */
	@Test
	public void buildWithStaticMethod()throws Exception{
		HttpHandler rootHandler = 
			MiddlewareBuilder
				.begin(MiddlewareBuilderTest::beginHandler)
				.next(MiddlewareBuilderTest::next1Handler)
				.next(MiddlewareBuilderTest::next2Handler)
				.complete(completeHandler());
			
		rootHandler.handleRequest(null);		
	}
	
	static BeginHandler beginHandler(HttpHandler next){
		return new BeginHandler(next);
	}
	static Next1Handler next1Handler(HttpHandler next){
		return new Next1Handler(next);
	}
	static Next2Handler next2Handler(HttpHandler next){
		return new Next2Handler(next);
	}
	static CompleteHandler completeHandler(){
		return new CompleteHandler();
	}
	static class BeginHandler implements HttpHandler{
		private HttpHandler next;
		BeginHandler(HttpHandler next){
			this.next = next;
		}
		@Override
		public void handleRequest(HttpServerExchange exchange) throws Exception{
			System.out.println("BeginHandler begin...");
			next.handleRequest(exchange);
			System.out.println("return to BeginHandler...");
		}
	}
	static class Next1Handler implements HttpHandler{
		private HttpHandler next;
		Next1Handler(HttpHandler next){
			this.next = next;
		}
		@Override
		public void handleRequest(HttpServerExchange exchange) throws Exception{
			System.out.println("Next1Handler begin...");
			next.handleRequest(exchange);
			System.out.println("return to Next1Handler...");
		}
	}
	static class Next2Handler implements HttpHandler{
		private HttpHandler next;
		Next2Handler(HttpHandler next){
			this.next = next;
		}
		@Override
		public void handleRequest(HttpServerExchange exchange) throws Exception{
			System.out.println("Next2Handler begin...");
			next.handleRequest(exchange);
			System.out.println("return to Next2Handler...");
		}
	}
	static class CompleteHandler implements HttpHandler{
		@Override
		public void handleRequest(HttpServerExchange exchange) throws Exception{
			System.out.println("CompleteHandler begin...");
			System.out.println("return to CompleteHandler...");
		}
	}
}
