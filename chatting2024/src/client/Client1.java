package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class Client1 extends JFrame implements ActionListener{
	
	//login GUI
	private JFrame loginGUI = new JFrame("Login");
	private JPanel loginJpanel;
	private JTextField serverIP_tf;
	private JTextField serverPort_tf;
	private JTextField clientID_tf;
	private JButton loginBtn;
	
	//main GUI
	private JPanel contentPane;
	private JButton noteBtn;
	private JTextArea chatArea;
	private JButton joinRoomBtn;
	private JButton createRoomBtn;
	private JList<String> roomJlist;
	private JList<String> clientJlist;
	private JTextField msg_tf;
	JButton sendBtn;


	public Client1() {
		// TODO Auto-generated constructor stub
		initLoginGUI();
		initMainGUI();
		addActionListeners();
	}
	
	void initLoginGUI() {
		loginGUI.setLayout(null);
		loginGUI.setBounds(100, 100, 310, 341);
        loginJpanel = new JPanel();
        loginJpanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        loginGUI.setContentPane(loginJpanel); // 1
        loginJpanel.setLayout(null);
        
		
		JLabel lblNewLabel = new JLabel("서버 IP");
		lblNewLabel.setBounds(12, 31, 50, 15);
		loginJpanel.add(lblNewLabel);
		
		serverIP_tf = new JTextField();
		serverIP_tf.setBounds(109, 25, 164, 21);
		loginJpanel.add(serverIP_tf);
		serverIP_tf.setColumns(10);
		
		JLabel lblPort = new JLabel("서버 port");
		lblPort.setBounds(12, 82, 50, 15);
		loginJpanel.add(lblPort);
		
		serverPort_tf = new JTextField();
		serverPort_tf.setColumns(10);
		serverPort_tf.setBounds(109, 76, 164, 21);
		loginJpanel.add(serverPort_tf);
		
		clientID_tf = new JTextField();
		clientID_tf.setColumns(10);
		clientID_tf.setBounds(109, 126, 164, 21);
		loginGUI.add(clientID_tf);
		
		JLabel lblId = new JLabel("클라이언트 ID");
		lblId.setBounds(12, 132, 85, 15);
		loginJpanel.add(lblId);
		
		loginBtn = new JButton("로그인");
		loginBtn.setBounds(12, 174, 261, 23);
		loginJpanel.add(loginBtn);
		
		
		loginGUI.setVisible(true);
	}
	
	void initMainGUI() {
		setBounds(100, 100, 500, 445);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("현재 접속자");
		lblNewLabel.setBounds(12, 10, 87, 15);
		contentPane.add(lblNewLabel);
		
		roomJlist = new JList();
		roomJlist.setBounds(12, 222, 118, 112);
		contentPane.add(roomJlist);
		
		noteBtn = new JButton("쪽지 전송");
		noteBtn.setBounds(12, 156, 118, 23);
		contentPane.add(noteBtn);
		
		JLabel lblNewLabel_1 = new JLabel("채팅방목록");
		lblNewLabel_1.setBounds(12, 197, 87, 15);
		contentPane.add(lblNewLabel_1);
		
		clientJlist = new JList();
		clientJlist.setBounds(12, 35, 118, 112);
		contentPane.add(clientJlist);
		
		createRoomBtn = new JButton("방만들");
		createRoomBtn.setBounds(12, 375, 118, 23);
		contentPane.add(createRoomBtn);
		
		joinRoomBtn = new JButton("채팅방참여");
		joinRoomBtn.setBounds(12, 344, 118, 23);
		contentPane.add(joinRoomBtn);
		
		chatArea = new JTextArea();
		chatArea.setBounds(138, 5, 336, 359);
		contentPane.add(chatArea);
		
		msg_tf = new JTextField();
		msg_tf.setBounds(138, 376, 267, 21);
		contentPane.add(msg_tf);
		msg_tf.setColumns(10);
		
		sendBtn = new JButton("전송");
		sendBtn.setBounds(410, 375, 64, 23);
		contentPane.add(sendBtn);
		
		this.setVisible(true);
	}
	
	public void addActionListeners() {
		loginBtn.addActionListener(this);
		noteBtn.addActionListener(this);
		joinRoomBtn.addActionListener(this);
		createRoomBtn.addActionListener(this);
		sendBtn.addActionListener(this);
	};
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==loginBtn) {
			System.out.println("서버 접속");
		} else if (e.getSource()==noteBtn) {
			System.out.println("쪽지 보내기");
		} else if(e.getSource()==joinRoomBtn) {
			System.out.println("채팅방 가입");
		} else if(e.getSource()==createRoomBtn) {
			System.out.println("채팅방 생성");
		} else if(e.getSource()==sendBtn) {
			System.out.println("메시지 전송");
		}
		
	}	
	
	public static void main(String[] args) {
		new Client1();
		
	}


}
