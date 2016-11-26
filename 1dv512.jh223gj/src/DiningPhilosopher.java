import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DiningPhilosopher {
	
	// Philosopher & Chopstick ids are the position in the list.
	private ArrayList<Philosopher> philosophers;
	private ArrayList<Chopstick> chopsticks;
	private int simulationTime;			// in ms
	ExecutorService exec;
	
	public DiningPhilosopher(){
		philosophers = new ArrayList<Philosopher>();
		chopsticks = new ArrayList<Chopstick>();
		simulationTime = 10000;			// default value
	}
	
	public ArrayList<Philosopher> getPhilosophers(){
		return philosophers;
	}
	
	public void setSimulationTime(int millis){
		simulationTime = millis;
	}
	
	public void initialize(){
		// initialize philosophers and chopsticks
		for (int i=0;i<5;i++){
			philosophers.add(new Philosopher(i+1));
			chopsticks.add(new Chopstick(i+1));
		}
		// initialize threads
		exec = Executors.newFixedThreadPool(5);
		// initialize logfile
		try {
			Philosopher.log = new FileWriter(Philosopher.logFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start(){
		for (int i = 0; i<5; i++){
			exec.submit(new RunnablePhilosopher(philosophers.get(i)) {
				public void run(){
					// my code (process each thread - think, eat ..)
					process(philosopher);	// process philosopher i
				}
			});
		}
	}
	
	// needs to implement :
	// while simulation time is not over
	// 	loop over:
	// 		think 
	// 		try to eat otherwise hungry-method and increase hungry time for each waiting second 
	//		-> not sure how to implement with lock, synchronized, wait/notify ..
	// 	end loop
	private void process(Philosopher philosopher){
		long startTime;		// System time at start to calculate simulationTime
		long time = 0;		// measured time: CurrentTime - StartTime (has to be smaller than simulationTime)
		long hungryStartTime;
		long hungryEndTime;
		
		startTime = System.currentTimeMillis();
		while (time<simulationTime) {
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
			time = System.currentTimeMillis() - startTime;			// calculate simulation time
		} 
	}
	
	private Chopstick aquireChopstick(int i){
		if (i>=chopsticks.size()) i = i%chopsticks.size();
		return (chopsticks.get(i));
	}
	

}
