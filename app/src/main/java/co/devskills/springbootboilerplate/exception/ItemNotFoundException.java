package co.devskills.springbootboilerplate.exception;

import lombok.Getter;

public class ItemNotFoundException extends IllegalArgumentException {
    @Getter
    private final String responseText;

    public ItemNotFoundException(String responseText) {
        this.responseText = responseText;
    }
}
