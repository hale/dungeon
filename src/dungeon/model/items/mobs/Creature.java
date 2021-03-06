package dungeon.model.items.mobs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import org.w3c.dom.Node;

import dungeon.App;
import dungeon.ai.Behaviour;
import dungeon.ai.DefaultBehaviour;
import dungeon.model.Game;
import dungeon.model.items.treasure.Gold;
import dungeon.model.items.treasure.Treasure;
import dungeon.utils.Persistent;
import dungeon.utils.XMLHelper;

/**
 * Class representing a creature
 * <BR>
 * Derived classes are <B>Orc</B> and <B>Ogre</B>
 */
public abstract class Creature extends Mob implements Persistent
{
	public static final String DEFAULT_FACTION = "Enemy";
	private String fFaction  = DEFAULT_FACTION;
	
	public String getFaction()
	{
		return fFaction;
	}

	public double getSize()
	{
		return 1;
	}
	
	public double getSpeed()
	{
		return 1;
	}

	public int getDefence()
	{
		return 5;
	}
	
	public int getStrength()
	{
		return 20;
	}
	
	public int getMaxHealth()
	{
		return 10;
	}
	
	public int getMaxEnergy()
	{
		return 10;
	}
	
	public Behaviour getBehaviour()
	{
		return fBehaviour;
	}
	Behaviour fBehaviour = new DefaultBehaviour(this);
	
	public void draw(Graphics2D g, Rectangle2D rect, boolean highlight)
	{
		Color c = Color.RED;
		if (!fFaction.equals(""))
		{
			Faction f = App.getGame().getFactions().find(fFaction);
			if (f != null)
				c = f.getColour();
		}
		
		defaultDraw(g, rect, c, highlight);
	}

	/**
	 * @param fBehaviour the fBehaviour to set
	 */
	public void setBehaviour(Behaviour behaviour)
	{
		fBehaviour = behaviour;
	}

	public void onTick(Game game)
	{
		boolean acted = fBehaviour.onTick(game);
		if (!acted)
		{
			int energy = Math.min(getCurrentEnergy() + 1, getMaxEnergy());
			setCurrentEnergy(energy);
		}
	}

	public void gameOverTick(Game game)
	{
		fBehaviour.gameOverTick(game);
	}

	public void deathTick(Game game)
	{
		fBehaviour.deathTick(game);
	}
	
	/**
	 * Called when the creature is killed
	 * <BR>
	 * Causes the creature to drop all its treasure
	 * 
	 * @param game The current game object
	 */
	public void onDeath(Game game)
	{
		//call behaviour one last time
		fBehaviour.deathTick(game);
		
		Vector<Treasure> treasures = new Vector<Treasure>();
		treasures.addAll(fInventory);
		if (fGold > 0)
		{
			Gold g = new Gold(fGold);
			treasures.add(g);
		}
		
		// Drop all carried loot randomly around the body
		while (treasures.size() > 0)
		{
			Treasure t = treasures.get(0);
			treasures.remove(0);
			
			drop(game, t);
		}
	}

	public void load(Node node)
	{
		super.load(node);
		
		if (XMLHelper.attributeExists(node, "Behaviour"))
		{
			String class_name = XMLHelper.getStrValue(node, "Behaviour");

			try
			{
				Class<?> c = Class.forName(class_name);
				
				if (Class.forName("dungeon.ai.Behaviour").isAssignableFrom(c))
				{
					Behaviour b = (Behaviour)c.getConstructor(Class.forName("dungeon.model.items.mobs.Creature")).newInstance(this);
					if (b != null)
						fBehaviour = b;
				}
				else
				{
					System.err.println("Specified behaviour " + class_name + " is not a subclass of dungeon.ai.Behaviour");
				}
			}
			catch (Exception ex)
			{
				System.err.println(ex);
			}
		}
		
		fFaction = Creature.DEFAULT_FACTION;
		if (XMLHelper.attributeExists(node, "Faction"))
			fFaction = XMLHelper.getStrValue(node, "Faction");
	}

	public void save(Node node)
	{
		super.save(node);
		
		if (getBehaviour() != null)
		{
			String class_name = getBehaviour().getClass().getName();
			XMLHelper.setStrValue(node,"Behaviour", class_name);
		}
		
		if (!getFaction().equals(DEFAULT_FACTION))
			XMLHelper.setStrValue(node,"Faction", fFaction);
	}
}
