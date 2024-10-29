package client;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client2024 extends JFrame implements ActionListener, KeyListener {
    private static final long serialVersionUID = 2L;

    // Login GUI 변수
    private JFrame loginGUI = new JFrame("Login"); // 11-19
    private JPanel loginJpanel;
    private JTextField ip_tf;
    private JTextField port_tf;
    private JTextField id_tf; // 클라이언트 ID
    private JLabel img_Label;
    private JButton loginBtn; // 11-13
    private String serverIP; // 11-14
    private int serverPort; // 11-14
    private String clientID; // 11-20 //클라이언트 ID

    // Main GUI 변수
    private JPanel contentPane;
    private JList<String> clientJlist = new JList(); // 전체 접속자 명단, 첫번째는 자기 자신 //11-20
    private JList<String> roomJlist = new JList(); // 11-21
    private JTextField msgTf;
    private JTextArea chatArea = new JTextArea(); // 채팅창 변수
    private JButton noteBtn = new JButton("쪽지 보내기"); // 11-27
    private JButton joinRoomBtn = new JButton("채팅방 참여");
    private JButton createRoomBtn = new JButton("방 만들기");
    private JButton sendBtn = new JButton("전송");
    private JButton exitRoomBtn = new JButton("탈퇴");
    private JButton clientExitBtn = new JButton("채팅종료");

    // 클라이언트 관리
    private Vector<String> clientVC = new Vector<>(); // 11-20
    private Vector<String> roomVC = new Vector<>(); // 11-21
    private String myRoom = ""; // 내가 참여한 채팅방 11-28

    // network 변수
    private Socket socket; // 11-14
    private DataInputStream dis;
    private DataOutputStream dos;

    // 기타
    StringTokenizer st;
    // private boolean stopped = false;
    private boolean socketEstablished = false;

    public Client2024() {
        initializeLoginGUI();
        initializeMainGUI();
        addActionListeners(); // 11-13
    }

    void initializeLoginGUI() {
        loginGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 1
        loginGUI.setBounds(100, 100, 385, 541); // 1
        loginJpanel = new JPanel();
        loginJpanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        loginGUI.setContentPane(loginJpanel); // 1
        loginJpanel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Server IP");
        lblNewLabel.setFont(new Font("굴림", Font.BOLD, 20));
        lblNewLabel.setBounds(12, 244, 113, 31);
        loginJpanel.add(lblNewLabel);

        ip_tf = new JTextField();
        ip_tf.setBounds(135, 245, 221, 33);
        loginJpanel.add(ip_tf);
        ip_tf.setColumns(10);

        JLabel lblServerPort = new JLabel("Server Port");
        lblServerPort.setFont(new Font("굴림", Font.BOLD, 20));
        lblServerPort.setBounds(12, 314, 113, 31);
        loginJpanel.add(lblServerPort);

        port_tf = new JTextField();
        port_tf.setColumns(10);
        port_tf.setBounds(135, 312, 221, 33);
        loginJpanel.add(port_tf);

        JLabel lblId = new JLabel("ID");
        lblId.setFont(new Font("굴림", Font.BOLD, 20));
        lblId.setBounds(12, 376, 113, 31);
        loginJpanel.add(lblId);

        id_tf = new JTextField();
        id_tf.setColumns(10);
        id_tf.setBounds(135, 377, 221, 33);
        loginJpanel.add(id_tf);

        loginBtn = new JButton("Login"); // 11-13
        loginBtn.setFont(new Font("굴림", Font.BOLD, 20));
        loginBtn.setBounds(12, 450, 344, 44);
        loginJpanel.add(loginBtn);

        try {
            ImageIcon im = new ImageIcon("images/다람쥐.jpg");
            img_Label = new JLabel(im);
            img_Label.setBounds(12, 23, 344, 154);
            loginJpanel.add(img_Label);
        } catch (Exception e) {
            // 이미지 로딩에 실패한 경우 예외 처리
            JOptionPane.showMessageDialog(this, "image.", "Error", JOptionPane.ERROR_MESSAGE);
            // 이 부분에 적절한 오류 처리를 추가하세요.
        }

        loginGUI.setVisible(true); // 1
    }

    void initializeMainGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(600, 100, 510, 460);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel 접속자 = new JLabel("전체 접속자");
        접속자.setBounds(12, 20, 73, 15);
        contentPane.add(접속자);
        
		clientJlist.setBounds(12,45 ,108 ,107); 
		contentPane.add(clientJlist); 

		clientExitBtn.setBounds(12 ,162 ,108 ,23); 
		contentPane.add(clientExitBtn); 
		
		noteBtn.setBounds(12 ,192 ,108 ,23); 
		contentPane.add(noteBtn); 

		JLabel 채팅방 = new JLabel("채팅방목록"); 
		채팅방.setBounds(12 ,225 ,97 ,15); 
		contentPane.add(채팅방); 
		
		roomJlist.setBounds(12 ,240 ,108 ,107); 
		contentPane.add(roomJlist); 

		joinRoomBtn.setBounds(6 ,357 ,60 ,23); 
		contentPane.add(joinRoomBtn); 
		joinRoomBtn.setEnabled(false); 
		
		exitRoomBtn.setBounds(68 ,357 ,60 ,23); 
		contentPane.add(exitRoomBtn); 
		exitRoomBtn.setEnabled(false); 
		
		createRoomBtn.setBounds(12 ,386 ,108 ,23); 
		contentPane.add(createRoomBtn); 

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(142 ,16 ,340 ,363); 
		contentPane.add(scrollPane); 
		scrollPane.setColumnHeaderView(chatArea); 
		chatArea.setEditable(false);

		msgTf = new JTextField(); 
		msgTf.setBounds(144 ,387 ,268 ,21); 
		contentPane.add(msgTf); 
		msgTf.setColumns(10); 
		msgTf.setEditable(false); 
		
		sendBtn.setBounds(412 ,386 ,70 ,23); 
		contentPane.add(sendBtn); 
		sendBtn.setEnabled(false); 
		
		this.setVisible(false);
    }

	void addActionListeners() { // ** Simplified action listener registration for clarity
	    loginBtn.addActionListener(this);
	    noteBtn.addActionListener(this); // ** Added action listener for note button.
	    joinRoomBtn.addActionListener(this);
	    createRoomBtn.addActionListener(this);
	    sendBtn.addActionListener(this);
	    exitRoomBtn.addActionListener(this); // 채팅방탈퇴 리스너
	    msgTf.addKeyListener(this); // 메시지 전송 리스너.
	    clientExitBtn.addActionListener(this); // 채팅 종료 리스너.
	}

	public void connectToServer() {
	    if (!socketEstablished) {
	        try {
	            serverIP = ip_tf.getText().trim();
	            serverPort = Integer.parseInt(port_tf.getText().trim());
	            socket = new Socket(serverIP, serverPort);

	            dis = new DataInputStream(socket.getInputStream()); 
	            dos = new DataOutputStream(socket.getOutputStream());
	            socketEstablished = true;

	            sendMyClientID(); // 클라이언트 ID 전송.
	        } catch (NumberFormatException e) {
	            JOptionPane.showMessageDialog(this,"잘못된 포트 번호입니다.","오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
	        } catch (IOException e) {
	            JOptionPane.showMessageDialog(this,"서버에 연결할 수 없습니다.","연결 오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
	        }
	    }
	}

	void sendMyClientID() {
	    clientID = id_tf.getText().trim(); 
	    sendMsg(clientID);

	    try {
	        String msg = dis.readUTF();
	        if ("DuplicateClientID".equals(msg)) {
	            JOptionPane.showMessageDialog(this,"이미 사용중인 ID입니다.","중복 ID", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
	            id_tf.setText("");
	            id_tf.requestFocus();

	        } else {
	            InitializeAndRecvMsg();
	        }
	    } catch (IOException e) {
	        JOptionPane.showMessageDialog(this,"서버로부터 응답을 받는 중 오류가 발생했습니다.","통신 오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
	    }
	}

	void InitializeAndRecvMsg() {
	    this.setVisible(true);
	    this.loginGUI.setVisible(false);

	    clientVC.add(clientID);
//      clientJlist.setListData(clientVC);// JLIST로 화면에 출력.
	    setTitle(clientID);// ** Set title to the client's ID.

	    new Thread(() -> {  
	        try {
	            String msg;  
	            while (true) {  
	                msg = dis.readUTF();  
	                System.out.println("서버로부터 받은 메시지: " + msg);
	                recvMsg(msg);
	            }
	        } catch (IOException e) {  
	            handleServerShutdown();  
	        }  
	    }).start();
	}

	void sendMsg(String msg) {  
	    try {  
	        dos.writeUTF(msg);  
	    } catch (IOException e) {  
	        JOptionPane.showMessageDialog(this,"메시지 전송 중 오류가 발생했습니다.","오류", JOptionPane.ERROR_MESSAGE);// ** Improved error message for clarity.
	    }  
	}

	void recvMsg(String msg) { // ** Process received messages based on protocol.
	    st = new StringTokenizer(msg,"/");
	    String protocol = st.nextToken();
	    String message = st.nextToken();

	    switch (protocol) {
	        case "NewClient":
	        case "OldClient":
	            addClientToList(message);// 서버가 등록할 정보만 전송한다.
	            break;

	        case "Note":// 쪽지 처리.
	            String note=st.nextToken();
	            showMessageBox(note,message + "님으로부터 쪽지");
	            break;

	        case "CreateRoom":
	            handleCreateRoom(message);
	            break;

	        case "NewRoom":
	        case "OldRoom":// 방 목록 업데이트.
	            handleAddRoomJlist(message);
	            break;

	        case "CreateRoomFail":// 방 생성 실패 처리.
	            showErrorMessage("Create Room Fail","알림");// ** Improved error message for clarity.
	            break;

	        case "JoinRoomMsg":// 방 참여 메시지 처리.
	            String msg2=st.nextToken();
	            appendToChatArea(message + ": " + msg2);// 메시지 추가.
	            break;

	        case "JoinRoom":
	            handleJoinRoom(message);
	            break;

	        case "SendMsg":
	            String chatMsg=st.nextToken();
	            appendToChatArea(message + "님이 전송: " + chatMsg);// 메시지 추가.
	            break;

	        case "ClientJlistUpdate":// 클라이언트 목록 업데이트 처리.
                xxxupdateClientJlist();// ** Update client list display.

            case "RoomJlistUpdate":// 방 목록 업데이트 처리.
                System.out.println("Updating Room List");
                xxxupdateRoomJlist();// ** Update room list display.

            case "ClientExit":
                removeClientFromJlist(message);// 클라이언트 퇴장 처리.
                break;

            case "ServerShutdown":
                handleServerShutdown();// 서버 종료 처리.
                break;

            case "RoomOut":
                handleRoomOut(message);// 방 퇴장 처리.
                break;

            case "ExitRoomMsg":
                String exitMsg=st.nextToken();
                appendToChatArea(message + ": " + exitMsg);// 메시지 추가.
                break;

            default:
                break;// 처리되지 않은 프로토콜에 대한 기본 처리.
	    }
	}

	private void showMessageBox(String msg,String title) {  
	    JOptionPane.showMessageDialog(null,msg,title,JOptionPane.CLOSED_OPTION);// 메시지 박스 표시.  
	}

	private void addClientToList(String clientID) {  
	    clientVC.add(clientID);// 클라이언트 목록에 추가.  
//      clientJlist.setListData(clientVC);// JLIST로 화면에 출력.  
   }

	private void xxxupdateClientJlist() {  
    	clientJlist.setListData(clientVC);// 클라이언트 목록 업데이트.  
   }

	private void handleCreateRoom(String roomName) {  
    	myRoom=roomName;// 현재 방 이름 설정.   
    	joinRoomBtn.setEnabled(false);// 참여 버튼 비활성화.   
    	createRoomBtn.setEnabled(false);// 방 생성 버튼 비활성화.   
    	exitRoomBtn.setEnabled(true);// 퇴장 버튼 활성화.   
    	msgTf.setEditable(true);// 메시지 입력 가능.   
    	sendBtn.setEnabled(true);// 전송 버튼 활성화.   
    	setTitle("사용자: "+clientID+" 채팅방: "+myRoom);// 타이틀 업데이트.   
    	appendToChatArea(clientID+"님이 "+myRoom+" 생성 및 가입\n");// 채팅창에 알림 추가.   
   }

	private void handleAddRoomJlist(String roomName) {  
    	if(myRoom.equals("")){   
        	joinRoomBtn.setEnabled(true);// 방이 없을 때 참여 버튼 활성화.   
     }   
    	roomVC.add(roomName);// 방 이름 추가.   
    	roomJlist.setListData(roomVC);// 방 목록 업데이트.   
   }

	private void xxxupdateRoomJlist() {  
    	roomJlist.setListData(roomVC);// 방 목록 업데이트.   
   }

	private void handleJoinRoom(String roomName) {  
    	myRoom=roomName;// 현재 방 이름 설정.   
    	joinRoomBtn.setEnabled(false);// 참여 버튼 비활성화.   
    	createRoomBtn.setEnabled(false);// 방 생성 버튼 비활성화.   
    	exitRoomBtn.setEnabled(true);// 퇴장 버튼 활성화.   
    	msgTf.setEditable(true);// 메시지 입력 가능.   
    	sendBtn.setEnabled(true);// 전송 버튼 활성화.   
    	setTitle("사용자: "+clientID+" 채팅방: "+myRoom);// 타이틀 업데이트.   
    	appendToChatArea(clientID+"님이 "+myRoom+" join.\n");// 채팅창에 알림 추가.   
    	showInfoMessage("Join Room success","알림");// 성공 메시지 표시.   
   }

	private void removeClientFromJlist(String clientID) {  
     	clientVC.remove(clientID);// 클라이언트 목록에서 제거.  
   }

	private void handleServerShutdown() {  
     	try {  
         	socket.close();// 소켓 닫기.  
         	clientVC.removeAllElements();// 클라이언트 목록 초기화.  
         	if(!myRoom.isEmpty()) {  
             	roomVC.removeAllElements();// 방 목록 초기화.  
          }  
      } catch(IOException e) {  
          e.printStackTrace();  
      }  
     	System.exit(0);// 애플리케이션 종료.  
   }

	private void handleRoomOut(String roomName) {  
     	roomVC.remove(roomName);// 지정된 방 제거.  
     	if(roomVC.isEmpty()) {   
         	joinRoomBtn.setEnabled(false);// 방이 없을 때 참여 버튼 비활성화.   
      }   
     	exitRoomBtn.setEnabled(false);// 퇴장 버튼 비활성화.   
   }

	private void showErrorMessage(String message,String title) {   
     	JOptionPane.showMessageDialog(null,message,title,JOptionPane.ERROR_MESSAGE);// 오류 메시지 표시.   
   }

	private void appendToChatArea(String message) {   
      chatArea.append(message+"\n");// 채팅창에 메시지 추가.   
   }

	private void showInfoMessage(String message,String title) {    
     	JOptionPane.showMessageDialog(null,message,title,JOptionPane.INFORMATION_MESSAGE);// 정보 메시지 표시.   
   }

   @Override    
   public void actionPerformed(ActionEvent e) {    
     	if(e.getSource()==loginBtn){    
         	System.out.println("login button clicked");    
         	connectToServer();    
      } else if(e.getSource()==noteBtn){    
         	System.out.println("note button clicked");    
         	handleNoteSendButtonClick();    
      } else if(e.getSource()==createRoomBtn){    
         	handleCreateRoomButtonClick();    
      } else if(e.getSource()==joinRoomBtn){    
         	handleJoinRoomButtonClick();    
      } else if(e.getSource()==sendBtn){    
         	handleSendButtonClick();    
      } else if(e.getSource()==clientExitBtn){    
         	handleClientExitButtonClick();    
      } else if(e.getSource()==exitRoomBtn){    
         	System.out.println("Exit Room Button Clicked");    
         	handleExitRoomButtonClick();    
      }    
   }

	public void handleNoteSendButtonClick() {   
     	System.out.println("note button clicked");   
     	String dstClient=(String)clientJlist.getSelectedValue();   

     	String note=JOptionPane.showInputDialog("보낼 메시지");   
     	if(note!=null){   
         	sendMsg("Note/"+dstClient+"/"+note);   
         	System.out.println("receiver: "+dstClient+" | 전송 노트: "+note);   
      }   
   }

	private void handleCreateRoomButtonClick() {
	    System.out.println("createRoomBtn clicked");

	    String roomName = JOptionPane.showInputDialog("Enter Room Name:"); // ** Prompt user for room name.
	    if (roomName == null || roomName.trim().isEmpty()) {
	        System.out.println("Room creation cancelled or no name entered"); // ** Log cancellation.
	        return; // ** Exit if no valid name is provided.
	    }
	    sendMsg("CreateRoom/" + roomName.trim()); // ** Send room creation request to server.
	}

	private void handleJoinRoomButtonClick() { // ** Method to handle joining a room.
	    System.out.println("joinRoomBtn clicked");
	    String roomName = (String) roomJlist.getSelectedValue(); // ** Get selected room name.
	    if (roomName != null) {
	        sendMsg("JoinRoom/" + roomName); // ** Send join request to server.
	    }
	}

	private void handleSendButtonClick() { // ** Method to send messages.
	    if (!myRoom.isEmpty()) { // ** Ensure user is in a room before sending.
	        sendMsg("SendMsg/" + myRoom + "/" + msgTf.getText().trim()); // ** Send message to server.

	        msgTf.setText(""); // ** Clear input field after sending.
	        msgTf.requestFocus(); // ** Set focus back to input field.
	    }
	}

	private void handleClientExitButtonClick() { // ** Handle client exit action.
	    if (!myRoom.isEmpty()) { // ** Check if user is in a room before exiting.
	        sendMsg("ExitRoom/" + myRoom); // ** Notify server of exit from room.
	    }

	    sendMsg("ClientExit/Bye"); // ** Notify server of client exit.

	    clientVC.removeAllElements(); // ** Clear client list.

	    if (!myRoom.isEmpty()) { 
	        roomVC.removeAllElements(); // ** Clear room list if in a room.
	        myRoom = ""; // ** Reset current room info.
	    }

	    closeSocket(); // ** Close socket connection properly.
	    System.exit(0); // ** Exit application.
	}

	private void closeSocket() { // ** Method to close socket and streams safely.
	    try {
	        if (dos != null) {
	            dos.close(); // ** Close output stream.
	        }
	        if (dis != null) {
	            dis.close(); // ** Close input stream.
	        }
	        if (socket != null) {
	            socket.close(); // ** Close socket connection.
	        }
	    } catch (IOException e) {
	        e.printStackTrace(); // ** Log any exceptions during closing process.
	    }
	}

	private void handleExitRoomButtonClick() { 
	    System.out.println("exitRoomBtn clicked"); 

	    sendMsg("ExitRoom/" + myRoom); // ** Notify server of exit from current room.

	    myRoom = ""; // ** Reset current room info.

	    exitRoomBtn.setEnabled(false); 
	    joinRoomBtn.setEnabled(roomVC.size() > 0); 
	    createRoomBtn.setEnabled(true); 
	    msgTf.setEditable(false); 
	    sendBtn.setEnabled(false); 

	    setTitle("사용자: " + clientID); // ** Update title to reflect client ID only.
	}

	public void keyPressed(KeyEvent e) { 
	}

	public void keyReleased(KeyEvent e) { 
	    if (e.getKeyCode() == KeyEvent.VK_ENTER) { 
	        if (!myRoom.isEmpty()) { 
	            sendMsg("SendMsg/" + myRoom + "/" + msgTf.getText().trim()); 
	            msgTf.setText(""); 
	            msgTf.requestFocus(); 
	        } 
	    } 
	}

	public void keyTyped(KeyEvent e) { 
	}

	public static void main(String[] args) { 
	    new Client2024(); // ** Start the client application. 
	}
}