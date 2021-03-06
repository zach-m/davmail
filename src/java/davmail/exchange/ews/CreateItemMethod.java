/*
 * DavMail POP/IMAP/SMTP/CalDav/LDAP Exchange Gateway
 * Copyright (C) 2010  Mickael Guessant
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package davmail.exchange.ews;

import davmail.Settings;

/**
 * Create Item method.
 */
public class CreateItemMethod extends EWSMethod {
    /**
     * Create exchange item.
     *
     * @param messageDisposition save or send option
     * @param savedItemFolderId  saved item folder id
     * @param item               item content
     */
    public CreateItemMethod(MessageDisposition messageDisposition, FolderId savedItemFolderId, EWSMethod.Item item) {
        super("Item", "CreateItem");
        this.savedItemFolderId = savedItemFolderId;
        this.item = item;
        addMethodOption(messageDisposition);
        setContentChunked(Settings.getBooleanProperty("davmail.enableChunkedRequest", false));
    }

    /**
     * Create exchange item.
     *
     * @param messageDisposition     save or send option
     * @param sendMeetingInvitations send invitation option
     * @param savedItemFolderId      saved item folder id
     * @param item                   item content
     */
    public CreateItemMethod(MessageDisposition messageDisposition, SendMeetingInvitations sendMeetingInvitations, FolderId savedItemFolderId, EWSMethod.Item item) {
        super("Item", "CreateItem");
        this.savedItemFolderId = savedItemFolderId;
        this.item = item;
        addMethodOption(messageDisposition);
        addMethodOption(sendMeetingInvitations);
    }

}
