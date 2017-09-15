package com.jerrylin.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;

import com.jsoniter.JsonIterator;
import com.jsoniter.any.Any;

public class JsoniterTest {
	@Test
	public void parseInputStream() throws FileNotFoundException, IOException{
		String file = "C:\\Users\\JerryLin\\Desktop\\config.conf";
		try(FileInputStream fis = new FileInputStream(new File(file))){
			JsonIterator.enableStreamingSupport();
			JsonIterator itr = JsonIterator.parse(fis, 2048);
			Any any = itr.readAny();
		}
	}
}
