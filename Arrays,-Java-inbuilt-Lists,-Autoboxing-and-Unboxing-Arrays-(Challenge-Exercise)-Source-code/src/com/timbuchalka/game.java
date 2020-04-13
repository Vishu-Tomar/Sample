import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.util.Random;

import javax.swing.JFrame;


public class BrickBreaker implements Runnable{
	
	private JFrame frame;
	private Canvas canvas;
	private Thread thread;
	int score=0;
	private BufferStrategy bs;
	private Graphics g;
	
	int px=250,py=480,mx=0,bx=250,by=300,mx1=2,my1=2,count=0;
	
	private Block[][] blocks;
	
	private Key key;
	Random rand;
	boolean show=false;
	public BrickBreaker(){
		frame = new JFrame("Brick Breaker");
		frame.setSize(500,500);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		rand = new Random();
		
		blocks = new Block[10][8];
		
		//50,20
		for(int i=0;i<10;i++){
			for(int j=0;j<8;j++){
				blocks[i][j] = new Block(i*50,j*30,(byte)50,(byte)(30));
			}
		}
		
		key = new Key();
		frame.addKeyListener(key);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(500,500));
		canvas.setMaximumSize(new Dimension(500,500));
		canvas.setMinimumSize(new Dimension(500,500));
		canvas.setFocusable(false);
		
		frame.add(canvas)
		;
		frame.pack();
		
	}
	
	public synchronized void start(){
		thread = new Thread(this);
		thread.start();
	}
	
	public void run(){
		long now,lastTime=System.nanoTime();
		double  delta=0, nsPertick = 1000000000/80;
		
		while(true){
			
			now = System.nanoTime();
			delta += (now-lastTime)/nsPertick;
			lastTime= now;
			
			if(delta>=1){
				tick();
				render();
				delta--;
			}
			
		}
		
	}
	
	public void tick(){
		if(!show)
		{		px+=mx;
		bx+=mx1;
		by+=my1;
		}if(bx+15>=500 || bx<=0){
			mx1=-mx1;
		}
		if(by>=500 ){
			by=300;
			bx=250;
			score=0;
			show = true;
		
			
		}
		if(by<=0){
			my1=-my1;
		}
		if(bx>px && bx<px+50){
			if(by+15>=py){
				my1=-my1;
				//mx1=-2;
			}
		}
		
		if(px<=0 ){
			px=0;
		}if(px+80>=500){
			px=420;
		}
		for(int i=0;i<10;i++){

			for(int j=0;j<8;j++){
				blocks[i][j].tick();
			}	
		}
	}
	
	public void render(){
		bs = canvas.getBufferStrategy();
		if(bs==null){
			canvas.createBufferStrategy(3);
			return;
		}
		g = bs.getDrawGraphics();
		
		g.clearRect(0, 0, 500, 500);
		g.setColor(Color.white);
		g.fillRect(0, 0, 500, 500);
		
	
		for(int i=0;i<10;i++){

			for(int j=0;j<8;j++){
				blocks[i][j].render();
			}	
		}
		g.setColor(Color.blue.brighter());
		g.fillRect(px, py, 80, 20);
		g.setColor(Color.blue.darker());
		g.drawRect(px, py, 80, 20);
		
		g.setColor(Color.green.brighter());
		g.fillOval(bx, by, 15, 15);
		g.setColor(Color.green.darker());
		g.drawOval(bx, by, 15, 15);
		g.setFont(new Font("Verdana",Font.PLAIN,24));
		g.setColor(Color.BLUE);
		
		if(show){
		g.setColor(Color.red);
		g.drawString("YOU LOSE", 200,200);
		count++;
		if(count>60){
			show=false;
			count=0;
		}
		}
		g.drawString("Your Score: " + score, 50, 50);
		bs.show();g.dispose();
		
	}
	
	public synchronized void stop(){
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		BrickBreaker b = new BrickBreaker();
		b.start();	
	}
	
	class Key implements KeyListener{

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				mx=3;
				System.out.println("YES");
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				mx=-3;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT){
				mx=0;
				System.out.println("YES");
			}
			if(e.getKeyCode() == KeyEvent.VK_LEFT)
				mx=0;			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	//add blocks
	//add physics
	
	class Block{
		int x,y;
		byte width,height;
		boolean run=true;
		public Block(int x,int y,byte width,byte height){
			this.x=x;
			this.y=y;
			this.width= width;
			this.height = height;
		}
		public void tick(){
			if(run){
			if(new Rectangle(bx,by,15,15).intersects(new Rectangle(x,y,width,height))){
				run=false;
				mx1=-mx1;
				score++;
				my1=-my1;
			}
			}
		}
		
		public void render(){
			if(run){
			g.setColor(Color.LIGHT_GRAY);
			g.fillRect(x, y, width, height);
			g.setColor(Color.gray.darker());
			g.drawRect(x, y, width, height);
			}
		}
		
	}
	
}