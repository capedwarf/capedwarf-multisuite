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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public final class TestItemParser {
    private TestItemParser() {
    }

    public static List<TestItem> parse(InputStream stream) throws IOException {
        if (stream == null)
            return Collections.emptyList();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        try {
            List<TestItem> items = new ArrayList<TestItem>();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '#')
                    continue;

                String[] split = line.split("=");
                items.add(new TestItem(TestItem.Type.valueOf(split[0].toUpperCase()), split[1].trim()));
            }
            return items;
        } finally {
            reader.close();
        }
    }
}
