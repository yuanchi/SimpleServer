package com.jerrylin.server.util;

import java.nio.ByteBuffer;

import com.jsoniter.output.JsonStream;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;

public interface JsonSender {
	default void sendJson(HttpServerExchange exchange, Object obj){
		exchange.getResponseHeaders().add(Headers.CONTENT_TYPE, "application/json");
		exchange.getResponseSender().send(ByteBuffer.wrap(JsonStream.serialize(obj).getBytes()));
	}
}
