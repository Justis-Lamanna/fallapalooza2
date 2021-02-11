package com.github.lucbui.birb.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingleRowColumnConfig extends SingleCellConfig {
    private Direction direction;
}
