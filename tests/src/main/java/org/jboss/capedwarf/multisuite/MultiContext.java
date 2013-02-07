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

package org.jboss.capedwarf.multisuite;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class MultiContext {
    private final WebArchive war;
    private final File root;
    private final ClassLoader classLoader;

    public MultiContext(WebArchive war, File root, ClassLoader classLoader) {
        this.war = war;
        this.root = root;
        this.classLoader = classLoader;
    }

    public Class<?> loadClass(String className) throws Exception {
        return classLoader.loadClass(className);
    }

    public URL getResource(String resource) {
        return classLoader.getResource(resource);
    }

    public InputStream getResourceAsStream(String resource) {
        return classLoader.getResourceAsStream(resource);
    }

    public String toFQN(File classFile) {
        if (classFile.getName().endsWith(".class") == false) {
            throw new IllegalArgumentException("File is not Java class: " + classFile);
        }
        String relativePath = getRelativePath(classFile);
        relativePath = relativePath.replace("/", ".");
        return relativePath.substring(0, relativePath.length() - ".class".length());
    }

    public Class<?> toClass(File classFile) throws Exception {
        return loadClass(toFQN(classFile));
    }

    public void addClass(String className) throws Exception {
        addClass(loadClass(className));
    }

    public void addClass(Class<?> current) {
        if (current == null || current == Object.class)
            return;

        war.addClass(current);

        for (Class<?> iface : current.getInterfaces()) {
            addClass(iface);
        }

        addClass(current.getSuperclass());
    }

    public String getRelativePath(File current) {
        String path = "";
        while (current.equals(root) == false) {
            path = "/" + current.getName() + path;
            current = current.getParentFile();
        }
        return path.length() > 0 ? path.substring(1) : path;
    }

    public WebArchive getWar() {
        return war;
    }

    public File getRoot() {
        return root;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
