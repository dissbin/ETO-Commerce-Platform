package com.ETO.content.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BtraceTest {
	public int add(int a,int b) {
		return a+b;
	}
	
	public static void main(String[] args) throws IOException {
		BtraceTest test = new BtraceTest();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		for(int i=0;i<10;i++) {
			reader.readLine();
			int a = (int) (Math.random() * 1000);
			int b = (int) (Math.random() * 1000);
			System.out.println(test.add(a, b));
			
		}

	}

}
