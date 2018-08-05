#include <stdlib.h>
#include <boost/thread/mutex.hpp>
#include <boost/thread.hpp>
#include "../include/Task0.h"

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

// constructor
Task0::Task0(std::mutex *mutexRead, std::condition_variable *cv, ConnectionHandler &handler) : _mutexRead(mutexRead), _cv(cv), _handler(handler), shouldWait(0){}

// run function
 void Task0:: run() {
    // task loop
    while (1) {
        // synchronize on the task
        std::unique_lock<std::mutex> lck(*_mutexRead);
        // the thread will wait until we got a respond from the server on the last command
        (*_cv).wait(lck, [this]{return (getShouldWait() == 1);});
        setShouldWait(0);
        // waiting for client command
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int len = line.length();
        // sending the data to the server. In case of error - disconnecting
        if (!_handler.sendLine(line)) {
            return;
        }
        // When the client request to sign out - stop the thread task
        if (len == 0) {
            return;
        }
        lck.unlock();
    }
}

void Task0:: setShouldWait(int i){
    shouldWait = i;
}

int Task0::getShouldWait() {
    return shouldWait;
}