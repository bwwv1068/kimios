/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kimios.kernel.user.impl.factory.genericldaplayer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.kimios.kernel.security.SecurityEntityType;
import org.kimios.kernel.user.impl.GenericLDAPImpl;

public abstract class GenericLDAPFactory
{
    protected GenericLDAPImpl source;

    public List<SearchResult> search(String s, int type) throws NamingException
    {
        try {
            String where = null;
            List<String> nodes = null;
            String dn;

            if (type == SecurityEntityType.GROUP) {
                dn = source.getGroupsDn();
            } else {
                dn = source.getUsersDn();
            }
            if (dn != null) {
                if (dn.indexOf(':') == -1) {
                    // single CN
                    where = getCnOnly(dn);
                } else {
                    // multiple CN
                    String[] ns = dn.split(":");
                    nodes = new ArrayList<String>();
                    for (int i = 0; i < ns.length; i++) {
                        nodes.add(getCnOnly(ns[i]));
                    }
                }
            }
            DirContext context =
                    this.getContext(source.getProviderUrl() + "/" + source.getBaseDn(), source.getAuthenticationMode(),
                            source.getReferralMode(),
                            source.getUsersPrefix() + source.getRootDn() + source.getUsersSuffix(),
                            source.getRootDnPassword());
            SearchControls sc = new SearchControls();
            sc.setSearchScope(source.isSubtreeScope() ? SearchControls.SUBTREE_SCOPE : SearchControls.ONELEVEL_SCOPE);

            List<SearchResult> searchResults = new ArrayList<SearchResult>();
            if (nodes == null) {
                buildResults(s, where, context, sc, searchResults);
            } else {
                for (String node : nodes) {
                    buildResults(s, node, context, sc, searchResults);
                }
            }
            context.close();
            return searchResults;
        } catch (NamingException ne) {
            // If connection to LDAP server is lost
            if (ne.getCause() != null && ne.getCause().getClass().getName().equals("java.net.SocketException")) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                }
                return search(s, type);
            } else {
                throw ne;
            }
        }
    }

    /**
     * @param s
     * @param where
     * @param context
     * @param sc
     * @param searchResults
     * @throws javax.naming.NamingException
     */
    private void buildResults(String s, String where, DirContext context, SearchControls sc,
            List<SearchResult> searchResults) throws NamingException
    {
        NamingEnumeration<SearchResult> result = context.search(where, s, sc);
        while (result.hasMoreElements()) {
            SearchResult sr = result.nextElement();
            searchResults.add(sr);
        }
    }

    protected String getCnOnly(String cn)
    {
        int index = cn.indexOf(this.source.getBaseDn());
        if (index != -1) {
            return cn.substring(0, index - 1);
        }
        return cn;
    }

    protected DirContext getContext(String ldapUrl, String ldapAuthenticateMode, String ldapReferralMode,
            String ldapUser, String ldapPassword)
            throws NamingException, AuthenticationException
    {
        String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, ldapUrl);
        env.put(Context.SECURITY_AUTHENTICATION, ldapAuthenticateMode);
        env.put(Context.SECURITY_PRINCIPAL, ldapUser);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        env.put(Context.REFERRAL, ldapReferralMode);
        return new InitialDirContext(env);
    }

    /**
     * Get the user string depending to the current implementation
     */
    protected String getUserString(String uid)
    {
        if (source.isActiveDirectory()) {
            return source.getUsersPrefix() + uid + source.getUsersSuffix();
        } else {
            return source.getUsersIdKey() + "=" + uid + "," + source.getUsersDn();
        }
    }
}

