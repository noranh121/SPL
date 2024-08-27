#ifndef BOOST_ECHO_CLIENT_READKEYBOARD_H
#define BOOST_ECHO_CLIENT_READKEYBOARD_H
#include <string>
#include <vector>
#include <iostream>
#include <codecvt>
#include <locale>
#include "connectionHandler.h"
using namespace std;


class sendSocket{
public:
    sendSocket(ConnectionHandler &handler);
    bool encode(string toEncode);
    vector<string> splitInput(string command);
    void operator()();
private:
    void shortToBytes(short num, char* bytesArr);
    short commandToOpcode(string str);
    ConnectionHandler &handler;
};
#endif //BOOST_ECHO_CLIENT_READKEYBOARD_H