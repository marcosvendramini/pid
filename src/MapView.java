import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;
import java.util.ArrayList;
import javax.swing.*;

// HELP: http://stackoverflow.com/questions/13547004/update-googlemap-static-image-from-java-swing
// MAP: https://developers.google.com/maps/documentation/static-maps/intro?hl=pt-br
// Chave: AIzaSyCjKIJdNOTV2fRkn3acaiCnzhprN1WRXa4

class MapView extends SwingWorker<BufferedImage, Object> {
  private BufferedImage img;
  private JLabel panel;
  private String latitude;
  private String longitude;

  private ArrayList<String> markers;

  private boolean load;
  private boolean run;

  //----------------------------------------------------------------------------
  // Construtor padrao
  //----------------------------------------------------------------------------
  public MapView(BufferedImage img, JLabel panel, String latitude, String longitude) {
    this.img = img;
    this.panel = panel;
    this.latitude = latitude;
    this.longitude = longitude;
    markers = new ArrayList<String>();
    load = false;
    run = true;
  }

  //----------------------------------------------------------------------------
  // marker: adiciona uma marca ao mapa
  //----------------------------------------------------------------------------
  public void marker(String lat, String lon) {
    markers.add("&markers=color:red%7C" + lat + ","+ lon);
  }

  //----------------------------------------------------------------------------
  // getLoad: metodo para acompanhar o carregamento da imagem
  //----------------------------------------------------------------------------
  public boolean getLoad() { return load; }

  public void cancel() { run = false; }

  //----------------------------------------------------------------------------
  // doInBackground: metodo do SwingWorker para executar em background usando
  // uma thread separada
  //----------------------------------------------------------------------------
  @Override
  protected BufferedImage doInBackground() {
    if(run) {
      try {
        String url = "https://maps.googleapis.com/maps/api/staticmap?center=" +
          latitude + "," + longitude + "&size=640x640";

        // le e adiciona marcas ao URL
        if(!markers.isEmpty())
          for(String s : markers) url += s;

        URL map = new URL(url + MyConstants.MAP_KEY);
        img= ImageIO.read(map);
      }
      catch (Exception e) { e.printStackTrace(); }
    }

    return img;
  }

  //----------------------------------------------------------------------------
  // done: metodo do SwingWorker executado quando a tarefa e concluida
  //----------------------------------------------------------------------------
  @Override
  protected void done() {
    load = true;
    if(run) panel.setIcon(new ImageIcon(img));
  }
}
