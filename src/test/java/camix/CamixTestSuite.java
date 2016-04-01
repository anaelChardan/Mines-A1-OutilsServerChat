package camix;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Classe de suite de tests unitaire pour Camix.
 *
 * @version 3.0.0.etu
 * @author Matthias Brun
 *
 */
public class CamixTestSuite
{

	/**
	 * Suite de tests pour Camix.
	 *
	 * @return la suite de tests.
	 *
	 * @see junit.framework.TestSuite
	 *
	 */	
	public static Test suite() 
	{
		final TestSuite suite = new TestSuite("Suite de tests pour Monix");

		suite.addTest(new TestSuite(CanalChatTest.class));
		suite.addTest(new TestSuite(ServiceChatTest.class));
		suite.addTest(new TestSuite(ClientChatTest.class));

		return suite;
	}
}

