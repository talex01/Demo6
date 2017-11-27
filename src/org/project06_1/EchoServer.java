/*
---------
Задание 1
---------

Написать многопоточный EchoServer
*/

package org.project06_1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    public static final int port = 5000;
    public static void main(String[] args) throws IOException {
        System.out.println("Server Started");
        while (true) {
            try (ServerSocket s = new ServerSocket(port)) {
//                while (true) {
                    Socket socket = s.accept();
                    try {
                        new MonoServer(socket);
                    } catch (IOException e) {
                        socket.close();
                    }
//                }
            }
        }
    }

    public static class MonoServer extends Thread {
//        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public MonoServer(Socket socket) throws IOException {
//            socket = s;
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedOutputStream(socket.getOutputStream()), true);

            out.println("Type 'Bye' to exit");
            start();
        }

        @Override
        public void run() {
            String line;
            try {
                while ((line = in.readLine()) != null) {
                    if (line.equalsIgnoreCase("Bye")) {
                        break;
                    }
                    System.out.println("client " + Thread.currentThread().getName() + " >>> " + line);
                    out.println(line);
                }
                in.close();
                out.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            System.out.println("Server is shutdown for " + Thread.currentThread().getName());
        }
    }
}