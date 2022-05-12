import java.io.IOException;

public class Algorithm {
    String name;
    LRR lrr;
    FC fc;
    FF ff;
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

//FIRST FIT ALGORITHM - Currently not working as intended, consider deleting and using another algorithm
class FF extends Algorithm{
    String serverType = "";
    int serverID = 0;
    boolean foundFirst = false;
    ServerList serverList = new ServerList();

    public FF(Clientv2 client, DATA data, JOBN latestJob) throws IOException{

        //Search all servers with Avail

        //Choose the first server that satisfies:
        //  1. Sufficient core count available for latestJob
        //  2. Server state is Inactive/Active
        
        //Else search servers with Capable

        //Choose the first server that satisfies:
        //  1. Sufficient core count available for latestJob
        //  2. Server state is Active/Booting 

        if(data.nRecs == 0){
            client.sendRequest("OK");
            client.receiveResponse(); //This will be a "."

            client.sendRequest(String.format("GETS Capable %d %d %d", latestJob.core, latestJob.memory, latestJob.disk));
            data = new DATA(client.receiveResponse());

            client.sendRequest("OK");

            for(int i = 0; i < data.nRecs; i++){
                String[] response = client.receiveResponse();
                Server server = new Server(response);

                this.serverList.addServer(server);
                
                if((server.core >= latestJob.core) && (!foundFirst)){
                    this.serverType = server.serverType;
                    this.serverID = server.serverID;
                    this.foundFirst = true;
                }
            }

            if(this.serverType.equals("")){
                this.serverType = this.serverList.getServer(0).serverType;
                this.serverID = this.serverList.getServer(0).serverID;
            }
        }

        else {
            client.sendRequest("OK");

            for(int i = 0; i < data.nRecs; i++){
                String[] response = client.receiveResponse();
                Server server = new Server(response);

                if((server.core >= latestJob.core) && (!foundFirst)){
                    this.serverType = server.serverType;
                    this.serverID = server.serverID;
                    this.foundFirst = true;
                }
            }
        }
    }
}