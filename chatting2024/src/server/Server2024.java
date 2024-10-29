package server;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class Server2024 extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane; 
    private JTextField port_tf; // 포트 번호 입력 필드
    private JTextArea textArea = new JTextArea(); // 서버 로그 출력
    private JButton startBtn = new JButton("서버 실행"); // 서버 시작 버튼
    private JButton stopBtn = new JButton("서버 중지"); // 서버 중지 버튼

    // 소켓 생성 및 연결 부분
    private ServerSocket ss; // 서버 소켓
    private Socket cs; // 클라이언트 소켓
    private int port = 12345; // 기본 포트 번호

    // 기타 변수 관리
    private Vector<ClientInfo> clientVC = new Vector<ClientInfo>(); // 클라이언트 정보 저장 벡터
    private Vector<RoomInfo> roomVC = new Vector<RoomInfo>(); // 방 정보 저장 벡터

    public Server2024() {
        initializeGUI(); // GUI 초기화 메서드 호출
        setupActionListeners(); // 버튼 리스너 설정
    }

    public void initializeGUI() {
        setTitle("Server Application"); // 프레임 제목 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 종료 시 프로그램 종료
        setBounds(30, 100, 321, 370); // 프레임 위치 및 크기 설정

        contentPane = new JPanel(); 
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5)); // 패널 테두리 설정
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout()); // 레이아웃 변경

        JPanel topPanel = new JPanel(); 
        contentPane.add(topPanel, BorderLayout.NORTH); // 상단 패널 추가

        JLabel lblNewLabel_2 = new JLabel("포트 번호"); 
        topPanel.add(lblNewLabel_2); // 포트 번호 레이블 추가

        port_tf = new JTextField(); 
        port_tf.setColumns(20); 
        topPanel.add(port_tf); // 포트 번호 입력 필드 추가

        JPanel bottomPanel = new JPanel(); 
        contentPane.add(bottomPanel, BorderLayout.SOUTH); // 하단 패널 추가

        startBtn.setBounds(12, 286, 138, 23); 
        bottomPanel.add(startBtn); // 서버 시작 버튼 추가

        stopBtn.setBounds(155, 286, 138, 23); 
        bottomPanel.add(stopBtn); 
        stopBtn.setEnabled(false); // ** Initially disable the stop button.

        JScrollPane scrollPane = new JScrollPane(); 
        contentPane.add(scrollPane, BorderLayout.CENTER); 

        textArea.setEditable(false); 
        scrollPane.setViewportView(textArea); // 로그 출력 영역 설정

        this.setVisible(true); // 화면 보이기
    }

    void setupActionListeners() { 
        startBtn.addActionListener(e -> startServer()); // ** Start server action.
        stopBtn.addActionListener(e -> stopServer()); // ** Stop server action.
    }

    @Override
    public void actionPerformed(ActionEvent e) { 
		if (e.getSource() == startBtn) { 
			startServer(); // ** Start server method call.
		} else if (e.getSource() == stopBtn) { 
			stopServer(); // ** Stop server method call.
		}
	}

    private void startServer() { 
		try { 
			port = Integer.parseInt(port_tf.getText().trim()); 
			ss = new ServerSocket(port); 
			textArea.append("서버가 포트 " + port + "에서 시작되었습니다.\n"); // ** Log server start message.
			startBtn.setEnabled(false); // ** Disable start button after starting the server.
			port_tf.setEditable(false); // ** Disable port input after starting.
			stopBtn.setEnabled(true); // ** Enable stop button after starting.
			waitForClientConnection(); // ** Wait for client connections.
		} catch (NumberFormatException e) { 
			textArea.append("잘못된 포트 번호입니다.\n"); // ** Log invalid port number error.
		} catch (IOException e) { 
			textArea.append("서버 시작 오류: " + e.getMessage() + "\n"); // ** Log server start error.
		}
	}

	private void stopServer() { 
	    for (ClientInfo c : clientVC) { 
	        c.sendMsg("ServerShutdown/Bye"); // ** Notify clients of server shutdown.
	        try { 
	            c.closeStreams(); // ** Close client streams.
	        } catch (IOException e) { 
	            e.printStackTrace(); // ** Log any exceptions during closing process.
	        }
	    }

	    try { 
	        if (ss != null && !ss.isClosed()) { 
	            ss.close(); // ** Close the server socket.
	        } 
	        roomVC.removeAllElements(); // ** Clear room information.
	    } catch (IOException e) { 
	        e.printStackTrace(); // ** Log any exceptions during closing process.
	    }

	    startBtn.setEnabled(true); // ** Enable start button again.
	    port_tf.setEditable(true); // ** Allow editing of the port field again.
	    stopBtn.setEnabled(false); // ** Disable stop button after stopping the server.
	}

	private void waitForClientConnection() {  
		new Thread(() -> {  
			try {  
				while (!ss.isClosed()) {  
					textArea.append("클라이언트 Socket 접속 대기중\n");  
					Socket clientSocket = ss.accept();  
					textArea.append("클라이언트 Socket 접속 완료\n");  

					ClientInfo client = new ClientInfo(clientSocket);  
					client.start();  
				}  
			} catch (IOException e) {  
				if (!ss.isClosed()) {  
					textArea.append("클라이언트 연결 수락 중 오류 발생: " + e.getMessage() + "\n");  
				}  
			}  
		}).start();  
	}

	class ClientInfo extends Thread {  
		private DataInputStream dis;  
		private DataOutputStream dos;  
		private Socket clientSocket;  
		private String clientID = "";  
		private String roomID = "";  

		public ClientInfo(Socket socket) {  
			try {  
				this.clientSocket = socket;  
				dis = new DataInputStream(clientSocket.getInputStream());  
				dos = new DataOutputStream(clientSocket.getOutputStream());			
				initNewClient();  
			} catch (IOException e) {  
				textArea.append("Error in communication: " + e.getMessage() + "\n");
			}
		}

	    public void run() {  
	    	try {  
	    		String msg = "";  
	    		while (true) {  
	    			msg = dis.readUTF();  
	    			recvMsg(msg);  
	    		}  
	    	} catch (IOException e) {  
	    		handleClientExitProtocol(); 
	    	}
	    }

	    private void initNewClient() {   
	    	while (true) {
	    		try {
	    			clientID = dis.readUTF(); 

	    			boolean isDuplicate = false;   
	    			for (int i = 0; i < clientVC.size(); i++) {
	    				ClientInfo c = clientVC.elementAt(i);
	    				if (c.clientID.equals(clientID)) {
	    					isDuplicate = true;
	    					break;
	    				}
	    			}

	    			if (isDuplicate) {
						sendMsg("DuplicateClientID");
					} else {
						sendMsg("GoodClientID");

						textArea.append("new Client: " + clientID + "\n");

						for (int i = 0; i < clientVC.size(); i++) {
							ClientInfo c = clientVC.elementAt(i);
							sendMsg("OldClient/" + c.clientID);
						}
						
						broadCast("NewClient/" + clientID);

						for (RoomInfo r : roomVC) {
							sendMsg("OldRoom/" + r.roomName);
						}

						sendMsg("RoomJlistUpdate/Update");
						clientVC.add(this);
						broadCast("ClientJlistUpdate/Update");
						break;
					}
	    		} catch (IOException e) {
	    			textArea.append("통신 중 오류 발생: " + e.getMessage() + "\n");
	    			break;
	    		}
	    	}
	    }

	    void sendMsg(String msg) {
	        try {
	            dos.writeUTF(msg);
	        } catch (IOException e) {
	            textArea.append("메시지 전송 오류: " + e.getMessage() + "\n");
	        }
	    }

	    public void recvMsg(String str) {
	        textArea.append(clientID + " 사용자로부터 수신한 메시지: " + str + "\n");
	        System.out.println(clientID + " 사용자로부터 수신한 메시지: " + str);
	        StringTokenizer st = new StringTokenizer(str, "/");
	        String protocol = st.nextToken();
	        String message = "";
	        if (st.hasMoreTokens()) {
	            message = st.nextToken();
	        }

	        switch (protocol) {
	            case "Note":
	                handleNoteProtocol(st, message);
	                break;
	            case "CreateRoom":
	                handleCreateRoomProtocol(message);
	                break;
	            case "JoinRoom":
	                handleJoinRoomProtocol(st, message);
	                break;
	            case "SendMsg":
	                handleSendMsgProtocol(st, message);
	                break;
	            case "ClientExit":
	                handleClientExitProtocol();
	                break;
	            case "ExitRoom":
	                handleExitRoomProtocol(message);
	                break;
	            default:
	                log("알 수 없는 프로토콜: " + protocol);
	                break;
	        }
	    }

	    private void handleNoteProtocol(StringTokenizer st, String recipientID) {
	        String note = st.nextToken();

	        for (ClientInfo c : clientVC) {
	            if (c.clientID.equals(recipientID)) {
	                c.sendMsg("Note/" + clientID + "/" + note);
	                break;
	            }
	        }
	    }

	    private void handleCreateRoomProtocol(String roomName) {

	        boolean roomExists = false;
	        for (RoomInfo r : roomVC) {
	            if (r.roomName.equals(roomName)) {
	                roomExists = true;
	                break;
	            }
	        }
	        if (roomExists) {
	            sendMsg("CreateRoomFail/OK");
	        } else {
	            RoomInfo r = new RoomInfo(roomName, this);
	            roomVC.add(r);
	            roomID = roomName;
	            sendMsg("CreateRoom/" + roomName);
	            broadCast("NewRoom/" + roomName);
	            broadCast("RoomJlistUpdate/Update");
	        }
	    }

	    private void handleJoinRoomProtocol(StringTokenizer st, String roomName) {
	        for (RoomInfo r : roomVC) {
	            if (r.roomName.equals(roomName)) {
	                r.broadcastRoomMsg("JoinRoomMsg/가입/***" + clientID + "님이 입장하셨습니다.********");
	                r.RoomClientVC.add(this);
	                roomID = roomName; 
	                sendMsg("JoinRoom/" + roomName);
	                break;
	            }
	        }
	    }

	    private void handleSendMsgProtocol(StringTokenizer st, String roomName) {
	        String sendMsg = st.nextToken();
	        for (RoomInfo r : roomVC) {
	            if (r.roomName.equals(roomName)) {
	                r.broadcastRoomMsg("SendMsg/" + clientID + "/" + sendMsg);
	            }
	        }
	    }

	    private void handleClientExitProtocol() {
	        try {
	            closeStreams();
	            clientVC.remove(this);
	            if (clientSocket != null && !clientSocket.isClosed()) {
	                clientSocket.close();
	                textArea.append(clientID + " Client Socket 종료.\n");
	            }

	            broadCast("ClientExit/" + clientID);
	            broadCast("ClientJlistUpdate/Update");

	        } catch (IOException e) {
	        	logError("사용자 로그아웃 중 오류 발생", e);
            }
       }

       private void handleExitRoomProtocol(String roomName) {
           roomID = roomName;
           log(clientID + " 사용자가 " + roomName + " 방에서 나감");

           for (RoomInfo r : roomVC) {
               if (r.roomName.equals(roomName)) {
                   r.broadcastRoomMsg("ExitRoomMsg/탈퇴/***" + clientID + "님이 채팅방에서 나갔습니다.********");
                   r.RoomClientVC.remove(this);
                   if (r.RoomClientVC.isEmpty()) {
                       roomVC.remove(r);
                       broadCast("RoomOut/" + roomName);
                       broadCast("RoomJlistUpdate/Update"); 
                   }
                   break;
               }
           }
       }

       private void broadCast(String str) {
           for (ClientInfo c : clientVC) {
               c.sendMsg(str);
           }
       }

       private void log(String message) {
           System.out.println(clientID + ": " + message);
       }

       private void logError(String message, Exception e) {
           System.err.println(clientID + ": " + message);
           e.printStackTrace();
       }

       public void closeStreams() throws IOException {
           if (dos != null) {
               dos.close();
           }
           if (dis != null) {
               dis.close();
           }
           if (cs != null && !cs.isClosed()) {
               cs.close();
               textArea.append(clientID + " Client Socket 종료.\n");
           }
       }
   }

   class RoomInfo {

       private String roomName;   //
       private Vector<ClientInfo> RoomClientVC;   //

       public RoomInfo(String name, ClientInfo c) {

           this.roomName=name;

           this.RoomClientVC=new Vector<ClientInfo>();

           this.RoomClientVC.add(c);

       }   

       public void broadcastRoomMsg(String message){   

           for(ClientInfo c: RoomClientVC){   

               c.sendMsg(message);

           }   

       }   

   }

   public static void main(String[] args) {

       new Server2024();

   }
}