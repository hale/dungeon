package dungeon.collections;

import java.util.HashMap;
import java.util.Vector;

import org.w3c.dom.Node;

import dungeon.model.items.mobs.Creature;
import dungeon.model.items.mobs.Ogre;
import dungeon.model.items.mobs.Orc;
import dungeon.utils.Persistent;
import dungeon.utils.XMLHelper;

/**
 * A collection of Creature objects
 */
public class CreatureList extends Vector<Creature> implements Persistent
{
	// Required
	private static final long serialVersionUID = 96864027610428628L;

	public void load(Node node)
	{
		HashMap<String, Class<? extends Creature>> classes = new HashMap<String, Class<? extends Creature>>();
		classes.put("Orc", Orc.class);
		classes.put("Ogre", Ogre.class);
		
		clear();
		
		// Find the base node
		Node base = XMLHelper.findChild(node, "Vector");
		if (base == null)
			return;

		int count = base.getChildNodes().getLength();
		for (int a = 0; a != count; ++a)
		{
			// Load this item
			try
			{
				Node first_node = base.getChildNodes().item(0);
				String classname = first_node.getNodeName();
				
				Class<? extends Creature> c = classes.get(classname);
				if (c != null) {
					Creature obj = c.newInstance();
				
					XMLHelper.loadObject(base, classname, obj);
					add(obj);
				}
			}
			catch (Exception ex)
			{
				System.err.println(ex);
			}
			
			Node child = base.getChildNodes().item(0);
			base.removeChild(child);
		}
	}

	public void save(Node node)
	{
		XMLHelper.saveVector(node, "Vector", this);
	}
}
