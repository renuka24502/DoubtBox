package clientChat;

import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * * @since  2017-11-19
 * @author Renuka
 * @version 1.0
 * <h1>Client Side</h1>
 * ClientSide program implements an application that creates the user interface for the Student
 * It uses Socket programming in order to establish the connection between the client and the server
 * The Client requires the IP Address of the Teacher in order to start the communication.
 * Hence through this the connection is establish between the client and Server
 */


public class ClientSide extends JFrame{

    /** Creates a new instance of ClientSide */
    private JTextArea ChatBox=new JTextArea(10,45);
    private JScrollPane myChatHistory=new JScrollPane(ChatBox,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    private JTextArea UserText = new JTextArea(5,47);
    private JScrollPane myUserHistory=new JScrollPane(UserText,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JButton Send = new JButton("Send");
    private JButton Start = new JButton("Connect");
    private Client ChatClient; // client is a class which extends Thread
    private ReadThread myRead=new ReadThread(); //ReadThread is a class used to read messages
    private JTextField Server=new JTextField(20);
    private JLabel myLabel=new JLabel("Server Name :");
    private JTextField User=new JTextField(20);
    private String ServerName;
    private String UserName;
    private JLabel chatHistory = new JLabel("Chat History");
    private JLabel chatBox = new JLabel("Chat Box : ");
    
    Font myFont = new Font("Aerial",Font.PLAIN,20);
    Font myFont1 = new Font("Aerial",Font.PLAIN,15);


    public ClientSide() {
    /**
     * This is the constructor of the class ClientSide which do the basic initialization such as
     * set title,size,layout,hgap,vgap,set background and font and create the front end by adding
     * several components. 
     */
        setResizable(false);
        setTitle("Client");
        setSize(700,550);
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
        chatHistory.setFont(myFont);
        chatBox.setFont(myFont);
        myLabel.setFont(myFont);
        Server.setFont(myFont);
        User.setFont(myFont);
        
        ChatBox.setEditable(false);
        
        cp.add(chatHistory);
        cp.add(myChatHistory);
        cp.add(chatBox);
        cp.add(myUserHistory);
        cp.add(Send);
        cp.add(Start);
        cp.add(myLabel);
        cp.add(Server);
        cp.add(User);
        /**
         * addActionListener implements the abstract method actionPerformed
         */
        Send.addActionListener(new ActionListener()
        {
        	
       /**
        * The method actionPerformed is an abstract method
        * @param ActionEvent This is the parameter of the method actionPerformed,
        *  it is an object of ActionEvent
        * @return nothing
        * This method elaborate what is done after clicking the buttons
        * UserText takes the input from the client and this message is send to the server by clicking on send
        */
        	
            public void actionPerformed(ActionEvent e) {
                if(ChatClient!=null) {

                    if(!(UserText.getText() == ""))
                    {
                    System.out.println(UserText.getText());
                    ChatClient.SendMassage(UserText.getText());
                    ChatBox.append("\nYou: " + UserText.getText());
                    }

                    UserText.setText("");
                }
            }
        });
        Start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatClient=new Client();
                ChatClient.start();
            }
        });
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);


    }

    /**
     *@author renuka_pg-2_2601
     *@version 1.0
     *@since 2017
     *<h1>Client</h1>
     *Client program implements Socket Programming
     *Client class extends the Thread class
     *To facilitate the communication between the client and server
     * 
     */


    public class Client extends Thread {
        private static final int PORT=9999;
        private LinkedList Clients;
        private ByteBuffer ReadBuffer;
        private ByteBuffer writeBuffer;
        private SocketChannel SChan;
        private Selector ReadSelector;
        private CharsetDecoder asciiDecoder;

        /**
         * This is the constructor of the class Client
         * It includes the various initialization of the object required by the this class 
         */
        public Client() {
            Clients=new LinkedList();
            ReadBuffer=ByteBuffer.allocateDirect(300);
            writeBuffer=ByteBuffer.allocateDirect(300);
            asciiDecoder = Charset.forName( "US-ASCII").newDecoder();
        }
        /**
         * Since the class client extends Thread hence it must override the run() method
         * This method establish the connection between client and server by calling method Connect
         * After establishing the connection it starts reading messages
         */

        public void run() {

            ServerName=Server.getText();
            System.out.println(ServerName);
            UserName=User.getText();

            Connect(ServerName);
            myRead.start();
            while (true) {

                ReadMassage();

                try {
                    Thread.sleep(30);
                } catch (InterruptedException ie){
                }
            }

        }
        
        /**
         * Method Connect establish the connection between server and client
         * Establish the connection by using PORT number of the server
         * It sets the configuration blocking false in order to avoid blocking of communication
         * @param hostname
         */        
        public void Connect(String hostname) {
            try {
                ReadSelector = Selector.open();
                InetAddress addr = InetAddress.getByName(hostname);
                SChan = SocketChannel.open(new InetSocketAddress(addr, PORT));
                SChan.configureBlocking(false);

                SChan.register(ReadSelector, SelectionKey.OP_READ, new StringBuffer());
            }

            catch (Exception e) {
            }
        }
        
        /**
         * This is SendMessage method which is used to send messages to the server
         * @param messg
         * @return nothing
         */
        
        public void SendMassage(String messg) {
            prepareBuffer(UserName+" says: "+messg);
            channelWrite(SChan);
        }
        
        
        /**
         * The method prepareBuffer is used to write messages into the buffer
         * @param massg
         * @return nothing
         */


        public void prepareBuffer(String massg) {
            writeBuffer.clear();
            writeBuffer.put(massg.getBytes());
            writeBuffer.putChar('\n');
            writeBuffer.flip();
        }
        
        /**
         * this is the method channelWrite is use to write into the buffer whatever the clients types
         * @param client
         * @return nothing
         */

        public void channelWrite(SocketChannel client) {
            long num=0;
            long len=writeBuffer.remaining();
            while(num!=len) {
                try {
                    num+=SChan.write(writeBuffer);

                    Thread.sleep(5);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch(InterruptedException ex) {

                }

            }
            writeBuffer.rewind();
        }
        
        /**
         * the method ReadMassage is used to read the message or respond given by the server
         * This method takes the input from server and store it into the buffer 
         * This buffer is then appended into the chatbox
         */
        public void ReadMassage() {

            try {

                ReadSelector.selectNow();

                Set readyKeys = ReadSelector.selectedKeys();

                Iterator i = readyKeys.iterator();

                while (i.hasNext()) {

                    SelectionKey key = (SelectionKey) i.next();
                    i.remove();
                    SocketChannel channel = (SocketChannel) key.channel();
                    ReadBuffer.clear();


                    long nbytes = channel.read(ReadBuffer);

                    if (nbytes == -1) {
                        ChatBox.append("You logged out !\n");
                        channel.close();
                    } else {

                        StringBuffer sb = (StringBuffer)key.attachment();
                        ReadBuffer.flip( );
                        String str = asciiDecoder.decode( ReadBuffer).toString( );
                        sb.append( str );
                        ReadBuffer.clear( );


                        String line = sb.toString();
                        if ((line.indexOf("\n") != -1) || (line.indexOf("\r") != -1)) {
                            line = line.trim();

                            ChatBox.append("> "+ line);
                            ChatBox.append(""+'\n');
                            sb.delete(0,sb.length());
                        }
                    }
                }
            } catch (IOException ioe) {
            } catch (Exception e) {
            }
        }
    }
    
    /**
     * 
     *@author renuka
     *@version 1.0
     *@since 2017-11-19
     *<h1> ReadThread </h1>
     *This class ReadThread extends Thread
     *This class overrides an abstract method run 
     *
     */
    
    class ReadThread extends Thread {
    	
    	/**
    	 * Method run is an abstract method 
    	 * This calls the method ReadMassage
    	 * @param nothing
    	 * @return nothing
    	 */
        public void run() {
            ChatClient.ReadMassage();
        }
    }
}