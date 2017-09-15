package com.jerrylin.server;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ClientUtils {
	public static void postJson(String url, String content, Callback callback){
		MediaType json = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(json, content);
		Request request = 
			new Request.Builder()
				.url(url)
				.post(body)
				.build();
		
		OkHttpClient client = new OkHttpClient();
		Call call = client.newCall(request);
		
		call.enqueue(callback);
	}
}
