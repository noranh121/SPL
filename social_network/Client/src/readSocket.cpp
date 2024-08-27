#include <sstream>
#include "../include/readSocket.h"

readSocket::readSocket(ConnectionHandler &handler):handler(handler){}

void readSocket::operator()() {
    while (!handler.ShouldTerminate()) {
        char bytes[4];
        if (handler.getBytes(bytes,4)) {
            decode(bytes);
        }
        else {
            handler.terminate();
        }
    }
}

void readSocket::decode(char bytes[]) {
    short result = bytesToShort(bytes, 0);
    short opcode = bytesToShort(bytes, 2);
    string content;
    handler.getLine(content);
    printSocket(result, opcode, content);
}

short readSocket::bytesToShort(char bytes[], int index){
    short res = (short)((bytes[index] & 0xff) << 8);
    res += (short)(bytes[index+1] & 0xff);
    return res;
}

void readSocket::printSocket(short result, short opcode, string content) {
    switch (result) {
        case 9:
            if(opcode == 0)
                cout << "NOTIFICATION PM " << content << endl;
            else
                cout << "NOTIFICATION Public" << content << endl;
            break;
        case 10:
            cout << "ACK " << opcode << " " << content << endl;
            if (opcode == 3){
                cout << "Sucessfully logged out, press enter to exit." << endl;
                handler.terminate();
            }
            break;
        case 11:
            cout << "ERROR " << opcode << endl;
            break;
        default: 
            break;
    }
}
