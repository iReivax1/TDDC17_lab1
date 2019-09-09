package tddc17;


import aima.core.environment.liuvacuum.*;
import aima.core.agent.Action;
import aima.core.agent.AgentProgram;
import aima.core.agent.Percept;
import aima.core.agent.impl.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

class MyAgentState
{
	public int[][] world = new int[30][30];
	public int initialized = 0;
	final int UNKNOWN 	= 0;
	final int WALL 		= 1;
	final int CLEAR 	= 2;
	final int DIRT		= 3;
	final int HOME		= 4;
	final int ACTION_NONE 			= 0;
	final int ACTION_MOVE_FORWARD 	= 1;
	final int ACTION_TURN_RIGHT 	= 2;
	final int ACTION_TURN_LEFT 		= 3;
	final int ACTION_SUCK	 		= 4;
	
	public int agent_x_position = 1;
	public int agent_y_position = 1;
	public int agent_last_action = ACTION_NONE;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	public int agent_direction = EAST;
	
	MyAgentState()
	{
		for (int i=0; i < world.length; i++)
			for (int j=0; j < world[i].length ; j++)
				world[i][j] = UNKNOWN;
		world[1][1] = HOME;
		agent_last_action = ACTION_NONE;
	}
	// Based on the last action and the received percept updates the x & y agent position
	public void updatePosition(DynamicPercept p)
	{
		Boolean bump = (Boolean)p.getAttribute("bump");

		if (agent_last_action==ACTION_MOVE_FORWARD && !bump)
	    {
			switch (agent_direction) {
			case MyAgentState.NORTH:
				agent_y_position--;
				break;
			case MyAgentState.EAST:
				agent_x_position++;
				break;
			case MyAgentState.SOUTH:
				agent_y_position++;
				break;
			case MyAgentState.WEST:
				agent_x_position--;
				break;
			}
	    }
		
	}
	
	public void updateWorld(int x_position, int y_position, int info)
	{
		world[x_position][y_position] = info;
	}
	
	public void printWorldDebug()
	{
		for (int i=0; i < world.length; i++)
		{
			for (int j=0; j < world[i].length ; j++)
			{
				if (world[j][i]==UNKNOWN)
					System.out.print(" ? ");
				if (world[j][i]==WALL)
					System.out.print(" # ");
				if (world[j][i]==CLEAR)
					System.out.print(" . ");
				if (world[j][i]==DIRT)
					System.out.print(" D ");
				if (world[j][i]==HOME)
					System.out.print(" H ");
			}
			System.out.println("");
		}
	}
}

class MyAgentProgram implements AgentProgram {

	private int initnialRandomActions = 20;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 20;
	public MyAgentState state = new MyAgentState();
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	private Action moveToRandomStartPosition(DynamicPercept percept) {
		int action = random_generator.nextInt(6);
		initnialRandomActions--;
		state.updatePosition(percept);
		if(action==0) {
		    state.agent_direction = ((state.agent_direction-1) % 4);
		    if (state.agent_direction<0) 
		    	state.agent_direction +=4;
		    state.agent_last_action = state.ACTION_TURN_LEFT;
			return LIUVacuumEnvironment.ACTION_TURN_LEFT;
		} else if (action==1) {
			state.agent_direction = ((state.agent_direction+1) % 4);
		    state.agent_last_action = state.ACTION_TURN_RIGHT;
		    return LIUVacuumEnvironment.ACTION_TURN_RIGHT;
		} 
		state.agent_last_action=state.ACTION_MOVE_FORWARD;
		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	}
	
	//The agent is here
	@Override
	public Action execute(Percept percept) {
		
		// DO NOT REMOVE this if condition!!!
    	if (initnialRandomActions>0) {
    		return moveToRandomStartPosition((DynamicPercept) percept);
    	} else if (initnialRandomActions==0) {
    		// process percept for the last step of the initial random actions
    		initnialRandomActions--;
    		state.updatePosition((DynamicPercept) percept);
			System.out.println("Processing percepts after the last execution of moveToRandomStartPosition()");
			state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
    	}
		
    	// This example agent program will update the internal agent state while only moving forward.
    	// START HERE - code below should be modified!
    	    	
    	System.out.println("x=" + state.agent_x_position);
    	System.out.println("y=" + state.agent_y_position);
    	System.out.println("dir=" + state.agent_direction);
    	
		
	    iterationCounter--;
	    
	    if (iterationCounter==0)
	    	return NoOpAction.NO_OP;

	    DynamicPercept p = (DynamicPercept) percept;
	    Boolean bump = (Boolean)p.getAttribute("bump");
	    Boolean dirt = (Boolean)p.getAttribute("dirt");
	    Boolean home = (Boolean)p.getAttribute("home");
	    System.out.println("percept: " + p);
	    
	    // State update based on the percept value and the last action
	    state.updatePosition((DynamicPercept)percept);
	    if (bump) {
			switch (state.agent_direction) {
			case MyAgentState.NORTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position-1,state.WALL);
				break;
			case MyAgentState.EAST:
				state.updateWorld(state.agent_x_position+1,state.agent_y_position,state.WALL);
				break;
			case MyAgentState.SOUTH:
				state.updateWorld(state.agent_x_position,state.agent_y_position+1,state.WALL);
				break;
			case MyAgentState.WEST:
				state.updateWorld(state.agent_x_position-1,state.agent_y_position,state.WALL);
				break;
			}
	    }
	    if (dirt)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.DIRT);
	    else
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.CLEAR);
	    
	    state.printWorldDebug();
	    
	    
	    // Next action selection based on the percept value
	    if (dirt)
	    {
	    	System.out.println("DIRT -> choosing SUCK action!");
	    	state.agent_last_action=state.ACTION_SUCK;
	    	return LIUVacuumEnvironment.ACTION_SUCK;
	    } 
	    else
	    {
	    	if (bump)
	    	{
	    		state.agent_last_action=state.ACTION_NONE;
		    	return NoOpAction.NO_OP;
	    	}
	    	else
	    	{
	    		state.agent_last_action=state.ACTION_MOVE_FORWARD;
	    		return LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
	    	}
	    }
	}
}


//The node class for creation of graph, use graph traversal for exploration
//Using BFS
class Node {
	
    int x , y; //for the coordinates of the nodes
    int direction;
    boolean visit;
    Action action;
 
    public Node(int x, int y, int direction, Action action)
    {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.action = action;
        this.visit = false; //node is not visited
    }
}
// all other nodes reachable from current position
class ChildNode{
	public Node front;
	public Node left;
	public Node right;
	public Node back;
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram() {
    		
    		public ChildNode getChildNode(Node node) {
    			int x = node.x;
    			int y = node.y;
    			int size = 15; // size of the map 15x15 is expected for demo
    			int direction = node.direction;
    			Action action = node.action;
    			ChildNode child = new ChildNode();
    			
    			// move front
    			int dx = 0; //change in the x axis, no change by default
    			int dy = 0; //change in the y axis
    			if(MyAgentState.EAST == direction) {
    				dx = 1;
    			}
    			else if (MyAgentState.WEST == direction) {
    				dx = -1;
    			}
    			
    			if (MyAgentState.SOUTH == direction) {
    				dy = 1;
    			}
    			else if(MyAgentState.NORTH == direction) {
    				dy = -1;
    			}
    			int newX = x + dx; // new X position
    			int newY = y + dy; // new Y position
    			
    			//sanity check for moving forwards,  1<=x<=15 , 1<=y<=15 
    			if(newX >= 1 && newX <= size && newY >= 1 && newY <= size) {
    				if(state.world[newX][newY] != 1) {// i.e. the new X and Y coordinates are not the edge walls
    					child.front = new Node(newX, newY, direction, action); // found a new tile for vacuum to move. 
    				}	
    			}
    			//move left
    			child.left = new Node (x,y,( (direction - 1) % 4 ), action); //use % 4 to makes sure that the direction value is within 0 and 3. - 1 to turn 90 degrees anti-clockwise aka turn left
    			//move right
    			child.right = new Node (x,y,( (direction + 1) % 4 ), action); //use % 4 to makes sure that the direction value is within 0 and 3. + 1 to turn 90 degrees clockwise aka turn right 			
    			//move back
    			child.back = new Node (x,y,( (direction + 2) % 4 ), action);
       			return child;
    		}
    		public Action BFS(int x, int y, int direction, int target) {

    			//Mark all the vertices as not visited (false)
    			boolean visited[][] = new boolean[30][30]; 
//    			TO-DO
				//////////////////////////
    			//how to mark current node?
    			
    			//Create a queue for BFS
    			LinkedList<Node> queue = new LinkedList<Node>();
    			
    			//dynamically add the child, to create the graph to traverse
    			Node tempNode = new Node(x, y, direction, NoOpAction.NO_OP);
    			ChildNode CNode = getChildNode(tempNode);
    			
    			//front of child node not empty
    			if (CNode.front != null) {
    				CNode.front.action = LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
    				if(CNode.visit == false)
    					queue.add(CNode); //add to the queue for exploration by BFS	if it is unvisited
        		}
    			//right of the child node is not empty
    			if (CNode.right != null) {
    				CNode.right.action = LIUVacuumEnvironment.ACTION_TURN_RIGHT;
    				if(CNode.visit == false)
    					queue.add(CNode); //add to the queue for exploration by BFS		
        		}
    			//left of the child node is not empty
    			if (CNode.left != null) {
    				CNode.left.action = LIUVacuumEnvironment.ACTION_TURN_LEFT;
    				if(CNode.visit == false)
    					queue.add(CNode);//add to the queue for exploration by BFS		
        		}
    			
    			//while queue is not empty
    			while (queue.size() != 0) {
    				
    				// Dequeue a vertex from queue
    				Node source = queue.poll();
    				//if target found, can return the action
    				if(state.world[source.x][source.y] == target) {
    					return source.action;
    				}
    				
    				//	TO-DO
    				//////////////////////////
    				//if havent found target, need to go to the next children Nodes to explore
    			
    			}
    		}	

    		//keep track of current status
    		public Action currentAction(Action a) {
    			if (a == LIUVacuumEnvironment.ACTION_MOVE_FORWARD)
    				state.agent_last_action = state.ACTION_MOVE_FORWARD;
    			else if (a == LIUVacuumEnvironment.ACTION_TURN_RIGHT)
    				state.agent_last_action = state.ACTION_TURN_RIGHT;
    			else if (a == LIUVacuumEnvironment.ACTION_TURN_LEFT)
    				state.agent_last_action = state.ACTION_TURN_LEFT;
    			else if (a ==LIUVacuumEnvironment.ACTION_SUCK)
    				state.agent_last_action = state.ACTION_SUCK; 			
    			return a;
    		}
    		
    		
    	});
	}
}
