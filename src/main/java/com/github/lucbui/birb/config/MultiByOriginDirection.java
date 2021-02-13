package com.github.lucbui.birb.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MultiByOriginDirection extends SingleCellConfig {
    private Direction direction;
}
