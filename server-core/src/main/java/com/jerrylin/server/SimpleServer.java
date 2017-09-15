package com.jerrylin.server;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsoniter.any.Any;

public class SimpleServer implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(SimpleServer.class);
	private static final long DEFAULT_SHUTDOWN_PERIOD = 1L; // TODO default shutdown period may adjust
	
	private final Undertow undertow;
	private List<GracefulShutdownHandler> shutdowns;
	private long shutdownPeriod = DEFAULT_SHUTDOWN_PERIOD;
	
	private SimpleServer(
		Undertow undertow,
		List<GracefulShutdownHandler> shutdowns,
		Any config){
		this.undertow = undertow;
		this.shutdowns = shutdowns;
		if(shutdowns == null){
			this.shutdowns = Collections.emptyList();
		}
		if(config != null){
			// TODO
		}
	}
	
	@Override
	public void run() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
		
		undertow.getListenerInfo().forEach(info->{
			logger.info("Start Server on {}", info.getAddress());
		});
		
		undertow.start();
	}
	
	public void shutdown(){
		try{
			logger.info("Stopping Http server");
			for(GracefulShutdownHandler shutdown : shutdowns){
				shutdown.shutdown();
				shutdown.awaitShutdown(shutdownPeriod);
			}
			undertow.stop();
		}catch(Exception e){
			Thread.currentThread().interrupt();
		}
	}
	
	public static class Builder{
		private final Undertow.Builder undertowBuilder;
		private List<GracefulShutdownHandler> shutdowns;
		private Any config;
		public Builder(){
			undertowBuilder = Undertow.builder();
		}
		public Builder addHttpListener(int port, String host, HttpHandler rootHandler){
			GracefulShutdownHandler shutdown = null;
			if(!(rootHandler instanceof GracefulShutdownHandler)){
				shutdown = new GracefulShutdownHandler(rootHandler);
			}else{
				shutdown = GracefulShutdownHandler.class.cast(rootHandler);
			}
			undertowBuilder.addHttpListener(port, host, shutdown);
			if(shutdowns == null){
				shutdowns = new ArrayList<>();
			}
			shutdowns.add(shutdown);
			return this;
		}
		public Builder configBuilder(Consumer<Undertow.Builder> configBuilder){
			configBuilder.accept(undertowBuilder);
			return this;
		}
		public Builder configServer(Any config){
			this.config = config;
			return this;
		}
		public SimpleServer build(){
			SimpleServer server = new SimpleServer(undertowBuilder.build(), shutdowns, config);
			return server;
		}
	}
	


}
