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
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kimios.front;


import org.apache.commons.lang.StringEscapeUtils;
import org.kimios.core.ParametersExtractor;
import org.kimios.controller.AdminControllerWeb;
import org.kimios.controller.AuthenticationControllerWeb;
import org.kimios.controller.Controller;
import org.kimios.controller.DmEntityControllerWeb;
import org.kimios.controller.DocumentActionHandler;
import org.kimios.controller.DocumentVersionActionHandler;
import org.kimios.controller.ExtensionControllerWeb;
import org.kimios.controller.FolderActionHandler;
import org.kimios.controller.InternationalizationControllerWeb;
import org.kimios.controller.LogControllerWeb;
import org.kimios.controller.MetaControllerWeb;
import org.kimios.controller.ReportingControllerWeb;
import org.kimios.controller.SearchControllerWeb;
import org.kimios.controller.SecurityControllerWeb;
import org.kimios.controller.StudioControllerWeb;
import org.kimios.controller.TreeControllerWeb;
import org.kimios.controller.UploadManager;
import org.kimios.controller.WorkflowControllerWeb;
import org.kimios.controller.WorkspaceActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @author Fabien Alin
 */
public class MainController extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(MainController.class);

    private Cookie getCookie(HttpServletRequest request, String name) throws Exception {
        Cookie[] cookies = request.getCookies();
        Cookie item = null;
        for (Cookie c : cookies) {
            if (c.getName().equalsIgnoreCase(name)) {
                item = c;
                break;
            }
        }
        return item;
    }

    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("servlet");

        /*
         * Not move this line after the upload manager process, or the document version handler
         */
        response.setContentType("application/json");
        Cookie sess = null;
        Cookie lang = null;
        try {
            sess = getCookie(request, "sessionUid");

        } catch (Exception e) {
            log.error("No session cookie", e);
        }
        try {
            lang = getCookie(request, "lang");

        } catch (Exception e) {
            log.error("No lang cookie", e);
        }
        String sessionUid = (sess != null ? sess.getValue() : null);
        String langSelected = (lang != null ? lang.getValue() : "en");
        String json = "";
        Controller c = null;
        boolean login = false;
        Map<String, String> params = ParametersExtractor.getParams(request);
        params.put("selected_lang", langSelected);
        try {
            if (action.equalsIgnoreCase("Uploader")) {
                c = new UploadManager(params, request, response);
            }
            if (action.equalsIgnoreCase("DmsEntity")) {
                c = new DmEntityControllerWeb(params);
            }
            if (action.equalsIgnoreCase("DmsMeta")) {
                c = new MetaControllerWeb(params);
            }
            if (action.equalsIgnoreCase("DmsTree")) {
                TreeControllerWeb.contextPath = request.getContextPath();
                c = new TreeControllerWeb(params);
            }
            if (action.equalsIgnoreCase("DmsSecurity")) {
                c = new SecurityControllerWeb(params);
            }
            if (action.equalsIgnoreCase("DmsExtension")) {
                c = new ExtensionControllerWeb(params);
            }
            if (action.equalsIgnoreCase("DocumentVersion")) {
                c = new DocumentVersionActionHandler(params, response);
            }
            if (action.equalsIgnoreCase("Version")) {
                c = new DocumentActionHandler(params);
            }
            if (action.equalsIgnoreCase("Folder")) {
                c = new FolderActionHandler(params);
            }
            if (action.equalsIgnoreCase("Search")) {
                c = new SearchControllerWeb(params);
            }
            if (action.equalsIgnoreCase("Workspace")) {
                c = new WorkspaceActionHandler(params);
            }
            if (action.equalsIgnoreCase("Security")) {
                c = new AuthenticationControllerWeb(params);
                try {
                    login = params.get("action").equals("login");
                } catch (Exception e) {
                    login = false;
                }
            }
            if (action.equalsIgnoreCase("Workflow")) {
                c = new WorkflowControllerWeb(params);
            }
            if (action.equalsIgnoreCase("Admin")) {
                c = new AdminControllerWeb(params);
            }
            if (action.equalsIgnoreCase("Studio")) {
                c = new StudioControllerWeb(params);
            }
            if (action.equalsIgnoreCase("Reporting")) {
                c = new ReportingControllerWeb(params);
            }
            if (action.equalsIgnoreCase("Log")) {
                c = new LogControllerWeb(params);
            }
            if (action.equalsIgnoreCase("Lang")) {
                c = new InternationalizationControllerWeb(params);
                ((InternationalizationControllerWeb) c).setResponse(response);
            }
            if (login) {
                sessionUid = c.execute();
                request.getSession().setAttribute("sessionUid", sessionUid);
                response.addCookie(new Cookie("sessionUid", sessionUid));
                json = "{success:true,sessionUid:\"" + sessionUid + "\"}";
                response.getWriter().write(json);
            } else {
                if (!response.isCommitted()) {
                    c.setSessionUid(sessionUid);
                    json = c.execute();
                    log.debug("JSON " + json);
                    if (!json.equals("downloadaction"))
                        response.getWriter().write(json);
                }
            }
        } catch (Exception e) {
            log.error("Error ", e);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            json = "{success:false,exception:\"" + StringEscapeUtils.escapeJava(e.getMessage()) + "\",trace:\"" + StringEscapeUtils.escapeJava(sw.toString()) + "\"}";
            log.debug("JSON " + json);
            response.getWriter().write(json);
        }
    }
}

