package addams.family.gui;
import static javax.swing.GroupLayout.Alignment.CENTER;

import java.awt.Container;
import java.awt.EventQueue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.json.simple.JSONObject;


public class Library extends JFrame {

	private static final long serialVersionUID = 1L;
	private JSlider sliderE1;
    private JSlider sliderE2;
    private JSlider sliderE3;
    private JSlider sliderE4;

    private static BlockingQueue<Integer> queueE1 = new ArrayBlockingQueue<Integer>(1024);
    private static BlockingQueue<Integer> queueE2 = new ArrayBlockingQueue<Integer>(1024);
    private static BlockingQueue<Integer> queueE3 = new ArrayBlockingQueue<Integer>(1024);
    private static BlockingQueue<Integer> queueE4 = new ArrayBlockingQueue<Integer>(1024);
    
    private static HelloThread tE1;
    private static HelloThread tE2;
    private static HelloThread tE3;
    private static HelloThread tE4;
    
    private static Connector connector;
    
    public Library() {
        
        initUI();
    }

    private void initUI() {      
        
        //load current values
    	JSONObject b = connector.getCurrentBrightnessValues();
        if(null != b) {
	        sliderE1 = new JSlider(0, 255, Integer.parseInt((String)b.get("E1")));
	        sliderE2 = new JSlider(0, 255, Integer.parseInt((String)b.get("E2")));
	        sliderE3 = new JSlider(0, 255, Integer.parseInt((String)b.get("E3")));
	        sliderE4 = new JSlider(0, 255, Integer.parseInt((String)b.get("E4")));
        } else {
            sliderE1 = new JSlider(0, 255, 0);
            sliderE2 = new JSlider(0, 255, 0);
            sliderE3 = new JSlider(0, 255, 0);
            sliderE4 = new JSlider(0, 255, 0);
        }
        
        sliderE1.addChangeListener(new ChangeListener() {
        
            @Override
            public void stateChanged(ChangeEvent event) {
                int value = sliderE1.getValue();
                try {
					queueE1.put(value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
              //  connector.setBrightness("E1", value);
            }
        });

        sliderE2.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent event) {             
                int value = sliderE2.getValue();
                try {
					queueE2.put(value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
             //   connector.setBrightness("E2", value);
            }
        });
        
        sliderE3.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent event) {
                int value = sliderE3.getValue();
                try {
					queueE3.put(value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            //    connector.setBrightness("E3", value);
            }
        });
        
        sliderE4.addChangeListener(new ChangeListener() {
            
            @Override
            public void stateChanged(ChangeEvent event) {               
                int value = sliderE4.getValue();
                try {
					queueE4.put(value);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        //        connector.setBrightness("E4", value);
            }
        });
       
        createLayout(sliderE1, sliderE2, sliderE3, sliderE4);
        
        setTitle("Brightness");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }  
    
    private void createLayout(JComponent... arg) {
        
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);               
        
        gl.setAutoCreateContainerGaps(true);
        gl.setAutoCreateGaps(true);
        
        gl.setVerticalGroup(gl.createParallelGroup(CENTER)
                .addComponent(arg[0])
                .addComponent(arg[1])
                .addComponent(arg[2])
                .addComponent(arg[3])
        );  
        
        gl.setHorizontalGroup(gl.createSequentialGroup()
                .addComponent(arg[0])
                .addComponent(arg[1])
                .addComponent(arg[2])
                .addComponent(arg[3])
        );      

        pack();
    }    

    public static void main(String[] args) {
    	connector = new Connector("http://192.168.1.98:4567"); 
    	tE1 = new HelloThread(queueE1, connector, "E1");
    	tE1.start();
    	tE2 = new HelloThread(queueE2, connector, "E2");
    	tE2.start();
    	tE3 = new HelloThread(queueE3, connector, "E3");
    	tE3.start();
    	tE4 = new HelloThread(queueE4, connector, "E4");
    	tE4.start();
    	
    	
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Library ex = new Library();
                ex.setVisible(true);
            }
        });
    }
}

class HelloThread extends Thread {

	BlockingQueue<Integer> queue;
	Connector connector;
	String channel;
	Integer lastVal;
	long time;
    public void run() {
        System.out.println("Hello from a thread:" + channel);
        while(true) {
        	Integer val = null;
			try {
				val = queue.take();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	if(null != val) {
        		lastVal = val;
        		time = System.nanoTime();
        	}
        	if(null != lastVal && queue.isEmpty()) {
        	//	if(System.nanoTime() - time > 10) {
        			connector.setBrightness(channel, lastVal);
        			lastVal = null;
        	//	}
        	}
        }
    }

    public HelloThread(BlockingQueue<Integer> queue, Connector connector, String channel) {
    	this.queue = queue;
    	this.connector = connector;
    	this.channel = channel;
    }
}