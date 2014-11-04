package ut.beacondisseminationapp.protocol;

import java.util.TimerTask;

public class SendSubscription extends TimerTask {

	@Override
	public void run() {
		//System.out.println("Preparing subscription!");
		Driver.subscribe();
	}
	
}
