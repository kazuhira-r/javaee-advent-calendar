package javaeeadventcalendar;

import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.jaxrs2.BraveTracingFeature;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.urlconnection.URLConnectionSender;

public class ClientApp {
    public static void main(String... args) throws InterruptedException {
        // Setup Zipkin / JAX-RS Client
        AsyncReporter<Span> asyncReporter =
                AsyncReporter
                        .builder(URLConnectionSender.create("http://localhost:9411/api/v1/spans")).build();

        Brave brave = new Brave.Builder("myJaxrsClientService").reporter(asyncReporter).build();

        Client client =
                ClientBuilder
                        .newBuilder()
                        .register(BraveTracingFeature.create(brave))
                        .build();

        Response registerResponse =
                client
                        .target("http://localhost:8080/book")
                        .request()
                        .put(Entity.entity(Book.create("978-4774183169", "パーフェクト Java EE", 3456),
                                MediaType.APPLICATION_JSON_TYPE));

        URI location = registerResponse.getLocation();
        System.out.println("Location = " + location);

        registerResponse.close();

        System.out.println("=====");

        Response findResponse =
                client
                        .target(location)
                        .request()
                        .get();

        Book responseBook = findResponse.readEntity(Book.class);
        System.out.println("isbn = " + responseBook.getIsbn());
        System.out.println("title = " + responseBook.getTitle());
        System.out.println("price = " + responseBook.getPrice());

        findResponse.close();

        client.close();

        // wait Zipkin request...
        System.out.println("wait Zipkin request...");
        TimeUnit.SECONDS.sleep(2L);
    }
}
