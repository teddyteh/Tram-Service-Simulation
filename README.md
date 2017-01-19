# Tram Service Simulation
A distributed tram service simulation with replication capabilities using Java RMI.

## Screenshots
#### Client
![Client](https://media.giphy.com/media/26xByqrZqnMyF28cU/source.gif)

#### Front end
![Front end](https://media.giphy.com/media/l3q2XXW9mzb0So14A/source.gif)

#### Replica manager (Server)
![Replica manager](https://media.giphy.com/media/l3q2DFLvOHfviyAco/source.gif)

#### Server control panel
![Server control panel](https://s29.postimg.org/ixdd60hev/server_control_panel.png)

### Changelog
1. Added a server front end which contains information about all running replica managers (RMs). This front end will be the only point of communication with clients, and will forward client requests to all running RMs as well as retrieve a reply from the first running replica manager.
2. Created two additional servers (replica managers) to create a distributed system. 
3. Changed all methods to only contain/ return the Message object.
4. Created a GUI to turn on and off any of the replica managers at any time. Upon starting the GUI, RM1 will be turned on. At all times, the GUI will make sure at least one RM is running.
