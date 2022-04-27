

import java.io.IOException;

public class Algorithm {
    String name;
    LRR lrr;
    FC fc;
}

class LRR extends Algorithm{
    String largestServerType = "";
    int largestCoreCount = 0, noOfServers = 0, currentIndex = -1;
    boolean hasRun = false;

    public LRR(Clientv2 client, DATA data) throws IOException{
        if(!hasRun){
            client.sendRequest("OK");

            for(int i = 0; i < data.nRecs; i++){
                //Receive a response
                String[] response = client.receiveResponse();

                //If the core count of the current server is larger than the previous and the server type differs; then update it
                if(Integer.parseInt(response[4]) > this.largestCoreCount){
                    this.largestServerType = response[0];
                    this.largestCoreCount = Integer.parseInt(response[4]);
                    this.noOfServers = 0;
                }

                //Else if the core counts are the same and the server types are the same then increase number of server by one
                else if((Integer.parseInt(response[4]) == this.largestCoreCount) && (response[0].equals(this.largestServerType))){
                    this.noOfServers++;
                }
            }
            this.hasRun = true;
        }
    }

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

class FC extends Algorithm{
    String serverType = "";
    int serverID = 0;

    public FC(Clientv2 client, DATA data) throws IOException{
        client.sendRequest("OK");

        for(int i = 0; i < data.nRecs; i++){
            String[] response = client.receiveResponse();
            if(i == 0){
                this.serverType = response[0];
                this.serverID = Integer.parseInt(response[1]);
            }
        }

    }
}