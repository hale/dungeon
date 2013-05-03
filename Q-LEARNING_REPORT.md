# q learning assessment 2

## State variables

* creature energy level
* creature health level
* whether creature is threatened or safe

## Actions

* pathfind to nearest treasure 
* stay still

---

### next steps .todo
* make the same q table used by all mobs in the faction @done
* load / save the q table to disk @done
* pick rewards which result in significant changes in q values @done
* put the q learning stuff in its own class away from creature behaviour @done
* have the q learning utility class provide an action (either randomly chosen or from the  q table) @done
* train and observe meaningful improvement in behaviour @done
* write report 
* document code
* submit