package camix;

import junit.framework.TestCase;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Test du client.
 * Nous ne testons que les methodes ajoutées, c'est à dire
 * Le fait que /q appelle fermeConnexion de service chat
 *
 * @author Anael Chardan
 */
public class ClientChatTest extends TestCase
{

    /**
     * Notre mock de service chat.
     */
    ServiceChat serviceChat;

    /**
     * Création du mock de service chat.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        this.serviceChat = EasyMock.createMock(ServiceChat.class);

    }

    /**
     * Test du bon fonctionnement de la commande quitter /q.
     * @throws IOException Exception d'entrée sortie
     */
    @Test
    public void testCommandeQuitter() throws IOException {
        ClientChat clientChat = new ClientChat(serviceChat, null, "client", new CanalChat("CanalChatellite"));
        serviceChat.fermeConnexion(clientChat);
        EasyMock.expectLastCall().times(2);
        EasyMock.replay(serviceChat);

        Class<?>[] methodeTypeParameters = {String.class};
        introspectMethodResult(clientChat, "traiteMessage", methodeTypeParameters, "/q");
        EasyMock.verify(this.serviceChat);

    }

    /**
     * Permet de récupérer le résultat d'une méthode par introspection.
     *
     * @param concernedObject L'object concerné
     * @param method La méthode invoké
     * @param argsMethodType Les types de la methods
     * @param realArgs Les arguments passées
     * @return L'optionnal
     */
    private static <T> Optional<T> introspectMethodResult(Object concernedObject, String method, Class<?>[] argsMethodType, Object... realArgs) {
        try {
            Method methode = concernedObject.getClass().getDeclaredMethod(method, argsMethodType);
            methode.setAccessible(true);
            methode.invoke(concernedObject, realArgs);

            T result = (T) methode.invoke(concernedObject, realArgs);
            if (result != null)
            {
                return Optional.of(result);
            }
        } catch (NoSuchMethodException e) {
            fail("Method not found");
        } catch (IllegalAccessException e) {
            fail("Illegal access");
        } catch (InvocationTargetException e) {
            fail("Invocation target exception" + e.getMessage());
        }

        return Optional.empty();
    }
}
