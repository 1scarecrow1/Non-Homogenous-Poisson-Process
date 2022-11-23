package Assignment2;

import java.io.IOException;
import java.util.Random;

import general.Counter;
import general.SystemState;
import general.annotations.AutoCounter;
import general.annotations.AutoMeasure;
import general.annotations.Initialize;
import general.annotations.StopCriterium;

/**
 * @author M-P
 * Models nonhomogenous Poisson arrival process
 *
 */

public class ConcertStateNH extends SystemState<ConcertStateNH>
{
	// Parameters
	private final int standingCapacity;
	private final int seatingCapacity;
	private final double mu;
	private final double lambdaStanding;
	private final double lambdaSeated;
	private final Random random;
	private final int nScanners;
	private final double time;

	// State variables
	private int nQueue;
	private int nBusy;
	private int nStanding;
	private int nSeated;

	// Counter variables
	@AutoCounter("Number of late visitors")
	private Counter nLate;

	public ConcertStateNH(int nScanners, int standingCapacity, int seatingCapacity, double mu,
						  double lambdaStanding, double lambdaSeated, double time, double timeHorizon, long seed) {
		super(timeHorizon, seed);
		this.nScanners = nScanners;
		this.standingCapacity = standingCapacity;
		this.seatingCapacity = seatingCapacity;
		this.mu = mu;
		this.time = time;
		this.lambdaStanding = getLambdaStanding(time);
		this.lambdaSeated = getLambdaSeated(time);
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

	public double getLambdaSeated(double eventTime){
		Random rand = new Random();
		double time = rand.nextDouble(1);
		time = eventTime;
        lambdaSeated = Math.max(2530*(-5*Math.pow(eventTime,2)+14.5*eventTime - 8.5), 10);
        return lambdaSeated;
	}

	public double getLambdaStanding(double eventTime){
		Random rand = new Random();
		double time = rand.nextDouble(2); //standing visitors start arriving an hour before seated
		time = eventTime;
		lambdaStanding = 1130 - 565*Math.sin(1.5 - 2*eventTime);
		return lambdaStanding;
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

    @StopCriterium
     public boolean venueFilled(){
		return nStanding == standingCapacity && nSeated == seatingCapacity;
     }

	@Override
	public void reset() {

	}
}
