package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 18, 2003
 * Time: 2:20:09 PM
 * To change this template use Options | File Templates.
 */
public final class BookExistsException extends LedgerException {
    public BookExistsException(final Ledger ledger, final String bookID) {
        super(ledger);
        this.bookID=bookID;
    }

    public final Book getBook() throws UnknownBookException, LowlevelLedgerException {
        return getLedger().getBook(bookID);
    }

    private final String bookID;

    public final String getSubMessage() {
        return "Book \""+bookID+"\" already exists";
    }
}
