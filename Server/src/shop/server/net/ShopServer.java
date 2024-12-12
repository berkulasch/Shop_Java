package shop.server.net;

import shop.common.Exceptions.ArtikelExistiertException;
import shop.common.Exceptions.BenutzerExistiertException;
import shop.common.Interface.ShopInterface;
import shop.server.Domain.Shop;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ShopServer {

    public final static int DEFAULT_PORT = 6788;
    protected int port;
    protected ServerSocket serverSocket;
    private ShopInterface shopInterface;


    /**
     * Konstruktor zur Erzeugung des Shopservers.
     *
     * @param port Portnummer, auf der auf Verbindungen gewartet werden soll
     * (wenn 0, wird Default-Port verwendet)
     */
    public ShopServer(int port) throws IOException, ArtikelExistiertException, BenutzerExistiertException {

        shopInterface = new Shop("Shop");

        if (port == 0)
            port = DEFAULT_PORT;
        this.port = port;

        try {
            // Server-Socket anlegen
            serverSocket = new ServerSocket(port);

            // Serverdaten ausgeben
            InetAddress ia = InetAddress.getLocalHost();
            System.out.println("Host: " + ia.getHostName());
            System.out.println("Server *" + ia.getHostAddress()	+ "* lauscht auf Port " + port);
        } catch (IOException e) {
            fail(e, "Eine Ausnahme trat beim Anlegen des Server-Sockets auf");
        }
    }

    /**
     * Methode zur Entgegennahme von Verbindungswünschen durch Clients.
     * Die Methode fragt wiederholt ab, ob Verbindungsanfragen vorliegen
     * und erzeugt dann jeweils ein ClientRequestProcessor-Objekt mit dem
     * fuer diese Verbindung erzeugten Client-Socket.
     */
    public void acceptClientConnectRequests() {

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientRequestProcessor c = new ClientRequestProcessor(clientSocket, shopInterface);
                Thread t = new Thread(c);
                t.start();
            }
        } catch (IOException e) {
            fail(e, "Fehler während des Lauschens auf Verbindungen");
        }
    }

    /**
     * main()-Methode zum Starten des Servers
     *
     * @param args kann optional Portnummer enthalten, auf der Verbindungen entgegengenommen werden sollen
     */
    public static void main(String[] args) {
        int port = 0;
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                port = 0;
            }
        }
        try {
            ShopServer server = new ShopServer(port);
            server.acceptClientConnectRequests();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e, " - ShopServer-Erzeugung");
        } catch (ArtikelExistiertException | BenutzerExistiertException e) {
            throw new RuntimeException(e);
        }
    }

    // Standard-Exit im Fehlerfall:
    private static void fail(Exception e, String msg) {
        System.err.println(msg + ": " + e);
        System.exit(1);
    }
}
