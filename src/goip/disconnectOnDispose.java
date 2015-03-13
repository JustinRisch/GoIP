/*
 * Copyright Orchestra Networks 2000-2013. All rights reserved.
 */
package goip;

import java.awt.event.*;
import java.util.function.*;

/**
 */
public class disconnectOnDispose implements WindowListener {
    Consumer<WindowEvent> doit; 
    public disconnectOnDispose(Consumer<WindowEvent> e) {
    doit=e;
}
	    
	    @Override
	    public void windowOpened(WindowEvent e) { }
	    
	    @Override
	    public void windowIconified(WindowEvent e) { }
	    
	    @Override
	    public void windowDeiconified(WindowEvent e) {}
	    
	    @Override
	    public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	    }
	    
	    @Override
	    public void windowClosing(WindowEvent e){
		
	    }
	    
	    @Override
	    public void windowClosed(WindowEvent e) {
		doit.accept(e);
		
	    }
	    
	    @Override
	    public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	    }
	}

