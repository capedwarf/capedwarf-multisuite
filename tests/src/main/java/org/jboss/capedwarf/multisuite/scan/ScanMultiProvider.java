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
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.capedwarf.multisuite.MultiContext;
import org.jboss.capedwarf.multisuite.MultiProvider;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class ScanMultiProvider implements MultiProvider {
    private final Pattern classPattern;

    public ScanMultiProvider() {
        this(".+TestCase\\.class");
    }

    public ScanMultiProvider(String classRegexp) {
        this.classPattern = Pattern.compile(classRegexp);
    }

    public void provide(MultiContext context) throws Exception {
        scan(context, context.getRoot());
    }

    protected void scan(MultiContext context, File current) throws Exception {
        if (current.isFile()) {
            String name = current.getName();
            if (classPattern.matcher(name).matches()) {
                Class<?> clazz = context.toClass(current);
                context.addClass(clazz);
                WebArchive war = readWebArchive(clazz, clazz);
                merge(context, war);
            }
        } else {
            for (File file : current.listFiles()) {
                scan(context, file);
            }
        }
    }

    protected void merge(MultiContext context, WebArchive war) {
        WebArchive uber = context.getWar();
        uber.merge(war);
    }

    protected WebArchive readWebArchive(Class<?> clazz, Class<?> current) throws Exception {
        if (current == null || current == Object.class) {
            throw new IllegalArgumentException("No @Deployment on test class: " + clazz.getName());
        }

        Method[] methods = current.getDeclaredMethods();
        for (Method m : methods) {
            if (m.isAnnotationPresent(Deployment.class)) {
                m.setAccessible(true); // in case of non-public
                return (WebArchive) m.invoke(null);
            }
        }

        return readWebArchive(clazz, current.getSuperclass());
    }
}
