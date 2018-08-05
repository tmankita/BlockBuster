#include <stdlib.h>
#include <boost/thread/mutex.hpp>
#include <boost/thread.hpp>
#include "../include/connectionHandler.h"
#include "../include/Task.h"
#include "../include/Task0.h"

    int main(int argc, char *argv[]) {
        // checks if the data of the port or the server ip is right
        if (argc < 3) {
            return -1;
        }
        std::string host = argv[1];
        short port = atoi(argv[2]);

        // trying to connect to the server
        ConnectionHandler handler(host, port);
        if (!handler.connect()) {
            return 1;
        }

        std::condition_variable cv;
        std::mutex mutexRead;

        // waiting for command from the client
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::string answer;

        // sending the data to the server
        if (!handler.sendLine(line)) {
            mutexRead.unlock();
            return 0;
        }

        // creates a new thread which waiting for commands from the user
        Task0 taskSend(&mutexRead, &cv, handler);
        boost::thread th2(boost::bind(&Task0::run, &taskSend));

        // creates a new thread which waiting for respond from the server
        Task tasklisten(&mutexRead, &cv, handler, &taskSend);
        boost::thread th1(boost::bind(&Task::run, &tasklisten));

        // when both threads finished their tasks - wait for them to shutdown
        th2.join();
        th1.join();

        // main method closed
        return 0;
    }






