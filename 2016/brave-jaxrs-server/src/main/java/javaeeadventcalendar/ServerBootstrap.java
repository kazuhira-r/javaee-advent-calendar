package javaeeadventcalendar;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.mysql.MySQLStatementInterceptorManagementBean;
import org.jboss.resteasy.plugins.server.netty.NettyJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.urlconnection.URLConnectionSender;

public class ServerBootstrap {
    public static void main(String... arsg) throws Exception {
        // Setup Zipkin / MySql
        AsyncReporter<Span> asyncReporter =
                AsyncReporter
                        .builder(URLConnectionSender.create("http://localhost:9411/api/v1/spans")).build();

        Brave brave = new Brave.Builder("myJaxrsMySqlService").reporter(asyncReporter).build();
        new MySQLStatementInterceptorManagementBean(brave.clientTracer());


        // Setup JAX-RS / RESTEasy & Netty 4
        NettyJaxrsServer jaxrsServer = new NettyJaxrsServer();

        ResteasyDeployment deployment = jaxrsServer.getDeployment();
        deployment.setApplicationClass(JaxrsActivator.class.getName());

        jaxrsServer.setRootResourcePath("");
        jaxrsServer.setPort(8080);
        jaxrsServer.setDeployment(deployment);

        jaxrsServer.start();
    }
}
