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

import kiss.I;
import kiss.XML;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;

public class Unit {

    private static final Directory UnitRoot = Locator.directory(".data/unit");

    public String id;

    public String path;

    public String nameJP;

    public String nameEN;

    /**
     * Read cached page data.
     * 
     * @return
     */
    public XML page() {
        // try to read from local
        File localPage = UnitRoot.directory(id).file("page.html");

        if (localPage.isPresent()) {
            return I.xml(localPage.asJavaPath());
        }

        I.info("Download page for [" + id + "]");

        // try to read from network
        XML xml = I.xml("https://aigis.fandom.com" + path);

        // remove all script and svg elements
        xml.find("script, svg").remove();

        // save as cache
        xml.to(localPage.newBufferedWriter());

        return xml;
    }
}
