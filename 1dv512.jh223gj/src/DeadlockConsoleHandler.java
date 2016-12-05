import java.io.IOException;
import java.lang.management.ThreadInfo;

public class DeadlockConsoleHandler implements IDeadlockHandler {
	
	 @Override
	  public void handleDeadlock(final ThreadInfo[] deadlockedThreads) {
		// DEADLOCK
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
	 }

}
