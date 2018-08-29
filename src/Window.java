import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;
import java.lang.System;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.RenderingHints;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import java.lang.Math;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;
import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.net.URL;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.*;
import org.opencv.core.*;
import org.opencv.imgproc.*;
import org.opencv.core.Rect;
import org.opencv.core.Point;



// REF: https://material.google.com/
// http://danilotl.com.br/blog/reconhecendo-caracteres-em-imagens-com-java-e-tess4j/
// https://en.wikipedia.org/wiki/Optical_character_recognition
// https://github.com/tesseract-ocr
// http://www.fatecsp.br/dti/tcc/tcc00068.pdf
// http://docs.opencv.org/2.4/doc/tutorials/introduction/desktop_java/java_dev_intro.html
// pixelCoordinate = worldCoordinate * 2zoomLevel

class Window implements ActionListener{
  // Atributos
  private BufferedImage image;
  private JFrame window;
  private JPanel buttonPanel;
  private JLabel mainPanel;
  private JFileChooser fileChooser;

  CustonTextField t1, t2, t3;
  JSeparator sep5, sep6, sep7;
  
  Mat matimg;
  
  static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

  //----------------------------------------------------------------------------
  // Actions: enum contendo todas as acoes possiveis dentro da janela
  //----------------------------------------------------------------------------
  protected enum Actions {
    GET_IMAGE, ROTATE, OPEN_MAP, GET_VALUE, NULL_ACTION, CLOSE
  }

  //----------------------------------------------------------------------------
  // Construtor Padrao
  //----------------------------------------------------------------------------
  public Window() {
    window = null;
    buttonPanel = null;
    mainPanel = null;
    fileChooser = null;
    image = null;
  }

  //----------------------------------------------------------------------------
  // showIt/hideIt: metodos para "esconder" e "aparecer" a janela
  //----------------------------------------------------------------------------
  public void showIt() { if(window != null) window.setVisible(true); }
  public void hideIt() { if(window != null) window.setVisible(false); }

  //----------------------------------------------------------------------------
  // custonSeparator: cria um separador customizado
  //----------------------------------------------------------------------------
  private JSeparator custonSeparator() {
    JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
    sep.setMaximumSize( new Dimension(Integer.MAX_VALUE, 1) );
    sep.setBackground(MyConstants.COLOR_4.brighter());
    sep.setForeground(MyConstants.COLOR_4.brighter());

    return sep;
  }

  //----------------------------------------------------------------------------
  // createButtonPanel: cria o painel que contem os botoes e os mesmos.
  //----------------------------------------------------------------------------
  private void createButtonPanel() {
    buttonPanel = new JPanel();
    GroupLayout layout = new GroupLayout(buttonPanel);
    buttonPanel.setLayout(layout);
    buttonPanel.setBackground(MyConstants.COLOR_4);

    // Componentes do GroupLayout
    CustonButton b1 = new CustonButton("Select Image", Actions.GET_IMAGE.name(), this);
    CustonButton b2 = new CustonButton("Identify Value", Actions.GET_VALUE.name(), this);
    CustonButton b3 = new CustonButton("Open Map", Actions.OPEN_MAP.name(), this);
    CustonButton b4 = new CustonButton("Close", Actions.CLOSE.name(), this);
    t1 = new CustonTextField("Latitude");
    t2 = new CustonTextField("Longitude");
    t3 = new CustonTextField("Angle");
    JSeparator sep1 = custonSeparator();
    JSeparator sep2 = custonSeparator();
    JSeparator sep3 = custonSeparator();
    JSeparator sep4 = custonSeparator();
    sep5 = custonSeparator();
    sep6 = custonSeparator();
    sep7 = custonSeparator();

    t1.setVisible(false);
    t2.setVisible(false);
    t3.setVisible(false);
    sep5.setVisible(false);
    sep6.setVisible(false);
    sep7.setVisible(false);

    BufferedImage myPicture = null;
    try {
      myPicture = ImageIO.read(new File("src/resources/icon.png"));
    } catch(Exception e) { e.printStackTrace(); }

    JLabel picLabel = new JLabel(new ImageIcon(myPicture));

    // Layout horizontal
    layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
      //.addComponent(picLabel, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(b1, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep1, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(t1, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep5, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(t2, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep6, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(t3, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep7, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(b2, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep2, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(b3, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep3, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(b4, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
      .addComponent(sep4, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
    );

    // Layout vertical
    layout.setVerticalGroup(layout.createSequentialGroup()
      .addGap(40)
      .addComponent(b1)
      .addComponent(sep1)
      .addComponent(t1)
      .addComponent(sep5)
      .addComponent(t2)
      .addComponent(sep6)
      .addComponent(t3)
      .addComponent(sep7)
      .addComponent(b2)
      .addComponent(sep2)
      .addComponent(b3)
      .addComponent(sep3)
      .addComponent(b4)
      .addComponent(sep4)
    );
  }

  //----------------------------------------------------------------------------
  // createMainPanel: cria o painel principal e o BufferedImage que contem a
  // imagem a ser trabalhada
  //----------------------------------------------------------------------------
  private void createMainPanel() {
    image = new BufferedImage(650, 650, BufferedImage.TYPE_INT_RGB);

    // criando o painel para "ajustar" a imagem
    mainPanel = new JLabel(new ImageIcon(image)) {
      // sobrescrevendo o metodo de ajustar o tamanho
      @Override
      public Dimension getPreferredSize() {
         return new Dimension(image.getWidth(), image.getHeight());
      }
    };

    // inicializa o painel todo branco
    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, 640, 640);
    g.dispose();

    // criando borda por questoes esteticas
    mainPanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 10));
  }

  //----------------------------------------------------------------------------
  // createFileChooser: cria a janela de selecao de arquivo
  //----------------------------------------------------------------------------
  private void createFileChooser() {
    fileChooser = new JFileChooser();

    // filtro da janela de aquivos
    FileNameExtensionFilter filter =
      new FileNameExtensionFilter("IMAGE FILES", "png", "jpeg");
    fileChooser.setFileFilter(filter);

    // pasta default
    String path = System.getProperty("user.home") +
      System.getProperty("file.separator") + "Pictures";
    fileChooser.setCurrentDirectory(new File(path));
  }

  //----------------------------------------------------------------------------
  // openFileChooser: abre a janela de selecao de arquivo
  //----------------------------------------------------------------------------
  private void openFileChooser() {
    int fileVal = fileChooser.showOpenDialog(window);

    // acao de selecionar imagem
    if (fileVal == JFileChooser.APPROVE_OPTION) {
      File file = fileChooser.getSelectedFile();

      //tenta ler a imagem
      try {
        image = ImageIO.read(file);
        mainPanel.setIcon(new ImageIcon(image));
      } catch(Exception e) { System.out.println("ERRO: Arquivo Invalido"); }


      t1.setText("");
      t2.setText("");
      t3.setText("");
      t1.setVisible(true);
      t2.setVisible(true);
      t3.setVisible(true);
      sep5.setVisible(true);
      sep6.setVisible(true);
      sep7.setVisible(true);
      buttonPanel.repaint();
      mainPanel.repaint();
    }
  }

  //----------------------------------------------------------------------------
  // loadingAnimation: cria uma animacao de carregamento e anima o frame usando
  // uma thread secundaria
  //----------------------------------------------------------------------------
  public void loadingAnimation(boolean load) {
    // Criando outra thread
    SwingUtilities.invokeLater(new Runnable() {
        // Metodo principal da thread
        public void run() {
          if(!load) {
            ImageIcon loading = new ImageIcon("src/resources/load.gif");
            mainPanel.setIcon(loading);
            mainPanel.repaint();
          }
        }
    });
  }

  //----------------------------------------------------------------------------
  // loadMap: carrega o mapa
  //----------------------------------------------------------------------------
  public void loadMap() {
    t1.setVisible(false);
    t2.setVisible(false);
    t3.setVisible(false);
    sep5.setVisible(false);
    sep6.setVisible(false);
    sep7.setVisible(false);
    buttonPanel.repaint();


    MapView map = new MapView(image, mainPanel, "-20.0113562", "-44.0912681");
    map.marker("-20.0113562", "-44.0912681");
    map.marker("-19.971031", "-44.034286");
    map.marker("-19.946022", "-44.111534");
    map.execute();
    loadingAnimation(map.getLoad());
    mainPanel.repaint();
  }

  //----------------------------------------------------------------------------
  // writeCoordenates: escreve as coordenadas no arquivo
  //----------------------------------------------------------------------------
  public void writeCoordenates(String str) {
    if(str == null) return;

    try {
      BufferedWriter bw = new BufferedWriter(
        new FileWriter("src/resources/coor.txt", true));
      bw.write(str);
      bw.close();
    }catch (Exception e) { e.printStackTrace(); }
  }

  //----------------------------------------------------------------------------
  // getTextValues: recebe os valores do texto e os checka
  //----------------------------------------------------------------------------
  public String getTextValues() {
    if(!t1.checkValue() || !t2.checkValue()) return null;
    return t1.getText() + t2.getText() + "\n";
  }

  //----------------------------------------------------------------------------
  // getValueAction: metodo do botao Identify Value
  //----------------------------------------------------------------------------
  public void getValueAction() {
	  writeCoordenates(getTextValues());
	  
	  BufferedImage rotatedImage = getRotatedImage(image, Integer.parseInt(t3.getText()));
	  BufferedImage grayImage = toGray(rotatedImage);
	  matimg = bufferedImageToMat(grayImage);
	  
	  matimg = binarizarImg(matimg);
	  matimg = medianBlur(matimg);
	  //matimg = erode(matimg);
	  //matimg = dilate(matimg);
	  //matimg = contorno(matimg);
	  matimg = retangulos(matimg);
	  //matimg = crop(matimg);
	  
	  
	  
	  BufferedImage newimage = toBufferedImage(matimg);
	  mainPanel.setIcon(new ImageIcon(newimage));
    t1.setVisible(false);
    t3.setVisible(false);
    t2.setVisible(false);
    sep5.setVisible(false);
    sep6.setVisible(false);
    sep7.setVisible(false);
    buttonPanel.repaint();
    mainPanel.repaint();
  }
  
  //----------------------------------------------------------------------------
  // rotateimg: metodo do botao Identify Value
  //----------------------------------------------------------------------------
  public static BufferedImage getRotatedImage(BufferedImage src, int angle) { 
	   GraphicsConfiguration gc = getDefaultConfiguration();

	    int transparency = src.getColorModel().getTransparency();
	    BufferedImage dest =  gc.createCompatibleImage(
	                              src.getWidth(), src.getHeight(), transparency );
	    Graphics2D g2d = dest.createGraphics();

	    AffineTransform origAT = g2d.getTransform(); 

	    AffineTransform rot = new AffineTransform(); 
	    rot.rotate(Math.toRadians(angle), src.getWidth()/2, src.getHeight()/2); 
	    g2d.transform(rot); 

	    g2d.drawImage(src, 0, 0, null);   

	    g2d.setTransform(origAT);   
	    g2d.dispose();

	    return dest; 
  }
  
  private static GraphicsConfiguration getDefaultConfiguration() {
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice gd = ge.getDefaultScreenDevice();
	    return gd.getDefaultConfiguration();
	}
  
  public BufferedImage toGray (BufferedImage gray){
	  BufferedImage imag = new BufferedImage(gray.getWidth(), gray.getHeight(),  
			    BufferedImage.TYPE_BYTE_GRAY);
	  Graphics g = imag.getGraphics();  
	  g.drawImage(gray, 0, 0, null); 
	  return imag;
  }
  
  public BufferedImage toBufferedImage(Mat m){
      int type = BufferedImage.TYPE_BYTE_GRAY;
      if ( m.channels() > 1 ) {
          type = BufferedImage.TYPE_3BYTE_BGR;
      }
      int bufferSize = m.channels()*m.cols()*m.rows();
      byte [] b = new byte[bufferSize];
      m.get(0,0,b); // get all the pixels
      BufferedImage ima = new BufferedImage(m.cols(),m.rows(), type);
      final byte[] targetPixels = ((DataBufferByte) ima.getRaster().getDataBuffer()).getData();
      System.arraycopy(b, 0, targetPixels, 0, b.length);  
      return ima;

  }
  
  public static Mat bufferedImageToMat(BufferedImage bi) {
	  Mat mat = new Mat(bi.getHeight(), bi.getWidth(), CvType.CV_8UC1);
	  byte[] data = ((DataBufferByte) bi.getRaster().getDataBuffer()).getData();
	  mat.put(0, 0, data);
	  return mat;
	}
  
  public Mat binarizarImg (Mat m){
	  Mat imgThreshold = new Mat();
	  Imgproc.threshold(m, imgThreshold, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
	  return imgThreshold;
  }
  
  public Mat binarizarInvImg (Mat m){
	  Mat imgThreshold = new Mat();
	  Imgproc.threshold(m, imgThreshold, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
	  return imgThreshold;
  }
  
  public Mat medianBlur (Mat m){
	  Mat imgBlur = new Mat();
	  Imgproc.medianBlur(m, imgBlur, 5);//5 tam matriz
	  
	  return imgBlur;
  }
  
  public Mat dilate (Mat m){
	  Mat imgDi = new Mat();
	  int dilation_size = 5;
	  Mat element1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*dilation_size + 1, 2*dilation_size+1));
      Imgproc.dilate(m, imgDi, element1);
	  
	  return imgDi;
  }
  public Mat erode (Mat m){
	  Mat imgEr = new Mat();
	  int erosion_size = 7;
	  Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new  Size(2*erosion_size + 1, 2*erosion_size+1));
      Imgproc.erode(m, imgEr, element);
	  
	  return imgEr;
  }
  
  public Mat contorno (Mat m){
	  List<MatOfPoint> contours = new ArrayList<>();
	  Mat hierarchy = new Mat();

	  // find contours
	  Imgproc.findContours(m, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);

	  // if any contour exist...
	  if (hierarchy.size().height > 0 && hierarchy.size().width > 0)
	  {
	          // for each contour, display it in blue
	          for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0])
	          {
	                  Imgproc.drawContours(m, contours, idx, new Scalar(250, 0, 0));
	          }
	  }
	  
	  return m;
  }
  
  public Mat retangulos (Mat m){
	  List<MatOfPoint> contours = new ArrayList<>();
	  Imgproc.findContours(m, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new org.opencv.core.Point(0, 0));
	  MatOfPoint2f approxCurve = new MatOfPoint2f();
	  for (int i = 0; i < contours.size(); i++) {

	      //Convert contours(i) from MatOfPoint to MatOfPoint2f
	      MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );

	      //Processing on mMOP2f1 which is in type MatOfPoint2f
	      double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
	      Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);

	      //Convert back to MatOfPoint
	      MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

	      // Get bounding rect of contour
	      org.opencv.core.Rect rect = Imgproc.boundingRect(points);
	      Core.rectangle(m, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 0, 0, 255), 1);
	      
	    	  
	  }
	            	  
	   
	  return m;
  }
  
//  public Mat crop (Mat m){
//	  List<MatOfPoint> contours = new ArrayList<>();
//	  Imgproc.findContours(m, contours, new Mat(), Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE, new org.opencv.core.Point(0, 0));
//	  MatOfPoint2f approxCurve = new MatOfPoint2f();
//	  Mat ROI= new Mat(130,130,m.type());
//	  for (int i = 0; i < contours.size(); i++) {
//
//	      //Convert contours(i) from MatOfPoint to MatOfPoint2f
//	      MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(i).toArray() );
//
//	      //Processing on mMOP2f1 which is in type MatOfPoint2f
//	      double approxDistance = Imgproc.arcLength(contour2f, true)*0.02;
//	      Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);
//
//	      //Convert back to MatOfPoint
//	      MatOfPoint points = new MatOfPoint( approxCurve.toArray() );
//
//	      // Get bounding rect of contour
//	      org.opencv.core.Rect rect = Imgproc.boundingRect(points);
//	      Core.rectangle(m, new Point(rect.x,rect.y), new Point(rect.x+rect.width,rect.y+rect.height), new Scalar(255, 0, 0, 255), 1);
//	      Rect rectCrop = new Rect(rect.x, rect.y, rect.width, rect.height);
//	      if(rectCrop.height<=70 && rectCrop.width<=70 && rectCrop.height>=20 && rectCrop.width>=20){
//	      //Mat b = m.submat(rectCrop.x, rectCrop.x + rectCrop.height, rectCrop.x, rectCrop.x + rectCrop.width);
//	      Mat b = new Mat (m,rectCrop);
//	    	  
//	      ROI = new Mat(ROI.rows(), ROI.cols() +  b.cols(), b.type());
//	      
//	      int aCols = ROI.cols();
//	      int aRows = ROI.rows();
//	      for (int p = 0; p < aRows; p++) {
//	          for (int j = 0; j < aCols; j++) {
//	              ROI.put(p, j, ROI.get(p, j));
//	          }
//	      }
//	      for (int p = 0; p < b.rows(); i++) {
//	          for (int j = 0; j < b.cols(); j++) {
//	              m.put(p, aCols + j, b.get(p, j));
//	          }
//	      }
//	      }
//	  }
//	        	  
//	   
//	  return ROI;
//                  
//
//                 
//  }
  
  //----------------------------------------------------------------------------
  // create: cria a janela e os elementos dentro da mesma.
  //----------------------------------------------------------------------------
  public void create() {
    window = new JFrame();

    // criando componentes
    createFileChooser();
    createMainPanel();
    createButtonPanel();

    // propriedades da janela
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setLayout(new BorderLayout());
    window.getContentPane().setBackground(Color.WHITE);
    window.add(buttonPanel, BorderLayout.LINE_START);
    window.add(mainPanel, BorderLayout.CENTER);
    window.pack();
    window.setMinimumSize(new Dimension(window.getWidth(), window.getHeight()));
  }

  //----------------------------------------------------------------------------
  // actionPerformed: metodo obrigatorio da interface ActionListener que
  // responde a alguma acao ocorrida na janela (Acoes contidas no Enum)
  //----------------------------------------------------------------------------
  @Override
  public void actionPerformed(ActionEvent event) {
    if (event.getActionCommand() == Actions.GET_IMAGE.name()) { openFileChooser(); }
    if (event.getActionCommand() == Actions.GET_VALUE.name()) { getValueAction(); }
    if (event.getActionCommand() == Actions.OPEN_MAP.name()) { loadMap(); }
    if (event.getActionCommand() == Actions.CLOSE.name()) { window.dispose(); }
  }
}
