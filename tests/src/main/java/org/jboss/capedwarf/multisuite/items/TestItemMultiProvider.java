/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2013, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.capedwarf.multisuite.items;

import java.io.InputStream;
import java.util.List;

import org.jboss.capedwarf.multisuite.MultiContext;
import org.jboss.capedwarf.multisuite.MultiProvider;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class TestItemMultiProvider implements MultiProvider {
    public void provide(MultiContext context) throws Exception {
        // Tests
        String file = System.getProperty("tests.file", "tests.txt");
        InputStream stream = context.getResourceAsStream(file);
        if (stream == null) {
            throw new IllegalArgumentException("No such file: " + file);
        }

        List<TestItem> items = TestItemParser.parse(stream);
        for (TestItem item : items) {
            if (TestItem.Type.CLASS == item.getType()) {
                context.addClass(item.getFqn());
            } else {
                context.getWar().addPackage(item.getFqn());
            }
        }
    }
}
