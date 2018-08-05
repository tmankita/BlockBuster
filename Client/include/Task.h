#ifndef _TASK_
#define _TASK_
#include <string>
#include <iostream>
#include <boost/asio.hpp>
#include <boost/thread/mutex.hpp>
#include <condition_variable>
#include <mutex>
#include "../include/connectionHandler.h"
#include "Task0.h"

using boost::asio::ip::tcp;
using std::mutex;
using std::condition_variable;


class Task {
private:
    // local variables
    mutex *_mutexRead;
    condition_variable *_cv;
    ConnectionHandler &_handler;
    Task0* task;

public:
    // constructor
    Task( std::mutex *mutexRead, std::condition_variable *cv, ConnectionHandler &handler, Task0* task);
    // This task operates a thread which waiting for the server reply
    void run();
};
#endif
