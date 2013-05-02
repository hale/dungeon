# q learning assessment 2

## State variables

* creature energy level
* creature health level
* whether creature is threatened or safe

## Actions

* pathfind to nearest treasure 
* stay still

---

### next steps @todo
1. make the same q table used by all mobs in the faction @done
2. load / save the q table to disk
3. pick rewards which result in significant changes in q values
4. put the q learning stuff in its own class away from creature behaviour
5. have the q learning utility class provide an action (either randomly chosen or from the  q table)
6. train and observe meaningful improvement in behaviour
7. write report 