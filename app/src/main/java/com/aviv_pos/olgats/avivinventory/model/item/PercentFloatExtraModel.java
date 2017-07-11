package com.aviv_pos.olgats.avivinventory.model.item;

/**
 * Created by olgats on 10/12/2015.
 */
public class PercentFloatExtraModel  extends FloatExtraModel{

    public PercentFloatExtraModel(String name, Float value) {
        super(name, value);
    }

    @Override
    public String valueToString() {
        return super.valueToString()+"%";
    }
}
