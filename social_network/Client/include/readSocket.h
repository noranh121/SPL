#ifndef BOOST_ECHO_CLIENT_BGRSENCODERDecoder_H
#define BOOST_ECHO_CLIENT_BGRSENCODERDecoder_H
#include <string>
#include <vector>
#include <iostream>
#include <codecvt>
#include <locale>
#include <mutex>
#include "connectionHandler.h"

using namespace std;

class readSocket {
public:
    readSocket(ConnectionHandler &handler);
    void decode(char* bytes);
    short bytesToShort(char bytes[], int index);
    void printSocket(short result, short opcode, string content);
    void operator()();
private:
    ConnectionHandler &handler;
};
#endif //BOOST_ECHO_CLIENT_BGRSENCODERDecoder_H
