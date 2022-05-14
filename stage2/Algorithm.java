/*
Project: Stage 2, S1 2022
Class: Distributed Systems - COMP3100
Author: Lachlan Rigg - 45209715
Module: Algorithm.java
*/

import java.io.IOException;

public class Algorithm {
    String name;
    LRR lrr;
    FC fc;
    OTT ott;
}

//LARGEST ROUND ROBIN - LRR
class LRR extends Algorithm{
    String largestServerType = "";
    int largestCoreCount = 0, noOfServers = 0, currentIndex = -1;

    ServerList serverList = new ServerList();

    public LRR(Clientv2 client, DATA data) throws IOException{
        client.sendRequest("OK");

        for(int i = 0; i < data.nRecs; i++){
            //Receive a response
            String[] response = client.receiveResponse();

            //Add the server to a server list
            Server server = new Server(response);
            this.serverList.addServer(server);

            //If the core count of the current server is larger than the previous and the server type differs; then update it
            if(server.core > this.largestCoreCount){
                this.largestServerType = server.serverType;
                this.largestCoreCount = server.core;
                this.noOfServers = 0;
            }

            //Else if the core counts are the same and the server types are the same then increase number of server by one
            else if((server.core == this.largestCoreCount) && (server.serverType.equals(this.largestServerType))){
                this.noOfServers++;
            }
        }
        
    }

    //Function to handle the looping server index for LRR
    public int nextServerIndex(){
        if(this.currentIndex >= this.noOfServers){
            this.currentIndex = 0;
        }
        else{
            this.currentIndex++;
        }

        return this.currentIndex;
    }
}

//FIRST CAPABLE ALGORITHM
class FC extends Algorithm{
    String serverType = "";
    int serverID = 0;

    public FC(Clientv2 client, DATA data) throws IOException{
        client.sendRequest("OK");

        //Receive server responses but only record the first server entry
        for(int i = 0; i < data.nRecs; i++){
            String[] response = client.receiveResponse();
            Server server = new Server(response);

            if(i == 0){
                this.serverType = server.serverType;
                this.serverID = server.serverID;
            }
        }

    }
}

//OPTIMISED TURNAROUND TIME - Custom algorithm for optimised turnaround time
class OTT extends Algorithm{
    String serverType = "";
    int serverID = 0;

    ServerList serverList = new ServerList();

    public OTT(Clientv2 client, DATA data, JOBN latestJob) throws IOException{
        client.sendRequest("OK");

        //If the DATA received from GETS Avail is zero, then we need to use GETS Capable
        if(data.nRecs == 0){
            client.receiveResponse(); //This will be a "."

            client.sendRequest(String.format("GETS Capable %d %d %d", latestJob.core, latestJob.memory, latestJob.disk));
            data = new DATA(client.receiveResponse());

            client.sendRequest("OK");

            //Iterate through the servers in the DATA response
            for(int i = 0; i < data.nRecs; i++){
                String[] response = client.receiveResponse();
                Server server = new Server(response);

                this.serverList.addServer(server);
                
                //If there is only one record then choose that
                if(data.nRecs == 1){
                    this.serverType = server.serverType;
                    this.serverID = server.serverID;
                }
            }

            //Otherwise if serverType is still empty, acknowledge
            if(this.serverType.equals("")){
                client.sendRequest("OK");
                client.receiveResponse(); //This will be a "."

                //Set the shortestWaitingTime initially to the largest integer value
                int shortestWaitingTime = Integer.MAX_VALUE;
                int serverWaitingTimeSum = 0;

                //Iterate through all the servers we stored in the serverList
                for(int i = 0; i < this.serverList.list.size(); i++){
                    Server server = this.serverList.getServer(i);

                    //Send an LSTJ request to the server
                    client.sendRequest(String.format("LSTJ %s %d", server.serverType, server.serverID));

                    data = new DATA(client.receiveResponse());
                    client.sendRequest("OK");

                    //For the records received from DATA response to LSTJ
                    for(int j = 0; j < data.nRecs; j++){
                        String[] response = client.receiveResponse();

                        //Sum all of the estimated job runtimes for the current server
                        serverWaitingTimeSum += Integer.parseInt(response[4]);
                    }

                    //If the estRuntime sum is smaller than the shortest waiting time
                    if(serverWaitingTimeSum < shortestWaitingTime){
                        //Set the server type and id to the current server
                        this.serverType = server.serverType;
                        this.serverID = server.serverID;
                        
                        //Set the shortest waiting time to the current servers estRuntime sum
                        shortestWaitingTime = serverWaitingTimeSum;
                    }

                    //Reset the sum to zero
                    serverWaitingTimeSum = 0;

                    //If the for loop is NOT on the last iteration, then send OK and receive a response
                    if(i < this.serverList.list.size() - 1){
                        client.sendRequest("OK");
                        client.receiveResponse();
                    }
                }
            }
        }

        //Else if the response to GETS Avail is greater than 0
        else{
            for(int i = 0; i < data.nRecs; i++){
                String[] response = client.receiveResponse();
                Server server = new Server(response);

                //Choose the first available server
                if(i == 0){
                    this.serverType = server.serverType;
                    this.serverID = server.serverID;
                }
            }
        }
    }
}