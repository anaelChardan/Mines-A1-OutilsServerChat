package camix;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Classe de test du canal chat.
 *
 * @version 3.0.0.etu
 * @author Anael Charan
 */
public class CanalChatTest {

    /**
     * Le client chat.
     */
    private ClientChat clientChat;

    /**
     * Le canal chat.
     */
    private CanalChat canalChat;

    /**
     * Crée, avec EasyMock, un simulacre de clientChat nécessaire aux tests.
     *
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     *
     * @see org.easymock.EasyMock
     *
     */
    @Before
    public void setUp() throws Exception
    {
        this.clientChat = EasyMock.createMock(ClientChat.class);
        this.canalChat = new CanalChat("canalChat");
    }

    /**
     * Test de l'ajout d'une client non présent
     */
    @Test
    public void testAjouteClient_nonPresent()
    {
        this.ajoutClient(1);
    }

    /**
     * Test de l'ajout d'un client déja présent.
     */
    @Test
    public void testAjouteClient_Present()
    {
        this.ajoutClient(2);
    }

    /**
     * Méthode générique pour l'ajout de client.
     * @param times Le nombre de fois que l'on ajoute le client
     */
    private void ajoutClient(int times)
    {
        final String id = "nanou";

        EasyMock.expect(this.clientChat.donneId()).andReturn(id);
        EasyMock.expectLastCall().times(2 + 2 * times);
        EasyMock.replay(this.clientChat);
        Assert.assertEquals((int)canalChat.donneNombreClients(), 0);
        Assert.assertFalse(canalChat.estPresent(this.clientChat));

        for (int i = 0; i < times; i++) {
            canalChat.ajouteClient(this.clientChat);

            Assert.assertTrue(canalChat.estPresent(this.clientChat));
            Assert.assertEquals((int)canalChat.donneNombreClients(), 1);
        }

        EasyMock.verify(this.clientChat);
    }
}
