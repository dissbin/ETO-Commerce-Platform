package com.ETO.sellergoods.service.impl;

import com.sun.source.tree.SynchronizedTree;

public class SynTest implements Runnable{
	int a,b;
	public  SynTest(int a,int b) {
		this.a = a;
		this.b = b;
	}
	@Override
	public void run() {
		synchronized (Integer.valueOf(a)) {
			synchronized (Integer.valueOf(b)) {
				System.out.println(a + b);
			}
		}
		
	}
	public static void main(String[] args) {
		for(int i=0;i<100;i++) {
			new Thread(new SynTest(1, 2),"Thread-dzb1").start();
			new Thread(new SynTest(2, 3),"Thread-dzb2").start();
		}
		
	}
}
