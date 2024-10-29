package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class Server1 extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JButton stopBtn;
	private JButton startBtn;
	private JTextField port_tf;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	public Server1() {
		initializeGUI(); // GUI 초기화 메서드 호출
		setupActionListeners();

	}

	public void initializeGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 330, 350);

		setLayout(null);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 296, 195);
		add(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JLabel lblNewLabel = new JLabel("포트 번호");
		lblNewLabel.setBounds(12, 232, 64, 15);
		;
		add(lblNewLabel);

		port_tf = new JTextField();
		port_tf.setBounds(99, 229, 209, 21);
		add(port_tf);
		port_tf.setColumns(10);

		startBtn = new JButton("서버 시작");
		startBtn.setBounds(12, 260, 130, 23);
		add(startBtn);

		stopBtn = new JButton("서버 중지");
		stopBtn.setBounds(178, 260, 130, 23);
		add(stopBtn);

		setVisible(true);
	}

	public void setupActionListeners() {
		startBtn.addActionListener(this);
		stopBtn.addActionListener(this);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == startBtn) {
			System.out.println("서버 실행");
		} else if (e.getSource() == stopBtn) {
			System.out.println("서버 중지");
		}

	}

	public static void main(String[] args) {

		new Server1();

	}
}