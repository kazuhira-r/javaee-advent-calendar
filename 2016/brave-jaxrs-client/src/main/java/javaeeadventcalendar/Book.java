package javaeeadventcalendar;

public class Book {
    private String isbn;
    private String title;
    private int price;

    public static Book create(String isbn, String title, int price) {
        Book book = new Book();
        book.setIsbn(isbn);
        book.setTitle(title);
        book.setPrice(price);
        return book;
    }

    public Book() {
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
