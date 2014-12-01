package javaee7.web.service;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import javaee7.web.entity.Book;

@RequestScoped
public class BookService {
    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void create(Book book) {
        em.persist(book);
    }

    @Transactional
    public Book update(Book book) {
        return em.merge(book);
    }

    @Transactional
    public void remove(Book book) {
        em.remove(em.merge(book));
    }

    public Book find(String isbn) {
        return em.find(Book.class, isbn);
    }

    @SuppressWarnings("unchecked")
    public List<Book> findAll() {
        return (List<Book>)em
            .createQuery("SELECT b FROM Book b ORDER BY b.price ASC")
            .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<Book> findByPrice(int price) {
        return (List<Book>)em
            .createNamedQuery("findByPrice", Book.class)
            .setParameter("price", price)
            .getResultList();
    }

    public Book findNative(String isbn) {
        return (Book) em
            .createNativeQuery("db.BOOK.findOne({\"_id\": \"" + isbn + "\"})", Book.class)
            .getSingleResult();
    }
}
