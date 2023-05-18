import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JFrame;

@SuppressWarnings("serial")
class Coord extends Rectangle2D.Double
{
	Coord(double x, double y, int size)
	{
		setFrame(translateX(x), translateY(y), size, size);
	}
	
	static public double translateX(double oldx)
	{
		double newx = 0d;
		newx = oldx + 250;
		return newx;
	}
	
	static public double translateY(double oldy)
	{
		double newy = 0d;
		newy = 250 - oldy;
		return newy;
	}
	
	static public double reversetranslateX(double oldx)
	{
		double newx = 0d;
		newx = oldx - 250;
		return newx;
	}
	
	static public double reversetranslateY(double oldy)
	{
		double newx = 0d;
		newx = 250 - oldy;
		return newx;
	}
}

@SuppressWarnings("serial")
class Dragger extends Ellipse2D.Double
{
	Color c;
	double r = 2.5d;
	
	Dragger()
	{
		setFrame(375d-2.5d, 250d-2.5d, r*2, r*2);
		c = Color.RED;
	}
	
	void switchColor(Boolean grabstate)
	{
		if (grabstate) c = Color.BLUE;
		else c = Color.RED;
	}
	
    public boolean isHit(float x, float y)
    {
        return getBounds2D().contains(x, y);
    }
}

@SuppressWarnings("serial")
class Frame extends JFrame
{
	protected Panel p;
	protected TrigPanel tp;

	public Frame()
	{
	    initUI();
	}
	
	private void initUI()
	{  
	    p = new Panel();
        MouseSense ma = new MouseSense();
        p.addMouseMotionListener(ma);
        p.addMouseListener(ma);
        
	    tp = new TrigPanel();
		
	    setTitle("Sinusoid Simulation");
        setPreferredSize(new Dimension(1000, 650));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(255, 255, 255));
    	pack();
		setLocationRelativeTo(null);
        setVisible(true);
		setFocusable(false);
		setLayout(null);
		
		while (!tp.ready)
		{
			try
			{
				TimeUnit.SECONDS.wait(1);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		add(p);
		add(tp);
	}
	
	class MouseSense extends MouseAdapter
    {
        private int x;
        private int y;
        private Boolean grabbed;

        @Override
        public void mouseReleased(MouseEvent e)
        {
        	grabbed = false;
        	p.release();
        }

        @Override
        public void mousePressed(MouseEvent e)
        {
        	x = e.getX();
            y = e.getY();
            
        	if (e.getButton() == MouseEvent.BUTTON1)
        	{
        		grabbed = p.pressed(x, y);
            }
        }

        @Override
        public void mouseDragged(MouseEvent e)
        {
        	x = e.getX();
            y = e.getY();
            
            if (grabbed)
            {
            	p.tick(x, y);
            	tp.sinTick(p.angle, p.y, p.x);
            }    
        }
    }
}

@SuppressWarnings("serial")
class Indicator extends Line2D.Double
{
	Indicator()
	{
		setLine(250d, 250d, 375d, 250d);
	}
}

@SuppressWarnings("serial")
class Info extends JLabel
{
	StringBuilder str;
	
	Info()
	{
		setText("θ = 0°");
		setSize(100, 80);
		setLocation(20, 25);
		setVisible(true);
		str = new StringBuilder();
	}
	
	void custsetText(double angle, double x, double y)
	{
		str.setLength(0);
		
		str.append("<html>");
		str.append("θ = ");
		str.append(Double.toString(roundDouble(angle, 2)));
		str.append("°");
		
		str.append("<br>");
		str.append("θ = ");
		double rangle = (angle * Math.PI) / 180.0d;
		str.append(Double.toString(roundDouble(rangle, 3)));
		str.append(" rad");
		
		str.append("<br>");
		str.append("x = ");
		str.append(Double.toString(roundDouble(x/125, 3)));
		
		str.append("<br>");
		str.append("y = ");
		str.append(Double.toString(roundDouble(y/125, 3)));
		
		str.append("</html>");
		
		setText(str.toString());
	}
	
	double roundDouble(double val, int length)
	{
		double mult = 1;
		for (int i = 0; i < length; i++)
		{
			mult *= 10;
		}
		return Math.round(val * mult) / mult;
	}
}

@SuppressWarnings("serial")
class Panel extends JPanel
{
	private ArrayList<Coord> points;
	private Coord xpoint;
	private Coord ypoint;
	private Info theta;
	double angle;
	private Indicator ind;
	private Dragger drag;
	double x = 0;
	double y = 0;
	
	public Panel()
	{
		points = new ArrayList<Coord>();
		theta = new Info();
		xpoint = new Coord(-2.5d, 0d, 5);
		ypoint = new Coord(0d, +2.5d, 5);
		xpoint.y = Coord.translateY(140d);
		ypoint.x = Coord.translateX(140d);
		ind = new Indicator();
		drag = new Dragger();
		angle = 0;
		
		initUI();
	}

    private void initUI()
    {
    	setOpaque(true);
		setSize(new Dimension(500, 500));
		setLocation(0, 50);
		setBackground(new Color(255, 255, 255));
		setBorder(BorderFactory.createLineBorder(Color.BLUE));
		setFocusable(true);
		requestFocus();
		setLayout(null);
		
		add(theta);
    }
    
    @Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g2d);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(Color.BLACK);
        g2d.draw(new Line2D.Float(250f, 0f, 250f, 500f));
        g2d.setColor(Color.BLACK);
        g2d.draw(new Line2D.Float(0f, 250f, 500f, 250f));
        
        for (Coord p : points)
        {
            g2d.setColor(Color.RED);
            g2d.fill(p);
        }
        
        g2d.setColor(Color.ORANGE);
        g2d.draw(xpoint);
        g2d.setColor(Color.GREEN);
        g2d.draw(ypoint);
        
        g2d.setColor(Color.CYAN);
        g2d.draw(ind);
        
        g2d.setColor(drag.c);
        g2d.draw(drag);
	}
    
    Boolean pressed(int x, int y)
    {
        if (drag.isHit(x, y))
        {
        	drag.switchColor(true);
        	System.out.println("clicked");
        	repaint();
        	return true;
        }
        return false;
    }
    
    void release()
    {
    	drag.switchColor(false);
    	System.out.println("released");
    	repaint();
    }
    
    void tick(int xval, int yval)
    {
        System.out.println("dragging");
        
        double realx = Coord.reversetranslateX(xval);
        double realy = Coord.reversetranslateY(yval);
        angle = Math.atan(realy/realx);
        if (realx < 0d)
        {
        	angle = angle - (Math.PI);
        }
        realx = x = Math.cos(angle) * 125;
		realy = y = Math.sin(angle) * 125;
		realx = Coord.translateX(realx);
		realy = Coord.translateY(realy);
        
		xpoint.x = realx-2.5d;
		ypoint.y = realy-2.5d;
        ind.x2 = realx;
		ind.y2 = realy;
		drag.x = realx-drag.r;
		drag.y = realy-drag.r;
		
		angle = ((angle * 180)/Math.PI);
		if (angle < 0)
		{
			angle = 360 + angle;
		}
		System.out.println(angle);
		theta.custsetText(angle, x, y);
        
    	drag.switchColor(true);
    	repaint();
    }
}

@SuppressWarnings("serial")
class TrigPanel extends JPanel
{
	private ArrayList<Coord> spoints;
	private ArrayList<Coord> cpoints;
	private ArrayList<Coord> tpoints;
	double xvalue = -250;
	private Coord sPoint;
	private Coord cPoint;
	private Coord tPoint;
	Boolean ready = false;
	
	public TrigPanel()
	{
		spoints = new ArrayList<Coord>();
		cpoints = new ArrayList<Coord>();
		tpoints = new ArrayList<Coord>();
		sPoint = new Coord(0d, 0d, 5);
		cPoint = new Coord(0d, 0d, 5);
		tPoint = new Coord(0d, 0d, 5);

		initUI();
		setup();
	}

    private void initUI()
    {
    	setOpaque(true);
		setSize(new Dimension(500, 500));
		setLocation(500, 50);
		setBackground(new Color(255, 255, 255));
		setBorder(BorderFactory.createLineBorder(Color.BLUE));
		setFocusable(true);
		requestFocus();
		setLayout(null);
    }
    
    @Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d = (Graphics2D)g;
		super.paintComponent(g2d);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Coord p : spoints)
        {
            g2d.setColor(Color.GREEN);
            g2d.fill(p);
        }
        for (Coord p : cpoints)
        {
            g2d.setColor(Color.ORANGE);
            g2d.fill(p);
        }
        for (Coord p : tpoints)
        {
            g2d.setColor(Color.MAGENTA);
            g2d.fill(p);
        }
        
        g2d.setColor(Color.BLACK);
        g2d.draw(new Line2D.Float(0f, 250f, 500f, 250f));
        
        g2d.setColor(Color.GREEN);
        g2d.draw(sPoint);
        g2d.setColor(Color.ORANGE);
        g2d.draw(cPoint);
        g2d.setColor(Color.MAGENTA);
        g2d.draw(tPoint);
	}
    
    public void setup()
    {
    	for (int i = 0; i <= 500; i++)
    	{
    		double angle = (i/500d)*360d;
    		angle = ((angle * Math.PI)/180);
    		
    		double realx = Math.cos(angle) * 125;
    		double realy = Math.sin(angle) * 125;
    		
        	spoints.add(new Coord(i-250, realy, 1));
        	cpoints.add(new Coord(i-250, realx, 1));
        	tpoints.add(new Coord(i-250, realy/realx, 1));
    	}
    	ready = true;
    }
    
    public void sinTick(double a, double syvalue, double cyvalue)
    {
    	double xval = (a/360d)*500d;
		
		sPoint.x = cPoint.x = tPoint.x = xval-2.5d;
		
		double angle = (xval/500d)*360d;
		angle = ((angle * Math.PI)/180);
		
		double realx = Math.cos(angle) * 125;
		double realy = Math.sin(angle) * 125;
		
		sPoint.y = Coord.translateY(realy)-2.5d;
		cPoint.y = Coord.translateY(realx)-2.5d;
		tPoint.y = Coord.translateY(realy/realx)-2.5d;
    	
    	repaint();
    }
}

class Main
{
	static Frame fr;

	public Main()
	{
		fr = new Frame();
	}

	public static void main(String[] args)
	{
		new Main();
	}
}
