CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -lboost_locale 


all: EchoClient
	g++ -o bin/BBclient bin/connectionHandler.o bin/echoClient.o bin/Task.o bin/Task0.o $(LDFLAGS) 

EchoClient: bin/connectionHandler.o bin/echoClient.o bin/Task.o bin/Task0.o
	
bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp
bin/Task.o: src/Task.cpp 
	g++ $(CFLAGS) -o bin/Task.o src/Task.cpp
bin/Task0.o: src/Task0.cpp
	g++ $(CFLAGS) -o bin/Task0.o src/Task0.cpp
bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp
	

.PHONY: clean
clean:
	rm -f bin/*
