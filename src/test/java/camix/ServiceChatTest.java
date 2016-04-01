package camix;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Optional;

/**
 * Test du ServiceChat.
 *
 * @author Anael Chardan
 */
public class ServiceChatTest extends TestCase
{
    /**
     * Le client chat.
     */
    private ClientChat clientChat;

    /**
     * Crée, avec EasyMock, un simulacre de clientChat nécessaire aux tests.
     * <p>Code exécuté avant les tests.</p>
     *
     * @throws Exception toute exception.
     * @see org.easymock.EasyMock
     */
    @Before
    public void setUp() throws Exception {
        this.clientChat = EasyMock.createMock(ClientChat.class);
    }

    /**
     * Permet de tester le bon comportement de la methode informeDepatClient dans la classe ServiceChat.
     */
    @Test
    public void testInformeDepartClient() {
        final String idClient = "nanouClient";
        final String nameCanal = "nanouCanal";
        final String message = String.format(ProtocoleChat.MESSAGE_DEPART_CHAT, idClient);

        ServiceChat serviceChat = new ServiceChat(nameCanal, 12345);
        EasyMock.expect(clientChat.donneSurnom()).andReturn(idClient);

        EasyMock.expectLastCall().times(1);

        //Permet de tester que l'envoie est bien fait
        clientChat.envoieContacts(message);

        EasyMock.replay(clientChat);

        serviceChat.informeDepartClient(clientChat);

        EasyMock.verify(clientChat);
    }

    /**
     * Test l'ajout d'un canal non présent.
     */
    @Test
    public void testAjouteCanalNonPresent() {
        this.ajoutCanal(false);
    }

    /**
     * Test l'ajout d'un canal déjà présent.
     */
    @Test
    public void testAjouteCanalDejaPresent() {
        this.ajoutCanal(true);
    }

    /**
     * Methode générique pour tester l'ajout d'un canal.
     * @param moreThanOne Est ce que l'on ajoute plusieurs fois le meme canal
     */
    protected void ajoutCanal(boolean moreThanOne) {
        //Initialisation des test
        Integer port = 12345;
        String nameChatDefault = "nanouServiceChat";
        String nameCanal = "nanouCanal";

        ServiceChat serviceChat = new ServiceChat(nameChatDefault, port);

        //Recuperation des canaux via introspection afin de tester que par défaut c'est 1
        Optional<Hashtable<String, CanalChat>> canaux = introspectField(serviceChat, "canaux");

        if (canaux.isPresent()) {
            assertEquals("Nombre de canaux par défaut", canaux.get().size(), 1);
        } else {
            fail("Récupération de l'attribut \"canaux\" non disponible");
        }

        //On envoie systematiquement un message
        String messageSuccess = String.format(ProtocoleChat.MESSAGE_CREATION_CANAL, nameCanal);

        this.clientChat.envoieMessage(messageSuccess);

        EasyMock.replay(clientChat);

        serviceChat.ajouteCanal(clientChat, nameCanal);

        EasyMock.verify(clientChat);

        //On veut on ajouter plus et ca ne fonctionnera pas
        if (moreThanOne) {
            EasyMock.reset(clientChat);

            String messageEchec = String.format(ProtocoleChat.MESSAGE_CREATION_IMPOSSIBLE_CANAL, nameCanal);
            this.clientChat.envoieMessage(messageEchec);

            EasyMock.replay(clientChat);

            serviceChat.ajouteCanal(clientChat, nameCanal);

            EasyMock.verify(clientChat);
        }

        //Malgre tout le canal de base est present et le nouveau que lon a ajouter
        if (canaux.isPresent()) {
            assertEquals("Nombre de canaux par défaut", canaux.get().size(), 2);
        } else {
            fail("Récupération de l'attribut \"canaux\" non disponible");
        }
    }

    /**
     * Permet d'introspecter un objet et de récupérer le champs que l'on veut.
     *
     * @param concernedObject L'objet concerné
     * @param field Le champ concerné
     * @return
     */
    private static <T> Optional<T> introspectField(Object concernedObject, String field) {
        try {
            Field attribute = concernedObject.getClass().getDeclaredField(field);
            attribute.setAccessible(true);
            return Optional.of((T) attribute.get(concernedObject));
        } catch (IllegalAccessException e) {
            fail("Illegal access");
        } catch (NoSuchFieldException e) {
            fail("Concerned attribute");
        }

        return Optional.empty();
    }
}
