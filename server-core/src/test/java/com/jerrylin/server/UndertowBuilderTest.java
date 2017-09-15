package com.jerrylin.server;

import io.undertow.Undertow;

import org.junit.Test;

public class UndertowBuilderTest {
	public static void main(String[]args){
		new UndertowBuilderTest().t();
	}
	@Test
	public void t(){
		Undertow.Builder builder = Undertow.builder();
		
		builder.addHttpListener(8080, "0.0.0.0", (exchange)->{
			exchange.endExchange();
		});
		
		Undertow undertow1 = builder.build();
		undertow1.start();
		undertow1.getListenerInfo().forEach(info->{
			System.out.println(info);
		});
		
		builder.addHttpListener(8081, "0.0.0.0", (exchange)->{
			exchange.endExchange();
		});
		
		Undertow undertow2 = builder.build();
//		undertow2.start();
//		undertow2.getListenerInfo().forEach(info->{
//			System.out.println(info);
//		});
		
		System.out.println(undertow1 == undertow2);
//		
//		undertow2.stop();
		undertow1.stop();
	}
}
