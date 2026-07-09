package com.inno.harbor.validator;

import com.inno.harbor.exception.PortException;

import java.util.List;

public class DataValidator {

    private static final String[] REQUIRED_KEYS = {"berths=", "warehouse_capacity=", "warehouse_initial="};

    public void validate(List<String> lines) {
        for (String key : REQUIRED_KEYS) {
            if (lines.stream().noneMatch(l -> l.startsWith(key))) {
                throw new PortException("Missing required config key: " + key.replace("=", ""));
            }
        }
        for (String line : lines) {
            if (line.startsWith("berths=") || line.startsWith("warehouse_capacity=") || line.startsWith("warehouse_initial=")) {
                requirePositiveInt(line, line.indexOf('=') + 1);
            } else if (line.startsWith("ship=")) {
                validateShipLine(line);
            } else {
                throw new PortException("Unknown config entry: " + line);
            }
        }
    }

    private void validateShipLine(String line) {
        String[] parts = line.substring("ship=".length()).split(",", -1);
        if (parts.length != 5) {
            throw new PortException("Ship entry must have 5 fields (name,current,capacity,toLoad,toUnload): " + line);
        }
        if (parts[0].isBlank()) {
            throw new PortException("Ship name must not be blank: " + line);
        }
        for (int i = 1; i < 5; i++) {
            requireNonNegativeInt(parts[i].trim(), line);
        }
    }

    private void requirePositiveInt(String line, int valueStart) {
        String value = line.substring(valueStart).trim();
        try {
            if (Integer.parseInt(value) <= 0) {
                throw new PortException("Value must be positive in: " + line);
            }
        } catch (NumberFormatException e) {
            throw new PortException("Invalid integer in: " + line, e);
        }
    }

    private void requireNonNegativeInt(String value, String line) {
        try {
            if (Integer.parseInt(value) < 0) {
                throw new PortException("Value must be non-negative in: " + line);
            }
        } catch (NumberFormatException e) {
            throw new PortException("Invalid integer '" + value + "' in: " + line, e);
        }
    }
}
