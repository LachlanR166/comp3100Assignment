# COMP3100 (Distributed Systems) - Stage 2
### Introduction

COMP3100 Stage 2 requires the implementation of a custom algorithm that is optimised for turnaround time, whilst still maintaining balanced performance metrics.

### Usage
---

- Open a terminal in the directory and enter ```chmod +x givePermissions.sh``` and then enter ```./givePermissions.sh``` to give execute permissions to all the scripts.
- Open a new terminal and run ```./startServer.sh``` to start ds-sim server (it will ask you which sample config to use in the range of 1 to 5, they can be found in ```ds-sim > configs > sample-configs```).
- Open a second terminal and run ```./startClient.sh``` to start the java client.
- It will pop up asking you which algorithm you would like to choose, for stage 2 please enter ```ott``` for optimised turnaround time.
- Use ```./ds-client.sh``` where it will then ask what algorithm to use, for the purposes of this project we are only interested in: ```fc, ff, bf, wf```.

---
