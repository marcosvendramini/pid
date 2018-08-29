import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.lang.System;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.io.*;

class CustonTextField extends JTextField implements FocusListener {
  private final String hint;
  private boolean showingHint;

  //----------------------------------------------------------------------------
  // Construtor Padrao
  //----------------------------------------------------------------------------
  public CustonTextField(final String hint) {
    super(hint);
    this.hint = hint;
    this.showingHint = true;
    super.addFocusListener(this);
    this.setMaximumSize(this.getPreferredSize());
    this.setForeground(MyConstants.COLOR_4.brighter());
    this.setBackground(MyConstants.COLOR_4);
    this.setCaretColor(Color.WHITE);
    this.setHorizontalAlignment(JTextField.CENTER);
    this.setBorder(BorderFactory.createMatteBorder(8, 16, 8, 16, MyConstants.COLOR_4));
  }

  //----------------------------------------------------------------------------
  // paintComponent: metodo de pintura do JTextField
  //----------------------------------------------------------------------------
  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);
  }

  //----------------------------------------------------------------------------
  // setVisible: sobrescrevendo o metodo de visibilidade para controle da hint
  //----------------------------------------------------------------------------
  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);

    showingHint = true;
    this.setText(hint);
    this.setForeground(MyConstants.COLOR_4.brighter());
  }

  //----------------------------------------------------------------------------
  // focusGained: metodo da interface FocusListener que "trata" o recebimento
  // de foco da janela
  //----------------------------------------------------------------------------
  @Override
  public void focusGained(FocusEvent e) {
    if(this.getText().isEmpty()) {
      super.setText("");
      showingHint = false;
      this.setForeground(Color.WHITE);
    }
  }

  //----------------------------------------------------------------------------
  // focusLost: metodo da interface FocusListener que "trata" a saida do foco
  // do componente
  //----------------------------------------------------------------------------
  @Override
  public void focusLost(FocusEvent e) {
    if(this.getText().isEmpty()) {
      super.setText(hint);
      showingHint = true;
      this.setForeground(MyConstants.COLOR_4.brighter());
    }
  }

  //----------------------------------------------------------------------------
  // getText: metodo do JTextField que retorna o texto do campo
  //----------------------------------------------------------------------------
  @Override
  public String getText() {
    return showingHint ? "" : super.getText();
  }

  //----------------------------------------------------------------------------
  // checkValue: metodo para verificar o valor do textfield
  //----------------------------------------------------------------------------
  public boolean checkValue() {
    if(this.getText() == null || this.getText().equals("")) return false;
    if(this.getText().equals(this.hint)) return false;
    return true;
  }
}
