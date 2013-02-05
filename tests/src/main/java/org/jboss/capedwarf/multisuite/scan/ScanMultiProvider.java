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

package org.jboss.capedwarf.multisuite.scan;

import java.io.File;
import java.util.regex.Pattern;

import org.jboss.capedwarf.multisuite.MultiContext;
import org.jboss.capedwarf.multisuite.MultiProvider;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ScanMultiProvider implements MultiProvider {
    private final Pattern classPattern;
    private final Pattern resourcePattern;

    public ScanMultiProvider() {
        this("[.]*\\.class", "([.]*\\.xml)|([.]*\\.properties)");
    }

    public ScanMultiProvider(String classRegexp, String resourceRegexp) {
        this.classPattern = Pattern.compile(classRegexp);
        this.resourcePattern = Pattern.compile(resourceRegexp);
    }

    public void provide(MultiContext context) throws Exception {
        scan(context, context.getRoot());
    }

    protected void scan(MultiContext context, File current) {
        if (current.isFile()) {
            String name = current.getName();
            if (classPattern.matcher(name).matches()) {
                context.getWar().addClass(context.getRelativePath(current).replace("/", "."));
            } else if (resourcePattern.matcher(name).matches()) {
                // TODO
            }
        } else {
            for (File file : current.listFiles()) {
                scan(context, file);
            }
        }
    }
}
