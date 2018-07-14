/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ptc.services.utilities.docgen.relationships;

import com.mks.api.Option;
import com.mks.api.response.APIException;
import com.mks.api.response.Field;
import com.mks.api.response.Item;
import com.mks.api.response.ItemList;
import com.mks.api.response.Response;
import com.mks.api.response.WorkItem;
import com.ptc.services.utilities.docgen.Integrity;
import com.ptc.services.utilities.docgen.session.APISession;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author veckardt
 */
public class IntegrityTypePropertyList extends HashMap<String, IntegrityTypeProperty> {

    public IntegrityTypePropertyList(Integrity api, String type) throws IntegrityException {
        this(api, type, null);
    }

    public IntegrityTypePropertyList(Integrity api, String type, String filter)
            throws IntegrityException {
        try {
            this.clear();
            com.mks.api.Command cmd = new com.mks.api.Command("im", "types");
            cmd.addOption(new Option("fields", "properties"));
            cmd.addSelection(type);
            Response res = api.execute(cmd);
            WorkItem wkProps = res.getWorkItem(type);
            Field propsField = wkProps.getField("properties");
            ItemList itemList = (ItemList) propsField.getList();
            String name;
            String value;
            String desc;
            for (Iterator i$ = itemList.iterator(); i$.hasNext(); debug((new StringBuilder()).append("Param: ").append(name).append(":").append(value).append("-").append(desc).toString())) {
                Object obj = i$.next();
                Item item = (Item) obj;
                name = item.getField("name").getString();
                value = item.getField("value").getString();
                desc = item.getField("description").getString();

                if (filter == null || name.startsWith(filter)) {
                    IntegrityTypeProperty prop = new IntegrityTypeProperty(name, value, desc);
                    this.put(prop.getName(), prop);
                }
            }

        } catch (APIException e) {
            throw new IntegrityException((new StringBuilder()).append("Error writing properties").append(e).toString());
        }
    }

    /**
     * Lists all items in the fieldList map
     */
    public void listAll() {
        for (Map.Entry<String, IntegrityTypeProperty> entry : this.entrySet()) {
            System.out.println("Key : " + entry.getKey() + " Value : "
                    + entry.getValue().toString());
        }
    }

    private void debug(String mess) {
        if (debug) {
            System.out.println(mess);
        }
    }
    private static boolean debug = false;
}
