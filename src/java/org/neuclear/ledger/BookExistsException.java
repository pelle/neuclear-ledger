package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 18, 2003
 * Time: 2:20:09 PM
 * To change this template use Options | File Templates.
 */
public class BookExistsException extends LedgerException {
    public BookExistsException(Ledger ledger, String bookID) {
        super(ledger);
        this.bookID=bookID;
    }

    public Book getBook() throws UnknownBookException, LowlevelLedgerException {
        return getLedger().getBook(bookID);
    }

    private String bookID;

    public String getSubMessage() {
        return "Book \""+bookID+"\" already exists";
    }
}
