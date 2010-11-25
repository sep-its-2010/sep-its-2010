package sep.conquest.model.behaviour;

import sep.conquest.model.State;

/**
 * The BehaviourFactory is responsible for the creation of behaviour-chains.
 * 
 * @author Andreas Wilhelm
 *
 */
public class BehaviourFactory {
	
	/**
	 * Build a behaviour-chain for a given state.
	 * 
	 * @param state The state of the robot.
	 * @return The behaviour-chain.
	 */
	public static IBehaviour createBehaviourChain(State state) {
		
		switch (state) {
		case IDLE:
			LocalLocalizeBehaviour loBeh = new LocalLocalizeBehaviour(state,
					null);
			return loBeh;
		case EXPLORE: 
			DistanceBehaviour distBeh = new DistanceBehaviour(state, null);
			InnerBehaviour inBeh = new InnerBehaviour(state, distBeh);
			CooperativeBehaviour coBeh = new CooperativeBehaviour(state, inBeh);
			return distBeh;
		default: 
			return null;
		}
		
	}

}
