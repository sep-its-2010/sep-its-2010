package sep.conquest.model.behaviour;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import sep.conquest.model.GraphNode;
import sep.conquest.model.GridMap;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.State;
import sep.conquest.util.Utility;

/**
 * InnerBehaviour represents a behaviour to identify inner areas of frontier
 * nodes within a map. It extends the Behaviour class for enabling a behaviour-
 * chain.
 * 
 * @author Andreas Wilhelm
 */
public final class InnerBehaviour extends Behaviour {

    /**
     * The constructor enables chain-handling by calling the constructor of
     * the super-class (Behaviour).
     * 
     * @param stateLevel The level of the state.
     * @param next A reference to the next behaviour.
     */
    protected InnerBehaviour(State stateLevel, IBehaviour next) {
        super(stateLevel, next);
    }

    /* (non-Javadoc)
     * @see sep.conquest.model.IBehaviour#execute(java.util.Map)
     */
    public boolean execute(Map<Integer, Integer> map, Puck robot) {
    	
    	boolean ret = super.execute(map, robot);
    	
    	LinkedList<GraphNode> frontiers = robot.getMap().getFrontierList();
    	Map<Integer, Set<Orientation>> innerNodes = new ConcurrentHashMap<Integer, Set<Orientation>>();
    	GridMap gridMap = robot.getMap();
    	
    	for (GraphNode frontier: frontiers) {
    		Set<Orientation> orientations = new TreeSet<Orientation>();
    		innerNodes.put(Utility.makeKey(frontier.getXValue(), frontier.getYValue()), orientations);
    	}
    	
    	Set<Integer> keys = innerNodes.keySet();    	
    	for (Integer key: keys) {
    		
    		int[] intKey = Utility.extractCoordinates(key);
    		// expand right neighbour-nodes
    		if (gridMap.getNode(intKey[0] - 1, intKey[1]) != null) {
    			innerNodes.get(key).add(Orientation.LEFT);
    			for (int i = intKey[0] + 1; i <= gridMap.getMapBorders()[2]; i++) {
    				GraphNode current = gridMap.getNode(i, intKey[1]);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE)
    						innerNodes.get(Utility.makeKey(current.getXValue(), current.getYValue())).add(Orientation.LEFT);    						
    					else
    						break;
    				}
    			}
    		} 
    		
    		// expand left neighbour-nodes
    		if (gridMap.getNode(intKey[0] + 1, intKey[1]) != null) {
    			innerNodes.get(key).add(Orientation.RIGHT);
    			for (int i = intKey[0] - 1; i >= gridMap.getMapBorders()[0]; i--) {
    				GraphNode current = gridMap.getNode(i, intKey[1]);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						innerNodes.get(Utility.makeKey(current.getXValue(), current.getYValue())).add(Orientation.RIGHT);    		 						
    					} else
    						break;
    				}
    			}
    		}     		

    		// expand upper neighbour-nodes
    		if (gridMap.getNode(intKey[0], intKey[1] + 1) != null) {
    			innerNodes.get(key).add(Orientation.UP);
    			for (int i = intKey[1] + 1; i <= gridMap.getMapBorders()[3]; i++) {
    				GraphNode current = gridMap.getNode(intKey[0], i);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						innerNodes.get(Utility.makeKey(current.getXValue(), current.getYValue())).add(Orientation.UP);    		   						
    					} else
    						break;
    				}
    			}
    		}  
    		
    		// expand lower neighbour-nodes
    		if (gridMap.getNode(intKey[0], intKey[1] - 1) != null) {
    			innerNodes.get(key).add(Orientation.DOWN);
    			for (int i = intKey[1] - 1; i >= gridMap.getMapBorders()[1]; i--) {
    				GraphNode current = gridMap.getNode(intKey[0], i);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						innerNodes.get(Utility.makeKey(current.getXValue(), current.getYValue())).add(Orientation.DOWN);    		   						
    					} else
    						break;
    				}
    			}
    		}    		
    	} // for all keys
    	
    	removeRealFrontiers(innerNodes);
    	
    	// add 10 to the navigation cost for inner nodes
    	for (Integer pos: innerNodes.keySet()) {
    		if (map.containsKey(pos)) {
    			map.put(pos, map.get(pos) + 10);
    			ret = true;
    		}	
    	}
        return ret;
    }
    
    /**
     * Removes frontier nodes from the map which are not bound by already
     * known areas.
     * 
     * @param innerNodes A map of inner nodes.
     */
    private void removeRealFrontiers(Map<Integer, Set<Orientation>> innerNodes) {
    	Set<Integer> keys = innerNodes.keySet();
    	for (Integer key: keys) {
    		if (innerNodes.containsKey(key))
    			removeNode(innerNodes, key);
    	}
    }
    
    /**
     * Removes a inner node recursively among every neighbour-frontier node.
     * 
     * @param innerNodes A map of inner nodes.
     * @param node The node that shall be removed.
     */
    private void removeNode(Map<Integer, Set<Orientation>> innerNodes, Integer node) {
    	if (innerNodes.containsKey(node)) {
			if (!(	innerNodes.get(node).contains(Orientation.LEFT) &&
					innerNodes.get(node).contains(Orientation.RIGHT) &&
					innerNodes.get(node).contains(Orientation.UP) &&
					innerNodes.get(node).contains(Orientation.DOWN)	)) {
				innerNodes.remove(node);
				int intKey[] = Utility.extractCoordinates(node);
				removeNode(innerNodes, Utility.makeKey(intKey[0]-1, intKey[1]));
				removeNode(innerNodes, Utility.makeKey(intKey[0]+1, intKey[1]));
				removeNode(innerNodes, Utility.makeKey(intKey[0], intKey[1]+1));
				removeNode(innerNodes, Utility.makeKey(intKey[0], intKey[1]-1));								
			}
		}
    }
}
