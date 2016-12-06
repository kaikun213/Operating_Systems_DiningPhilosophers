import java.lang.management.ThreadInfo;

public interface IDeadlockHandler {
	  void handleDeadlock(final ThreadInfo[] deadlockedThreads);
	  void addSubscriber(IDeadlockObserver observer);
	  void notifyAllObservers();
}
