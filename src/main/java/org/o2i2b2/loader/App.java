package org.o2i2b2.loader;

import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import com.swtdesigner.SWTResourceManager;
import org.eclipse.swt.widgets.Link;

public class App {

	public static ResourceBundle messages = ResourceBundle.getBundle("messages", Locale.getDefault());

	protected Shell shell;
	private Button okButton;
	private Label labelIntro;
	private Label label_1;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
//System.out.println("starting app...");
			App window = new App();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();

		Monitor monitor = display.getPrimaryMonitor();
		Rectangle bounds = monitor.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shell.setLocation(x, y);
	    
	    okButton = new Button(shell, SWT.NONE);
	    okButton.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	            ImportWizard wizard = new ImportWizard();
	            
	            WizardDialog dialog = new WizardDialog(shell, wizard);
	            dialog.setBlockOnOpen(true);
	            int returnCode = dialog.open();
	            if(returnCode == Dialog.OK) {
	              //System.out.println(wizard.data);
	            } else {
	              System.out.println("Cancelled");
	            }
	    		shell.dispose();
	    	}
	    });
	    okButton.setBounds(203, 134, 75, 25);
	    okButton.setText(messages.getString("app.button.run"));

	    ImportWizard wizard = new ImportWizard();
        WizardDialog dialog = new WizardDialog(shell, wizard);
        
        Label label = new Label(shell, SWT.NONE);
        label.setImage(SWTResourceManager.getImage(App.class, "/org/o2i2b2/loader/images/i2b2Import2.png"));
        label.setBounds(13, 13, 72, 70);
        
        labelIntro = new Label(shell, SWT.WRAP);
        labelIntro.setBounds(88, 51, 251, 33);
        labelIntro.setText(messages.getString("app.intro"));
        
        label_1 = new Label(shell, SWT.NONE);
        label_1.setFont(SWTResourceManager.getFont("Segoe UI", 13, SWT.BOLD));
        label_1.setBounds(88, 19, 251, 25);
        label_1.setText(App.messages.getString("app.window.title"));
        
        Link link = new Link(shell, SWT.NONE);
        link.setBounds(88, 96, 311, 15);
        link.setText("<a>" + App.messages.getString("app.xsd.url") + "</a>");
        
		WizardDialog.setDefaultImage(SWTResourceManager.getImage(App.class, "/org/o2i2b2/loader/images/i2b2Import2.png")); 
        
        dialog.setBlockOnOpen(true);
	    
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {

		
		shell = new Shell();
		shell.setImage(SWTResourceManager.getImage(App.class, "/org/o2i2b2/loader/images/i2b2Import2.png"));
		shell.setSize(425, 207);
		shell.setText(messages.getString("app.window.title"));

	}
}
