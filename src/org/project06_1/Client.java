package org.project06_1;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Client {
    public static void main(String[] args) {
        while (MonoClient.getThreadcount() < MonoClient.MAX_THREADS) {
                new MonoClient();
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MonoClient extends Thread {

        static final int MAX_THREADS = 2;

        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private static int counter = 0;
        private int id = counter++;
        private static int threadcount = 0;

        public static int getThreadcount() {
            return threadcount;
        }

        public MonoClient() {
            System.out.println("Making client " + id);
            threadcount++;

            try {
                socket = new Socket(InetAddress.getLocalHost(), EchoServer.port);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            setName("ClientThread" + threadcount);
            start();
        }

        @Override
        public void run() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            try  {
                String line;
                line = in.readLine();
                System.out.println("server >>> " + line);

                while (true) {
                    System.out.print(Thread.currentThread().getName() + " >>> ");
                    line = reader.readLine();
                    out.println(line);
                    if (line.equalsIgnoreCase("Bye")) {
                        break;
                    }
                    String answer = in.readLine();
                    System.out.println("server >>> " + answer);
                }
                System.out.println("Client closed connection");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                threadcount--;
            }
        }
    }
}
