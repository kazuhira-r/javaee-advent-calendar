package javaeeadventcalendar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.jaxrs2.BraveTracingFeature;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.urlconnection.URLConnectionSender;

@ApplicationPath("")
public class JaxrsActivator extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        return new HashSet<>(Arrays.asList(BookResource.class));
    }

    @Override
    public Set<Object> getSingletons() {
        // Setup Zipkin / JAX-RS Server
        AsyncReporter<Span> asyncReporter =
                AsyncReporter
                        .builder(URLConnectionSender.create("http://localhost:9411/api/v1/spans")).build();

        Brave brave = new Brave.Builder("myJaxrsServerService").reporter(asyncReporter).build();
        BraveTracingFeature tracingFeature = BraveTracingFeature.create(brave);
        return new HashSet<>(Arrays.asList(tracingFeature));
    }
}
