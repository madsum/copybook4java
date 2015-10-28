package dk.nversion.copybook.converters;

import dk.nversion.copybook.exceptions.CopyBookException;

import java.util.HashMap;
import java.util.Map;

public class StringToEnum extends StringToString {
    Object[] enumConstants;
    Map<String,Object> toEnumMap = new HashMap<>();
    Map<Object,byte[]> fromEnumMap = new HashMap<>();

    @Override
    public void initialize(TypeConverterConfig config) throws CopyBookException {
        super.initialize(config);
        enumConstants = type.getEnumConstants();
        if(TypeConverterStringEnum.class.isAssignableFrom(type)) {
            for (Object enumConstant : enumConstants) {
                String value = ((TypeConverterStringEnum) enumConstant).getValue();
                toEnumMap.put(value, enumConstant);
                fromEnumMap.put(enumConstant, value.getBytes(this.charset));
            }
        }
    }

    @Override
    public void validate(Class type, int size, int decimals) throws TypeConverterException {
        if(!(TypeConverterStringEnum.class.isAssignableFrom(type))) {
            throw new TypeConverterException("Only supports converting to and from an Enum that implements TypeConverterStringEnum");
        }
    }

    @Override
    public Object to(byte[] bytes, int offset, int length, int decimals, boolean removePadding) throws TypeConverterException {
        String value = (String)super.to(bytes, offset, length, decimals, removePadding);

        if (toEnumMap.containsKey(value)) {
            return toEnumMap.get(value);

        } else {
            throw new TypeConverterException("Unknown value for enum: " + value);
        }
    }

    @Override
    public byte[] from(Object value, int length, int decimals, boolean addPadding) throws TypeConverterException {
        if(value == null && this.defaultValue == null) {
            return null;
        }

        byte[] strBytes = fromEnumMap.get(value);
        if (strBytes.length > length) {
            throw new TypeConverterException("Field to small for value: " + length + " < " + strBytes.length);
        }

        if (addPadding) {
            strBytes = padBytes(strBytes, length);
        }

        return strBytes;
    }


}
