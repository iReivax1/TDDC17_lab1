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
		else if (agent_last_action == ACTION_TURN_RIGHT) {
			agent_direction = (agent_direction + 1) % 4;
		}
		else if (agent_last_action == ACTION_TURN_LEFT) {
			agent_direction = (agent_direction - 1) % 4;
			if (agent_direction < 0) 
				agent_direction += 4;
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

	protected int initnialRandomActions = 10;
	private Random random_generator = new Random();
	
	// Here you can define your variables!
	public int iterationCounter = 10000;
	public MyAgentState state = new MyAgentState();
	
	// moves the Agent to a random start position
	// uses percepts to update the Agent position - only the position, other percepts are ignored
	// returns a random action
	protected Action moveToRandomStartPosition(DynamicPercept percept) {
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
		MyVacuumAgent agent = new MyVacuumAgent();
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
	    if(home)
	    	state.updateWorld(state.agent_x_position,state.agent_y_position,state.HOME);
	    else if(!home)
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
	
    int x;
    int y; //for the coordinates of the nodes
    int direction;
    Action action;
 
    public Node(int x, int y, int direction, Action action)
    {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.action = action;
    }
}
// all other neighbouring tiles/ nodes  from current position
class ChildNode{
	public Node front;
	public Node left;
	public Node right;
}

public class MyVacuumAgent extends AbstractAgent {
    public MyVacuumAgent() {
    	super(new MyAgentProgram() {
    		int goal = 0; // 0 = unknown, 1 = wall , 2 = clear , 3 = dirt, 4 = home
    		
    		//get the current's node child's function
    		public ChildNode getChildNode(Node node) {
    			int x = node.x;
    			int y = node.y;
    			int size = 15; // size of the map 15x15 is expected for demo
    			int direction = node.direction;
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
    		
				if(state.world[newX][newY] != 1) {// i.e. the new X and Y coordinates are not the edge walls
					child.front = new Node(newX, newY, direction, node.action); // found a new tile for vacuum to move. 
				}	
    			
    			
    			//move right
    			child.right = new Node (x,y,( (direction + 1) % 4 ), node.action); //use % 4 to makes sure that the direction value is within 0 and 3. + 1 to turn 90 degrees clockwise aka turn right 			
    			
    			//move left
    			direction = (direction - 1) % 4;
    			if(direction < 0)
    				direction += 4;
    			child.left = new Node (x,y,direction, node.action); //use % 4 to makes sure that the direction value is within 0 and 3. - 1 to turn 90 degrees anti-clockwise aka turn left
    			
       			return child;
    		}
    		
    		public Action BFS(int x, int y, int direction, int goal) {

    			//Mark all the vertices as not visited (false) first, add to visited if it is visited, explored
    			// maximum is 30x30. each tile can be visited in 4 direction. 0 - 3
    			boolean[][][] visited = new boolean[30][30][4]; 
    			//another array for visiting, meaning that the tile needs to be visited, frontier
    			boolean[][][] visiting = new boolean[30][30][4]; 
    			
    			//Create a queue for BFS
    			LinkedList<Node> queue = new LinkedList<Node>();
    			
    			//dynamically add the child, to create the graph to traverse
    			Node curNode = new Node(x, y, direction, NoOpAction.NO_OP);
    			ChildNode CNode = getChildNode(curNode); 
    			
    			//front of child node not empty, add that node into the queue
    			if (CNode.front != null) {
    				CNode.front.action = LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
					//add to the queue for exploration by BFS	if it is unvisited
					queue.add(CNode.front); 
					//set that coordinate and direction to true, meaning want to visit that node.
					visiting[CNode.front.x][CNode.front.y][CNode.front.direction] = true; 
        		}
    			//right of the child node is not empty, add that node into the queue
    			if (CNode.right != null) {
    				CNode.right.action = LIUVacuumEnvironment.ACTION_TURN_RIGHT;
    				//add to the queue for exploration by BFS	if it is unvisited
					queue.add(CNode.right); 
					//set that coordinate and direction to true, meaning want to visit that node.
					visiting[CNode.right.x][CNode.right.y][CNode.right.direction] = true; 
        		}
    			//left of the child node is not empty, add that node into the queue
    			if (CNode.left != null) {
    				CNode.left.action = LIUVacuumEnvironment.ACTION_TURN_LEFT;
    				//add to the queue for exploration by BFS	if it is unvisited
					queue.add(CNode.left); 
					visiting[CNode.left.x][CNode.left.y][CNode.left.direction] = true; 
        		}
    			
    			//while queue is not empty
    			while (queue.size() != 0) {
    				
    				System.out.print("in the loop");
    				// Dequeue a node(vertex of a graph) from queue
    				Node vertex = queue.pollFirst();
    				//if the node from the queue is not null, mark it as visited 
    				if (vertex != null) {
        				visiting[vertex.x][vertex.y][vertex.direction] = false;
        			}
    				//if target found, can return the action
    				//since unknown = 0, find all the unknowns
    				if(state.world[vertex.x][vertex.y] == goal) {  
    					return vertex.action;
    				}
    				
    				//get the next child node for the temp node that we created, i.e.temp node is now the parent instead of child
    				ChildNode nextNode = getChildNode(vertex);
    			
    				//front of child node not empty, add that node into the queue
        			if (nextNode.front != null) {
        				// if this node is not already visited and not in the to visit (visiting) array already, then add it into visiting
        				if(visited[nextNode.front.x][nextNode.front.y][nextNode.front.direction] == false && visiting[nextNode.front.x][nextNode.front.y][nextNode.front.direction] == false) {
        						queue.add(nextNode.front); 
        						visiting[nextNode.front.x][nextNode.front.y][nextNode.front.direction] = true; 
        				}
            		}
        			//right of the child node is not empty, add that node into the queue
        			if (nextNode.right != null) {
        				//if this node is not already visited and not in the to visit (visiting) array already, then add it into visiting
        				if(visited[nextNode.right.x][nextNode.right.y][nextNode.right.direction] == false && visiting[nextNode.right.x][nextNode.right.y][nextNode.right.direction] == false) {
        						queue.add(nextNode.right); 
        						visiting[nextNode.right.x][nextNode.right.y][nextNode.right.direction] = true; 
        				}
            		}
        			//left of the child node is not empty, add that node into the queue
        			if (nextNode.left != null) {
        				//if this node is not already visited and not in the to visit (visiting) array already, then add it into visiting
        				if(visited[nextNode.left.x][nextNode.left.y][nextNode.left.direction] == false && visiting[nextNode.left.x][nextNode.left.y][nextNode.left.direction] == false) {
        						queue.add(nextNode.left); 
        						visiting[nextNode.left.x][nextNode.left.y][nextNode.left.direction] = true; 
        				}
            		}
        			// mark this node as visited
    				visited[vertex.x][vertex.y][vertex.direction] = true;
    			}
    			System.out.print("end of BFS");
				return NoOpAction.NO_OP;
    		}		
    		
    		public Action execute(Percept percept) {
    			MyVacuumAgent agent = new MyVacuumAgent();
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
    		    
    		    if (iterationCounter==0) {
    		    	System.out.print("no more iteration");
    		    	return NoOpAction.NO_OP;
    		    }
    		    
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
    		    
    		    if(!home)
    		    	state.updateWorld(state.agent_x_position, state.agent_y_position,state.CLEAR); 
    		    else if (home)
    		    	state.updateWorld(state.agent_x_position, state.agent_y_position,state.HOME); 
    		    
    		    
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
    		    	//start the BFS
    		    	System.out.println("Time to explore");	
		    		Action action = BFS(state.agent_x_position, state.agent_y_position, state.agent_direction, goal);	
		    		
		    		//no more actions to do and not at home yet, time to go home
		    		if(action == NoOpAction.NO_OP && goal != 4) {
		    			goal = 4;
		    			action = LIUVacuumEnvironment.ACTION_MOVE_FORWARD;
		    		}
		    		else if (goal == 4 && home == true) {
		    			//already at home
		    			System.out.print("I am at home");
		    			state.agent_last_action = state.HOME;
		    			return NoOpAction.NO_OP;
		    		}		
		    		return currentAction(action);
    		    }
    		}
    		
    		//keep track of current status / model of the agent
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