/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * ConnectionImplTest.java
 *
 * Created on October 2, 2000, 10:36 AM
 */

package com.sun.enterprise.tools.verifier.tests.connector.cci;

import com.sun.enterprise.tools.verifier.*;
import com.sun.enterprise.deployment.ConnectorDescriptor;
import com.sun.enterprise.deployment.OutboundResourceAdapter;
import com.sun.enterprise.deployment.ConnectionDefDescriptor;
import com.sun.enterprise.tools.verifier.tests.*;
import com.sun.enterprise.tools.verifier.tests.*;
import java.util.*;

/**
 * Utility methods for all tests related to java.resource.cci.Connection 
 * interface implementation
 *
 * @author  Jerome Dochez
 * @version 
 */
public abstract class ConnectionTest extends CCITest {

   /**
     * <p>
     * Get the <code>Class</code> object of the class declared as implementing
     * the jakarta.resource.cci.Connection interface and declared in the 
     * archive deployment descriptors under the connection-impl-class
     * </p> 
     *
     * @param descriptor the rar file deployment descriptor
     * @param result instance to use to put the result of the test
     * @return Class object for the connectionimpl-class that implements
     * jakarta.resource.cci.Connection
     */
    protected Class getConnectionImpl(ConnectorDescriptor descriptor,
    Result result) 
    {
      ComponentNameConstructor compName = 
        getVerifierContext().getComponentNameConstructor();
      OutboundResourceAdapter outboundRA =
        descriptor.getOutboundResourceAdapter();
      if(outboundRA == null)
      {
        return null;
      }
      Set connDefs = outboundRA.getConnectionDefs();
      Iterator iter = connDefs.iterator();
      while(iter.hasNext()) 
      {
        ConnectionDefDescriptor connDefDesc = (ConnectionDefDescriptor)
          iter.next();
        String impl = connDefDesc.getConnectionImpl();
        Class implClass = null;
        try
        {
          implClass = Class.forName(impl, false, getVerifierContext().getClassLoader());
        } catch(ClassNotFoundException cnfe) {
          result.addErrorDetails(smh.getLocalString
              ("tests.componentNameConstructor",
               "For [ {0} ]",
               new Object[] {compName.toString()}));
          result.failed(smh.getLocalString
              ("com.sun.enterprise.tools.verifier.tests.connector.cci.ConnectionTest.nonexist",
               "Error: The class [ {0} ] as defined in the connection-impl-class deployment descriptor does not exist",
               new Object[] {impl}));
          return null;
        }            
        if(isImplementorOf(implClass, "jakarta.resource.cci.Connection"))
          return implClass;
      }
      return null;
    }


    /**
     * <p>
     * Test whether the class declared in the deployemnt descriptor under the 
     * connection-impl-class tag is available
     * </p>
     * 
     * @param descriptor the deployment descriptor
     * @param result instance to use to put the result of the test
     * @return true if the test succeeds
     */
    protected Class testConnectionImpl(ConnectorDescriptor descriptor, Result result) 
    {
      Class mcf = getConnectionImpl(descriptor, result);
      if (mcf == null) {
        ComponentNameConstructor compName = 
          getVerifierContext().getComponentNameConstructor();
        result.addErrorDetails(smh.getLocalString
            ("tests.componentNameConstructor",
             "For [ {0} ]",
             new Object[] {compName.toString()}));
        result.failed(smh.getLocalString
            ("com.sun.enterprise.tools.verifier.tests.connector.cci.ConnectionTest.nonimpl", 
             "Error: The resource adapter must implement the jakarta.resource.cci.Connection interface and declare it in the connection-impl-class deployment descriptor."));                
      }
      return mcf;
    }

    /**
     * <p>
     * Test wether the class declared in the deployemnt descriptor under the 
     * connectionfactory-impl-class tag implements an interface 
     * </p>
     * 
     * @param descriptor the deployment descriptor
     * @param interfaceName the interface name we look for
     * @param result instance to use to put the result of the test
     * @return true if the test succeeds
     */
    protected boolean testImplementationOf(ConnectorDescriptor descriptor, String interfaceName, Result result) 
    {
      Class mcf = testConnectionImpl(descriptor, result);
      if (mcf != null) 
        return testImplementationOf(mcf, interfaceName, result);
      return false;
    }
}
