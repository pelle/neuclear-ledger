package org.neuclear.ledger;

import java.util.Date;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/**
 * User: pelleb
 * Date: Apr 19, 2004
 * Time: 10:51:01 AM
 */
public class Book {
    protected Book() {
    }

    public Book(String id, String nickname, String type, String source, Date registered, Date updated, String registrationid) {
        this.id = id;
        this.nickname = nickname;
        this.type = type;
        this.source = source;
        this.registered = registered;
        this.updated = updated;
        this.registrationid = registrationid;
    }

    public Book(String id, Date registered) {
        this.id = id;
        this.nickname = id;
        this.type = "identity";
        this.source = null;
        this.registered = registered;
        this.updated = registered;
        this.registrationid = null;
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getType() {
        return type;
    }

    public String getSource() {
        return source;
    }

    public Date getRegistered() {
        return registered;
    }

    public Date getUpdated() {
        return updated;
    }

    public String getRegistrationId() {
        return registrationid;
    }


    protected String id;
    protected String nickname;
    protected String type;
    protected String source;
    protected Date registered;
    protected Date updated;
    protected String registrationid;
}
