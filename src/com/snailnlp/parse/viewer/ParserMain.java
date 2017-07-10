package com.snailnlp.parse.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileView;

import edu.stanford.nlp.ling.StringLabelFactory;
import edu.stanford.nlp.parser.ui.TreeJPanel;
import edu.stanford.nlp.swing.FontDetector;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;

public class ParserMain extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6952124478740951040L;
	
	
	static final int WIDTH = 940;
	static final int HEIGHT = 600;
	static int LocX = 100;
	static int LocY = 20;
	
	static final String title = "短语句法分析-查看工具";
	Image iconImage;
	
	JTextField smField;
	JButton parseBtn;
	JButton clearBtn;
	JButton saveAsPicBtn;
	//JTextArea parseResult;
	JPanel controlPanel;
	JPanel treeContainer;
	JLabel infoLabel;
	TreeJPanel treePanel;
	
	Tree nowTree;
	
	JFileChooser chooser;//文件选择
	
	public ParserMain(){
		this.setSize(WIDTH, HEIGHT);
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		LocX = (screenSize.width - WIDTH)/2;
		LocY = (screenSize.height - HEIGHT)/2;
		this.setLocation(LocX, LocY);
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		iconImage = toolkit.getImage("res/images/books.png");
		MediaTracker imageTracker = new MediaTracker(this);
		imageTracker.addImage(iconImage,1);
		try{
			imageTracker.checkID(1,true);
			imageTracker.waitForID(1);
		}catch(Exception exception){
			exception.printStackTrace();
		}
		this.setIconImage(iconImage);
		
		
		smField = new JTextField(60);	smField.setFont(new Font("宋体",Font.PLAIN,15));
		parseBtn = new JButton("开始"); parseBtn.setFont(new Font("宋体",Font.PLAIN,16));
		parseBtn.addActionListener(this);
		clearBtn = new JButton("清除"); clearBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		clearBtn.addActionListener(this);
		saveAsPicBtn = new JButton("导出图片"); saveAsPicBtn.setFont(new Font("宋体", Font.PLAIN, 16));
		saveAsPicBtn.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e)
					{
						SaveAsPicFile();		//如果被触发，则调用新建文件函数段
					}
				}
		);
		//parseResult = new JTextArea(); parseResult.setFont(new Font("宋体",Font.PLAIN,20));
		controlPanel = new JPanel();
		controlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
		controlPanel.add(smField);
		controlPanel.add(parseBtn);
		controlPanel.add(clearBtn);
//		controlPanel.add(saveAsPicBtn);

		treeContainer = new JPanel();
		treePanel = new TreeJPanel();
		java.util.List<Font> fonts = FontDetector.supportedFonts(FontDetector.CHINESE);
		Font font = new Font(fonts.get(0).getName(), Font.PLAIN, 14);
	    smField.setFont(font);
	    smField.setText("(ROOT (IP-HLN (NP-PN-SBJ (NR 中国)) (VP (ADVP (AD 将)) (VP (VV 延长) (NP-OBJ (IP-APP (NP-SBJ (NN 外商) (NN 投资) (NN 企业)) (VP (ADVP (AD 免税)) (VP (VV 进口) (NP-OBJ (NN 设备) (CC 和) (NN 原材料))))) (NP (NN 宽限期)))))) ) ");
	    infoLabel = new JLabel();
	    infoLabel.setText("短语结构树如下：");
	    treePanel.setFont(font);
		
	    setLayout(new java.awt.BorderLayout());
	    
	    treeContainer.setLayout(new java.awt.BorderLayout());
	    treeContainer.setBackground(new java.awt.Color(255, 255, 255));
	    treeContainer.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.RAISED));
	    treeContainer.setForeground(new java.awt.Color(0, 0, 0));
	    treeContainer.setPreferredSize(new java.awt.Dimension(200, 200));
	    treeContainer.add("North", infoLabel);
	    treeContainer.add("Center", treePanel);
	    treePanel.setBackground(Color.white);
	    
		add(controlPanel, BorderLayout.NORTH);
		add(treeContainer, BorderLayout.CENTER);
		//add(parseResult);
		smField.setBounds(10,50,400,40);
		parseBtn.setBounds(420,44,80,50);
		clearBtn.setBounds(510,44,80,50);
		saveAsPicBtn.setBounds(600,44,100,50);
		//parseResult.setAutoscrolls(true);
		//parseResult.setBounds(50, 120, 660, 420);
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG","jpg");
		
		chooser = new JFileChooser();//颜色选择
		chooser.setCurrentDirectory(new File("."));//默认当前文件夹
		chooser.setFileFilter(filter);//设置文件过滤器
		chooser.setAccessory(new ImagePreviewer(chooser));//添加图片预览
		chooser.setAcceptAllFileFilterUsed(false);//不显示'所有文件(*.*)选项
		chooser.setFileView(new FileIconView(filter, new ImageIcon( )));
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());//设置观感管理器
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.setTitle(title);
		this.setVisible(true);
//		this.pack();
				
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == parseBtn){
			if(this.smField.getText().length() == 0) {
				infoLabel.setText("请输入短语结构句子！");
			    return;
			}
			
			String cleanTxt = this.smField.getText().replaceAll("(\t|\r\n|\n)+", " ").replaceAll("[ ]{2,}", " ");
			this.smField.setText(cleanTxt);
			
			//String ptbTreeString = "(ROOT (IP-HLN (NP-PN-SBJ (NR 中国)) (VP (ADVP (AD 将)) (VP (VV 延长) (NP-OBJ (IP-APP (NP-SBJ (NN 外商) (NN 投资) (NN 企业)) (VP (ADVP (AD 免税)) (VP (VV 进口) (NP-OBJ (NN 设备) (CC 和) (NN 原材料))))) (NP (NN 宽限期)))))) ) ";
			String ptbTreeString = this.smField.getText();
			
			try {
				Tree tree = (new PennTreeReader(new StringReader(ptbTreeString), new LabeledScoredTreeFactory(new StringLabelFactory()))).readTree();
				treePanel.setTree(tree);
				treePanel.setBackground(Color.white);
				nowTree = tree;
			} catch (IOException ioe) {
				// TODO Auto-generated catch block
				ioe.printStackTrace();
				infoLabel.setText("请检查输入的短语结构是否正确！");
			}
		}else if(e.getSource() == clearBtn){
			this.smField.setText("");
			this.treePanel.setTree(null);
		}	
		
	}
	
	public BufferedImage getNowImage(){
		BufferedImage savebi = new BufferedImage(treePanel.getWidth(), treePanel.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics savegg = savebi.getGraphics();
		savegg.setColor(Color.white);// 将画笔调整为白色
		savegg.fillRect(0, 0, treePanel.getWidth(), treePanel.getHeight());// 将图像涂白
		
//		Graphics2D saveg2d=(Graphics2D)savegg;
		
//		saveg2d.setFont(new Font("宋体", Font.BOLD, 16));
		
		if (nowTree == null) {
		      return null;
		}
		
//		treePanel.paintGivenComponent(savegg);
		
//		saveg2d.drawString("测试输出图片", 100, 200);
			
		return savebi;
	}
	
	void SaveAsPicFile()//保存文件功能
	{
//		this.repaint();
		FileNameExtensionFilter jpgFilter = new FileNameExtensionFilter("JPG","jpg");
		FileFilter conllFilter = chooser.getFileFilter();
		
		chooser.setFileFilter(jpgFilter);
		int s = chooser.showSaveDialog(null);
		
		BufferedImage savebi = getNowImage();
		
		if (s == JFileChooser.APPROVE_OPTION) {
			try {
				ImageIO.write(savebi, "jpg", chooser.getSelectedFile());//输出
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		chooser.setFileFilter(conllFilter);
	}

}


class FileIconView extends FileView//文件预览
{
	public FileIconView(FileFilter aFilter, Icon anIcon)
	{
		filter = aFilter;
		icon = anIcon;
	}

	public Icon getIcon(File f)
	{
		if (!f.isDirectory() && filter.accept(f)) return icon;
		else return null;
	}

	private FileFilter filter;
	private Icon icon;
}


class ImagePreviewer extends JLabel
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -750352175292222446L;

	public ImagePreviewer(JFileChooser chooser)
	{
		setPreferredSize(new Dimension(100, 100));//设置最佳大小
		setBorder(BorderFactory.createEtchedBorder());//设置边框样式
		chooser.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent event)
			{
				if (event.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
				{
					File f = (File) event.getNewValue();
					if (f == null)
					{
						setIcon(null);
						return;
					}
					ImageIcon icon = new ImageIcon(f.getPath());
					if (icon.getIconWidth() > getWidth()) icon = new ImageIcon(icon.getImage()
							.getScaledInstance(getWidth(), -1, Image.SCALE_DEFAULT));//图片太大，设置为缩略图
					setIcon(icon);
				}
			}
		});
	}
}
