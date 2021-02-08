package com.heasy.knowroute.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.heasy.knowroute.bean.PositionBean;
import com.heasy.knowroute.utils.JsonUtil;

public class HeasyQueue {
	private ExecutorService executorService;
	private Lock lock = new ReentrantLock();
	private LinkedList<PositionBean> queue = new LinkedList<>();
	private int seq = 1;
	
//	public static void main(String[] args) {
//		HeasyQueue heasyQueue = new HeasyQueue();
//		heasyQueue.start();
//	}
	
	public void start() {
		executorService = Executors.newCachedThreadPool();
		executorService.execute(new Receiver());
		executorService.execute(new Sender());
	}
	
	class Sender extends Thread{
		public Sender() {
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while(true) {
				lock.lock();
				try {
					PositionBean bean = new PositionBean();
					bean.setLongitude(1.0);
					bean.setLatitude(2.0);
					bean.setAddress(String.valueOf(seq++));
					
					queue.addLast(bean);
					System.out.println("sender: " + queue.size() + " " + JsonUtil.object2ArrayString(queue));
					
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
				
				try {
					TimeUnit.SECONDS.sleep(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	class Receiver extends Thread{
		public Receiver() {
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while(true) {
				int toIndex = 0;

				lock.lock();
				try {
				if(queue.size() >=5) {
					toIndex = 5;
				}else {
					toIndex = queue.size();
				}
				System.out.println("toIndex=" + toIndex);
				
				List<PositionBean> dataList = queue.subList(0, toIndex);
				System.out.println("receiver: " + JsonUtil.object2ArrayString(dataList));
				} finally {
					lock.unlock();
				}
				
				lock.lock();
				try {
					queue.subList(0, toIndex).clear();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
				
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
