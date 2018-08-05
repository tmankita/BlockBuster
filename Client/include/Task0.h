#ifndef _TASK0_
#define _TASK0_
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <boost/thread/mutex.hpp>
#include <mutex>
#include <condition_variable>
#include "../include/connectionHandler.h"

using boost::asio::ip::tcp;
using std::mutex;
using std::condition_variable;

class Task0 {
private:
    // local variables
    mutex *_mutexRead;
    condition_variable *_cv;
    ConnectionHandler &_handler;
    int shouldWait;

public:
    // consturctor
    Task0( std::mutex *mutexRead, std::condition_variable *cv, ConnectionHandler &handler);
    // This task operates a thread which waiting to commands from the client(keyboard)
    void run();
    // set shouldWait to the given itn
    void setShouldWait(int i);
    // return the shouldWait value
    int getShouldWait();
};

#endif
