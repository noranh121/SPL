CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: bin/BGSclient

bin/BGSclient: bin/readSocket.o bin/connectionHandler.o bin/sendSocket.o bin/BGSclient.o
	@echo 'Building target: main'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/BGSclient bin/connectionHandler.o bin/readSocket.o bin/sendSocket.o bin/BGSclient.o $(LDFLAGS)
	@echo 'Finished building target: main'
	@echo ' '

bin/BGSclient.o: src/BGSclient.cpp
	g++ $(CFLAGS) -o bin/BGSclient.o src/BGSclient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/readSocket.o: src/readSocket.cpp
	g++ $(CFLAGS) -o bin/readSocket.o src/readSocket.cpp

bin/sendSocket.o: src/sendSocket.cpp
	g++ $(CFLAGS) -o bin/sendSocket.o src/sendSocket.cpp

.PHONY: clean
clean:
	rm -f bin/*