package sep.conquest.model.behaviour;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import sep.conquest.model.AStarPathFinder;
import sep.conquest.model.GraphNode;
import sep.conquest.model.GridMap;
import sep.conquest.model.NodeType;
import sep.conquest.model.Orientation;
import sep.conquest.model.Puck;
import sep.conquest.model.RobotStatus;
import sep.conquest.model.State;

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
    public Map<int[], Integer> execute(Map<int[], Integer> map, Puck robot) {
    	
    	LinkedList<GraphNode> frontiers = robot.getMap().getFrontierList();
    	Map<int[], Set<Orientation>> innerNodes = new TreeMap<int[], Set<Orientation>>();
    	GridMap gridMap = robot.getMap();
    	
    	for (GraphNode frontier: frontiers) {
    		int[] pos = new int[2];
    		pos[0] = frontier.getXValue();
    		pos[1] = frontier.getYValue();
    		Set<Orientation> orientations = new TreeSet<Orientation>();
    		innerNodes.put(pos, orientations);
    	}
    	
    	Set<int[]> keys = innerNodes.keySet();    	
    	for (int[] key: keys) {
    		
    		// expand right neighbour-nodes
    		if (gridMap.getNode(key[0] - 1, key[1]) != null) {
    			innerNodes.get(key).add(Orientation.LEFT);
    			for (int i = key[0] + 1; i <= gridMap.getMapBorders()[2]; i++) {
    				GraphNode current = gridMap.getNode(i, key[1]);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						int[] pos = new int[2];
    						pos[0] = current.getXValue();
    						pos[1] = current.getYValue();
    						innerNodes.get(pos).add(Orientation.LEFT);    						
    					} else
    						break;
    				}
    			}
    		} 
    		
    		// expand left neighbour-nodes
    		if (gridMap.getNode(key[0] + 1, key[1]) != null) {
    			innerNodes.get(key).add(Orientation.RIGHT);
    			for (int i = key[0] - 1; i >= gridMap.getMapBorders()[0]; i--) {
    				GraphNode current = gridMap.getNode(i, key[1]);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						int[] pos = new int[2];
    						pos[0] = current.getXValue();
    						pos[1] = current.getYValue();
    						innerNodes.get(pos).add(Orientation.RIGHT);    						
    					} else
    						break;
    				}
    			}
    		}     		

    		// expand upper neighbour-nodes
    		if (gridMap.getNode(key[0], key[1] + 1) != null) {
    			innerNodes.get(key).add(Orientation.UP);
    			for (int i = key[1] + 1; i <= gridMap.getMapBorders()[3]; i++) {
    				GraphNode current = gridMap.getNode(key[0], i);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						int[] pos = new int[2];
    						pos[0] = current.getXValue();
    						pos[1] = current.getYValue();
    						innerNodes.get(pos).add(Orientation.UP);    						
    					} else
    						break;
    				}
    			}
    		}  
    		
    		// expand lower neighbour-nodes
    		if (gridMap.getNode(key[0], key[1] - 1) != null) {
    			innerNodes.get(key).add(Orientation.DOWN);
    			for (int i = key[1] - 1; i >= gridMap.getMapBorders()[1]; i--) {
    				GraphNode current = gridMap.getNode(key[0], i);
    				if (current != null) {
    					if (current.getNodeType() == NodeType.FRONTIERNODE) {
    						int[] pos = new int[2];
    						pos[0] = current.getXValue();
    						pos[1] = current.getYValue();
    						innerNodes.get(pos).add(Orientation.DOWN);    						
    					} else
    						break;
    				}
    			}
    		}    		
    	} // for all keys
    	
    	removeRealFrontiers(innerNodes);
    	
    	// add 10 to the navigation cost for inner nodes
    	for (int[] pos: innerNodes.keySet()) {
    		Integer node = map.get(pos);
    		node += 10;
    	}
    	
        return super.execute(map, robot);
    }
    
    /**
     * Removes frontier nodes from the map which are not bound by already
     * known areas.
     * 
     * @param innerNodes A map of inner nodes.
     */
    private void removeRealFrontiers(Map<int[], Set<Orientation>> innerNodes) {
    	Set<int[]> keys = innerNodes.keySet();
    	for (int[] key: keys) {
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
    private void removeNode(Map<int[], Set<Orientation>> innerNodes, int[] node) {
    	if (innerNodes.containsKey(node)) {
			if (!(	innerNodes.get(node).contains(Orientation.LEFT) &&
					innerNodes.get(node).contains(Orientation.RIGHT) &&
					innerNodes.get(node).contains(Orientation.UP) &&
					innerNodes.get(node).contains(Orientation.DOWN)	)) {
				innerNodes.remove(node);
				int left[] = {node[0]-1, node[1]};
				removeNode(innerNodes, left);
				int right[] = {node[0]+1, node[1]};
				removeNode(innerNodes, right);
				int up[] = {node[0], node[1]+1};
				removeNode(innerNodes, up);
				int down[] = {node[0], node[1]-1};
				removeNode(innerNodes, down);				
			}
		}
    }
}
