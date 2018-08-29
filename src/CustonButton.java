import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.lang.System;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.Timer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

// REF: https://uxplanet.org/button-ux-design-best-practices-types-and-states-647cf4ae0fc6#.qtpt17vcr

class CustonButton extends JButton {

  //----------------------------------------------------------------------------
  // Construtor que recebe o texto do botao, comando a ser executado quando
  // clickado e o ActionListener que respodera a acao
  //----------------------------------------------------------------------------
  public CustonButton(String text, String command, ActionListener action) {
    // Linkando acoes
    this.setActionCommand(command);
    this.addActionListener(action);

    // Caracteristicas esteticas padrao dos botoes
    this.setText(text);
    this.setForeground(Color.WHITE);
    this.setBackground(MyConstants.COLOR_4);
    this.setFocusPainted(false);
    this.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    this.setContentAreaFilled(false);
    this.setFont(new Font("Gill Sans", Font.BOLD, 12));
  }

  //----------------------------------------------------------------------------
  // paintComponent: sobrescrevendo o metodo de pintura do botao
  //----------------------------------------------------------------------------
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);

    // Criando um graphics 2D para melhorar o anti-aliasing na criacao
    // do botao com bordas arredondadas
    Graphics2D g2 = (Graphics2D) g;
    RenderingHints qualityHints = new RenderingHints(
      RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON );
    qualityHints.put(
      RenderingHints.KEY_RENDERING,
      RenderingHints.VALUE_RENDER_QUALITY );
    g2.setRenderingHints( qualityHints );

    // Estados do botao
    if (getModel().isPressed()) {
      g2.setColor(MyConstants.COLOR_3.brighter());
      this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    else if(getModel().isRollover()) {
      g2.setColor(MyConstants.COLOR_3);
      this.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    else {
      g2.setColor(MyConstants.COLOR_4);
      this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    // Pintando o fundo
    g2.fillRect(2, 2, getWidth()-4, getHeight()-4);

    // Calculando a posicao do texto no botao
    FontMetrics metrics = g2.getFontMetrics(this.getFont());
    int x = (this.getWidth() - metrics.stringWidth(getText())) / 2;
    int y = ((this.getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();

    // Escrevendo no botao
    if (getModel().isPressed() ||
      getModel().isRollover()) g2.setColor(Color.WHITE);
    else g2.setColor(Color.WHITE);
    g2.drawString(getText(), x, y);
  }
}
