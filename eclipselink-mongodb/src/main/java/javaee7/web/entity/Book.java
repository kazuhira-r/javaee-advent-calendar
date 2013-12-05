package javaee7.web.entity;

import java.io.Serializable;
import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.Field;
import org.eclipse.persistence.nosql.annotations.NoSql;

@XmlRootElement
@Entity
@NamedQuery(name = "findByPrice", query = "SELECT b FROM Book b WHERE b.price >= :price ORDER BY b.price ASC")
@NoSql(dataFormat = DataFormatType.MAPPED)
public class Book implements Serializable {
    @Id
    @GeneratedValue
    @Field(name="_id")
    private String isbn;

    @Field(name = "title")
    private String title;

    @Field(name = "price")
    private Integer price;

    @Temporal(TemporalType.DATE)
    @Field(name = "publish_date")
    private Date publishDate;

    @ElementCollection
    @Field(name = "tags")
    private List<String> tags = new ArrayList<>();

    @ElementCollection
    @Field(name = "authors")
    private List<EmbeddedAuthor> authors = new ArrayList<>();

    public Book addAuthor(EmbeddedAuthor author) {
        authors.add(author);
        return this;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setAuthors(List<EmbeddedAuthor> authors) {
        this.authors = authors;
    }

    public List<EmbeddedAuthor> getAuthors() {
        return authors;
    }
}
