package com.jerrylin.server.util;

public class Exchange {
	public static interface BodyImpl extends
		JsonSender,
		JsonParser{}
	private static final BodyImpl BODY = new BodyImpl(){};
	public static BodyImpl body(){
		return BODY;
	}
}
