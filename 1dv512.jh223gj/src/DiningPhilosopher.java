import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiningPhilosopher {
	
	// Philosopher & Chopstick ids are the position in the list.
	private ArrayList<Philosopher> philosophers;
	private ArrayList<Chopstick> chopsticks;
	private int simulationTime;			// in ms
	ExecutorService exec;
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
		Random rand = new Random();
		for (int i=0;i<5;i++){
			philosophers.add(new Philosopher(i+1));
			philosophers.get(i).setSeed(rand.nextLong());
			chopsticks.add(new Chopstick(i+1));
		}
		// initialize threads
		exec = Executors.newFixedThreadPool(5);
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
		for (int i = 0; i<5; i++){
			exec.submit(new RunnablePhilosopher(philosophers.get(i)) {
				public void run(){
					// my code (process each thread - think, eat ..)
					//while ((System.currentTimeMillis()-Philosopher.startTime) <= simulationTime) {			// loop until termination 
					while (true){
					process(philosopher);	// process philosopher i
					}
				}
			});
		}
		exec.shutdown(); //stop accepting new tasks

		try {
			Thread.sleep(10000); 		// sleep 10 secs to write to file
			// waits simulationTime to finish the tasks
			exec.awaitTermination(simulationTime, TimeUnit.MILLISECONDS); 
			// search for deadlocks
			detectDeadLock();
		} catch (InterruptedException e) {
			// e.printStackTrace(); sleep threads will be interrupted
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
			synchronized (aquireChopstick(philosopher.getId()-1)){	// -1 for correct position
				
				synchronized (aquireChopstick(philosopher.getId())){
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
	
	private void detectDeadLock(){		
		long[] deadThreads = threadTool.findDeadlockedThreads();
		
		if (deadThreads != null){
			// DEADLOCK
			try {
				Philosopher.log.write("DEADLOCK after" + (System.currentTimeMillis()-Philosopher.startTime) + "ms");
				System.out.println("DEADLOCK after" + (System.currentTimeMillis()-Philosopher.startTime) + "ms");
				for (int i=0;i<deadThreads.length;i++){
					System.out.println("DeadThread id's: " + deadThreads[i]);
					Philosopher.log.write("DeadThread id's: " + deadThreads[i]);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			// shutdown all threads
			exec.shutdownNow();
			
		}
	}

}
