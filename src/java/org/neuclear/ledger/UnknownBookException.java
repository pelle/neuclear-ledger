package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 17, 2003
 * Time: 4:46:06 PM
 * To change this template use Options | File Templates.
 */
public final class UnknownBookException extends LedgerException {
    public UnknownBookException(final Ledger ledger,final String bookID) {
        super(ledger);
        this.bookID=bookID;
    }
    public final String getBookID() {
        return bookID;
    }

    public final Book createBook() throws BookExistsException,LowlevelLedgerException {
        return getLedger().createNewBook(bookID);
    }
    private final String bookID;

    public final String getSubMessage() {
        return "Unknown Book: "+bookID;
    }
}
