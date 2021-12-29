/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package aigis;

import java.util.concurrent.ConcurrentSkipListMap;

import kiss.Managed;
import kiss.Singleton;
import kiss.Storable;

/**
 * 
 */
@SuppressWarnings("serial")
@Managed(Singleton.class)
public class Units extends ConcurrentSkipListMap<String, Unit> implements Storable {

    private Units() {
        restore();
    }
}
