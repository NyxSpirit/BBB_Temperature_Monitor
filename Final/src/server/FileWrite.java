package server;

import java.util.Queue;

public class FileWrite implements Runnable {

	private final Queue<String> queue;
	
	private boolean stop;
	
	public FileWrite(Queue<String> queue){
		this.queue = queue;
		this.stop = false;
	}
	
	@Override
	public void run() {
		while (!stop) {
			try {
				if (queue.size() == 10) {
					System.out.println(queue.size());
					writeToFile();
				}
				synchronized (queue) {
	                queue.wait();
	            }
			} catch (InterruptedException e1) {
				
				/*
				 * Ensure everything is closed and good
				 */
				System.out.println("hi");
			}
		}
		// Do final write of queue contents
		if (!queue.isEmpty()) {
			System.out.println(queue.size());
			writeToFile();
		}
		System.out.println("Writer stopped");
	}
	
	private void writeToFile() {
		/*
		 * Write to the file and remove data from queue
		 */
		System.out.println("Wrote Queue to file");
		queue.clear();
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
