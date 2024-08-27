#include <sstream>
#include "../include/sendSocket.h"

sendSocket::sendSocket(ConnectionHandler &handler):handler(handler){}

void sendSocket::operator()()  {
    while (!handler.ShouldTerminate()){
        short buffer_size=1024;
        char buffer[buffer_size];
        cin.getline(buffer, buffer_size);
        string line(buffer);
        if(!handler.ShouldTerminate() & !encode(line)){
            handler.terminate();
            break;
        }
    }
}

vector<string> sendSocket::splitInput(string command){
    vector<string> input;
    istringstream split(command);
    for(string s;split>>s;)
        input.push_back(s);
    return input;
}

bool sendSocket::encode(string command) {
    vector<string> input = splitInput(command);
    if(input.size() > 0){
        short opcode = commandToOpcode(input[0]);
        if(opcode != -1) {
            char bytesArr[2];
            shortToBytes(opcode, bytesArr);
            if(handler.sendBytes(bytesArr,2)){
                for (unsigned i=1; i < input.size(); i++){
                    handler.sendLine(input[i]);
                }
		char delimiter[] = {';'};
		handler.sendBytes(delimiter, 1);
                return true;
            } else return false;
        } else {
            cout << "Unknown command" << endl;
        }
    }
        return true;
}

void sendSocket::shortToBytes(short num, char* bytesArr){
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}

short sendSocket::commandToOpcode(string command) {
    if(command=="REGISTER") return 1;
    if (command=="LOGIN") return 2;
    if(command=="LOGOUT") return 3;
    if(command=="FOLLOW") return 4;
    if(command=="POST") return 5;
    if (command=="PM") return 6;
    if(command=="LOGSTAT") return 7;
    if(command=="STAT") return 8;
    if (command=="BLOCK") return 12;
    return -1;
}