package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import core.DownloadListener;
import core.Downloader;
import model.CompleteInfo;
import model.ProgressInfo;
import util.Utils;


public class MainFrame extends JFrame implements ActionListener, DownloadListener{
	
	private static final long serialVersionUID = 1L;

	private Downloader downloader;
	
	@Override
	public synchronized void onCancel() {
		infoArea.setText("");
		infoArea.append("任务已取消");
		progressBar.setValue(0);
		startButton.setEnabled(true);
		this.downloader = null;
	}
	
	@Override
	public synchronized void onProgress(ProgressInfo progressInfo) {
		StringBuilder msg = new StringBuilder(100);
		msg.append("总大小：").append(Utils.formatByte(progressInfo.getTotalBytes()));
		msg.append("\n").append("已下载：").append(Utils.formatByte(progressInfo.getDownloadedBytes()));
		msg.append("    已完成：").append(progressInfo.getProgress()).append("%\n");
		msg.append("下载速度：").append(Utils.formatByte(progressInfo.getSpeed())).append("/s\n");
		msg.append("已用时间：");
		msg.append(Utils.formatTime(progressInfo.getCostSeconds()));
		msg.append("\n");
		msg.append("剩余时间：");
		if (progressInfo.getRemainingSeconds() == -1) {
			msg.append("未知");
		} else {
			msg.append(Utils.formatTime(progressInfo.getRemainingSeconds()));
		}
		msg.append("\n");
		infoArea.setText(msg.toString());
		progressBar.setValue(progressInfo.getProgress());
	}

	@Override
	public synchronized void onError(String errorMsg) {
		infoArea.setText("下载失败！");
		infoArea.append("\n\n");
		infoArea.append("错误信息：\n");
		infoArea.append("--------------------------------\n");
		infoArea.append(errorMsg);
		startButton.setEnabled(true);
		this.downloader = null;
	}

	@Override
	public synchronized void onSuccess(CompleteInfo completeInfo) {
		StringBuilder msg = new StringBuilder(100);
		msg.append("下载完成！\n");
		msg.append("总大小：").append(Utils.formatByte(completeInfo.getBytes())).append("\n");
		msg.append("总耗时：").append(Utils.formatTime(completeInfo.getCost())).append("\n");
		msg.append("平均下载速度：").append(Utils.formatByte(completeInfo.getSpeed())).append("/s\n");
		infoArea.setText("");
		infoArea.append(msg.toString());
		startButton.setEnabled(true);
		this.downloader = null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == startButton) { //下载按钮，开始下载
			String path = pathField.getText().trim();
			String saveDir = fileField.getText().trim();
			int threadNum = (int) threadNumComboBox.getSelectedItem();
			startButton.setEnabled(false);
			//实例化一个下载器，并开始下载任务
			downloader = new Downloader(path, saveDir, threadNum, this);
			downloader.download();
			
		} else if (e.getSource() == chooseButton) { //浏览按钮，选择文件保存路径
			fileChooser = new JFileChooser(".");
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("选择文件夹");
			int ret = fileChooser.showOpenDialog(MainFrame.this);
			if (ret == JFileChooser.APPROVE_OPTION) {
				//文件夹路径
				fileField.setText(fileChooser.getSelectedFile().getAbsolutePath() + File.separator);
			}
			
		} else if (e.getSource() == cancelButton) { //取消按钮，取消下载
			if (downloader != null) {
				downloader.cancel();
			}
		}
	}
	
	public MainFrame() throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		this.setTitle("多线程下载");
		this.setSize(600, 350);
		this.setResizable(false);
		Container contentPanel = this.getContentPane();
		contentPanel.setLayout(new BorderLayout());
				
		infoArea = new JTextArea();
		infoArea.setLineWrap(true);
		infoArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(infoArea); 
		
		//分别设置水平和垂直滚动条自动出现 
		scroll.setHorizontalScrollBarPolicy( 
		JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
		scroll.setVerticalScrollBarPolicy( 
		JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, 100); 
		progressBar.setStringPainted(true);
		JPanel centerPanel = new JPanel(new BorderLayout(5, 2));
		centerPanel.add(progressBar, BorderLayout.NORTH);
		centerPanel.add(scroll,BorderLayout.CENTER);
		
		JLabel pathLabel = new JLabel("下载路径：");
		pathField = new JTextField("http://dldir1.qq.com/qqfile/qq/QQ2013/2013Beta5/6966/QQ2013Beta5.exe", 86);
		JPanel pathPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
		pathPanel.add(pathLabel);
		pathPanel.add(pathField);
		
		startButton = new JButton("下载");
		cancelButton = new JButton("取消");
		startButton.addActionListener(this);
		cancelButton.addActionListener(this);
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(startButton);
		buttonPanel.add(cancelButton);
		
		JLabel fileLabel = new JLabel("保存路径：");
		String currentDir = System.getProperty("user.dir");
		fileField = new JTextField(currentDir+File.separator,54);
		chooseButton = new JButton("浏览");
		chooseButton.addActionListener(this);
		
		JLabel threadNumLabel = new JLabel("    启用线程数：");
		Integer num[] = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
		threadNumComboBox = new JComboBox<Integer>(num);
		int processorCount = Runtime.getRuntime().availableProcessors(); //处理器核心数
		threadNumComboBox.setSelectedItem(Math.min(processorCount, 10));
		
		JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 0));
		filePanel.add(fileLabel);
		filePanel.add(fileField);
		filePanel.add(chooseButton);
		filePanel.add(threadNumLabel);
		filePanel.add(threadNumComboBox);
		
		JPanel northPanel = new JPanel(new GridLayout(3, 1, 0, 0));
		northPanel.add(pathPanel);
		northPanel.add(filePanel);
		northPanel.add(buttonPanel);
		
		contentPanel.add(northPanel,BorderLayout.NORTH);
		contentPanel.add(centerPanel,BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	
	private JTextArea infoArea;
	private JProgressBar progressBar; 
	private JTextField pathField;
	private JButton startButton;
	private JButton cancelButton;
	
	private JTextField fileField;
	private JButton chooseButton;
	private JFileChooser fileChooser;
	private JComboBox<Integer> threadNumComboBox;
}
