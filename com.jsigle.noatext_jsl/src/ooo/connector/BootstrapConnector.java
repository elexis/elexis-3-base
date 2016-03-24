package ooo.connector;

import com.sun.star.bridge.UnoUrlResolver;
import com.sun.star.bridge.XUnoUrlResolver;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.connection.ConnectionSetupException;
import com.sun.star.connection.NoConnectException;
import com.sun.star.frame.XDesktop;
import com.sun.star.lang.IllegalArgumentException;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

import ag.ion.noa4e.internal.ui.preferences.LocalOfficeApplicationPreferencesPage;
import ooo.connector.server.OOoServer;

/**
 * A bootstrap connector which establishes a connection to an OOo server.
 * 
 * Most of the source code in this class has been taken from the Java class
 * "Bootstrap.java" (Revision: 1.15) from the UDK projekt (Uno Software Develop-
 * ment Kit) from OpenOffice.org (http://udk.openoffice.org/). The source code
 * is available for example through a browser based online version control
 * access at http://udk.openoffice.org/source/browse/udk/. The Java class
 * "Bootstrap.java" is there available at
 * http://udk.openoffice.org/source/browse/udk/javaunohelper/com/sun/star/comp/helper/Bootstrap.java?view=markup
 * 
 * The idea to develop this BootstrapConnector comes from the blog "Getting
 * started with the OpenOffice.org API part III : starting OpenOffice.org with
 * jars not in the OOo install dir by Wouter van Reeven"
 * (http://technology.amis.nl/blog/?p=1284) and from various posts in the
 * "(Unofficial) OpenOffice.org Forum" at http://www.oooforum.org/ and the
 * "OpenOffice.org Community Forum" at http://user.services.openoffice.org/
 * complaining about "no office executable found!".
 */
public class BootstrapConnector {

    /** The OOo server. */
    private OOoServer oooServer;
    
    /** The connection string which has ben used to establish the connection. */
    private String oooConnectionString;

    /**
     * Constructs a bootstrap connector which uses the folder of the OOo
     * installation containing the soffice executable.
     * 
     * @param   oooExecFoder   The folder of the OOo installation containing the soffice executable
     */
    public BootstrapConnector(String oooExecFolder) {
        
        this.oooServer = new OOoServer(oooExecFolder);
        this.oooConnectionString = null;
    }

    /**
     * Constructs a bootstrap connector which connects to the specified
     * OOo server.
     * 
     * @param   oooServer   The OOo server
     */
    public BootstrapConnector(OOoServer oooServer) {

        this.oooServer = oooServer;
        this.oooConnectionString = null;
    }

    /**
     * Connects to an OOo server using the specified accept option and
     * connection string and returns a component context for using the
     * connection to the OOo server.
     * 
     * The accept option and the connection string should match to get a
     * connection. OOo provides to different types of connections:
     * 1) The socket connection
     * 2) The named pipe connection
     * 
     * To create a socket connection a host and port must be provided.
     * For example using the host "localhost" and the port "8100" the
     * accept option and connection string looks like this:
     * - accept option    : -accept=socket,host=localhost,port=8100;urp;
     * - connection string: uno:socket,host=localhost,port=8100;urp;StarOffice.ComponentContext
     * 
     * To create a named pipe a pipe name must be provided. For example using
     * the pipe name "oooPipe" the accept option and connection string looks
     * like this:
     * - accept option    : -accept=pipe,name=oooPipe;urp;
     * - connection string: uno:pipe,name=oooPipe;urp;StarOffice.ComponentContext
     * 
     * @param   oooAcceptOption       The accept option
     * @param   oooConnectionString   The connection string
     * @return                        The component context
     */
    public XComponentContext connect(String oooAcceptOption, String oooConnectionString) throws BootstrapException {

        System.out.println("BootstrapConnector: connect(oooAcceptOption, oooConnectionString) begin");

        this.oooConnectionString = oooConnectionString;

        if (this.oooConnectionString==null)	System.out.println("BootstrapConnector: connect(1): this.oooConnectionString==null");
        else								System.out.println("BootstrapConnector: connect(1): this.oooConnectionString="+ this.oooConnectionString.toString());

        XComponentContext xContext = null;
        try {
            // get local context
            XComponentContext xLocalContext = getLocalContext();

            if (oooAcceptOption==null)		System.out.println("BootstrapConnector: connect(1): oooAcceptOption==null");
            else							System.out.println("BootstrapConnector: connect(1): oooAcceptOption="+ oooAcceptOption.toString());
            
            System.out.println("BootstrapConnector: connect(1): about to oooServer.start(oooAcceptOption)...");

            oooServer.start(oooAcceptOption);

            // initial service manager
            System.out.println("BootstrapConnector: connect(1): about to xLocalContext.getServiceManager()...");

            XMultiComponentFactory xLocalServiceManager = xLocalContext.getServiceManager();
            if ( xLocalServiceManager == null )
                throw new BootstrapException("no initial service manager!");

            // create a URL resolver
            System.out.println("BootstrapConnector: connect(1): about to UnoUrlResolver.create(xLocalContext)...");
            XUnoUrlResolver xUrlResolver = UnoUrlResolver.create(xLocalContext);

            if (xUrlResolver==null)			System.out.println("BootstrapConnector: connect(1): xUrlResolver==null");
            else							System.out.println("BootstrapConnector: connect(1): xUrlResolver="+ xUrlResolver.toString());
            
			//20130310js: Introduced in NOAText_jsl Version 1.4.8:
            //The maximum loop count for the connection attempt is user configurable via the NOAText_jsl configuration dialog.
            //Although this setting would not have to be read as often as timoutThreadedWatchdog in LoadDocumentOperation,
			//I'm using the same mechanism as over there and read it from a public integer variable. 
            //Only 1 imports has been added to this file for this purpose.		
			Integer timeoutBootstrapConnect=LocalOfficeApplicationPreferencesPage.getTimeoutBootstrapConnect();
			
			if (timeoutBootstrapConnect<30) {
				System.out.println("BootstrapConnector: WARNING: You're allowing "+timeoutBootstrapConnect / 2+"+ seconds for Office to load and become available for a connection. Is this realistic?");				
			}
            
            
            // wait until office is started
            System.out.println("BootstrapConnector: connect(1): Loop: Try to getRemoteContext()...");
            for (int i = 0;; ++i) {
                try {
                    System.out.println("BootstrapConnector: connect(1): about to getRemoteContext(xUrlResolver)...");
                    xContext = getRemoteContext(xUrlResolver);
                    System.out.println("BootstrapConnector: connect(1): getRemoteContext(xUrlResolver) has returned. break...");
                    break;
                } catch ( com.sun.star.connection.NoConnectException ex ) {
                    System.out.println("BootstrapConnector: connect(1): WARNING: caught NoConnectException: "+i+"...");
                    // Wait 500 ms, then try to connect again, but do not wait
                    // longer than 5 min (= 600 * 500 ms) total:
                    // 201302190302js: changed this from 600 down to 60, i.e. 30 sec max. if (i == 600) {
                    // 201303100428js: hopefully got this configurable now from the NOAText_jsl preferences dialog. >= instead of == is important now!
                    if (i >= timeoutBootstrapConnect) {
                    	System.out.println("BootstrapConnector: connect(1): WARNING: timeoutBootstrapConnect reached, so throwing new BootstrapException() to inform parent.");
                        throw new BootstrapException(ex.toString());
                    } 
                    System.out.println("BootstrapConnector: connect(1): WARNING: calling Thread.sleep(500) before trying again...");
                    Thread.sleep(500);
                }
                System.out.println("BootstrapConnector: connect(1): TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                System.out.println("BootstrapConnector: connect(1): TODO: Maybe we should handle other types of exceptions by breaking the loop immediately.");
                System.out.println("BootstrapConnector: connect(1): TODO: After all, e.g. invalid variables passed won't improve simply by retrying.     js ");
                System.out.println("BootstrapConnector: connect(1): TODO: !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                
                //js: getRemoteContext() could also throw other types of exception.
                //js: But NoConnectException() is probably the only one that should be handled by waiting and trying again -
                //js: because it might occur when the office software package would require just a bit more time to start up.
            }
            System.out.println("BootstrapConnector: connect(1): Loop trying to getRemoteContext() is over.");
        } catch (java.lang.RuntimeException e) {
            System.out.println("BootstrapConnector: connect(1): WARNING: caught RuntimeException e; throwing e to inform parent.");
            throw e;
        } catch (java.lang.Exception e) {
            System.out.println("BootstrapConnector: connect(1): WARNING: caught Exception; throwing new BootstrapException(e) to inform parent.");
            throw new BootstrapException(e);
        }

        System.out.println("BootstrapConnector: connect(1): About to return xContext:");
        if (xContext==null)			System.out.println("BootstrapConnector: connect(1): xContext==null");
        else						System.out.println("BootstrapConnector: connect(1): xContext="+ xContext.toString());

        return xContext;
    }

    /**
     * Disconnects from an OOo server using the connection string from the
     * previous connect.
     * 
     * If there has been no previous connect, the disconnects does nothing.
     * 
     * If there has been a previous connect, disconnect tries to terminate
     * the OOo server and kills the OOo server process the connect started.
     */
    public void disconnect() {
    	System.out.println("BootstrapConnector: disconnect() begin");

        if (oooConnectionString == null)	{
        	System.out.println("BootstrapConnector: disconnect(): WARNING: oooConnectionString==null, so simply returning.");
            return;
        }

        // call office to terminate itself
        try {
        	System.out.println("BootstrapConnector: disconnect(): Trying to call office to terminate itself.");

        	// get local context
            XComponentContext xLocalContext = getLocalContext();

            // create a URL resolver
            XUnoUrlResolver xUrlResolver = UnoUrlResolver.create(xLocalContext);

            // get remote context
            XComponentContext xRemoteContext = getRemoteContext(xUrlResolver);

            // get desktop to terminate office
            Object desktop = xRemoteContext.getServiceManager().createInstanceWithContext("com.sun.star.frame.Desktop",xRemoteContext);
            XDesktop xDesktop = (XDesktop) UnoRuntime.queryInterface(XDesktop.class, desktop);
            xDesktop.terminate();
        }
        catch (Exception e) {
            // Bad luck, unable to terminate office
        	System.out.println("BootstrapConnector: disconnect(): Bad luck - unable to terminate office.");
        }

    	System.out.println("BootstrapConnector: disconnect(): about to oooServer.kill(); oooConnectionString = null...");

    	oooServer.kill();
        oooConnectionString = null;
    }

    /**
     * Create default local component context.
     * 
     * @return      The default local component context
     */
    private XComponentContext getLocalContext() throws BootstrapException, Exception {

    	System.out.println("BootstrapConnector: getLocalContext() begin");

    	XComponentContext xLocalContext = Bootstrap.createInitialComponentContext(null);
        if (xLocalContext == null) {
            throw new BootstrapException("no local component context!");
        }
        return xLocalContext;
    }

    /**
     * Try to connect to office.
     * 
     * @return      The remote component context
     */
    private XComponentContext getRemoteContext(XUnoUrlResolver xUrlResolver) throws BootstrapException, ConnectionSetupException, IllegalArgumentException, NoConnectException {

    	System.out.println("BootstrapConnector: getRemoteContext(xUrlResolver) begin");

    	if (oooConnectionString==null)	System.out.println("BootstrapConnector: getRemoteContext(1): oooConnectionString==null");
        else							System.out.println("BootstrapConnector: getRemoteContext(1): oooConnectionString="+oooConnectionString);
    	
    	System.out.println("BootstrapConnector: getRemoteContext(1): about to xUrlResolver.resolve(oooConnectionString)...");
        
        Object context = xUrlResolver.resolve(oooConnectionString);
    	
        System.out.println("BootstrapConnector: getRemoteContext(1): xUrlResolver.resolve(oooConnectionString) returned");

        if (context==null)				System.out.println("BootstrapConnector: getRemoteContext(1): context==null");
        else							System.out.println("BootstrapConnector: getRemoteContext(1): context="+context.toString());
        
        System.out.println("BootstrapConnector: getRemoteContext(1): about to UnoRuntime.queryInterface(XComponentContext.class, context)...");
        
    	XComponentContext xContext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, context);

        System.out.println("BootstrapConnector: getRemoteContext(1): UnoRuntime.queryInterface(XComponentContext.class, context) returned");
        
        if (xContext==null)				System.out.println("BootstrapConnector: getRemoteContext(1): xContext==null");
        else							System.out.println("BootstrapConnector: getRemoteContext(1): xContext="+context.toString());
    	
    	if (xContext == null) {
            throw new BootstrapException("no component context!");
        }

        System.out.println("BootstrapConnector: getRemoteContext(1): about to return xContext");
        return xContext;
    }

    /**
     * Bootstraps a connection to an OOo server in the specified soffice
     * executable folder of the OOo installation using the specified accept
     * option and connection string and returns a component context for using
     * the connection to the OOo server.
     * 
     * The accept option and the connection string should match in connection
     * type and pipe name or host and port to get a connection.
     * 
     * @param   oooExecFolder         The folder of the OOo installation containing the soffice executable
     * @param   oooAcceptOption       The accept option
     * @param   oooConnectionString   The connection string
     * @return                        The component context
     */
    public static final XComponentContext bootstrap(String oooExecFolder, String oooAcceptOption, String oooConnectionString) throws BootstrapException {

    	System.out.println("BootstrapConnector: bootstrap(oooExecFolder, oooAcceptOption, oooConnectionString) begin");
      	if (oooExecFolder==null)		System.out.println("BootstrapConnector: bootstrap(3): oooExecFolder==null");
        else							System.out.println("BootstrapConnector: bootstrap(3): oooExecFolder="+oooExecFolder);
      	if (oooAcceptOption==null)		System.out.println("BootstrapConnector: bootstrap(3): oooAcceptOption==null");
        else							System.out.println("BootstrapConnector: bootstrap(3): oooAcceptOption="+oooAcceptOption);
      	if (oooConnectionString==null)	System.out.println("BootstrapConnector: bootstrap(3): oooConnectionString==null");
        else							System.out.println("BootstrapConnector: bootstrap(3): oooConnectionString="+oooConnectionString);
  
    	BootstrapConnector bootstrapConnector = new BootstrapConnector(oooExecFolder);
        return bootstrapConnector.connect(oooAcceptOption, oooConnectionString);
    }
}