package com.aviv_pos.olgats.avivinventory.model.item;

/**
 * Created by olgats on 10/12/2015.
 */
public class FloatExtraModel extends AbstractExtraModel<Float> {
    private String format;

    public FloatExtraModel(String name, Float value, String format) {
        super(AbstractExtraModel.FLOAT_TYPE,name, value);
        this.format = format != null ? format : "%.3f";
    }

    public FloatExtraModel(String name, Float value) {
        this(name, value, "%.3f");
    }


    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String valueToString() {
        Float v = getValue();
        return String.format(format, v != null ? v : 0);
    }
}
