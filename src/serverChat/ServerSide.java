package serverChat;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <h1>Server Side</h1>
 * The server can be connected to multiple students window.
 * In this situation, messages can be sent to a specific student
 *  when the student selects a particular teacher.
 *
 *@since 2017-11-19
 * @author Kashish
 * @version 1.0
 */

        public class ServerSide extends JFrame{

	    /** Creates a new instance of ServerSide */
	    private JTextArea ChatBox=new JTextArea(12,55);
	    private JScrollPane myChatHistory=new JScrollPane(ChatBox,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
	            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	    private JTextArea UserText = new JTextArea(8,55);
	    private JScrollPane myUserHistory=new JScrollPane(UserText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
	            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	    private JButton Send = new JButton("Send");
	    private JButton Start = new JButton("Start Server!");
	    private server ChatServer;
	    private InetAddress ServerAddress ;
	    private JLabel doubtBox = new JLabel("DoubtBox");
	    private JLabel chatBox = new JLabel("Chat Box : ");
    
    Font myFont = new Font("Aerial",Font.PLAIN,20);
    Font myFont1 = new Font("Aerial",Font.PLAIN,15);
    

    public ServerSide()
    
    /**
     * this is the constructor of the ServerSide class which do the basic initialization such as
     * set title, size,layout,hgap.vgap,sets background,font and adds multiple components for 
     * enhancing the front-end.
     */
    
    {
        setTitle("Server");
        setResizable(false);
        setSize(750,580);
        Container cp=getContentPane();
        FlowLayout f = new FlowLayout();
        f.setHgap(10);
        f.setVgap(10);
        cp.setLayout(f);
        
        Send.setBackground(Color.pink);
        Start.setBackground(Color.pink);
        
        Send.setFont(myFont);
        Start.setFont(myFont);
        ChatBox.setFont(myFont1);
        ChatBox.setFont(myFont1);
        doubtBox.setFont(myFont);
        chatBox.setFont(myFont);
        
        
        cp.setBackground(Color.lightGray);
        cp.add(doubtBox);
        cp.add(myChatHistory);
        cp.add(chatBox);
        cp.add(myUserHistory);
        cp.add(Send);
        cp.add(Start);
        ChatBox.setEditable(false);


        /**
         * addActionListner implements the abstract method actionPerformed 
         */
        
        Start.addActionListener(new ActionListener() 
       
        {
        	
        /**
         * actionPerformed implements the abstract method.
         * @param ActionEvent This is the parameter of the method actionPerformed, it is an object 
         * of ActionEvent.
         * This method elaborate what is to be done after clicking the buttons
         * UserText takes the input from the server and 
         * this message is send to client by clicking the send button.
         */
            public void actionPerformed(ActionEvent e) {

                ChatServer=new server();
                ChatServer.start();

            }
        });
        Send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatServer.SendMassage(ServerAddress.getHostName()+" < Server > "+UserText.getText());
                UserText.setText("");
            }
        });


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
       
    }
    
    
/**
 *@since 2017-11-19
 *@author Kashish
 *@version 1.0
 * This is the server class which defines how the communication 
 * takes place between the teacher and the student by establishing the port number 
 * and creating the LinkedLists of the clients and creating the buffer for the storage for the communication
 * chat taken place between the student and the teacher.
 * 
 */

    public class server extends Thread {
        private static final int PORT=9999;
        private LinkedList Clients;
        private ByteBuffer ReadBuffer;
        private ByteBuffer WriteBuffer;
        public  ServerSocketChannel SSChan;
        private Selector ReaderSelector;
        private CharsetDecoder asciiDecoder;

        /**
         * This is the constructor of the Server class which do the 
         * basic initialization required by the class.
         */

        public server() {
            Clients=new LinkedList();
            ReadBuffer=ByteBuffer.allocateDirect(300);
            WriteBuffer=ByteBuffer.allocateDirect(300);
            asciiDecoder = Charset.forName( "US-ASCII").newDecoder();
        }

        
        /**
         * The InitServer opens the connection and looks that
         * there is no blocking in the path between the client and the server.
         * It takes the IP address of the localHost so that the connection can be established properly.
         */
        
        
        public void InitServer() {
            try {
                SSChan=ServerSocketChannel.open();
                SSChan.configureBlocking(false);
                ServerAddress=InetAddress.getLocalHost();
                System.out.println(ServerAddress.toString());

                SSChan.socket().bind(new InetSocketAddress(ServerAddress,PORT));

                ReaderSelector=Selector.open();//to select the channel
                ChatBox.setText(ServerAddress.getHostName()+"<Server> Started. \n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
        /**
         * It accepts the new connection which is requested by the clients as it calls the InitServer, 
         * calls the Init method.
         *calls the acceptNewConnection method
         */
        
        public void run() {
            InitServer();

            while(true) {
                acceptNewConnection();

                ReadMassage();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }


            }
        }
        
        /**
         * acceptNewConnection is a method which accepts and establishes the connection between the 
         * server and the client.
         * Sends the welcome message to the client side.
         */
        
        public void acceptNewConnection() {
            SocketChannel newClient;
            try {

                while ((newClient = SSChan.accept()) != null) {
                    ChatServer.addClient(newClient);

                    sendBroadcastMessage(newClient,"Login from: " +newClient.socket().getInetAddress());

                    SendMassage(newClient,ServerAddress.getHostName()+"<server> welcome you !\n Note :To exit" +
                            " from server write 'quit' .\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /**
         * addClient functions add clients , looks that there is no
         *  blocking between the client as well as server. 
         *  registers the client
         * @param newClient
         */
        
        public void addClient(SocketChannel newClient) {
            Clients.add(newClient);
            try {
                newClient.configureBlocking(false);
                newClient.register(ReaderSelector,SelectionKey.OP_READ,new StringBuffer());

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
       
        /**
         * SendMassage calss the 2 functions:
         * preparedBuffer() and channelWriteBuffer()
         * @param client
         * @param messg
         */
        
        public void SendMassage(SocketChannel client ,String messg) {
            prepareBuffer(messg);
            channelWrite(client);
        }
        
        /**
         * Sends the message to the client
         * @param massg
         */
        
        public void SendMassage(String massg) {
            if(Clients.size()>0) {
                for(int i=0;i<Clients.size();i++) {
                    SocketChannel client=(SocketChannel)Clients.get(i);
                    SendMassage(client,massg);
                }
            }
        }

        /**
         * prepareBuffer prepares the buffer for the messages.
         * @param massg
         */

        public void prepareBuffer(String massg) {
            WriteBuffer.clear();
            WriteBuffer.put(massg.getBytes());
            WriteBuffer.putChar('\n');
            WriteBuffer.flip();
        }
        
/**
 * This function stores the messages and send it to the client.
 * It writes the message to the channel
 * @param client
 */
        public void channelWrite(SocketChannel client) {
            long num=0;
            long len=WriteBuffer.remaining();
            while(num!=len) {
                try {
                    num+=client.write(WriteBuffer);

                    Thread.sleep(5);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch(InterruptedException ex) {

                }
            }
            WriteBuffer.rewind();
        }
    
        
        /**
         * This function stores the messages and send it to the client.
         * @param client
         * @param mesg
         */
    
        public void sendBroadcastMessage(SocketChannel client,String mesg) {
            prepareBuffer(mesg);
            Iterator i = Clients.iterator();
            while (i.hasNext()) {
                SocketChannel channel = (SocketChannel)i.next();
                if (channel != client) {
                    channelWrite(channel);
                }
            }
        }
        
        
        /**
         * This function reads the messages of the client.
         */
        
        
        public void ReadMassage() {
            try {

                ReaderSelector.selectNow();
                Set readkeys=ReaderSelector.selectedKeys();
                Iterator iter=readkeys.iterator();
                while(iter.hasNext()) {
                    SelectionKey key=(SelectionKey) iter.next();
                    iter.remove();

                    SocketChannel client=(SocketChannel)key.channel();
                    ReadBuffer.clear();

                    long num=client.read(ReadBuffer);

                    if(num==-1) {
                        client.close();
                        Clients.remove(client);
                        sendBroadcastMessage(client,"logout: " +
                                client.socket().getInetAddress());

                    } else {

                        StringBuffer str=(StringBuffer)key.attachment();
                        ReadBuffer.flip();
                        String data= asciiDecoder.decode(ReadBuffer).toString();
                        ReadBuffer.clear();

                        str.append(data);

                        String line = str.toString();
                        if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
                            line = line.trim();
                            System.out.println(line);

                            if (line.endsWith("quit")) {
                                client.close();

                                Clients.remove(client);

                                ChatBox.append("Logout: " + client.socket().getInetAddress());

                                sendBroadcastMessage(client,"Logout: "
                                        + client.socket().getInetAddress());
                                ChatBox.append(""+'\n');
                            } else {
                                ChatBox.append(client.socket().getInetAddress() + ": " + line);

                                sendBroadcastMessage(client,client.socket().getInetAddress()
                                + ": " + line);

                                ChatBox.append(""+'\n');

                                str.delete(0,str.length());
                            }
                        }
                    }
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}