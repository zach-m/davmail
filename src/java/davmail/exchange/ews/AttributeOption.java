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

import java.io.IOException;
import java.io.Writer;

/**
 * Generic attribute option.
 */
public class AttributeOption extends Option {

    protected AttributeOption(String name, String value) {
        super(name, value);
    }

    /**
     * @inheritDoc
     */
    public void appendTo(StringBuilder buffer) {
        buffer.append(' ').append(name).append("=\"").append(value).append('"');
    }

    /**
     * @inheritDoc
     */
    @Override
    public void write(Writer writer) throws IOException {
        writer.write(" ");
        writer.write(name);
        writer.write("=\"");
        writer.write(value);
        writer.write("\"");
    }
}
