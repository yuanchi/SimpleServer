package com.jerrylin.server.util;

import io.undertow.io.Receiver.FullBytesCallback;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.AttachmentKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.jsoniter.JsonIterator;

public interface JsonParser {
	/**
	 * this method needs to enable blocking(using BlockingHandler)
	 * @param exchange
	 * @param type
	 * @return
	 */
	default <T> T parseJsonBlocked(HttpServerExchange exchange, Class<T> type){
		T t = null;
		int bufferSize = exchange.getConnection().getBufferSize();
		byte[] buffered = new byte[bufferSize];
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream();){
			InputStream is = exchange.getInputStream();
			int read;
			while((read = is.read(buffered, 0, bufferSize)) != -1){
				baos.write(buffered, 0, read);
			}
			baos.flush();
			byte[] data = baos.toByteArray();
			JsonIterator iterator = JsonIterator.parse(data);
			t = iterator.read(type);
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		return t;
	}
	
	default <T>void parseJsonNotBlocked(HttpServerExchange exchange, Class<T> type, AttachmentKey<T> key){
		
		FullBytesCallback callback 
			= new FullBytesCallback(){
				@Override
				public void handle(HttpServerExchange exchange, byte[] message) {
					T t = JsonIterator.deserialize(message, type);
					exchange.putAttachment(key, t);
				}
			};
		exchange.getRequestReceiver().receiveFullBytes(callback);
	}
}
