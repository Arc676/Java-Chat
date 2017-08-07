// MIT License
 
// Copyright (c) 2017 Matthew Chen, Arc676/Alessandro Vinciguerra
 
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
 
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

import java.io.*;
import java.net.*;
import java.util.*;

//port number: 4267

public class Client{

    //terminal IO
    private Scanner sc;

    //network IO
    private Socket sock;
    private PrintWriter out;
    private BufferedReader in;
    private int port;
    private String host;

    private String username;

    private MsgThread msgThread;

    public Client(String[] args) {
        sc = new Scanner(System.in);
        if (args.length == 3){
            username = args[0];
            host = args[1];
            try {
                port = Integer.parseInt(args[2]);
            } catch (NumberFormatException e){
                System.err.println("Invalid port input. Using default port 4267.");
                port = 4267;
            }
        } else {
            while (true){
                System.out.print("Enter username: ");
                username = sc.nextLine();
                if (username.equals("server") || username.equals("global")){
                    System.out.println("Error: reserved usernames cannot be used");
                } else {
                    break;
                }
            }
            System.out.print("Enter host: ");
            host = sc.nextLine();
            try {
                System.out.print("Enter port number: ");
                port = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e){
                System.err.println("Invalid port input. Using default port 4267.");
                port = 4267;
            }
        }
        try {
            sock = new Socket(host, port);
            out = new PrintWriter(sock.getOutputStream(), true);
            out.println(username);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            msgThread = new MsgThread(in, null, username);
            msgThread.start();
        } catch (IOException e){
            System.err.println("IOException occurred.");
            return;
        }

        run();
    }

    private void run(){
        System.out.println("Begin chat with " + host + " on port " + port);
        String inLine = "";
        while (true){
            String toSend = sc.nextLine();
            out.println(username + ": " + toSend);
            if (toSend.equals("/disconnect") || !msgThread.running){
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println(username + ": " + toSend);
            }
        }
        try {
            msgThread.running = false;
            sock.close();
        } catch (IOException e){
            System.err.println("err....");
        }
        System.out.println("Chat terminated");
    }

    public static void main(String[] args) {
        new Client(args);
    }

}