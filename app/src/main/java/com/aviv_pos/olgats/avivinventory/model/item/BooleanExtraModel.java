package com.aviv_pos.olgats.avivinventory.model.item;

/**
 * Created by olgats on 10/12/2015.
 */
public class BooleanExtraModel extends AbstractExtraModel<Boolean> {

    public BooleanExtraModel(String name, Boolean value) {
        super(AbstractExtraModel.BOOL_TYPE,name, value);
    }
}
