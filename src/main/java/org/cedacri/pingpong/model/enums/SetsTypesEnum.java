package org.cedacri.pingpong.model.enums;

import lombok.Getter;

@Getter
public enum SetsTypesEnum {
    BEST_OF_ONE(1),
    BEST_OF_THREE(3),
    BEST_OF_FIVE(5),
    BEST_OF_SEVEN(7),
    ;

    private final int value;

    SetsTypesEnum(int value) {
        this.value = value;
    }

}
