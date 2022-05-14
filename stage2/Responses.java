/*
Project: Stage 2, S1 2022
Class: Distributed Systems - COMP3100
Author: Lachlan Rigg - 45209715
Module: Responses.java
*/

import java.util.ArrayList;

//Class for DATA responses
class DATA {
    int nRecs, recLen;

    public DATA(String[] response){
        this.nRecs = Integer.parseInt(response[1]);
        this.recLen = Integer.parseInt(response[2]);
    }
}

//Class to hold a an ArrayList of Server objects
class ServerList {
    Server largestServer;
    ArrayList<Server> list = new ArrayList<Server>();

    public void addServer(Server server){
        this.list.add(server);
    }

    public Server getServer(int index){
        return this.list.get(index);
    }

    public Server getLargestServer(){
        int largestCoreCount = 0;
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).core > largestCoreCount){
                largestCoreCount = list.get(i).core;
                this.largestServer = list.get(i);
            }
        }
        return this.largestServer;
    }
}

//Class for server objects
class Server {
    String serverType, state;
    int serverID, curStartTime, core, memory, disk;

    public Server(String[] response){
        if(response.length > 1){
            this.serverType = response[0];
            this.serverID = Integer.parseInt(response[1]);
            this.state = response[2];
            this.curStartTime = Integer.parseInt(response[3]);
            this.core = Integer.parseInt(response[4]);
            this.memory = Integer.parseInt(response[5]);
            this.disk = Integer.parseInt(response[6]);
        }
    }

    public String toString(){
        return String.format("type = %s, id = %d, state = %s, cores = %d, memory = %d, disk = %d", this.serverType, this.serverID, this.state, this.core, this.memory, this.disk);
    }
}

//Class for JOBN responses
class JOBN {
    int submitTime, jobID, estRuntime, core, memory, disk;

    public JOBN(String[] response){
        this.submitTime = Integer.parseInt(response[1]);
        this.jobID = Integer.parseInt(response[2]);
        this.estRuntime = Integer.parseInt(response[3]);
        this.core = Integer.parseInt(response[4]);
        this.memory = Integer.parseInt(response[5]);
        this.disk = Integer.parseInt(response[6]);
    }

    public String toString(){
        return String.format("jobid = %d, jobCores = %d, jobMem = %d, jobDisk = %d", this.jobID, this.core, this.memory, this.disk);
    }
}