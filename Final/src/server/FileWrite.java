package server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
					writeToFile();
				}
				synchronized (queue) {
	                queue.wait();
	            }
			} catch (InterruptedException e1) {

			} catch (IOException e) {
				
			}
		}
		// Do final write of queue contents
		if (!queue.isEmpty()) {
			try {
				writeToFile();
			} catch (IOException e) {
				
			}
		}
		System.out.println("Writer stopped");
	}
	
	private void writeToFile() throws IOException {
		/*
		 * Write to the file and remove data from queue
		 */
		String currentDay = "";
		String currentMonth = "";
		String currentYear = "";
		
		DateFormat dateYear = new SimpleDateFormat("yyyy");
		DateFormat dateMonth = new SimpleDateFormat("MM");
		DateFormat dateDay = new SimpleDateFormat("dd");
		
		currentDay = dateDay.format(new Date());
		currentMonth = dateMonth.format(new Date());
		currentYear = dateYear.format(new Date());
		
		// FOR WINDOWS SYSTEMS
		//File fileDir = new File(currentYear + "\\" + currentMonth);
		//File fileDay = new File(currentYear + "\\" + currentMonth + "\\" + currentDay +".dat");
		
		// FOR LINUX
		File fileDir = new File(currentYear + "/" + currentMonth);
		File fileDay = new File(currentYear + "/" + currentMonth + "/" + currentDay +".dat");
				
		// Make the proper directories
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		
		FileWriter fw = new FileWriter(fileDay);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0; i < queue.size(); i++) {
			bw.write(queue.remove());
			bw.newLine();
			bw.flush();
		}
		queue.clear();
		bw.close();
		fw.close();
		System.out.println("Wrote data to file");
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
