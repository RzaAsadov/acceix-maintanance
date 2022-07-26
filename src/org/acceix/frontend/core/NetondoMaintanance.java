/*
 * The MIT License
 *
 * Copyright 2022 Rza Asadov (rza dot asadov at gmail dot com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package org.acceix.frontend.core;

import org.acceix.frontend.crud.loaders.CustomAssetsLoader;
import org.acceix.frontend.crud.loaders.CustomViewsLoader;
import org.acceix.frontend.crud.loaders.FunctionLoader;
import org.acceix.frontend.crud.loaders.ObjectLoader;
import org.acceix.frontend.crud.loaders.ScriptLoader;
import org.acceix.frontend.crud.models.CrudFunction;
import org.acceix.frontend.crud.models.CrudObject;
import org.acceix.frontend.helpers.ActionSettings;
import org.acceix.frontend.helpers.ModuleHelper;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.acceix.logger.NLog;
import org.acceix.logger.NLogBlock;
import org.acceix.logger.NLogger;


/**
 *
 * @author zrid
*/

public class NetondoMaintanance extends org.acceix.frontend.helpers.ModuleHelper {
     

    @Override
    public void construct() {
        
            setModuleName("maintanance");
            addAction(new ActionSettings("reloadcustomviews", true,this::reloadcustomviews));
            addAction(new ActionSettings("reloadcustomwebassets", true,this::reloadcustomwebassets));
            addAction(new ActionSettings("reloadobjects", true,this::reloadobjects));
            addAction(new ActionSettings("reloadsingleobject", true,this::reloadsingleobject));
            addAction(new ActionSettings("reloadfunctions", true,this::reloadfunctions));
            addAction(new ActionSettings("reloadsinglefunction", true,this::reloadsinglefunction));
            addAction(new ActionSettings("reloadscripts", true,this::reloadscripts));            
            addAction(new ActionSettings("showlogs", true, this::showlogs));
            addAction(new ActionSettings("showconsolelogs", true, this::showconsolelogs));
            
    }
    
        public ModuleHelper getInstance() {
            return new NetondoMaintanance();
        }    
    
    
    private void reloadcustomwebassets() {
        
        CustomAssetsLoader loader = new CustomAssetsLoader();

        loader.reset();

        loader.loadAll(getGlobalEnvs().get("custom_web_assets_path").toString());

        addToDataModel("message","All custom assets reloaded");
        addToDataModel("result", "success");

        renderData();
        
    }      
    
    private void reloadcustomviews() {
        
        CustomViewsLoader loader = new CustomViewsLoader();
        
        loader.reset();
        
        loader.loadAll(getGlobalEnvs().get("custom_views_path").toString());
        
        addToDataModel("message","All custom views reloaded");
        addToDataModel("result", "success");
        
        renderData();
        
    }      

    private void reloadscripts() {
        
        ScriptLoader loader = new ScriptLoader();
        
        loader.reset();
        
        loader.loadAll(getGlobalEnvs().get("scripts_path").toString());
        
        addToDataModel("message","All scripts reloaded");
        addToDataModel("result", "success");
        
        renderData();
        
    }    
    
    private void reloadobjects() {
        
        ObjectLoader loader = new ObjectLoader();
        
        loader.reset();
        loader.loadAll(getGlobalEnvs().get("objects_path").toString());
        
        addToDataModel("message","All objects reloaded");
        addToDataModel("result", "success");
        
        renderData();
        
    }
    
    private void reloadfunctions() {
        
        FunctionLoader loader = new FunctionLoader();
        
        loader.reset();
        
        loader.loadAll(getGlobalEnvs().get("functions_path").toString());
        
        addToDataModel("message","All objects reloaded");
        addToDataModel("result", "success");
        
        renderData();
        
    }    
    
    
    private void reloadsingleobject() {
        
        ObjectLoader loader = new ObjectLoader();        
        
        String name = (String) getParameter("name");
        
        CrudObject nCrudObject = loader.get(name);
        
        loader.load(new File(nCrudObject.getFilepath()));
        
        
        addToDataModel("message","Object '" + nCrudObject.getName() + "' reloaded");
        addToDataModel("result", "success");
        
        renderData();
        
    }   
    
    
    private void reloadsinglefunction() {
        
        String name = (String) getParameter("name");
        
        FunctionLoader loader = new FunctionLoader();        
        
        CrudFunction crudFunction = loader.get(name);
        
        loader.load(new File(crudFunction.getFilepath()));
        
        
        addToDataModel("message","Function '" + crudFunction.getName() + "' reloaded");
        addToDataModel("result", "success");
        
        renderData();
        
    }      
    
    
    private void showlogs () {
        
        //String logtype = (String)getParameter("logtype");
        
        LinkedList<NLog> mylogs = NLogger.getLogs();
        
        Map<Integer,Map<String,Object>> tableRows = new LinkedHashMap<>();
        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd (HH:mm:ss)");
        
        List<String> headers = new LinkedList<>();
        
        headers.add("Block");
        headers.add("Module");
        headers.add("Action");
        headers.add("Username");
        headers.add("Type");
        headers.add("Timestamp");
        headers.add("Message");
        //headers.add("Class name");
        
        int sizeOfLog = mylogs.size();
        
        for (int i=sizeOfLog-1; i >= 0 ; i--) {
            
            Map<String,Object> columns = new LinkedHashMap<>();
            
            columns.put("0", "");
            
            columns.put("1", NLogBlock.asString(mylogs.get(i).getLogBlock()));            
            columns.put("2", mylogs.get(i).getModule());
            columns.put("3", mylogs.get(i).getAction());
            columns.put("4", mylogs.get(i).getUser());
            columns.put("5", mylogs.get(i).getLogTypeAsString());
            columns.put("6", dateFormat.format(new Date(mylogs.get(i).getLogTimestamp().getTime())));
            columns.put("7", mylogs.get(i).getLogMessage());
            //columns.put("4", mylogs.get(i).getEventClass() + "->" + mylogs.get(i).getEventMethod() + "(" + mylogs.get(i).getLineNumber());
            
            tableRows.put(tableRows.size(), columns);
        }
        
        addToDataModel("title", "Logs");
        addToDataModel("pageLength", "100");
        addToDataModel("headers", headers);
        addToDataModel("data", tableRows);
        
        try {
            renderData("/development/dev_Logs");
        } catch (IOException ex) {
            Logger.getLogger(NetondoMaintanance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void showconsolelogs () {
        
        //String logtype = (String)getParameter("logtype");
        
        LinkedList<NLog> mylogs = NLogger.getLogs();
        
        Map<Integer,Map<String,Object>> tableRows = new LinkedHashMap<>();
        Format dateFormat = new SimpleDateFormat("yyyy-MM-dd (HH:mm:ss)");
        
        List<String> headers = new LinkedList<>();
        
        headers.add("Type");
        headers.add("Timestamp");
        headers.add("Message");
        //headers.add("Class name");
        
        int sizeOfLog = mylogs.size();
        
        for (int i=sizeOfLog-1; i >= 0 ; i--) {
            
            Map<String,Object> columns = new LinkedHashMap<>();
            
            columns.put("0", "");
                    
            columns.put("1", mylogs.get(i).getLogTypeAsString());
            columns.put("2", dateFormat.format(new Date(mylogs.get(i).getLogTimestamp().getTime())));
            columns.put("3", mylogs.get(i).getLogMessage());
            //columns.put("4", mylogs.get(i).getEventClass() + "->" + mylogs.get(i).getEventMethod() + "(" + mylogs.get(i).getLineNumber());
            
            tableRows.put(tableRows.size(), columns);
        }
        
        addToDataModel("title", "Logs");
        addToDataModel("pageLength", "100");
        addToDataModel("headers", headers);
        addToDataModel("data", tableRows);
        
        try {
            renderData("/development/dev_Logs");
        } catch (IOException ex) {
            Logger.getLogger(NetondoMaintanance.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    
}
