package com.aviv_pos.olgats.avivinventory.model.item;


import com.aviv_pos.olgats.avivinventory.dao.DatabaseHandler;

import java.util.TreeMap;

/**
 * Created by olgats on 10/12/2015.
 */
public class MapExtraModel extends AbstractExtraModel<Integer> {
    private DatabaseHandler.Master master;

    public MapExtraModel(String name, Integer value, DatabaseHandler.Master master) {
        super(AbstractExtraModel.MAP_TYPE,name, value);
        this.master = master;
    }

    @Override
    public String valueToString() {
        Integer i = getValue();
        String str = i != null && master != null ? master.getValue(i) : null;
        return str != null ? str : String.valueOf(i);
    }
}
