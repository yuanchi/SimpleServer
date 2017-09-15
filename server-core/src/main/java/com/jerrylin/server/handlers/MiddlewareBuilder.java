package com.jerrylin.server.handlers;

import io.undertow.server.HttpHandler;

import java.util.function.Function;
/**
 * for the experiment and learning,<br>
 * copying from
 * <a href="https://github.com/StubbornJava/StubbornJava/blob/master/stubbornjava-undertow/src/main/java/com/stubbornjava/undertow/handlers/MiddlewareBuilder.java">this.</a>
 * <br><br>
 * One of the most powerful features of Undertow is the handler chain,<br>
 * but its composing order is contrary to the executing order,<br>
 * or need to nest embed multiple handlers.
 * <br><br>
 * In any case, it seems more verbose or counter intuitive.
 * <br><br>
 * Via this builder, it can keep the format succinct,<br>
 * and make the execution order as expressed.
 * <br><br>
 * I write some <a href="https://github.com/yuanchi/SimpleServer/blob/master/server-core/src/test/java/com/jerrylin/server/handlers/MiddlewareBuilderTest.java">test cases</a> to present.
 */
public class MiddlewareBuilder {
	private final Function<HttpHandler, HttpHandler> function;
	
	private MiddlewareBuilder(Function<HttpHandler, HttpHandler> function){
		if(null == function){
			throw new IllegalArgumentException("Middleware function can't be null");
		}
		this.function = function;
	}
	
	public static MiddlewareBuilder begin(Function<HttpHandler, HttpHandler> function){
		return new MiddlewareBuilder(function);
	}
	
	public MiddlewareBuilder next(Function<HttpHandler, HttpHandler> before){
		return new MiddlewareBuilder(function.compose(before));
	}
	
	public HttpHandler complete(HttpHandler handler){
		return function.apply(handler);
	}
}
