import java.io.IOException;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.List;

public class DeadlockConsoleHandler implements IDeadlockHandler {
	
	List<IDeadlockObserver> observers = new ArrayList<IDeadlockObserver>();
	
	 @Override
	  public void handleDeadlock(final ThreadInfo[] deadlockedThreads) {
		// DEADLOCK
		 System.err.println("DEADLOCK DETECTED");
		try {
			Philosopher.log.write("DEADLOCK after" + (System.currentTimeMillis()-Philosopher.startTime) + "ms");
			System.out.println("DEADLOCK after" + (System.currentTimeMillis()-Philosopher.startTime) + "ms");
			for (int i=0;i<deadlockedThreads.length;i++){
				System.out.println("DeadThread id's: " + deadlockedThreads[i]);
				Philosopher.log.write("DeadThread id's: " + deadlockedThreads[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		notifyAllObservers();
	 }

	@Override
	public void addSubscriber(IDeadlockObserver observer) {
		observers.add(observer);
	}

	@Override
	public void notifyAllObservers() {
		for (IDeadlockObserver o : observers){
			o.deadlockOccoured();
		}
	}

}
