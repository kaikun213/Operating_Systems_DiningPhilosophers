import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiningPhilosopher implements IDeadlockObserver {
	
	// Philosopher & Chopstick ids are the position in the list.
	private ArrayList<Philosopher> philosophers;
	private ArrayList<Chopstick> chopsticks;
	private int simulationTime;			// in ms
	ExecutorService exec;
	DeadlockDetector deadlockDetector;
	ThreadMXBean threadTool;

	
	public DiningPhilosopher(){
		philosophers = new ArrayList<Philosopher>();
		chopsticks = new ArrayList<Chopstick>();
		threadTool = ManagementFactory.getThreadMXBean();
		simulationTime = 10000;			// default value
	}
	
	public ArrayList<Philosopher> getPhilosophers(){
		return philosophers;
	}
	
	public void setSimulationTime(int millis){
		simulationTime = millis;
	}
	
	public void initialize(){
		// initialize philosophers with random seeds and chopsticks
		Random rand = new Random(System.currentTimeMillis());
		for (int i=0;i<5;i++){
			philosophers.add(new Philosopher(i+1));
			philosophers.get(i).setSeed(rand.nextLong());
			chopsticks.add(new Chopstick(i+1));
		}
		// initialize threads
		exec = Executors.newFixedThreadPool(5);
		//initialize deadlockhandler
		IDeadlockHandler deadlockHandler = new DeadlockConsoleHandler();
		deadlockHandler.addSubscriber(this);
		deadlockDetector = new DeadlockDetector(deadlockHandler, 10, TimeUnit.MILLISECONDS);
		// initialize logfile
		try {
			// clear content for new logs
			PrintWriter writer = new PrintWriter(Philosopher.logFile);
			writer.print("");
			writer.close();
			// initialize logWriter
			Philosopher.log = new FileWriter(Philosopher.logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// initialize time for log file 
		Philosopher.startTime = System.currentTimeMillis();
	}
	
	public void start(){
		// start philosophers 
		for (int i = 0; i<5; i++){
			exec.submit(new RunnablePhilosopher(philosophers.get(i)) {
				public void run(){
					// my code (process each thread - think, eat ..)
					while ((System.currentTimeMillis()-Philosopher.startTime) <= simulationTime) {			// loop until termination 
					//while (true){
					process(philosopher);	// process philosopher i
					}
				}
			});
		}
		// start deadlock-detection
		deadlockDetector.start();
		
		//stop accepting new tasks
		exec.shutdown(); 
		try {
			// waits simulationTime to finish the tasks
			exec.awaitTermination(simulationTime, TimeUnit.MILLISECONDS); 
			deadlockDetector.terminate(simulationTime + 1000);
		} catch (InterruptedException e) {
			e.printStackTrace(); //sleep threads will be interrupted
		}
	}
	
	@Override
	public void deadlockOccoured() {
		System.out.println("Deadlock occoured and shutdown-service in DiningPhilosopher-class is called\n");
		// 	Shutdown philosopher threads now
		/* 	There are no guarantees beyond best-effort attempts to stop processing actively executing tasks.
			For example, typical implementations will cancel via Thread.interrupt,
		 	so any task that fails to respond to interrupts may never terminate. */
		try {
			exec.shutdown();
	        if (!exec.awaitTermination(0, TimeUnit.MICROSECONDS)) { 
	            System.out.println("\nExecutor did not terminate in the specified time."); 
	            List<Runnable> droppedTasks = exec.shutdownNow(); 
	            System.out.println("Executor was abruptly shut down. " + droppedTasks.size() + " tasks will not be executed.\n");
	        }
			// shutdown deadlockDetector thread now
			deadlockDetector.terminate(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// If shutdown did not succeed properly -> exit JVM (to avoid endless running)
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		// terminate JVM because some threads can not be terminated.
		boolean terminate = false;
		if (threadSet.size() > 1) {
			for (Thread t : threadSet) {
				System.out.println(t.getName() + " is still alive: " + t.isAlive());
				// if the thread is still alive but not a daemon or the main-thread then we need to terminate the JVM.
				if (!t.isDaemon()) {
					if (!t.getName().equals("main")){
						terminate = true;
					}
				}
			}
		}
		
		if (terminate) {
			System.out.println("\nTerminate program abruptly. Deadlocked threads did not terminate normally.");
			System.exit(1);
		}

	}
	
	
	private void process(Philosopher philosopher){
		long hungryStartTime;
		long hungryEndTime;
		
			// think
			philosopher.think();
			// write log, set status hungry and increase hungry-turns
			philosopher.hungry();
			
			// calculate hungry time
			hungryStartTime = System.nanoTime();
			synchronized (aquireChopstick(philosopher.getId()-1)){	// -1 for correct position -> left chopstick first
				synchronized (aquireChopstick(philosopher.getId())){	// -> right chopstick
					hungryEndTime = System.nanoTime();
					// calculate nanoTime to millis	=> cut long to int (not 100% precise but ok since simulation time not larger then int)
					philosopher.increaseHungryTime((int)Math.round((hungryEndTime-hungryStartTime)/1000000));
					
					// eat and release chopsticks
					philosopher.eat();
				}
			}
	}
	
	private Chopstick aquireChopstick(int i){
		if (i>=chopsticks.size()) i = i%chopsticks.size();
		return (chopsticks.get(i));
	}

}
