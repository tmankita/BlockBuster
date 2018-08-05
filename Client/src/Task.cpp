#include <stdlib.h>
#include <boost/thread/mutex.hpp>
#include <boost/thread.hpp>
#include <condition_variable>
#include "../include/connectionHandler.h"
#include "../include/Task.h"

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

// constructor
Task::Task( std::mutex *mutexRead, std::condition_variable *cv, ConnectionHandler &handler, Task0* task) :  _mutexRead(mutexRead),_cv(cv), _handler(handler), task(task) {}

// run function
 void Task:: run() {
     // task loop
     while (1) {
         std::string answer;
         while(_handler.hasInSocket()) {
             int len;
             answer = "";
             // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
             // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
             if (!_handler.getLine(answer)) {
                 return;
             }
             len = answer.length();
             // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
             // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
             answer.resize(len - 1);
             // connectionHandler.sendLine(line) appends '\n' to the message. Therefor we send len+1 bytes.
             std::cout <<  answer << std::endl;
             if ((answer.find("ACK") != std::string::npos) | (answer.find("ERROR") != std::string::npos)) {
                 // wake up the thread which waiting to the client commands
                 task->setShouldWait(1);
                 (*_cv).notify_all();
             }
         }
         // In case which we recieved acknowledge of SIGNOUT command - stop the thread
         if(answer.find("ACK signout succeeded")!=std::string:: npos){
             std::cout <<"Ready to exit. Press enter"<< std::endl;
             return;
         }
     }
 }


