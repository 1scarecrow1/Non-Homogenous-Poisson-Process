package Assignment2;

import general.Counter;
import general.SystemState;
import general.annotations.AutoCounter;
import general.annotations.AutoMeasure;
import general.annotations.Initialize;

/**
 * Simulates arrival of visitors according to 2 independent Poisson processes and scanning of tickets
 * @author M-P
 *
 */

public class ConcertState extends SystemState<ConcertState>
{
	// Parameters
	private final int standingCapacity;
	private final int seatingCapacity;
	private final double mu;
	private final int lambdaStanding;
	private final int lambdaSeated;
	private final Random random;

	// State variables
	private int nQueue;
	private int nBusy;
	private int nStanding;
	private int nSeated;

	// Counter variables
	@AutoCounter("Number of late visitors")
	private Counter nLate;

	public ConcertState(int nScanners, int standingCapacity, int seatingCapacity, double mu,
						int lambdaStanding, int lambdaSeated, double timeHorizon, long seed) {
		super(timeHorizon, seed);
		this.nScanners = nScanners;
		this.standingCapacity = standingCapacity;
		this.seatingCapacity = seatingCapacity;
		this.mu = mu;
		this.lambdaStanding = lambdaStanding;
		this.lambdaSeated = lambdaSeated;
		random = new Random(seed);
		reset();
	}

	@Initialize
	public void initReplication() {
		double nextStandingArrivalTime = UtilsConcert.nextInterArrivalTime(this.random, this.lambdaStanding);
		addEvent(nextStandingArrivalTime, this::dostandinggroupArrival);
		double nextSeatedArrivalTime = UtilsConcert.nextInterArrivalTime(this.random, this.lambdaSeated);
		addEvent(nextSeatedArrivalTime, this::doseatedgroupArrival);
	}

	public void dostandinggroupArrival(double eventTime) {
		Random rand = new Random();
		float unifRand = rand.nextFloat();
		if (unifRand > 0.0 && unifRand <= 0.05) {
			nStanding++;
		}
		elseif (unifRand > 0.05 && unifRand <= 0.45){
			nStanding+=2;
		}
		elseif (unifRand > 0.45 && unifRand <= 0.7){
			nStanding+=3;
		}
		elseif(unifRand > 0.7 && unifRand <= 1) {
			nStanding += 4;
		}

		nQueue+=nStanding-(nScanners - nBusy);
		double currentTime = eventTime;
		double serviceDuration = UtilsConcert.nextServiceTime(random, mu);
		double departureTime = currentTime + nQueue*serviceDuration;
		addEvent(departureTime, this::scanTicket);

		}

	public void doseatedgroupArrival(double eventTime) {
		Random rand = new Random();
		float unifRand = rand.nextFloat();
		if (unifRand > 0.0 && unifRand <= 0.05) {
			nSeated++;
		}
		elseif (unifRand > 0.05 && unifRand <= 0.45){
			nSeated+=2;
		}
		elseif (unifRand > 0.45 && unifRand <= 0.7){
			nSeated+=3;
		}
		elseif(unifRand > 0.7 && unifRand <= 1) {
			nSeated += 4;
		}

		nQueue += nSeated - (nScanners - nBusy);
		double currentTime = eventTime;
		double serviceDuration = UtilsConcert.nextServiceTime(random, mu);
		double departureTime = currentTime + nQueue * serviceDuration;
		addEvent(departureTime, this::scanTicket);

	}


	public void scanTicket(double eventTime) {
		if (nQueue > 0) {
			nQueue--;
		}
		double scanTicketTime = eventTime;
		addEvent(eventTime, this::scanTicket);
		if (eventTime > 1) {
			nLate.increment();
		}
	}

		@AutoMeasure("Number of late visitors")
		public int getNumberLateVisitors() {
			return nLate.getValue();
		}

		@Override
	public void reset() {
			nBusy = 0;
			nStanding = standingCapacity;
			nSeated = seatingCapacity;
			nQueue = 0;
	}
}
