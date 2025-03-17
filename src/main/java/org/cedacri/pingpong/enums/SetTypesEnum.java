package org.cedacri.pingpong.enums;

import lombok.Getter;

@Getter
public enum SetTypesEnum
{
    BEST_OF_ONE(1),
    BEST_OF_THREE(3),
    BEST_OF_FIVE(5),
    BEST_OF_SEVEN(7),
    ;

    private final int value;

    SetTypesEnum(int value)
    {
        this.value = value;
    }

}
