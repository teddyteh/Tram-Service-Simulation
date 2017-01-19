1. Added a server front end which contains information about all running replica managers. This front end will be the only point of communication with clients,
and will forward client requests to all running RMs as well as retrieve a reply from the first running replica manager.
2. Created two additional servers (replica managers) besides the one from assignment 1.
3. Changed all methods to only contain/ return the Message object.
4. Created a GUI to turn on and off any of the replica managers at any time. Upon starting the GUI, RM1 will be turned on. At all times, the GUI will make sure
at least one RM is running.