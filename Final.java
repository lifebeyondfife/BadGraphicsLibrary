import java.awt.*;
import java.applet.Applet;
import java.net.URL;
import java.io.*;


public class Final extends Applet{

  public Points points; // "points" stores the 3d points and all the polygons
  private int temp;
  private Button wirre, light, transx, transy, scale, rotatx, rotaty, rotatz;
  private TextArea t;
  public String text;
  public int n;
  public boolean shading = false;
  public boolean wire = false;
  public double[] vv = new double[4];
  int sort[];
  
  public void init(){
    wirre = new Button("Wireframe");  // These are the buttons for the user
    light = new Button("Lighting");   // interface
    transx = new Button("Transform x");
    transy = new Button("Transform y");
    scale = new Button("Scale");
    rotatx = new Button("Rotate x");
    rotaty = new Button("Rotate y");
    rotatz = new Button("Rotate z");
    t = new TextArea(1, 10);
    add(wirre);
    add(light);
    add(transx);
    add(transy);
    add(scale);
    add(rotatx);
    add(rotaty);
    add(rotatz);
    add(t);
    t.insertText("1.5707962", 0);  // This is pi/2 (1/4 revolution in radians)
    text = t.getText();
    vv[3] = 1;
    try{ // This try/catch method reads in a data file and stores it in "points"
      InputStream clear = null;
      clear = new URL(getDocumentBase(), "cow.dat").openStream();
      StreamTokenizer file = new StreamTokenizer(clear);
      file.eolIsSignificant(false);
      file.nextToken();
      temp = (int) file.nval;
      file.nextToken();
      points = new Points(temp, (int) file.nval);
      for(int i=0; i<7; i++) file.nextToken();
      for(int i=0; i<points.pts3d.length; i++){
        for(int j=0; j<3; j++){
          points.pts3d[i][j] = file.nval;
          file.nextToken();
        }
      }
      for(int i=0; i<points.poly.length; i++){
        points.poly[i] = new Poly( (int) file.nval);
        file.nextToken();
        for(int j=0; j<points.poly[i].coords.length; j++){
          points.poly[i].coords[j] = (int) file.nval;
          file.nextToken();
        }
      }      
    } catch(java.net.MalformedURLException e){
      System.err.println("File isn't there...");
    } catch(IOException e){
      System.err.println("IOException (of sorts)...");
    }
    points.ox += 300;  // The rest of this method moves and scales the 3d object
    points.oy += 300;  // so that it is in a good starting position
    for(int z=0; z<points.pts3d.length; z++){
      for(int q=0; q<3; q++){
        vv[q] = points.pts3d[z][q];
      }
      points.pts3d[z] = points.trans(300, 300, 0, vv);
    }
    for(int z=0; z<points.pts3d.length; z++){
      for(int q=0; q<3; q++){
        vv[q] = points.pts3d[z][q];
      }
      points.pts3d[z] = points.scale(50, 50, 50, vv);
    }
    sort = new int[points.poly.length];
  }
  
  public void paint(Graphics g){  // This method draws the 3d object
    double d = -1000;
    double[] temp = new double[4];
    int[] x3 = new int[3];
    int[] x4 = new int[4];
    int[] y3 = new int[3];
    int[] y4 = new int[4];

    temp[3] = 1;
    setBackground(Color.pink);
    mailsort(); // Very important! This orders the polygons i.e. VSD
    diffuse();  // This calculates the intensity of light on each polygon

    for(int i=0; i<points.pts3d.length; i++){ // This for loop perspectively
      for(int j=0; j<3; j++){                 // projects all the 3d points
        temp[j] = points.pts3d[i][j];         // into 2d points
      }
      temp = points.project(d, temp);
      for(int k=0; k<3; k++){
        points.projected[i][k] = temp[k];
      }
    }
    
    for(int f=0; f<points.poly.length; f++){        // This for loop draws all
      if(points.poly[sort[f]].coords.length == 3){  // polygons
        for(int l=0; l<3; l++){
          x3[l] = 700-(int) (points.projected[(points.poly[sort[f]].coords[l])-1][0]);
          y3[l] = 700-(int) (points.projected[(points.poly[sort[f]].coords[l])-1][1]);
        }
        if(shading){
          g.setColor(new Color((int)(points.poly[sort[f]].intensity * 0.4),
        			(int)(points.poly[sort[f]].intensity * 0.9),
        			(int)(points.poly[sort[f]].intensity * 0.6)));
          g.fillPolygon(x3, y3, 3);
        } else{
          if(!wire){
            g.setColor(new Color((int)(255*0.4),(int)(255*0.9),(int)(255*0.6)));
            g.fillPolygon(x3, y3, 3);
          }
          g.setColor(Color.black);
          g.drawPolygon(x3, y3, 3);
        }
      } else{
        for(int h=0; h<4; h++){
          x4[h] = 700-(int) (points.projected[(points.poly[sort[f]].coords[h])-1][0]);
          y4[h] = 700-(int) (points.projected[(points.poly[sort[f]].coords[h])-1][1]);
        }
        if(shading){
          g.setColor(new Color((int)(points.poly[sort[f]].intensity * 0.4),
        			(int)(points.poly[sort[f]].intensity * 0.9),
        			(int)(points.poly[sort[f]].intensity * 0.6)));
          g.fillPolygon(x4, y4, 4);
        } else{
          if(!wire){
            g.setColor(new Color((int)(255*0.4),(int)(255*0.9),(int)(255*0.6)));
            g.fillPolygon(x4, y4, 4);
          }
          g.setColor(Color.black);
          g.drawPolygon(x4, y4, 4);
        }
      }
    }
  }
  
  public void mailsort(){  // This is the method that sorts the polygons
    int order[][];
    int minx;
    double min;
    double minmax = 0;
    int minmaxint;
    double max;
    int a, b, c;
    a = b = c = 0;
    
    for(int i=0; i<points.poly.length; i++){// This loop calculates the smallest
      min = points.pts3d[(points.poly[i].coords[0])-1][2]; // z value of each
      minx = points.poly[i].coords[0];                     // polygon
      for(int j=1; j<points.poly[i].coords.length; j++){
        if(points.pts3d[(points.poly[i].coords[j])-1][2] < min){
          min = points.pts3d[(points.poly[i].coords[j])-1][2];
          minx = points.poly[i].coords[j];
        }
      }
      if(min < minmax) minmax = min;
      sort[i] = minx;
    }
    
    minmaxint = (int) -minmax + 1;
        
    max = points.pts3d[0][2];
    for(int k=0; k<points.pts3d.length; k++){
      if(points.pts3d[k][2] > max){
        max = points.pts3d[k][2];
      }
    }
    order = new int[1500][1000];
    for(int qw=0; qw<order.length; qw++)
      for(int er=0; er<order[0].length; er++)
        order[qw][er] = 0;
    
    for(int l=0; l<sort.length; l++){       // This loop writes the polygon info
      for(int m=0; m<order[0].length; m++){ // in the sparse 2d array
        if(order[(int) (points.pts3d[sort[l]-1][2])+minmaxint][m] == 0){
          order[(int) (points.pts3d[sort[l]-1][2])+minmaxint][m] = l;
          break;
        }
      }
    }
    
    while(a<sort.length){  // This while loop compacts the data in the sparse
      if(order[b][c] != 0){// 2d array
        sort[a] = order[b][c];
        a++;
        c++;
      } else{
        b++;
        c=0;
      }
      if(b == order.length || c == order.length) break;
    }
  }
  
  public void diffuse(){        // This method calculates the intensity of light
    double[] s = new double[3]; // resting on each polygon
    double[] l = new double[3];
    double[] m = new double[3];
    double[] n = new double[3];
    double h;
    int intensity;
    
    for(int i=0; i<points.poly.length; i++){
      for(int j=0; j<3; j++){
        s[j] = points.pts3d[points.poly[i].coords[0]-1][j];
        m[j] = points.pts3d[points.poly[i].coords[1]-1][j];
        l[j] = points.pts3d[points.poly[i].coords[points.poly[i].coords.length-1]-1][j];
      }

      h = Math.sqrt(Math.pow(s[0],2) + Math.pow(s[1],2) + Math.pow(s[2],2));
      for(int k=0; k<3; k++){
        m[k] -= s[k];
        l[k] -= s[k];
        s[k] += 200;
      }
      
      s[1] += 100;  // s[] is the vector to the light source
      s[0] -= 100;
      n[0] = m[1]*l[2] - m[2]*l[1];  // n[] is the cross product of two points
      n[1] = m[2]*l[0] - m[0]*l[2];  // on a polygon. This give the normal to 
      n[2] = m[0]*l[1] - m[1]*l[0];  // the polygon
      
      h = Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1],2) + Math.pow(n[2],2));
      n[0] /= h;
      n[1] /= h;
      n[2] /= h; // n[] has been normalised (i.e. it is a unit vector)
      h = Math.sqrt(Math.pow(s[0],2) + Math.pow(s[1],2) + Math.pow(s[2],2));
      s[0] /= h;
      s[1] /= h;
      s[2] /= h; // n[] has been normalised (i.e. it is a unit vector)
      
      intensity = (int) (280* (n[0]*s[0] + n[1]*s[1] + n[2]*s[2]));
//      if(intensity < 50) intensity = 50;   // These 3 if statements make sure
      if(intensity < 0) intensity = 0;     // that the value of intensity is
//      if(intensity > 280) intensity = 280; // within valid boundaries
      points.poly[i].intensity = intensity;
    }
  }
  
  
  
  public boolean action(Event e, Object o){ // This method listen for the user
    Double D = Double.valueOf(t.getText()); // operating the buttons
    double d = D.floatValue();
    if(e.target == wirre){
      wire = !wire;
      shading = false;
      repaint();
    }
    else if(e.target == light){
      shading = !shading;
      repaint();
    }
    else if(e.target == transx){
      points.ox += d;
      for(int z=0; z<points.pts3d.length; z++){
        for(int q=0; q<3; q++){
          vv[q] = points.pts3d[z][q];
        }
        points.pts3d[z] = points.trans(d, 0, 0, vv);
      }
      repaint();
    }
    else if(e.target == transy){
      points.oy += d;
      for(int z=0; z<points.pts3d.length; z++){
        for(int q=0; q<3; q++){
          vv[q] = points.pts3d[z][q];
        }
        points.pts3d[z] = points.trans(0, d, 0, vv);
      }
      repaint();
    }
    else if(e.target == scale){
      for(int z=0; z<points.pts3d.length; z++){
        for(int q=0; q<3; q++){
          vv[q] = points.pts3d[z][q];
        }
        points.pts3d[z] = points.scale(d, d, d, vv);
      }
      repaint();
    }
    else if(e.target == rotatx){
      for(int z=0; z<points.pts3d.length; z++){
        for(int q=0; q<3; q++){
          vv[q] = points.pts3d[z][q];
        }
        points.pts3d[z] = points.rotatex(d, vv);
      }
      repaint();
    }
    else if(e.target == rotaty){
      for(int z=0; z<points.pts3d.length; z++){
        for(int q=0; q<3; q++){
          vv[q] = points.pts3d[z][q];
        }
        points.pts3d[z] = points.rotatey(d, vv);
      }
      repaint();
    }
    else if(e.target == rotatz){
      for(int z=0; z<points.pts3d.length; z++){
        for(int q=0; q<3; q++){
          vv[q] = points.pts3d[z][q];
        }
        points.pts3d[z] = points.rotatez(d, vv);
      }
      repaint();
    }
    return true;
  }
  
}

class Points{  // This class stores all the points, all the polygons and all the
               // matrix transformations needed to alter the 3d object.
  public Poly[] poly;
  public double[][] pts3d;
  public double[][] projected;
  public double[][] transf = new double[4][4];
  public double[][] scalef = new double[4][4];
  public double[][] rotatx = new double[4][4];
  public double[][] rotaty = new double[4][4];
  public double[][] rotatz = new double[4][4];
  public double ox, oy, oz = 0.0;
  
  public Points(int numPoints, int numPolys){ // This constructor initialises
    pts3d = new double[numPoints][3];         // the matrices and arrays
    projected = new double[numPoints][3];
    poly = new Poly[numPolys];
    for(int i=0; i<transf.length; i++){
      for(int j=0; j<transf[0].length; j++){
        if(i == j){
          transf[i][j] = 1;
          scalef[i][j] = 1;
          rotatx[i][j] = 1;
          rotaty[i][j] = 1;
          rotatz[i][j] = 1;
        } else{
          transf[i][j] = 0;
          scalef[i][j] = 0;
          rotatx[i][j] = 0;
          rotaty[i][j] = 0;
          rotatz[i][j] = 0;
        }
      }
    }
  }
      
  private double[] matrix(double[][] m1, double[] m2){ // This method multiplies
    double temp;                                       // a point by a matrix
    double m3[] = new double[(m2.length)];
    for(int i=0; i<m1[0].length; i++){
      temp = 0.0;
      for(int k=0; k<m1[0].length; k++){
        temp += m2[k] * m1[k][i];
      }
      m3[i] = temp;
    }
    return m3;
  }
// The rest of this class describes the transformation matrices  
  public double[] trans(double x, double y, double z, double[] point){
    transf[3][0] = x;
    transf[3][1] = y;
    transf[3][2] = z;
    return matrix(transf, point);
  }

  public double[] scale(double x, double y, double z, double[] point){
    scalef[0][0] = x;
    scalef[1][1] = y;
    scalef[2][2] = z;
    point = trans(-ox, -oy, -oz, point);
    point = matrix(scalef, point);
    point = trans(ox, oy, oz, point);
    return point;
  }

  public double[] rotatex(double theta, double[] point){
    rotatx[1][1] = Math.cos(theta);
    rotatx[1][2] = Math.sin(theta);
    rotatx[2][1] = -Math.sin(theta);
    rotatx[2][2] = Math.cos(theta);
    point = trans(-ox, -oy, -oz, point);
    point = matrix(rotatx, point);
    point = trans(ox, oy, oz, point);
    return point;
  }

  public double[] rotatey(double theta, double[] point){
    rotaty[0][0] = Math.cos(theta);
    rotaty[0][2] = -Math.sin(theta);
    rotaty[2][0] = Math.sin(theta);
    rotaty[2][2] = Math.cos(theta);
    point = trans(-ox, -oy, -oz, point);
    point = matrix(rotaty, point);
    point = trans(ox, oy, oz, point);
    return point;
  }

  public double[] rotatez(double theta, double[] point){
    rotatz[0][0] = Math.cos(theta);
    rotatz[0][1] = Math.sin(theta);
    rotatz[1][0] = -Math.sin(theta);
    rotatz[1][1] = Math.cos(theta);
    point = trans(-ox, -oy, -oz, point);
    point = matrix(rotatz, point);
    point = trans(ox, oy, oz, point);
    return point;
  }

  public double[] project(double d, double[] point){
    point[0] /= (point[2]/d) + 1;
    point[1] /= (point[2]/d) + 1;
    point[2] = 0;
    point[3] = 1;
    return point;
  }
}

class Poly{  // This class stores the polygons
  public int coords[];
  public int intensity;

  public Poly(int numPoints){
    coords = new int[numPoints];  
  }
}
