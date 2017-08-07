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

import java.util.*;
import java.io.*;
import java.net.*;

public class Server{

    private int portNum;
    private String yourLine;
    private ServerSocket serverSocket;
    private ArrayList<ClientHandler> clientArray;

    public Server(String givenPort){
        clientArray = new ArrayList();
        Scanner scan = new Scanner(System.in);
        try {
            if (givenPort.equals("")){
                System.out.print("Enter port: ");
                portNum = Integer.parseInt(scan.nextLine());
            }else{
                portNum = Integer.parseInt(givenPort);
            }
        } catch (NumberFormatException e){
            System.err.println("Invalid port. Using default of 4267");
            portNum = 4267;
        }
        try {
            //Listens for socket
            System.out.println("Hosting chat server on port " + portNum);
            serverSocket = new ServerSocket(portNum);
            AcceptThread acceptThread = new AcceptThread(serverSocket, this);
            acceptThread.start();

            while(true){
                yourLine = scan.nextLine();
                System.out.println("Server: " + yourLine);

                broadcastToClients("Server: " + yourLine, "server");

                if (yourLine.equals("/close")){
                    break;
                }
            }
            acceptThread.running = false;
            for (int i = 0; i < clientArray.size(); i++){
                clientArray.get(i).stopRunning();
            }
            System.out.println("Closing server");
            serverSocket.close();
        }catch(Exception e){
            System.out.println("Error by the way");
            e.printStackTrace();
        }
    }

    public void addClientHandler(ClientHandler clientHandler){
        clientArray.add(clientHandler);
    }

    public void broadcastToClients(String text, String source){
        for (ClientHandler ch : clientArray){
            if(!ch.username.equals(source)){
                ch.send(text);
            } 
        }

        if (!source.equals("server")){
            System.out.println(text);
        }
    }

    public static void main(String[] args){
        if (args.length == 1){
            new Server(args[0]);
        }else{
            new Server("");
        }
    }

}