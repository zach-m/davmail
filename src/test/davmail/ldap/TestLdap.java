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
package davmail.ldap;

import davmail.DavGateway;
import davmail.Settings;
import davmail.exchange.AbstractExchangeSessionTestCase;
import davmail.exchange.ExchangeSessionFactory;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Test LDAP.
 */
@SuppressWarnings({"JavaDoc"})
public class TestLdap extends AbstractExchangeSessionTestCase {
    InitialLdapContext ldapContext;

    @Override
    public void setUp() throws IOException {
        boolean needStart = !loaded;
        super.setUp();
        if (needStart) {
            // start gateway
            DavGateway.start();
        }
        if (ldapContext == null) {
            Hashtable<String, String> env = new Hashtable<String, String>();
            //env.put("java.naming.security.authentication", "CRAM-MD5");
            env.put("java.naming.security.authentication", "simple");
            env.put("java.naming.security.principal", Settings.getProperty("davmail.username"));
            env.put("java.naming.security.credentials", Settings.getProperty("davmail.password"));

            env.put("com.sun.jndi.ldap.connect.pool", "true");
            env.put("java.naming.factory.initial", "com.sun.jndi.ldap.LdapCtxFactory");
            env.put("java.naming.provider.url", "ldap://127.0.0.1:" + Settings.getIntProperty("davmail.ldapPort"));
            env.put("java.naming.referral", "follow");

            try {
                ldapContext = new InitialLdapContext(env, null);
            } catch (NamingException e) {
                throw new IOException(e);
            }
        }
        if (session == null) {
            session = ExchangeSessionFactory.getInstance(Settings.getProperty("davmail.username"), Settings.getProperty("davmail.password"));
        }
    }

    public void testSearchOneLevel() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(objectclass=*)", searchControls);
    }

    public void testSearchMail() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"mail"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(objectclass=*)", searchControls);
    }

    public void testMozillaSearchAttributes() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"custom1", "mozillausehtmlmail", "postalcode", "custom2", "custom3", "custom4", "street", "surname", "telephonenumber", "mozillahomelocalityname", "orgunit", "mozillaworkstreet2", "xmozillanickname", "mozillahomestreet", "description", "cellphone", "homeurl", "mozillahomepostalcode", "departmentnumber", "postofficebox", "st", "objectclass", "sn", "ou", "fax", "mozillahomeurl", "mozillahomecountryname", "streetaddress", "cn", "company", "mozillaworkurl", "mobile", "region", "birthmonth", "birthday", "labeleduri", "carphone", "department", "xmozillausehtmlmail", "givenname", "nsaimid", "workurl", "facsimiletelephonenumber", "mozillanickname", "title", "nscpaimscreenname", "xmozillasecondemail", "mozillacustom3", "countryname", "mozillacustom4", "mozillacustom1", "mozillacustom2", "homephone", "mozillasecondemail", "pager", "zip", "mail", "c", "mozillahomestate", "o", "l", "birthyear", "modifytimestamp", "locality", "commonname", "notes", "pagerphone", "mozillahomestreet2"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(objectclass=*)", searchControls);
    }

    public void testGalfind() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(uid="+session.getAlias()+ ')', searchControls);
        assertTrue(searchResults.hasMore());
        SearchResult searchResult = searchResults.next();
        Attributes attributes = searchResult.getAttributes();
        Attribute attribute = attributes.get("uid");
        assertEquals(session.getAlias(), attribute.get());
        // given name not available on Exchange 2007 over Dav (no gallookup)
        //assertNotNull(attributes.get("givenName"));
    }

    public void testOSXSearch() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"uid", "jpegphoto", "postalcode", "mail", "sn", "apple-emailcontacts", "c", "street", "givenname", "l", "apple-user-picture", "telephonenumber", "cn", "st", "apple-imhandle"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("cn=users, o=od", "(&(objectclass=inetOrgPerson)(|(givenname=Charles*)(|(uid=Ch*)(cn=Ch*))(sn=Ch*))(objectclass=shadowAccount)(objectclass=extensibleObject)(objectclass=posixAccount)(objectclass=apple-user))", searchControls);
        assertTrue(searchResults.hasMore());
    }

    public void testOSXICalSearch() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"uid", "mail", "sn", "cn", "description", "apple-generateduid", "givenname", "apple-serviceslocator", "uidnumber"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("cn=users, o=od",
                "(&(objectclass=inetOrgPerson)(objectclass=extensibleObject)(objectclass=apple-user)(|(|(uid=fair*)(cn=fair*))(givenname=fair*)(sn=fair*)(cn=fair*)(mail=fair*))(objectclass=posixAccount)(objectclass=shadowAccount))", searchControls);
    }

    public void testSearchByGivenNameWithoutReturningAttributes() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"uid"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(givenName=mic*)", searchControls);
    }

    public void testSearchByGalfindUnsupportedAttribute() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(postalcode=N18 1ZF)", searchControls);
    }

    public void testSearchByCnReturnSn() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"sn"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(cn=*)", searchControls);
    }

    public void testSearchByCnReturnGivenName() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"givenName"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(cn=*a*)", searchControls);
    }

    public void testSearchIPad() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"postalcode", "labeleduri", "street", "givenname", "telephonenumber", "facsimiletelephonenumber", "title", "imhandle", "homepostaladdress", "st", "homephone", "applefloor", "jpegphoto", "pager", "mail", "sn", "buildingname", "ou", "destinationindicator", "c", "o", "l", "co", "postaladdress", "cn", "mobile"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(|(mail=Test*)(cn=Test*)(givenname=Test*)(sn=Test*))", searchControls);
    }

    public void testThunderbird() throws NamingException {
        String filter = "(|(sn=*stocker*)(givenname=*stocker*)(mail=*stocker*)(cn=*stocker*))";
        String[] returningAttributes = new String[]{"custom1", "mozillausehtmlmail", "postalcode", "custom2", "custom3", "custom4", "street", "surname", "telephonenumber", "mozillahomelocalityname", "orgunit", "mozillaworkstreet2", "xmozillanickname", "mozillahomestreet", "description", "cellphone", "homeurl", "mozillahomepostalcode", "departmentnumber", "postofficebox", "st", "objectclass", "sn", "ou", "fax", "mozillahomeurl", "mozillahomecountryname", "streetaddress", "cn", "company", "mozillaworkurl", "mobile", "region", "birthmonth", "birthday", "labeleduri", "carphone", "department", "xmozillausehtmlmail", "givenname", "nsaimid", "workurl", "facsimiletelephonenumber", "mozillanickname", "title", "nscpaimscreenname", "xmozillasecondemail", "mozillacustom3", "countryname", "mozillacustom4", "mozillacustom1", "mozillacustom2", "homephone", "mozillasecondemail", "pager", "zip", "mail", "c", "mozillahomestate", "o", "l", "birthyear", "modifytimestamp", "locality", "commonname", "notes", "pagerphone", "mozillahomestreet2"};
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(returningAttributes);
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", filter, searchControls);
    }

     public void testSearchNotFilter() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"mail"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people", "(!(objectclass=test))", searchControls);
    }

    public void testEmailSearch() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"uid", "mail", "sn", "cn", "description", "apple-generateduid", "givenname", "apple-serviceslocator", "uidnumber"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("cn=users, o=od",
                "(mail="+"CHARLEPG@intersmtp.com"+")", searchControls);
    }

    public void testIcalLionInitialSearch() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"gidnumber", "altsecurityidentities", "uid", "mail", "cn", "apple-generateduid", "givenname", "apple-serviceslocator", "objectclass", "uidnumber"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("cn=users, ou=people",
                "(&(|(mail=702820784)(uid=702820784)(cn=702820784)(altsecurityidentities=702820784))(&(objectclass=extensibleObject)(objectclass=posixAccount)(objectclass=shadowAccount)(objectclass=inetOrgPerson)(objectclass=apple-user)))", searchControls);
    }

    public void testTBGalSearch() throws NamingException {
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.ONELEVEL_SCOPE);
        searchControls.setReturningAttributes(new String[]{"mail", "cn"});
        NamingEnumeration<SearchResult> searchResults = ldapContext.search("ou=people",
                "(|(sn=mich*)(mail=mich*)(cn=mich*))", searchControls);
    }
}
