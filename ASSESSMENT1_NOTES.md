# AI for Games assessment

## TOMORROW .todo

* **Determine faction goal point:** @done
	* Closest treasure?
		* For now: move to first treasure in list. @done
		* Next: A* in reverse, find nearest treasure. @done
	* if no treasure, move to enemies
		* For now: first enemy in list. @done
		* Next: closest enemy @done
* **Check fighting.** @done
	* Write an xml map based on 2013.xml with two factions. @done
	* test against your own behaviour @done
* **Refactor** @done
	* Rename SimplePathFind to AStarPathFind @done
	* Clean up and lightly refactor classes in `dungeon.ai.hale` @done
* **Improve performance** - speed up A* @done
	* use a heap for open list @done
	* replace LinkedLists with ArrayDeques. @done
* **Documentation**
	* Write javadoc for public methods. @done
	* Write the report explaining strategy and implementation details of each.

---



## Behaviour.todo
* (40+) Find optimal paths using A* search: @done
	* * `2013_1_1.xml` @done
	* Map with narrow corridors. @done
	* Map with locked doors, potions, flame traps and pits. @done
	* All the above with two mobs in a faction . @done
	* All the above with more than two mobs in a faction. @done
* "Exhibit sensible context-sensitive behaviour":
	* (50+) In `2013_1_1.xml`
	* (60+) Map with narrow corridors
	* (70+) Map with locked doors, potions, flame traps and pits.

## Report.todo
* (10) List files, installation and usage instructions.
* (40) Describe your strategy for the faction and its creatures. Describe techniques and algorithms used in to implement it.
* (30) Implementation details and performance trade-offs.
* (20) Description of how you tested your code, and recommendations of how to test "this type of game". 

---



# Faction Pathfinding
Having a follower and a leader results in difficulties when fetching treasure from the end of narrow corridors.  The leader becomes blocked by the follower, and telling the follower to move out of the way is nontrivial.

Therefore, return to the basic solution of independent pathfinding between creatures in a faction.  In order to ensure they move as a group, only pathfind when the mob is far away from another.


# New Plan of Action



# Plan of Action .todo
* Faction behaviour to pathfind as a group to another room.
	* leader move to touchpoint
	* followers follow leader
	* when faction 'together', leader move to center of other room.
* Faction behaviour to collect treasure in a room.
	* when all members in room, colect treasure
		* one member per treasure.  remaining go to center.
* Switch state: 
	* when room has treasure, collect treasure.
	* When room has no treausure, move to room with treasure.
	* When no room with treasure, 

# Report

## Faction strategy.

My faction acts defensively.  By default it aims to collect as much treasure as possible.  When a faction member is under threat, it makes a fight-or-flight check.  If it fights, the whole faction attacks.  If it flees, the faction groups together and then attacks.

*To do the above, what diferent behaviours would we need?*

* Find treasure.
	* Leader pathfinds to room with treasure, followers follow leader.
* Collect treasure.
	* Faction splits and pathfinds to treasure in room.
* Fight behaviour
	* Creatures attack enemy.
* Defend.
	* Creatures move closer to each other.
	

If an enemy enters the same room as the faction, the faction is considered under threat.  At this point, faction members move towards each other and fight the approaching enemy.

--

An influence map is used to make the pathfinding more intelligent.  When treasure-hunting, areas of the map that are dangerous (because of the proximity of enemy creatures) are given a higher movement cost.

