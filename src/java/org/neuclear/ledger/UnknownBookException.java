package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 17, 2003
 * Time: 4:46:06 PM
 * To change this template use Options | File Templates.
 */
public class UnknownBookException extends LedgerException {
    public UnknownBookException(Ledger ledger,String bookID) {
        super(ledger);
        this.bookID=bookID;
    }
    public String getBookID() {
        return bookID;
    }

    public Book createBook() throws BookExistsException,LowlevelLedgerException {
        return getLedger().createNewBook(bookID);
    }
    private String bookID;

    public String getSubMessage() {
        return "Unknown Book: "+bookID;
    }
}
