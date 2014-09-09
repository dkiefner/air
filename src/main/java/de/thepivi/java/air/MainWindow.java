package de.thepivi.java.air;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.imgscalr.Scalr;

// TODO zielgröße xxhdpi eingeben und diese basis nutzen, wenn source pics zu groß
// TODO nodpi ignorieren
// TODO sonstige folder?
// TODO ignore ninepatch
public class MainWindow {

	private static void createFolder(File... folder) {
		for ( File f : folder ) {
			if ( !f.exists() ) {
				f.mkdirs();
			}
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JFrame frame;
	private JButton btnStartProcess;

	private JLabel lblSrc;

	private JLabel lblDest;
	private JFileChooser fileChooser;

	private File srcFolder;

	private File destFolder;
	private JLabel lblLog;
	private JScrollPane scrollPane;
	private JTextArea textArea;

	public MainWindow() {
		initialize();
	}

	private File getCacheFile() {
		File cacheFile = new File(System.getProperty("java.io.tmpdir"), Cache.NAME);
		System.out.println("using temp file: " + cacheFile.getAbsolutePath());
		return cacheFile;
	}

	private void initialize() {
		// MainFrame
		frame = new JFrame();
		frame.setBounds(100, 100, 600, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.0,
				Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 1.0, 0.0,
				0.0, 0.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JLabel lblSourceResFolder = new JLabel("Source Res Folder:");
		GridBagConstraints gbc_lblSourceResFolder = new GridBagConstraints();
		gbc_lblSourceResFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblSourceResFolder.anchor = GridBagConstraints.WEST;
		gbc_lblSourceResFolder.gridx = 0;
		gbc_lblSourceResFolder.gridy = 0;
		frame.getContentPane().add(lblSourceResFolder, gbc_lblSourceResFolder);

		lblSrc = new JLabel("None");
		GridBagConstraints gbc_lblNone = new GridBagConstraints();
		gbc_lblNone.insets = new Insets(0, 0, 5, 5);
		gbc_lblNone.anchor = GridBagConstraints.WEST;
		gbc_lblNone.weightx = 1.0;
		gbc_lblNone.gridx = 1;
		gbc_lblNone.gridy = 0;
		frame.getContentPane().add(lblSrc, gbc_lblNone);

		JButton btnChooseSrc = new JButton("Choose");
		btnChooseSrc.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				srcFolder = showFileChooserForFolder(srcFolder, lblSrc);
				saveTempData();

				//				if ( !srcFolder.getName().toLowerCase().contains("dpi") ) {
				//					// TODO show list with found *dpi folder to select the root folder
				//				}
			}
		});
		GridBagConstraints gbc_btnChoose = new GridBagConstraints();
		gbc_btnChoose.insets = new Insets(0, 0, 5, 5);
		gbc_btnChoose.gridx = 2;
		gbc_btnChoose.gridy = 0;
		frame.getContentPane().add(btnChooseSrc, gbc_btnChoose);

		JLabel lblOutResFolder = new JLabel("Out Res Folder:");
		GridBagConstraints gbc_lblOutResFolder = new GridBagConstraints();
		gbc_lblOutResFolder.insets = new Insets(0, 0, 5, 5);
		gbc_lblOutResFolder.anchor = GridBagConstraints.WEST;
		gbc_lblOutResFolder.gridx = 0;
		gbc_lblOutResFolder.gridy = 1;
		frame.getContentPane().add(lblOutResFolder, gbc_lblOutResFolder);

		lblDest = new JLabel("None");
		GridBagConstraints gbc_lblNone_1 = new GridBagConstraints();
		gbc_lblNone_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNone_1.anchor = GridBagConstraints.WEST;
		gbc_lblNone_1.weightx = 1.0;
		gbc_lblNone_1.gridx = 1;
		gbc_lblNone_1.gridy = 1;
		frame.getContentPane().add(lblDest, gbc_lblNone_1);

		JButton btnChooseDest = new JButton("Choose");
		btnChooseDest.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				destFolder = showFileChooserForFolder(destFolder, lblDest);
				saveTempData();
			}
		});
		GridBagConstraints gbc_btnChoose_1 = new GridBagConstraints();
		gbc_btnChoose_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnChoose_1.gridx = 2;
		gbc_btnChoose_1.gridy = 1;
		frame.getContentPane().add(btnChooseDest, gbc_btnChoose_1);

		btnStartProcess = new JButton("Start Process");
		btnStartProcess.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				if ( srcFolder != null ) {
					String srcName = srcFolder.getName();
					if ( srcName.endsWith("dpi") ) {
						try {
							if ( srcName.endsWith("xxxhdpi") ) {
								startProcess(1.0f, 0.75f, 0.5f, 0.375f, 0.25f);
							} else if ( srcName.endsWith("xxhdpi") ) {
								startProcess(-1.0f, 1.0f, 2f / 3f, 0.5f, 1f / 3f);
							} else if ( srcName.endsWith("xhdpi") ) {
								startProcess(-1.0f, -1.0f, 1.0f, 0.75f, 0.5f);
							} else if ( srcName.endsWith("hdpi") ) {
								startProcess(-1.0f, -1.0f, -1.0f, 1.0f, 3f / 2f);
							} else {
								System.out.println("no appropriate folders found");
							}
						} catch (IOException e) {
							System.err.println(e);
						}
					} else {
						System.out.println("no appropriate folders found");
					}
				} else {
					System.out.println("srcFolder is null");
				}
			}
		});
		GridBagConstraints gbc_btnStartProcess = new GridBagConstraints();
		gbc_btnStartProcess.gridwidth = 2;
		gbc_btnStartProcess.insets = new Insets(0, 0, 5, 5);
		gbc_btnStartProcess.anchor = GridBagConstraints.WEST;
		gbc_btnStartProcess.gridx = 0;
		gbc_btnStartProcess.weightx = 1.0;
		gbc_btnStartProcess.gridy = 2;
		frame.getContentPane().add(btnStartProcess, gbc_btnStartProcess);

		lblLog = new JLabel("Log:");
		GridBagConstraints gbc_lblLog = new GridBagConstraints();
		gbc_lblLog.insets = new Insets(0, 0, 5, 5);
		gbc_lblLog.anchor = GridBagConstraints.WEST;
		gbc_lblLog.weightx = 1.0;
		gbc_lblLog.gridx = 0;
		gbc_lblLog.gridy = 3;
		frame.getContentPane().add(lblLog, gbc_lblLog);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 5;
		gbc_scrollPane.gridwidth = 4;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 4;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		try {
			loadCacheData();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void loadCacheData() throws FileNotFoundException, IOException {
		File cacheFile = getCacheFile();
		if ( cacheFile.exists() ) {
			System.out.println("Read from cache");
			Properties props = new Properties();
			props.load(new FileReader(cacheFile));

			String src = props.getProperty(Cache.Keys.SRC);
			if ( src != null && src.length() > 0 ) {
				System.out.println("src=" + src);
				srcFolder = new File(src);
				lblSrc.setText(src);
			}

			String dest = props.getProperty(Cache.Keys.DEST);
			if ( dest != null && dest.length() > 0 ) {
				System.out.println("dest=" + dest);
				destFolder = new File(dest);
				lblDest.setText(dest);
			}
		} else {
			System.out.println("No cache available");
		}
	}

	private void resizeDrawable(BufferedImage src, int maxWidth, int maxHeight, float factor, String outFormat,
			File outFolder, String name) throws IOException {
		BufferedImage dest;
		if ( factor > 0 ) {
			dest = Scalr.resize(src, (int)(maxWidth * factor), (int)(maxHeight * factor));
			ImageIO.write(dest, outFormat, new File(outFolder, name));
		}
	}

	private void saveTempData() {
		File tempFile = getCacheFile();

		FileOutputStream out = null;
		try {

			if ( !tempFile.exists() ) {
				tempFile.createNewFile();
			}

			out = new FileOutputStream(tempFile);
			Properties props = new Properties();
			if ( srcFolder != null ) {
				props.setProperty(Cache.Keys.SRC, srcFolder.getAbsolutePath());
			}
			if ( destFolder != null ) {
				props.setProperty(Cache.Keys.DEST, destFolder.getAbsolutePath());
			}
			props.store(out, "--- AIR Cache ---");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if ( out != null ) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private File showFileChooserForFolder(File selectedFile, JLabel resultLabel) {
		if ( selectedFile != null ) {
			fileChooser.setSelectedFile(selectedFile);
		}

		int result = fileChooser.showOpenDialog(frame);
		if ( result == JFileChooser.APPROVE_OPTION ) {
			File folder = fileChooser.getSelectedFile();
			resultLabel.setText(folder.getAbsolutePath());
			return folder;
		}

		return null;
	}

	private void startProcess(float xxxhdpi, float xxhdpi, float xhdpi, float hdpi, float mdpi) throws IOException {
		// change button text
		btnStartProcess.setText("Processing...");
		textArea.setText("");

		File outMdpi = new File(destFolder, "drawable-mdpi");
		File outHdpi = new File(destFolder, "drawable-hdpi");
		File outXHdpi = new File(destFolder, "drawable-xhdpi");
		File outXXHdpi = new File(destFolder, "drawable-xxhdpi");
		File outXXXHdpi = new File(destFolder, "drawable-xxxhdpi");

		if ( xxxhdpi > 0f ) {
			createFolder(outXXXHdpi);
		}
		if ( xxhdpi > 0f ) {
			createFolder(outXXHdpi);
		}
		if ( xhdpi > 0f ) {
			createFolder(outXHdpi);
		}
		if ( hdpi > 0f ) {
			createFolder(outHdpi);
		}
		if ( mdpi > 0f ) {
			createFolder(outMdpi);
		}

		String fileName;
		BufferedImage src;
		System.out.println(String.format("start processing %d file(s)", srcFolder.listFiles().length));
		textArea.append(String.format("start processing %d file(s)...\n", srcFolder.listFiles().length));
		for ( File file : srcFolder.listFiles() ) {
			fileName = file.getName();
			System.out.println("processing: " + fileName);
			textArea.append("\nprocessing: " + fileName);
			String outFormat = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
			src = ImageIO.read(file);

			int maxWidth = src.getWidth();
			int maxHeight = src.getHeight();

			resizeDrawable(src, maxWidth, maxHeight, mdpi, outFormat, outMdpi, file.getName());
			resizeDrawable(src, maxWidth, maxHeight, hdpi, outFormat, outHdpi, file.getName());
			resizeDrawable(src, maxWidth, maxHeight, xhdpi, outFormat, outXHdpi, file.getName());
			resizeDrawable(src, maxWidth, maxHeight, xxhdpi, outFormat, outXXHdpi, file.getName());
			resizeDrawable(src, maxWidth, maxHeight, xxxhdpi, outFormat, outXXXHdpi, file.getName());
		}

		textArea.append("\n\nfinished processing.");
		btnStartProcess.setText("Start Process");
		
		JOptionPane.showMessageDialog(frame, "Finished");
	}

}
