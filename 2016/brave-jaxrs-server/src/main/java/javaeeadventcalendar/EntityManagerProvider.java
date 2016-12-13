package javaeeadventcalendar;

import java.util.function.Consumer;
import java.util.function.Function;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerProvider {
    private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("brave.jaxrs.pu");

    public static <T> T call(Function<EntityManager, T> func) {
        EntityManager entityManager = emf.createEntityManager();
        try {
            return func.apply(entityManager);
        } finally {
            entityManager.close();
        }
    }

    public static void run(Consumer<EntityManager> func) {
        EntityManager entityManager = emf.createEntityManager();
        try {
            func.accept(entityManager);
        } finally {
            entityManager.close();
        }
    }
}
