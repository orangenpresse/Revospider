package revo.spider;

public class Watchdog implements Runnable {
	private Object object;
	private long time;
	private boolean active = true;
	private int counter;
	
	Watchdog(Object object, long time) {
		this.time = time;
		this.object = object;
	}

	public void run() {
		while(active) {
			try {
				Thread.sleep(time);
			} catch (InterruptedException e) {
				break;
			}

			counter++;
			synchronized(object) {
				object.notify();
			}
		}
	}

	public int getCount() {
		return this.counter;
	}
	
	public void resetCounter() {
		this.counter = 0;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
}
