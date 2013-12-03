package org.o2i2b2.loader;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.Wizard;
import org.o2i2b2.dao.objects.MetadataTableAccess;
import org.o2i2b2.i2b2.schema.O2I2B2;
import org.o2i2b2.utils.JdbcTemplateFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

//class ImportData {
//	String importFilePath;
//
//	String metadataDatabaseType;
//	String metadataHost;
//	String metadataPort;
//	String metadataDatabase;
//	String metadataUsername;
//	String metadataPassword;
//
//	String starSchemaDatabaseType;
//	String starSchemaHost;
//	String starSchemaPort;
//	String starSchemaDatabase;
//	String starSchemaUsername;
//	String starSchemaPassword;
//
//	boolean clearI2b2Ind = true;
//	MetadataTableAccess metadataTableAccess;
//
//	java.util.Map<String, MetadataTableAccess> metadataTableAccessHashMap = new HashMap<String, MetadataTableAccess>();
//
//	private Parser parser = null;
//	private O2I2B2 o2i2b2 = null;
//
//	protected Parser getParser() {
//		if (parser == null) parser = new Parser(importFilePath);
//		return parser;
//	}
//
//	protected O2I2B2 getO2i2b2() {
//		if (o2i2b2 == null) o2i2b2 = getParser().getO2i2b2Object();
//		return o2i2b2;
//	}
//
//	public String toString() {
//		StringBuffer sb = new StringBuffer();
//		sb.append("* i2b2 Import Details *\n");
//		sb.append(App.messages.getString("app.text.import") + ":\t" + importFilePath + "\n");
//		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.type") + ":\t" + metadataDatabaseType + "\n");
//		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.hostnameOrIP") + ":\t" + metadataHost + "\n");
//		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.port") + ":\t" + metadataPort + "\n");
//		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.database") + ":\t" + metadataDatabase + "\n");
//		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.username") + ":\t" + metadataUsername + "\n");
//		sb.append("Metadata " + App.messages.getString("app.wizard.label.db.password") + ":\t" + metadataPassword + "\n");
//		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.type") + ":\t" + starSchemaDatabaseType + "\n");
//		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.hostnameOrIP") + ":\t" + starSchemaHost + "\n");
//		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.port") + ":\t" + starSchemaPort + "\n");
//		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.database") + ":\t" + starSchemaDatabase + "\n");
//		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.username") + ":\t" + starSchemaUsername + "\n");
//		sb.append("Star Schema " + App.messages.getString("app.wizard.label.db.password") + ":\t" + starSchemaPassword + "\n");
//
//		return sb.toString();
//	}
//}

//class ImportWizard extends Wizard implements IPageChangeProvider {
class ImportWizard extends Wizard {

	static final String DIALOG_SETTING_FILE = "userInfo.xml";

	static final String KEY_IMPORT_FILE_PATH = "import-file-path";
	static final String KEY_METADATA_DB_TYPE = "metadata-db-type";
	static final String KEY_METADATA_HOST = "metadata-db-host";
	static final String KEY_METADATA_PORT = "metadata-db-port";
	static final String KEY_METADATA_DATABASE = "metadata-db-database";
	static final String KEY_METADATA_USERNAME = "metadata-db-username";
	static final String KEY_METADATA_PASSWORD = "metadata-db-password";
	static final String KEY_STARSCHEMA_DB_TYPE = "starschema-db-type";
	static final String KEY_STARSCHEMA_HOST = "starschema-db-host";
	static final String KEY_STARSCHEMA_PORT = "starschema-db-port";
	static final String KEY_STARSCHEMA_DATABASE = "starschema-db-database";
	static final String KEY_STARSCHEMA_USERNAME = "starschema-db-username";
	static final String KEY_STARSCHEMA_PASSWORD = "starschema-db-password";

	Page4 page4 = new Page4();

	// the model object. 
	ImportData data = new ImportData();

	public ImportWizard() {
		setWindowTitle(App.messages.getString("app.window.title"));
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(ImageDescriptor.createFromFile(App.class, "images/i2b2Import2.png"));

		DialogSettings dialogSettings = new DialogSettings("userInfo");
		try {
			// loads existing settings if any. 
			dialogSettings.load(DIALOG_SETTING_FILE);
		} catch (IOException e) {
            System.out.println("INFO: Unable to locate user settings file.  Default values will be used.");
			//e.printStackTrace();
		}    

		setDialogSettings(dialogSettings);



	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	public void addPages() {
		addPage(new Page1());
		addPage(new Page2());
		addPage(new Page3());
		addPage(page4);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish() {
		if(getDialogSettings() != null) {
			getDialogSettings().put(KEY_IMPORT_FILE_PATH, data.importFilePath);
			getDialogSettings().put(KEY_METADATA_DB_TYPE, data.metadataDatabaseType);
			getDialogSettings().put(KEY_METADATA_HOST, data.metadataHost);
			getDialogSettings().put(KEY_METADATA_PORT, data.metadataPort);
			getDialogSettings().put(KEY_METADATA_DATABASE, data.metadataDatabase);
			getDialogSettings().put(KEY_METADATA_USERNAME, data.metadataUsername);
			getDialogSettings().put(KEY_METADATA_PASSWORD, data.metadataPassword);
			getDialogSettings().put(KEY_STARSCHEMA_DB_TYPE, data.starSchemaDatabaseType);
			getDialogSettings().put(KEY_STARSCHEMA_HOST, data.starSchemaHost);
			getDialogSettings().put(KEY_STARSCHEMA_PORT, data.starSchemaPort);
			getDialogSettings().put(KEY_STARSCHEMA_DATABASE, data.starSchemaDatabase);
			getDialogSettings().put(KEY_STARSCHEMA_USERNAME, data.starSchemaUsername);
			getDialogSettings().put(KEY_STARSCHEMA_PASSWORD, data.starSchemaPassword);
			try {
				// Saves the dialog settings into the specified file. 
				getDialogSettings().save(DIALOG_SETTING_FILE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		final JdbcTemplate metadataJdbcTemplate = new JdbcTemplateFactory(data.metadataDatabaseType, data.metadataHost, data.metadataPort, data.metadataDatabase, data.metadataUsername, data.metadataPassword).getJdbcTemplate();
		final JdbcTemplate starSchemaJdbcTemplate = new JdbcTemplateFactory(data.starSchemaDatabaseType, data.starSchemaHost, data.starSchemaPort, data.starSchemaDatabase, data.starSchemaUsername, data.starSchemaPassword).getJdbcTemplate();

		if (metadataJdbcTemplate != null && starSchemaJdbcTemplate != null) {
			final O2I2B2 o2i2b2 = data.getO2i2b2();

			if (o2i2b2 != null & o2i2b2.getOrgCode() != null && o2i2b2.getOrgName() != null) {
				try {
					getContainer().run(true, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {

							Initializer initializer = new Initializer(metadataJdbcTemplate, starSchemaJdbcTemplate, o2i2b2, ImportUtils.getSourceId(o2i2b2.getOrgCode()), data.metadataTableAccess);

							if (data.clearI2b2Ind) {
								monitor.beginTask(MessageFormat.format(App.messages.getString("app.initializer.status.clear.default"),o2i2b2.getOrgName()), 12);
							} else {
								initializer.setSkipClear(true);
								monitor.beginTask(MessageFormat.format(App.messages.getString("app.initializer.status.clear.default"),o2i2b2.getOrgName()), 10);
							}

							Thread initThread = new Thread(initializer);
							initThread.start();

							String previousMessage = "";
							int previousStep = 0;

							while (!initializer.isComplete()) {
								String currentMessage = initializer.getMessage();
								if (!currentMessage.equals(previousMessage)) {
									previousMessage = currentMessage;
									monitor.setTaskName(currentMessage);
								}
								int currentStep = initializer.getStep();
								if (previousStep < currentStep) {
									monitor.worked(currentStep - previousStep);
									previousStep = currentStep;
								}
								Thread.sleep(500);
								if (monitor.isCanceled()) {
									initializer.setInteruptMe(true);
									break;
								}
								if (ImportData.isFatalError()) {
									initializer.setInteruptMe(true);
									break;
								}
							}

							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				try {
					getContainer().run(true, true, new IRunnableWithProgress() {
						public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {


							data.getParser().setMetadataJdbcTemplate(metadataJdbcTemplate);
							data.getParser().setStarSchemaJdbcTemplate(starSchemaJdbcTemplate);
							data.getParser().setO2i2b2(o2i2b2);
							data.getParser().setSourceId(ImportUtils.getSourceId(o2i2b2.getOrgCode()));
							data.getParser().setMetadataTableAccess(data.metadataTableAccess);

							// TODO: Perhaps run this in a separate thread and timeout if it's taking too long
							int steps = data.getParser().countSteps();

							monitor.beginTask(MessageFormat.format(App.messages.getString("app.parser.status.purge"),o2i2b2.getOrgName()), steps);

							Thread parseThread = new Thread(data.getParser());
							parseThread.start();

							int previousStep = 0;
							String previousMessage = "";

							while (!data.getParser().isComplete()) {
								String currentMessage = data.getParser().getMessage();
								if (!currentMessage.equals(previousMessage)) {
									previousMessage = currentMessage;
									monitor.setTaskName(currentMessage);
								}
								int currentStep = data.getParser().getStep();
								if (previousStep < currentStep) {
									monitor.worked(currentStep - previousStep);
									previousStep = currentStep;
								}
								Thread.sleep(500);
								if (monitor.isCanceled()) {
									data.getParser().setInteruptMe(true);
									break;
								}
								if (ImportData.isFatalError()) {
									data.getParser().setInteruptMe(true);
									break;
								}

							}
							monitor.done();
						}
					});
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (ImportData.isFatalError()) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
					messageBox.setText(App.messages.getString("app.parser.status.error"));
					messageBox.setMessage(MessageFormat.format(App.messages.getString("app.wizard.message.error"), ImportData.getFatalErrorMessage()));
					messageBox.open();					
				} else if (data.getParser().isInteruptMe()) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_WARNING);
					messageBox.setText(App.messages.getString("app.parser.status.canceled"));
					messageBox.setMessage(App.messages.getString("app.wizard.message.canceled"));
					messageBox.open();					
				} else {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION);
					messageBox.setText(App.messages.getString("app.parser.status.complete"));
					messageBox.setMessage(App.messages.getString("app.wizard.message.complete"));
					messageBox.open();
				}
			} else {
				MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
				messageBox.setText(App.messages.getString("app.wizard.message.parse.o2i2b2.error.title"));
				messageBox.setMessage(App.messages.getString("app.wizard.message.parse.o2i2b2.error.message"));
				messageBox.open();				
				return false;
			}
		} else {
			MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
			messageBox.setText(App.messages.getString("app.wizard.message.database.connection.error.title"));
			messageBox.setMessage(App.messages.getString("app.wizard.message.database.connection.error.message"));
			messageBox.open();
			return false;
		}

		return true;
	}



	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	public boolean performCancel() {
		boolean ans = MessageDialog.openConfirm(getShell(), App.messages.getString("app.wizard.event.cancel.title"), App.messages.getString("app.wizard.event.cancel.message"));
		if(ans)
			return true;
		else
			return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page instanceof Page4) {
			
			
			if (data.metadataTableAccessHashMap == null || data.metadataTableAccessHashMap.size() == 0) {
				System.out.println("populate metadata dropdown...");
				
				java.util.List<MetadataTableAccess> metadataOntologies = new ArrayList<MetadataTableAccess>();

				final JdbcTemplate metadataJdbcTemplate = new JdbcTemplateFactory(data.metadataDatabaseType, data.metadataHost, data.metadataPort, data.metadataDatabase, data.metadataUsername, data.metadataPassword).getJdbcTemplate();
				try {
					metadataOntologies = (java.util.List<MetadataTableAccess>) metadataJdbcTemplate.query("SELECT c_table_cd, c_table_name, c_fullname, c_name FROM i2b2metadata.table_access", new RowMapper() {
						public Object mapRow(ResultSet resultSet, int rowNum)
						throws SQLException {
							MetadataTableAccess metadataTableAccess = new MetadataTableAccess();
							metadataTableAccess.setTableCode(resultSet.getString(1));
							metadataTableAccess.setTableName(resultSet.getString(2));
							metadataTableAccess.setPath(resultSet.getString(3));
							metadataTableAccess.setDisplayName(resultSet.getString(4));
							return metadataTableAccess;
						}
					}
					);
				} catch (Exception e) {
					e.printStackTrace();
				}

				page4.clearOntologyRoots();
				page4.addOntologyRoot("", null);
				page4.addOntologyRoot(App.messages.getString("app.wizard.label.new.ontologyRoot.option"),null);
				for (MetadataTableAccess metadataOntology : metadataOntologies) page4.addOntologyRoot(String.format("%s (%s)", metadataOntology.getDisplayName(), metadataOntology.getTableName()), metadataOntology);
			}

		}

		return super.getNextPage(page);
	}

	@Override
	public boolean canFinish() {
//		return (data.metadataTableAccess == null) ? false : super.canFinish();
 		if (data.metadataTableAccess == null) { 
			return false;
		} else {
			return true;
		}
	} 


}

class Page2 extends WizardPage {
	Combo comboDatabaseType;
	Text textHost;
	Text textPort;
	Text textDatabase;
	Text textUsername;
	Text textPassword;

	public Page2() {
		super("MetadataDBSettings");
		setTitle(App.messages.getString("app.wizard.page2.title"));
		setDescription(App.messages.getString("app.wizard.page2.description"));

		setPageComplete(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.type"));
		comboDatabaseType = new Combo(composite, SWT.READ_ONLY | SWT.BORDER);
		comboDatabaseType.add("Oracle");
		comboDatabaseType.add("Microsoft SQL Server");
		comboDatabaseType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.hostnameOrIP"));
		textHost = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textHost.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.port"));
		textPort = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.database"));
		textDatabase = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.username"));
		textUsername = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textUsername.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.password"));
		textPassword = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		textPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL);
		Button btnTestConnection = new Button(composite, SWT.NONE );
		btnTestConnection.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JdbcTemplateFactory jdbcTemplateFactory = new JdbcTemplateFactory(comboDatabaseType.getText().trim(), textHost.getText().trim(), textPort.getText().trim(), textDatabase.getText().trim(), textUsername.getText().trim(), textPassword.getText().trim());
				if (jdbcTemplateFactory.testConnection()) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION);
					messageBox.setText(App.messages.getString("app.wizard.message.connection.test.title.success"));
					messageBox.setMessage(App.messages.getString("app.wizard.message.connection.test.message.success"));
					messageBox.open();
				} else {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
					messageBox.setText(App.messages.getString("app.wizard.message.connection.test.title.error"));
					messageBox.setMessage(App.messages.getString("app.wizard.message.connection.test.message.error"));
					messageBox.open();
				}
			}
		});
		btnTestConnection.setText(App.messages.getString("app.button.testConnection"));

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				//				if (event.widget == null || !(event.widget instanceof Text)) return;
				if (event.widget == null) return;

				String string = "";

				if (event.widget instanceof Text) {
					string = ((Text) event.widget).getText();
				} else if (event.widget instanceof Combo) {
					string = ((Combo) event.widget).getText().trim();
				} else {
					return;
				}

				if (event.widget == comboDatabaseType) {
					((ImportWizard) getWizard()).data.metadataDatabaseType =
						string;
				} else if (event.widget == textHost) {
					((ImportWizard) getWizard()).data.metadataHost =
						string;
				} else if (event.widget == textPort) {
					if (string.length() > 0) {
						try {
							new Integer(string);
							setErrorMessage(null);
						} catch (Exception e) {
							setErrorMessage("Invalid port number.");
						}
					} else {
						setErrorMessage(null);
					}

					((ImportWizard) getWizard()).data.metadataPort =
						string;
				} else if (event.widget == textDatabase) {
					((ImportWizard) getWizard()).data.metadataDatabase =
						string;
				} else if (event.widget == textUsername) {
					((ImportWizard) getWizard()).data.metadataUsername =
						string;
				} else if (event.widget == textPassword) {
					((ImportWizard) getWizard()).data.metadataPassword =
						string;
				}

				ImportData data = ((ImportWizard) getWizard()).data;

				if (data.metadataDatabaseType != null
						&& data.metadataHost != null
						&& data.metadataPort != null
						&& data.metadataDatabase != null
						&& data.metadataUsername != null
						&& data.metadataPassword != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		};

		comboDatabaseType.addListener(SWT.Selection, listener);
		textHost.addListener(SWT.Modify, listener);
		textPort.addListener(SWT.Modify, listener);
		textDatabase.addListener(SWT.Modify, listener);
		textUsername.addListener(SWT.Modify, listener);
		textPassword.addListener(SWT.Modify, listener);

		if (getDialogSettings() != null && validDialogSettings()) {

			((ImportWizard) getWizard()).data.metadataDatabaseType = getDialogSettings().get(ImportWizard.KEY_METADATA_DB_TYPE);
			((ImportWizard) getWizard()).data.metadataHost = getDialogSettings().get(ImportWizard.KEY_METADATA_HOST);
			((ImportWizard) getWizard()).data.metadataPort = getDialogSettings().get(ImportWizard.KEY_METADATA_PORT);
			((ImportWizard) getWizard()).data.metadataDatabase = getDialogSettings().get(ImportWizard.KEY_METADATA_DATABASE);
			((ImportWizard) getWizard()).data.metadataUsername = getDialogSettings().get(ImportWizard.KEY_METADATA_USERNAME);
			((ImportWizard) getWizard()).data.metadataPassword = getDialogSettings().get(ImportWizard.KEY_METADATA_PASSWORD);

			String metadataDbTypeTmp = getDialogSettings().get(ImportWizard.KEY_METADATA_DB_TYPE);
			if (metadataDbTypeTmp != null && metadataDbTypeTmp.trim().length() > 0) {
				if (metadataDbTypeTmp.equalsIgnoreCase("Oracle")) {
					comboDatabaseType.select(0);
				} else if (metadataDbTypeTmp.equalsIgnoreCase("Microsoft SQL Server")) {
					comboDatabaseType.select(1);
				}
			}

			textHost.setText(
					getDialogSettings().get(
							ImportWizard.KEY_METADATA_HOST));

			textPort.setText(
					getDialogSettings().get(
							ImportWizard.KEY_METADATA_PORT));

			textDatabase.setText(
					getDialogSettings().get(
							ImportWizard.KEY_METADATA_DATABASE));

			textUsername.setText(
					getDialogSettings().get(
							ImportWizard.KEY_METADATA_USERNAME));

			textPassword.setText(
					getDialogSettings().get(
							ImportWizard.KEY_METADATA_PASSWORD));

			ImportData data = ((ImportWizard) getWizard()).data;

			if (data.metadataDatabaseType != null
					&& data.metadataHost != null
					&& data.metadataPort != null
					&& data.metadataDatabase != null
					&& data.metadataUsername != null
					&& data.metadataPassword != null) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}

		}

		setControl(composite);
	}

	private boolean validDialogSettings() {
		if (getDialogSettings().get(ImportWizard.KEY_METADATA_DB_TYPE)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_METADATA_HOST)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_METADATA_PORT)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_METADATA_DATABASE)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_METADATA_USERNAME)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_METADATA_PASSWORD)
				== null)
			return false;
		return true;
	}
}

class Page3 extends WizardPage {
	Combo comboDatabaseType;
	Text textHost;
	Text textPort;
	Text textDatabase;
	Text textUsername;
	Text textPassword;

	public Page3() {
		super("StarSchemaDBSettings");
		setTitle(App.messages.getString("app.wizard.page3.title"));
		setDescription(App.messages.getString("app.wizard.page3.description"));

		setPageComplete(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.type"));
		comboDatabaseType = new Combo(composite, SWT.READ_ONLY | SWT.BORDER);
		comboDatabaseType.add("Oracle");
		comboDatabaseType.add("Microsoft SQL Server");
		comboDatabaseType.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.hostnameOrIP"));
		textHost = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textHost.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.port"));
		textPort = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textPort.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.database"));
		textDatabase = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textDatabase.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.username"));
		textUsername = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textUsername.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.db.password"));
		textPassword = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		textPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL);
		Button btnTestConnection = new Button(composite, SWT.NONE );
		btnTestConnection.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				JdbcTemplateFactory jdbcTemplateFactory = new JdbcTemplateFactory(comboDatabaseType.getText().trim(), textHost.getText().trim(), textPort.getText().trim(), textDatabase.getText().trim(), textUsername.getText().trim(), textPassword.getText().trim());
				if (jdbcTemplateFactory.testConnection()) {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_INFORMATION);
					messageBox.setText(App.messages.getString("app.wizard.message.connection.test.title.success"));
					messageBox.setMessage(App.messages.getString("app.wizard.message.connection.test.message.success"));
					messageBox.open();
				} else {
					MessageBox messageBox = new MessageBox(getShell(), SWT.ICON_ERROR);
					messageBox.setText(App.messages.getString("app.wizard.message.connection.test.title.error"));
					messageBox.setMessage(App.messages.getString("app.wizard.message.connection.test.message.error"));
					messageBox.open();
				}
			}
		});
		btnTestConnection.setText(App.messages.getString("app.button.testConnection"));

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				//				if (event.widget == null || !(event.widget instanceof Text)) return;
				if (event.widget == null) return;

				String string = "";

				if (event.widget instanceof Text) {
					string = ((Text) event.widget).getText();
				} else if (event.widget instanceof Combo) {
					string = ((Combo) event.widget).getText().trim();
				} else {
					return;
				}

				if (event.widget == comboDatabaseType) {
					((ImportWizard) getWizard()).data.starSchemaDatabaseType =
						string;
				} else if (event.widget == textHost) {
					((ImportWizard) getWizard()).data.starSchemaHost =
						string;
				} else if (event.widget == textPort) {
					if (string.length() > 0) {
						try {
							new Integer(string);
							setErrorMessage(null);
						} catch (Exception e) {
							setErrorMessage("Invalid port number.");
						}
					} else {
						setErrorMessage(null);
					}

					((ImportWizard) getWizard()).data.starSchemaPort =
						string;
				} else if (event.widget == textDatabase) {
					((ImportWizard) getWizard()).data.starSchemaDatabase =
						string;
				} else if (event.widget == textUsername) {
					((ImportWizard) getWizard()).data.starSchemaUsername =
						string;
				} else if (event.widget == textPassword) {
					((ImportWizard) getWizard()).data.starSchemaPassword =
						string;
				}

				ImportData data = ((ImportWizard) getWizard()).data;

				if (data.starSchemaDatabaseType != null
						&& data.starSchemaHost != null
						&& data.starSchemaPort != null
						&& data.starSchemaDatabase != null
						&& data.starSchemaUsername != null
						&& data.starSchemaPassword != null) {
					setPageComplete(true);
				} else {
					setPageComplete(false);
				}
			}
		};

		comboDatabaseType.addListener(SWT.Selection, listener);
		textHost.addListener(SWT.Modify, listener);
		textPort.addListener(SWT.Modify, listener);
		textDatabase.addListener(SWT.Modify, listener);
		textUsername.addListener(SWT.Modify, listener);
		textPassword.addListener(SWT.Modify, listener);

		if (getDialogSettings() != null && validDialogSettings()) {

			((ImportWizard) getWizard()).data.starSchemaDatabaseType = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_DB_TYPE);
			((ImportWizard) getWizard()).data.starSchemaHost = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_HOST);
			((ImportWizard) getWizard()).data.starSchemaPort = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_PORT);
			((ImportWizard) getWizard()).data.starSchemaDatabase = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_DATABASE);
			((ImportWizard) getWizard()).data.starSchemaUsername = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_USERNAME);
			((ImportWizard) getWizard()).data.starSchemaPassword = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_PASSWORD);

			String starSchemaDbTypeTmp = getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_DB_TYPE);
			if (starSchemaDbTypeTmp != null && starSchemaDbTypeTmp.trim().length() > 0) {
				if (starSchemaDbTypeTmp.equalsIgnoreCase("Oracle")) {
					comboDatabaseType.select(0);
				} else if (starSchemaDbTypeTmp.equalsIgnoreCase("Microsoft SQL Server")) {
					comboDatabaseType.select(1);
				}
			}

			textHost.setText(
					getDialogSettings().get(
							ImportWizard.KEY_STARSCHEMA_HOST));

			textPort.setText(
					getDialogSettings().get(
							ImportWizard.KEY_STARSCHEMA_PORT));

			textDatabase.setText(
					getDialogSettings().get(
							ImportWizard.KEY_STARSCHEMA_DATABASE));

			textUsername.setText(
					getDialogSettings().get(
							ImportWizard.KEY_STARSCHEMA_USERNAME));

			textPassword.setText(
					getDialogSettings().get(
							ImportWizard.KEY_STARSCHEMA_PASSWORD));

			ImportData data = ((ImportWizard) getWizard()).data;

			if (data.starSchemaDatabaseType != null
					&& data.starSchemaHost != null
					&& data.starSchemaPort != null
					&& data.starSchemaDatabase != null
					&& data.starSchemaUsername != null
					&& data.starSchemaPassword != null) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}

		}

		setControl(composite);
	}

	private boolean validDialogSettings() {
		if (getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_DB_TYPE)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_HOST)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_PORT)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_DATABASE)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_USERNAME)
				== null
				|| getDialogSettings().get(ImportWizard.KEY_STARSCHEMA_PASSWORD)
				== null)
			return false;
		return true;
	}
}

class Page1 extends WizardPage {

	public Page1() {
		super("ImportFilePath");
		setTitle(App.messages.getString("app.wizard.page1.title"));
		setDescription(App.messages.getString("app.wizard.page1.description"));

		setPageComplete(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		composite.setLayout(gridLayout);


		new Label(composite, SWT.NULL).setText(App.messages.getString("app.text.import"));
		final Text textImportFilePath = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textImportFilePath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button browseOutputFilePath = new Button(composite, SWT.NONE);
		browseOutputFilePath.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterPath(System.getProperty("user.home"));
				String dir = dialog.open();
				if (dir != null) textImportFilePath.setText(dir);
			}
		});
		browseOutputFilePath.setText(App.messages.getString("app.button.browse"));

		browseOutputFilePath.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				((ImportWizard)getWizard()).data.importFilePath = textImportFilePath.getText();


				if(((ImportWizard)getWizard()).data.importFilePath != null && 
						((ImportWizard)getWizard()).data.importFilePath != null)
					setPageComplete(true);
				else
					setPageComplete(false);
			}
		});

		// draws a line. 
		Label line = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;
		line.setLayoutData(gridData);

		if (getDialogSettings() != null && validDialogSettings()) {

			((ImportWizard) getWizard()).data.importFilePath = getDialogSettings().get(ImportWizard.KEY_IMPORT_FILE_PATH);

			textImportFilePath.setText(
					getDialogSettings().get(
							ImportWizard.KEY_IMPORT_FILE_PATH));

			ImportData data = ((ImportWizard) getWizard()).data;

			if (data.importFilePath != null) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}

		}


		setControl(composite);
	}

	private boolean validDialogSettings() {
		if (getDialogSettings().get(ImportWizard.KEY_IMPORT_FILE_PATH)
				== null)
			return false;
		return true;
	}

}

class Page4 extends WizardPage {

	Combo comboOntologyRoot = null;

	public Page4() {
		super("RunImport");
		setTitle(App.messages.getString("app.wizard.page4.title"));
		setDescription(App.messages.getString("app.wizard.page4.description"));

		setPageComplete(true);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(1, false);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		composite.setLayout(gridLayout);

		final Button clearI2b2Ind = new Button(composite, SWT.CHECK);
		clearI2b2Ind.setSelection(true);

		clearI2b2Ind.setText(App.messages.getString("app.wizard.label.confirm.clear"));		
		clearI2b2Ind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (clearI2b2Ind.getSelection()) {
					((ImportWizard) getWizard()).data.clearI2b2Ind = true;
				} else {
					((ImportWizard) getWizard()).data.clearI2b2Ind = false;
				}
			}
		});

		Composite composite2 = new Composite(composite, SWT.NULL);
		composite2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridLayout gridLayout2 = new GridLayout(2, false);
		composite2.setLayout(gridLayout2);


		new Label(composite2, SWT.NULL).setText(App.messages.getString("app.wizard.label.pick.ontologyRoot"));
		comboOntologyRoot = new Combo(composite2, SWT.READ_ONLY | SWT.BORDER);
		comboOntologyRoot.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		comboOntologyRoot.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String text = comboOntologyRoot.getText();

				if (text != null && text.trim().length() > 0) {
					if (text.equalsIgnoreCase(App.messages.getString("app.wizard.label.new.ontologyRoot.option"))) {
						NewOntologyDialog newOntologyDialog = new NewOntologyDialog(getShell());
						newOntologyDialog.open();

						if (newOntologyDialog.getOntologyCode() != null && newOntologyDialog.getOntologyCode().trim().length() > 0 && newOntologyDialog.getOntologyName() != null && newOntologyDialog.getOntologyName().trim().length() > 0) {
							MetadataTableAccess metadataTableAccess = new MetadataTableAccess();
							metadataTableAccess.setDisplayName(newOntologyDialog.getOntologyName());
							metadataTableAccess.setTableCode(newOntologyDialog.getOntologyCode());
							metadataTableAccess.setTableName(newOntologyDialog.getOntologyCode());
							metadataTableAccess.setPath(String.format("\\%s\\",newOntologyDialog.getOntologyCode()));
							addOntologyRoot(String.format("%s (%s)", newOntologyDialog.getOntologyName(), newOntologyDialog.getOntologyCode()), metadataTableAccess);

							((ImportWizard) getWizard()).data.metadataTableAccess = metadataTableAccess;

							comboOntologyRoot.select(((ImportWizard) getWizard()).data.metadataTableAccessHashMap.size());
						}
					} else {
						((ImportWizard) getWizard()).data.metadataTableAccess = ((ImportWizard) getWizard()).data.metadataTableAccessHashMap.get(text);
					}
					getWizard().canFinish();
					getWizard().getContainer().updateButtons();
					
				}
			}
		});


		if (getDialogSettings() != null && validDialogSettings()) {

			((ImportWizard) getWizard()).data.importFilePath = getDialogSettings().get(ImportWizard.KEY_IMPORT_FILE_PATH);

			//			textImportFilePath.setText(
			//					getDialogSettings().get(
			//							ImportWizard.KEY_IMPORT_FILE_PATH));

			ImportData data = ((ImportWizard) getWizard()).data;

			if (data.importFilePath != null) {
				setPageComplete(true);
			} else {
				setPageComplete(false);
			}

		}

		setControl(composite);

	}

	public void addOntologyRoot(String ontologyRoot, MetadataTableAccess metadataTableAccess) {
		((ImportWizard) getWizard()).data.metadataTableAccessHashMap.put(ontologyRoot, metadataTableAccess);
		comboOntologyRoot.add(ontologyRoot);
	}

	public void clearOntologyRoots() {
		((ImportWizard) getWizard()).data.metadataTableAccessHashMap = new HashMap<String, MetadataTableAccess>();
		comboOntologyRoot.removeAll();
	}

	private boolean validDialogSettings() {
		if (getDialogSettings().get(ImportWizard.KEY_IMPORT_FILE_PATH)
				== null)
			return false;
		return true;
	}



}


/**
 * This class shows an about box, based on TitleAreaDialog
 */
class NewOntologyDialog extends TitleAreaDialog {
	
	private String ontologyCode;
	private String ontologyName;
	
	/**
	 * NewOntologyDialog constructor
	 * 
	 * @param shell the parent shell
	 */
	public NewOntologyDialog(Shell shell) {
		super(shell);
	}

	/**
	 * Closes the dialog box Override so we can dispose the image we created
	 */
	public boolean close() {
		return super.close();
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param parent the parent composite
	 * @return Control
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		// Set the title
		setTitle(App.messages.getString("app.wizard.label.new.ontologyRoot.dialog.title"));

		// Set the message
		setMessage(App.messages.getString("app.wizard.label.new.ontologyRoot.dialog.message"), IMessageProvider.INFORMATION);

		return contents;
	}

	/**
	 * Creates the gray area
	 * 
	 * @param parent the parent composite
	 * @return Control
	 */
	protected Control createDialogArea(Composite parent) {
		//    Composite composite = (Composite) super.createDialogArea(parent);
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.new.ontologyRoot.code"));
		final Text textOntologyRootCode = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textOntologyRootCode.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		textOntologyRootCode.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				e.text = e.text.toUpperCase().replaceAll("[\\W]", "_");
				if (textOntologyRootCode.getText().length() >= 50) e.text = null;
			}
		});

		textOntologyRootCode.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				if (event.widget instanceof Text) {
					String text = ((Text) event.widget).getText();
					ontologyCode =  text;
				}
			}
		});
		
		new Label(composite, SWT.NULL).setText(App.messages.getString("app.wizard.label.new.ontologyRoot.name"));
		final Text textOntologyRootName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textOntologyRootName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		textOntologyRootName.addVerifyListener(new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				if (textOntologyRootName.getText().length() >= 2000) e.text = null;
			}
		});

		textOntologyRootName.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent event) {
				if (event.widget instanceof Text) {
					String text = ((Text) event.widget).getText();
					ontologyName =  text;
				}
			}
		});

		return composite;
	}

	/**
	 * Creates the buttons for the button bar
	 * 
	 * @param parent the parent composite
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

	public String getOntologyCode() {
		return ontologyCode;
	}

	public void setOntologyCode(String ontologyCode) {
		this.ontologyCode = ontologyCode;
	}

	public String getOntologyName() {
		return ontologyName;
	}

	public void setOntologyName(String ontologyName) {
		this.ontologyName = ontologyName;
	}

	@Override
	protected void cancelPressed() {
		this.ontologyCode = null;
		this.ontologyName = null;
		super.cancelPressed();
	}
}
